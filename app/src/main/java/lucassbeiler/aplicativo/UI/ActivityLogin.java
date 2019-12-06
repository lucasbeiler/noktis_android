package lucassbeiler.aplicativo.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.textfield.TextInputLayout;
import com.sdsmdg.tastytoast.TastyToast;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.models.Login;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogin extends AppCompatActivity {
    private SharedPreferences sharp;
    private EditText enderecoEmail, usuarioSenha;
    private TextInputLayout txtEmail, txtSenha;
    private ProgressBar barraDeLoading;
    private CheckBox lembrar;
    private SharedPreferences.Editor loginLembrar_edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences loginLembrar;
        TextView botaoRegistrar;
        Button botaoLogin;

        botaoLogin = findViewById(R.id.login);
        botaoRegistrar = findViewById(R.id.criarconta_botao);
        enderecoEmail = findViewById(R.id.endereco_de_email);
        usuarioSenha = findViewById(R.id.senha_login);
        txtEmail = findViewById(R.id.layout_entrada_texto);
        txtSenha = findViewById(R.id.layout_entrada_texto2);
        barraDeLoading = findViewById(R.id.barraLoading);
        lembrar = findViewById(R.id.lembrar_login);
        loginLembrar = getSharedPreferences("login", MODE_PRIVATE);
        loginLembrar_edt = loginLembrar.edit();


        txtEmail.setHintEnabled(true);
        txtSenha.setHintEnabled(true);

        botaoLogin.setTextColor(ContextCompat.getColor(ActivityLogin.this, R.color.textoBotoes));

        botaoRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityLogin.this, ActivityRegistro.class));
            }
        });

//      //  esqueceuSenha.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(ActivityLogin.this, ActivityRecuperarSenha.class));
//            }
//        });

        if (loginLembrar.getBoolean("salvarLogin", true)) {
            enderecoEmail.setText(loginLembrar.getString("email", ""));
            usuarioSenha.setText(loginLembrar.getString("senha", ""));
            lembrar.setChecked(true);
        }
        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                final String email = enderecoEmail.getText().toString();
                String senha = usuarioSenha.getText().toString();

                if(lembrar.isChecked()){
                    loginLembrar_edt.putBoolean("salvarLogin", true);
                    loginLembrar_edt.putString("email", email);
                    loginLembrar_edt.putString("senha", senha);
                    loginLembrar_edt.apply();
                }else{
                    loginLembrar_edt.clear();
                    loginLembrar_edt.apply();
                }

                if(TextUtils.isEmpty(email)){
                    txtEmail.setError(getString(R.string.erro_email_vazio));
                    return;
                }else{
                    txtEmail.setError(null);
                }if (TextUtils.isEmpty(senha)){
                    txtSenha.setError(getString(R.string.erro_senha_vazia));
                }else{
                    txtSenha.setError(null);
                }

                barraDeLoading.setVisibility(View.VISIBLE);
                enviaLogin(new Login(email, senha), new CallsAPI());
            }
        });
    }

    private void enviaLogin(final Login login, final CallsAPI callsAPI){
        callsAPI.retrofitBuilder().fazLogin(login).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> resposta) {
                if(resposta.body() != null && resposta.isSuccessful()){
                    if(!resposta.body().getToken().isEmpty()){
                        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edtr = sharp.edit();
                        edtr.putString("token", resposta.body().getToken());
                        edtr.putString("email", resposta.body().getUser().getEmail());
                        edtr.putString("senha", usuarioSenha.getText().toString());
                        edtr.putString("bio", resposta.body().getUser().getBio());
                        edtr.putString("imagemURL", callsAPI.uploadsDir + resposta.body().getUser().getFilename());
                        edtr.apply();
                        barraDeLoading.setVisibility(View.GONE);
                        finishAffinity();
                        startActivity(new Intent(ActivityLogin.this, ActivityCentral.class));
                        finish();
                    }
                }else{
                    try {
                        TastyToast.makeText(ActivityLogin.this, new JSONObject(resposta.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
                        barraDeLoading.setVisibility(View.GONE);
                    }catch (Exception e){
                        Log.d("LOGIN EXCEPTION", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.d("LOGIN EXCEPTION", t.getMessage());
            }
        });
    }
}