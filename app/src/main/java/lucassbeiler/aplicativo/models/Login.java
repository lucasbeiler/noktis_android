package lucassbeiler.aplicativo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("user")
    @Expose
    private Usuario user;

    @SerializedName("error")
    @Expose
    private String error;

    private String token;

    public Login(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

    public Usuario getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}