package lucassbeiler.aplicativo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.UI.ActivityConversa;
import lucassbeiler.aplicativo.UI.ActivityMatches;
import lucassbeiler.aplicativo.models.OutroUsuario;
import lucassbeiler.aplicativo.models.Perfis;
import lucassbeiler.aplicativo.models.Posts;
import lucassbeiler.aplicativo.models.Usuario;
import lucassbeiler.aplicativo.models.Usuarios;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;

public class AdapterFeedUsuarioAtual extends RecyclerView.Adapter<AdapterFeedUsuarioAtual.ViewHolder>{
    private Posts listaPosts;
    private Perfis perfil;
    private Context context;

    public AdapterFeedUsuarioAtual(Posts listaPosts, Perfis perfil, Context context){
        this.listaPosts = listaPosts;
        this.perfil = perfil;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publicacao_perfil_usuario, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position){
        Uri uriImagem = Uri.parse(CallsAPI.uploadsDir + perfil.getFilename());
        holder.fotoMatchView.setImageURI(uriImagem);
        holder.nomeMatchView.setText(perfil.getName());
        SimpleDateFormat dataPost = new SimpleDateFormat("dd/MM/yyyy '" + context.getString(R.string.as_horas) + "' kk:mm");
        holder.horaPostView.setText(dataPost.format(listaPosts.getPost().get(position).getDate()));
        holder.descricaoMatchView.setText(listaPosts.getPost().get(position).getDescription());

        if(!listaPosts.getPost().get(position).getImage().isEmpty()){
            holder.fotoPostView.setVisibility(View.VISIBLE);
            Uri uriImagemPost = Uri.parse(CallsAPI.uploadsDir + listaPosts.getPost().get(position).getImage());
            holder.fotoPostView.setImageURI(uriImagemPost);
        }

        holder.listPostsUsuarioView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
    }

    @Override
    public int getItemCount(){
        return listaPosts.getPost().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView fotoMatchView, fotoPostView;
        TextView nomeMatchView, descricaoMatchView, horaPostView;
        RelativeLayout listPostsUsuarioView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            fotoMatchView = itemView.findViewById(R.id.foto_perfil_usuario);
            fotoPostView = itemView.findViewById(R.id.foto_post_usuario);
            horaPostView = itemView.findViewById(R.id.hora_post);
            nomeMatchView = itemView.findViewById(R.id.nome_usuario_post);
            listPostsUsuarioView = itemView.findViewById(R.id.post_usuario);
            descricaoMatchView = itemView.findViewById(R.id.publicacao_usuario);
        }
    }
}