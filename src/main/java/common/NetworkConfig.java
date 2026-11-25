package common;

public final class NetworkConfig {
    // 서버가 여는 포트
    public static final int LISTEN_PORT = 50023;

    // 클라이언트가 접속할 주소 (Railway 배포 후 변경)
    public static final String SERVER_IP = "10.240.14.2";
    public static final int SERVER_PORT = 50023;

    private NetworkConfig() {} // 객체 생성 막기
}