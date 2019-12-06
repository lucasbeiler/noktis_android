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

    public void atualizaLocalizacao(Context context, final String token){
        FusedLocationProviderClient flpc;
        flpc = LocationServices.getFusedLocationProviderClient(context);
        flpc.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>(){
                    @Override
                    public void onSuccess(Location location){
                        if(location != null){
                            atualizaLocalizacaoRemota(new CallsAPI(), location.getLatitude(), location.getLongitude(), token);
                            Log.d("LOCALIZACAOGOOGLE", "LATE " + location.getLatitude());
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
