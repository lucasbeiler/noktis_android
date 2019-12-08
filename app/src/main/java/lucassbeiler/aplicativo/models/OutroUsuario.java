package lucassbeiler.aplicativo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OutroUsuario {
    private int id;
    private Distancia locations;
    private Perfis profiles;
    private Perfis profile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Distancia getLocations(){
        return locations;
    }

    public void setLocations(Distancia locations){
        this.locations = locations;
    }

    public Perfis getProfiles(){
        return profiles;
    }

    public Perfis getProfile(){
        return profile;
    }

    public void setProfiles(Perfis profiles){
        this.profiles = profiles;
    }
}
