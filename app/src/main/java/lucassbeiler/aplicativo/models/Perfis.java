package lucassbeiler.aplicativo.models;

public class Perfis{
    private String name, sex, bio, filename, email;
    private Integer age, max_distance;
    private Integer[] age_range;

    public Integer getMax_distance() {
        return max_distance;
    }

    public Integer[] getAge_range() {
        return age_range;
    }

    public String getName(){
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getSex(){
        return sex;
    }

    public void setSex(String sex){
        this.sex = sex;
    }

    public String getBio(){
        return bio;
    }

    public void setBio(String bio){
        this.bio = bio;
    }

    public String getFilename(){
        return filename;
    }

    public void setFilename(String filename){
        this.filename = filename;
    }

    public Integer getAge(){
        return age;
    }

    public void setAge(Integer age){
        this.age = age;
    }
}
