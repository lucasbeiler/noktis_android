package lucassbeiler.aplicativo.models;

public class Sessao {
    private String ip;
    private String authorization;
    private String phone;
    private Long timestamp;

    public String getIp() {
        return ip;
    }

    public Long getTimestamp(){
        return timestamp;
    }

    public String getAuthorization() {
        return authorization;
    }

    public String getPhone() {
        return phone;
    }
}
