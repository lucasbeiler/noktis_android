package lucassbeiler.aplicativo.models;

public class Spot {
    private static int counter = 0;

    public long id;
    public String nome;
    public String distancia;
    public String urlImagem;
    public Integer usuarioID;
    public String bio;
    public String cidade;

    public Spot(String nome, String distancia, String urlImagem, Integer usuarioID, String bio, String cidade) {
        this.id = counter++;
        this.nome = nome;
        this.distancia = distancia;
        this.urlImagem = urlImagem;
        this.usuarioID = usuarioID;
        this.bio = bio;
        this.cidade = cidade;
    }
}
