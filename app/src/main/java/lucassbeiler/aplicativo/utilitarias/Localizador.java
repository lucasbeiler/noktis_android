package lucassbeiler.aplicativo.utilitarias;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sdsmdg.tastytoast.TastyToast;

import org.jetbrains.annotations.NotNull;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.models.Localizacao;
import mumayank.com.airlocationlibrary.AirLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Localizador extends AppCompatActivity {
    private AirLocation airLocation;

    public void atualizaLocalizacao(AppCompatActivity act, final Context context, final String token) {
        airLocation = new AirLocation(act, false, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NotNull Location localizacao) {
                try {
                    SharedPreferences sharp = context.getSharedPreferences("local", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edtr = sharp.edit();
                    edtr.putString("latitude", Double.toString(localizacao.getLatitude()));
                    edtr.putString("longitude", Double.toString(localizacao.getLongitude()));
                    edtr.apply();
                    atualizaLocalizacaoRemota(new CallsAPI(), localizacao.getLatitude(), localizacao.getLongitude(), token);
                } catch (Exception e) {
                    SharedPreferences sharp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                    if(!sharp.getString("token", "").isEmpty()) {
                        TastyToast.makeText(context, "Exceção ao detectar localização!", Toast.LENGTH_SHORT, TastyToast.ERROR);
                    }
                }
            }

            @Override
            public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                SharedPreferences sharp = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                if(!sharp.getString("token", "").isEmpty()) {
                    TastyToast.makeText(context, "Erro ao detectar localização!", Toast.LENGTH_SHORT, TastyToast.ERROR);
                }
            }
        });
    }



    private void atualizaLocalizacaoRemota(CallsAPI callsAPI, Double latitude, Double longitude, String token){
        Localizacao localizacao = new Localizacao(latitude, longitude);
        Log.d("LOCALIZA", "Lat " + latitude + " | Long: " + longitude);

        callsAPI.retrofitBuilder().localizaUsuario(localizacao, "Bearer " + token).enqueue(new Callback<Localizacao>(){
            @Override
            public void onResponse(Call<Localizacao> call, Response<Localizacao> response){
                if(response.isSuccessful()){
                    Log.d("teste", "Localização enviada!");
                }else{
                    try{
                        Log.d("teste", response.errorBody().string());
                    }catch(Exception e){
                        Log.d("teste", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Localizacao> call, Throwable t){
                Log.d("teste", t.getMessage());
            }
        });
    }
}
