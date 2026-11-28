package common.config;

// 서버 포트와 클라이언트 접속 주소 설정값
public final class NetworkConfig {
    // 서버가 여는 포트
    public static final int LISTEN_PORT = 50023;

    // 클라이언트가 접속할 주소 (Railway 배포 후 변경)
    public static final String SERVER_IP = "viaduct.proxy.rlwy.net";
    public static final int SERVER_PORT = 17510;

    private NetworkConfig() {} // 객체 생성 막기
}