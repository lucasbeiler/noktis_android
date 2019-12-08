package lucassbeiler.aplicativo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {
    private String email;
    private String password;
    private Usuario user;
    private String phone;

    private String error;
    private String token;

    public Login(String email, String password, String phone) {
        super();
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public Usuario getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}