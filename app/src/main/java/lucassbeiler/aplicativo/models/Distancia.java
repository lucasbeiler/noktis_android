package lucassbeiler.aplicativo.models;

public class Distancia{
    private Integer distance;
    private String address;

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public Integer getDistance(){
        return distance;
    }

    public void setDistance(Integer distance){
        this.distance = distance;
    }
}