package server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.config.NetworkConfig;
import server.room.RoomManager;

public class ServerMain {
    public static final int PORT = NetworkConfig.LISTEN_PORT;

    public static void main(String[] args) {
    	
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[서버] 시작 (포트 " + PORT + ")");
            
            while (true) {
                Socket socket = serverSocket.accept();   // 손님 올 때까지 대기
                System.out.println("[서버] 접속: " + socket.getRemoteSocketAddress());
                new Thread(new ClientHandler(socket)).start();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}