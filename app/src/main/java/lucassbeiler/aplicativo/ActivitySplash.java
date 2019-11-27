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

}