package lucassbeiler.aplicativo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.UI.ActivityCentral;
import lucassbeiler.aplicativo.UI.ActivityTimelineUsuarioAtual;
import lucassbeiler.aplicativo.models.Spot;

public class AdapterCards extends RecyclerView.Adapter<AdapterCards.ViewHolder> {
    private LayoutInflater inflater;
    private List<Spot> spots;

    public AdapterCards(Context context, List<Spot> spots) {
        this.inflater = LayoutInflater.from(context);
        this.spots = spots;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_spot, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Spot spot = spots.get(position);
        holder.nome.setText(spot.nome);
        holder.cidade.setText(spot.cidade);
        Uri uriFotoUsuario = Uri.parse(spot.urlImagem);
        holder.urlImagem.setImageURI(uriFotoUsuario);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("idUsuario", spot.usuarioID.toString());
                v.getContext().startActivity(new Intent(v.getContext(), ActivityTimelineUsuarioAtual.class)
                        .putExtra("idUsuario", spot.usuarioID.toString())
                        .putExtra("nomeUsuario", spot.nome)
                        .putExtra("urlImagemUsuario", spot.urlImagem)
                        .putExtra("bioUsuario", spot.bio));
                //putExtra("EXTRA_SESSION_ID", sessionId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return spots.size();
    }

    public List<Spot> getSpots() {
        return spots;
    }

    public void setSpots(List<Spot> spots) {
        this.spots = spots;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome;
        TextView cidade;
        SimpleDraweeView urlImagem;
        ViewHolder(View view) {
            super(view);
            this.nome = view.findViewById(R.id.item_name);
            this.cidade = view.findViewById(R.id.item_city);
            this.urlImagem = view.findViewById(R.id.item_image);
        }
    }

}