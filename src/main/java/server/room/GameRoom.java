package server.room;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import common.message.ErrorCode;
import common.message.ErrorMessage;
import common.message.Message;
import common.message.Role;
import common.message.SignalMessage;
import common.message.Theme;
import common.message.game.PlayerProfile;
import common.message.lobby.*;
import common.message.game.*;
import server.game.ServerGameManager;
import server.game.player.Player;
import server.game.word.WordRepository;
import server.network.ClientHandler;

public class GameRoom {
	private static final AtomicInteger NEXT_GAME_ID = new AtomicInteger(1); // 모든 방이 공유하는 게임 번호
	private static final AtomicInteger NEXT_PLAYER_ID = new AtomicInteger(1); // 고유한 playerId
	private static final long TICK_MS = 50; // 게임 상태 갱신 주기
	private static final long COUNTDOWN_MS = 3000; // 클라 GameStartCountdown(3초)과 맞춤

	// 모든 방이 같이 쓰는 게임 타이머 (방마다 쓰레드 만들지 않게)
	private static final ScheduledExecutorService TICKER = Executors.newSingleThreadScheduledExecutor(r -> {
		Thread t = new Thread(r, "game-ticker");
		t.setDaemon(true);
		return t;
	});
	
    private final int id;
    private ClientHandler host;
    private ClientHandler guest;
    private boolean guestReady = false;
    private Theme theme = Theme.BASIC; // 방장이 안 고르면 기본 테마
    private RoomState state = RoomState.WAITING;

    private final WordRepository wordRepository;
    private ServerGameManager game;      // 게임 중일 때만 존재
    private ScheduledFuture<?> tickTask; // 게임 중일 때만 존재

    
    public GameRoom(int id, WordRepository wordRepository) {
        this.id = id;
        this.wordRepository = wordRepository;
    }

    
    // 들어갈 수 있으면 자리 배정하고 true, 꽉 찼거나 게임 중이면 false
    public synchronized boolean tryJoin(ClientHandler player) {
        if (state != RoomState.WAITING || guest != null) {
            return false;
        }
        
        player.setPlayerId(NEXT_PLAYER_ID.getAndIncrement());  // 입장 성공할 때만 발급
        
        if (host == null) {
            host = player;
            player.send(new JoinedMessage(id, Role.HOST));
        }
        
        else {
            guest = player;
            player.send(new JoinedMessage(id, Role.GUEST));
            host.send(new OpponentMessage(player.getNickname()));    // 방장에게 상대 정보
            player.send(new OpponentMessage(host.getNickname()));    // 참가자에게 상대 정보
        }
        
        return true;
    }
    
    
    // 채팅
    public synchronized void chat(ClientHandler sender, String content) {
        broadcast(new ChatMessage(sender.getNickname(), content)); // 보낸 사람 닉네임은 서버가 붙임
    }
    
    
    // 준비 (참가자만)
    public synchronized void setReady(ClientHandler sender, boolean ready) {
        if (sender != guest) {
            return; // 참가자가 아니면 조용히 무시
        }
        guestReady = ready;
        broadcast(new ReadyMessage(ready)); // 방장 + 참가자 둘 다
    }
    
    
    //  테마 (방장만)
    public synchronized void setTheme(ClientHandler sender, Theme newTheme) {
        if (sender != host) {
            sender.send(new ErrorMessage(ErrorCode.NOT_HOST));
            return;
        }
        theme = newTheme; // 게임 시작 때 쓰려고 저장
        broadcast(new ThemeMessage(theme)); // 테마 변경을 전체 공지
    }
    
    
    // 시작 (방장만)
    public synchronized void start(ClientHandler sender) {
        if (sender != host) {
            sender.send(new ErrorMessage(ErrorCode.NOT_HOST));
            return;
        }
        
        if (guest == null || !guestReady || state == RoomState.PLAYING) {
            return; // 시작 조건이 아니면 조용히 무시
        }
        
        int gameId = NEXT_GAME_ID.getAndIncrement();
        game = new ServerGameManager(gameId,
                new Player(host.getPlayerId(), host.getNickname()),
                new Player(guest.getPlayerId(), guest.getNickname()),
                theme, wordRepository);

        state = RoomState.PLAYING;
        PlayerProfile p1 = new PlayerProfile(host.getPlayerId(), host.getNickname());
        PlayerProfile p2 = new PlayerProfile(guest.getPlayerId(), guest.getNickname());
        broadcast(new GameStartMessage(gameId, theme, p1, p2));

        // 클라 카운트다운(3초)이 끝나는 시점에 게임 시작
        tickTask = TICKER.schedule(this::beginGame, COUNTDOWN_MS, TimeUnit.MILLISECONDS);
    }
    
    
    // 카운트다운이 끝나면 TICKER 쓰레드에서 호출
    private synchronized void beginGame() {
        if (game == null) {
            return; // 카운트다운 중에 누가 나가서 이미 정리됨
        }
        
        try {
            game.startGame(); // 여기서 DB 단어 불러옴
        } catch (RuntimeException e) {
            e.printStackTrace();
            stopGame();
            return;
        }
        
        tickTask = TICKER.scheduleAtFixedRate(this::tick, TICK_MS, TICK_MS, TimeUnit.MILLISECONDS);
    }
    
    
    // 단어 입력 (게임 중에만)
    public synchronized void submitWord(ClientHandler sender, String word, double typingTime) {
        if (game == null) {
            return;
        }
        game.processInput(sender.getPlayerId(), word, typingTime);
    }
    
    
    // 게임 타이머가 TICK_MS마다 호출
    private synchronized void tick() {
        if (game == null) {
            return; // 이미 끝난 게임
        }
        
        try {
            game.updateGame();

            if (game.checkGameOver()) {
                broadcast(new GameFinishedMessage(game.getGameResult()));
                stopGame();
                return;
            }

            broadcast(GameStateMapper.toMessage(game)); // 두 클라이언트 같은 화면
        } catch (Exception e) {
            // 예외로 타이머가 조용히 죽지 않게 로그 남기고 게임 정리
            e.printStackTrace();
            stopGame();
        }
    }
    
    
    // 게임 종료 정리: 타이머 끄고 대기실 상태로
    private void stopGame() {
        if (tickTask != null) {
            tickTask.cancel(false);
            tickTask = null;
        }
        game = null;
        state = RoomState.WAITING;
        guestReady = false;
    }
    
    
    // 퇴장
    public synchronized void leave(ClientHandler leaver) {
        boolean hostLeft;
        
        if (leaver == guest) {
            guest = null;
            hostLeft = false;
        }
        
        else if (leaver == host) {
            host = guest; // 남은 참가자가 방장 승계
            guest = null;
            hostLeft = true;
        }
        
        else {
            return;
        }

        stopGame(); // 게임 중이었다면 타이머 끄고, 대기실 상태로

        if (host != null) { // 둘 다 나간 게 아닐 때
            host.send(SignalMessage.opponentLeft());
            if (hostLeft) { // 승계됐다고 알려주기
                host.send(new RoleMessage(Role.HOST));
            }
        }
    }
    
    
    // ===== 공통 =====
    private void broadcast(Message message) {
        if (host != null) host.send(message);
        if (guest != null) guest.send(message);
    }

    
    public synchronized boolean isEmpty() {
        return host == null && guest == null;
    }

    
    public int getId() {
        return id;
    }
    
}