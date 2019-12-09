package lucassbeiler.aplicativo.utilitarias;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ClasseApplication extends Application{
    private Socket socket;
    private FusedLocationProviderClient flc;

    @Override
    public void onCreate(){
        super.onCreate();
        flc = LocationServices.getFusedLocationProviderClient(this);
        iniciaSocket();
    }

    private void iniciaSocket(){
        try{
            SharedPreferences sharp = getApplicationContext().getSharedPreferences("login", Context.MODE_PRIVATE);
            String token = sharp.getString("token", "");
            int uid = sharp.getInt("uid", 0);
            if(!TextUtils.isEmpty(token) && uid != 0){
                IO.Options opts = new IO.Options();
                opts.forceNew = true;
                opts.reconnection = true;
                opts.query = "token=" + sharp.getString("token", "") + "&user=" + uid;
                socket = IO.socket(CallsAPI.enderecoServidor, opts);
            }
        }catch(Exception e){
           Log.d("EXCEPTIONSOCKET", e.getMessage());
        }
    }

    public Socket anulaSocket(){
        socket = null;

        return socket;
    }

    public Socket getSocket(){
        if(socket == null){
            iniciaSocket();
        }
        return socket;
    }
}
