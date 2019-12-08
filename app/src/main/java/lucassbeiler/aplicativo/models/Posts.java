package lucassbeiler.aplicativo.models;

import java.util.List;

public class Posts {

    private List<Post> post = null;
    private Perfis profile;
    private Integer id;

    public Posts(Integer id) {
        this.id = id;
    }

    public Perfis getPerfis() {
        return profile;
    }

    public List<Post> getPost() {
        return post;
    }

    public void setPosts(List<Post> post) {
        this.post = post;
    }
}