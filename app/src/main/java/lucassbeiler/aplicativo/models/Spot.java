package lucassbeiler.aplicativo.models;

public class Spot {
    private static int counter = 0;

    public long id;
    public String nome;
    public String cidade;
    public String urlImagem;
    public Integer usuarioID;
    public String bio;

    public Spot(String nome, String cidade, String urlImagem, Integer usuarioID, String bio) {
        this.id = counter++;
        this.nome = nome;
        this.cidade = cidade;
        this.urlImagem = urlImagem;
        this.usuarioID = usuarioID;
        this.bio = bio;
    }
}
