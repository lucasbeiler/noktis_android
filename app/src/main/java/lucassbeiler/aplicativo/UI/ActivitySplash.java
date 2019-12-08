package lucassbeiler.aplicativo.UI;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.library.bubbleview.BubbleTextView;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import lucassbeiler.aplicativo.BuildConfig;
import lucassbeiler.aplicativo.utilitarias.Localizador;
import lucassbeiler.aplicativo.R;

public class ActivitySplash extends AppCompatActivity{
    private TextView nomeApp, compiladoEm;
    private Localizador localiza = new Localizador();
    private SharedPreferences sharp;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Fresco.initialize(this);
        SharedPreferences sharp = getSharedPreferences("login", MODE_PRIVATE);
        localiza.atualizaLocalizacao(this, sharp.getString("token", ""));

        SimpleDateFormat dataCompilacao = new SimpleDateFormat("EEEE, dd/MM/yyyy '" + getString(R.string.as_horas) + "' kk:mm:ss");
        nomeApp = findViewById(R.id.nome_app);
        nomeApp.setText(getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME);

        compiladoEm = findViewById(R.id.compilado_em);
        compiladoEm.setText("Compilado em: " + dataCompilacao.format(BuildConfig.BUILD_TIME));

        new Timer().schedule(new TimerTask(){
            @Override
            public void run(){
                startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                finish();
            }
        }, 1992);
    }
}