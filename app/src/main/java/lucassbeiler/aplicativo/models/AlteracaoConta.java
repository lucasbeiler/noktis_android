package lucassbeiler.aplicativo.models;

public class AlteracaoConta {
    private String email;
    private String password;
    private String confirmPassword;
    private String oldPassword;
    private String bio;
    private Integer[] age_range;
    private Integer max_distance;

    public AlteracaoConta(String email, String password, String confirmPassword, String oldPassword, String bio, Integer[] age_range, Integer max_distance) {
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
        }if(age_range != null){
            this.age_range = age_range;
        }if(max_distance != null){
            this.max_distance = max_distance;
        }
    }
}