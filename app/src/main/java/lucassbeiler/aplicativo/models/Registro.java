package lucassbeiler.aplicativo.models;

public class Registro {
    private String token;
    private String name;
    private String email;
    private String password;
    private String bio;
    private String sex;
    private String birth_timestamp;
    private String phone;

    private Usuario user;

    public Registro(String name, String email, String password, String bio, String sex, String birth_timestamp, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.sex = sex;
        this.birth_timestamp = birth_timestamp;
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public Usuario getUser() {
        return user;
    }
}
