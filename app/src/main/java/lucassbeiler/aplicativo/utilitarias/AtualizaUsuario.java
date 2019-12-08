package lucassbeiler.aplicativo.utilitarias;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;

import lucassbeiler.aplicativo.UI.ActivityCentral;
import lucassbeiler.aplicativo.UI.ActivityTimelineUsuarioAtual;
import lucassbeiler.aplicativo.models.OutroUsuario;
import lucassbeiler.aplicativo.models.Perfis;
import lucassbeiler.aplicativo.models.Reacao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AtualizaUsuario {
    public void atualizaDadosUsuario(CallsAPI callsAPI, final Context context){
        final SharedPreferences sharp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        callsAPI.retrofitBuilder().atualizaInformacoes("Bearer " + sharp.getString("token", "")).enqueue(new Callback<OutroUsuario>(){
            @Override
            public void onResponse(Call<OutroUsuario> call, final Response<OutroUsuario> response){
                if(response.isSuccessful() && response.body() != null){
                    SharedPreferences.Editor edtr = sharp.edit();
                    edtr.putString("email", response.body().getProfile().getEmail());
                    edtr.putString("bio", response.body().getProfile().getBio());
                    edtr.putString("imagemURL", CallsAPI.uploadsDir + response.body().getProfile().getFilename());
                    edtr.apply();
                }
            }

            @Override
            public void onFailure(Call<OutroUsuario> call, Throwable t){
                TastyToast.makeText(context, "Erro", Toast.LENGTH_SHORT, TastyToast.ERROR);
            }
        });
    }

}
