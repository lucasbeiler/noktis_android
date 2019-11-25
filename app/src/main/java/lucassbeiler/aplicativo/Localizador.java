package lucassbeiler.aplicativo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sdsmdg.tastytoast.TastyToast;

import org.jetbrains.annotations.NotNull;

import mumayank.com.airlocationlibrary.AirLocation;

public class Localizador extends AppCompatActivity {
    private AirLocation airLocation;
    private SharedPreferences sharp;

    public void atualizaLocalizacao(AppCompatActivity actv, final Context context) {
        airLocation = new AirLocation(actv, true, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NotNull Location localizacao) {
                try {
                    sharp = context.getSharedPreferences("local", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edtr = sharp.edit();
                    edtr.putString("latitude", Double.toString(localizacao.getLatitude()));
                    edtr.putString("longitude", Double.toString(localizacao.getLongitude()));
                    edtr.putString("hora", Long.toString(System.currentTimeMillis()));
                    edtr.apply();
                } catch (Exception e) {
                    //
                }
            }

            @Override
            public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                TastyToast.makeText(context, context.getString(R.string.nao_possivel_detectar_localizacao_titulo), Toast.LENGTH_LONG, TastyToast.ERROR);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("DEU", "DEU");
                }
            }
        }
    }

//    public String enviaLocalizacao(Double latitude, Double longitude, String token) throws Exception {
//        FuncoesJSON fJSON = new FuncoesJSON();
//
//        String respostaAPICoords = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/location/send", fJSON.geraJSON(chavesJSONCoords, valoresJSONCoords).toString(), token).get();
//        Log.d("RESPONDEU isso", respostaAPICoords);
//        return respostaAPICoords;
//    }
}
