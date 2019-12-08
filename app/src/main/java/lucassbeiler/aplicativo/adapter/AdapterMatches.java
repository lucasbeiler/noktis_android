package lucassbeiler.aplicativo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telecom.Call;
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
import java.util.ArrayList;
import java.util.List;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.UI.ActivityConversa;
import lucassbeiler.aplicativo.UI.ActivityMatches;
import lucassbeiler.aplicativo.models.OutroUsuario;
import lucassbeiler.aplicativo.models.Usuario;
import lucassbeiler.aplicativo.models.Usuarios;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;

public class AdapterMatches extends RecyclerView.Adapter<AdapterMatches.ViewHolder>{
    private Usuarios listaMatches;
    private Context context;

    public AdapterMatches(Usuarios listaMatches, Context context){
        this.listaMatches = listaMatches;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_listamatches, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position){
        Uri uriImagem = Uri.parse(CallsAPI.uploadsDir + listaMatches.getUserFilter().get(position).getProfiles().getFilename());
        holder.fotoMatchView.setImageURI(uriImagem);
        holder.nomeMatchView.setText(listaMatches.getUserFilter().get(position).getProfiles().getName());

        holder.listMatchesView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                context.startActivity(new Intent(context, ActivityConversa.class).putExtra("idMatch", String.valueOf(listaMatches.getUserFilter().get(position).getId())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public int getItemCount(){
        return listaMatches.getUserFilter().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView fotoMatchView;
        TextView nomeMatchView;
        RelativeLayout listMatchesView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            fotoMatchView = itemView.findViewById(R.id.foto_match);
            nomeMatchView = itemView.findViewById(R.id.nome_match);
            listMatchesView = itemView.findViewById(R.id.lista_matches);
        }
    }
}