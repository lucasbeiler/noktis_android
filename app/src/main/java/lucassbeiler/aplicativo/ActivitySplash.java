package lucassbeiler.aplicativo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sdsmdg.tastytoast.TastyToast;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ActivitySplash extends AppCompatActivity {
    private TextView nomeApp, compiladoEm;
    private Localizador localiza = new Localizador();
    private SharedPreferences sharp;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SimpleDateFormat dataCompilacao = new SimpleDateFormat("EEEE, dd/MM/yyyy '" + getString(R.string.as_horas) + "' kk:mm:ss");
        nomeApp = findViewById(R.id.nome_app);
        nomeApp.setText("Noktis v" + BuildConfig.VERSION_NAME);

        compiladoEm = findViewById(R.id.compilado_em);
        compiladoEm.setText("Compilado em: " + dataCompilacao.format(BuildConfig.BUILD_TIME));

        localiza.atualizaLocalizacao(ActivitySplash.this, this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(ActivitySplash.this, ActivityCentral.class));
                finish();
            }
        }, 1990);
    }

    public void enviaSocket(final AppCompatActivity act) {
        try {
            sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            opts.query = "user=" + sharp.getString("uid", "");
            socket = IO.socket("http://34.95.164.190:3333/", opts);
        }catch(Exception e){
            //
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("CONECTADO", "CONECTOU");
            }

        }).on("match", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
               // try {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TastyToast.makeText(getApplicationContext(), "DEU MATCH COM ", Toast.LENGTH_SHORT, TastyToast.SUCCESS);
                        }
                    });
                    Log.d("MATCH", "MATCH COM");
                    //}catch (Exception e){
                    //Log.d("MATCH EXCEPTION", e.getMessage());
                    // }
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("DESCONECTADO", args[0].toString());
            }
        }).on("teste", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("teste", args[0].toString());
            }

        });

        socket.connect();
    }
}