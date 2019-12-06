package lucassbeiler.aplicativo.models;

public class AlteracaoConta {
    private String email;
    private String password;
    private String confirmPassword;
    private String oldPassword;
    private String bio;

    public AlteracaoConta(String email, String password, String confirmPassword, String oldPassword, String bio) {
        if(!email.isEmpty()) {
            this.email = email;
        }if(!password.isEmpty()) {
            this.password = password;
        }if(!confirmPassword.isEmpty()) {
            this.confirmPassword = confirmPassword;
        }if(!oldPassword.isEmpty()) {
            this.oldPassword = oldPassword;
        }if(!bio.isEmpty()) {
            this.bio = bio;
        }
    }
}