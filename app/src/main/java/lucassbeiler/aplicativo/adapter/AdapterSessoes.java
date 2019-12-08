package lucassbeiler.aplicativo.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.UI.ActivityConversa;
import lucassbeiler.aplicativo.UI.ActivityMatches;
import lucassbeiler.aplicativo.models.AlteracaoConta;
import lucassbeiler.aplicativo.models.OutroUsuario;
import lucassbeiler.aplicativo.models.Sessoes;
import lucassbeiler.aplicativo.models.Usuario;
import lucassbeiler.aplicativo.models.Usuarios;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterSessoes extends RecyclerView.Adapter<AdapterSessoes.ViewHolder>{
    private Sessoes listaSessoes;
    private Context context;

    public AdapterSessoes(Sessoes listaSessoes, Context context){
        this.listaSessoes = listaSessoes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sessao_listview, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position){
//        Uri uriImagem = Uri.parse(CallsAPI.uploadsDir + listaSessoes.getSessions().get(position).getAuthorization());
//        holder.fotoMatchView.setImageURI(uriImagem);
        SharedPreferences sharp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        String celular = listaSessoes.getSessions().get(position).getPhone();
//        Log.d("HORAPOST", String.valueOf(listaSessoes.getSessions().get(position).getTimestamp()));
//        SimpleDateFormat dataPost = new SimpleDateFormat("dd/MM/yyyy '" + context.getString(R.string.as_horas) + "' kk:mm");
//        String hora = dataPost.format(listaSessoes.getSessions().get(position).getTimestamp());
        String ip = listaSessoes.getSessions().get(position).getIp();
        if(listaSessoes.getSessions().get(position).getAuthorization().equals(sharp.getString("token", ""))){
            celular += "\n(Sessão atual)";
        }
        holder.tituloSessaoView.setText(celular);
        holder.ipSessaoView.setText(ip);
//        holder.horaInicioSessaoView.setText(hora);

        holder.botaoEncerrarSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallsAPI callsAPI = new CallsAPI();
                callsAPI.retrofitBuilder().encerraSessao("Bearer " + listaSessoes.getSessions().get(position).getAuthorization()).enqueue(new Callback<ResponseBody>(){
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> resposta){
                        if(resposta.isSuccessful()){
                            try {
                                TastyToast.makeText(context, resposta.body().string(), Toast.LENGTH_LONG, TastyToast.SUCCESS);
                            }catch (Exception e){
                                //
                            }
                        }else{
                            try{
                                TastyToast.makeText(context, new JSONObject(resposta.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
                            }catch(Exception e){
                                Log.d("EDITACONTA EXCEPTION", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t){
                        Log.d("EDITACONTA EXCEPTION2", t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount(){
        return listaSessoes.getSessions().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tituloSessaoView, ipSessaoView, horaInicioSessaoView;
        RelativeLayout listMatchesView;
        FloatingActionButton botaoEncerrarSessao;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            horaInicioSessaoView = itemView.findViewById(R.id.hora_inicio_sessao);
            botaoEncerrarSessao = itemView.findViewById(R.id.encerrar_sessao);
            tituloSessaoView = itemView.findViewById(R.id.celular_sessao);
            ipSessaoView = itemView.findViewById(R.id.ip_sessao);
            listMatchesView = itemView.findViewById(R.id.sessao);
        }
    }
}