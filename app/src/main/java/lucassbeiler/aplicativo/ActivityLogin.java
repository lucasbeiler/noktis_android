package lucassbeiler.aplicativo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.textfield.TextInputLayout;
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
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import lucassbeiler.aplicativo.requisicoes.FuncoesPOSTJSON;
import okhttp3.MediaType;

public class ActivityLogin extends AppCompatActivity {
    private Button botaoLogin;
    private SharedPreferences sharp;
    private TextView botaoRegistrar;
    private EditText enderecoEmail;
    private EditText usuarioSenha;
    private TextInputLayout txtEmail;
    private TextInputLayout txtSenha;
    private ProgressBar barraDeLoading;
    private CheckBox lembrar;
    private SharedPreferences loginLembrar;
    private SharedPreferences.Editor loginLembrar_edt;
    private boolean salvarLogin;
    public static String ir = "";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final FuncoesJSON fJSON = new FuncoesJSON();
    private Localizador localiza = new Localizador();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localiza.atualizaLocalizacao(ActivityLogin.this, this);

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

        salvarLogin = loginLembrar.getBoolean("salvarLogin", true);
        if (salvarLogin == true) {
            enderecoEmail.setText(loginLembrar.getString("email", ""));
            usuarioSenha.setText(loginLembrar.getString("senha", ""));
            lembrar.setChecked(true);
        }
        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                InputMethodManager inmng = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                inmng.hideSoftInputFromWindow(enderecoEmail.getWindowToken(), 0);

                sharp = getSharedPreferences("local", Context.MODE_PRIVATE);
                Double longitude = Double.valueOf(sharp.getString("longitude", "0.0"));
                Double latitude = Double.valueOf(sharp.getString("latitude", "0.0"));
                Log.d("coord sera", latitude.toString());
                final String email = enderecoEmail.getText().toString();
                String senha = usuarioSenha.getText().toString();

                if (lembrar.isChecked()) {
                    loginLembrar_edt.putBoolean("salvarLogin", true);
                    loginLembrar_edt.putString("email", email);
                    loginLembrar_edt.putString("senha", senha);
                    loginLembrar_edt.commit();
                } else {
                    loginLembrar_edt.clear();
                    loginLembrar_edt.commit();
                }

                if (TextUtils.isEmpty(email)) {
                    txtEmail.setError(getString(R.string.erro_email_vazio));
                    return;
                } else {
                    txtEmail.setError(null);
                }
                if (TextUtils.isEmpty(senha)) {
                    txtSenha.setError(getString(R.string.erro_senha_vazia));
                } else {
                    txtSenha.setError(null);
                }

                barraDeLoading.setVisibility(View.VISIBLE);

                String[] chavesJSONLogin = {"email", "password"};
                String[] valoresJSONLogin = {email, senha};

                String[] chavesJSONCoords = {"latitude", "longitude"};
                String[] valoresJSONCoords = {latitude.toString(), longitude.toString()};

                String[] chavesJSONStatus = {"online"};
                String[] valoresJSONStatus = {"true"};

                try{
                    sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edtr = sharp.edit();
                    String respostaAPILogin = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/sessions", fJSON.geraJSON(chavesJSONLogin, valoresJSONLogin).toString(), "").get();
                    JSONObject jSONO = new JSONObject(respostaAPILogin);
                    if(jSONO.has("error")){
                        TastyToast.makeText(getApplicationContext(), jSONO.getString("error"), Toast.LENGTH_SHORT, TastyToast.ERROR);
                        return;
                    }else if(jSONO.has("token")){
                        edtr.putString("token", jSONO.getString("token"));
                        edtr.putString("email", jSONO.getJSONObject("user").getString("email"));
                        edtr.putString("bio", jSONO.getJSONObject("user").getString("bio"));
                        edtr.putString("uid", jSONO.getJSONObject("user").getString("id"));
                        edtr.putString("imagemURL", "http://34.95.164.190:3333/uploads/" + jSONO.getJSONObject("user").getString("filename"));
                        edtr.apply();
                        String respostaAPIStatus = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/set/status", fJSON.geraJSON(chavesJSONStatus, valoresJSONStatus).toString(), jSONO.getString("token")).get();
                        String respostaAPICoords = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/location/send", fJSON.geraJSON(chavesJSONCoords, valoresJSONCoords).toString(), jSONO.getString("token")).get();
                        if(respostaAPICoords.contains("true")){
                            Log.d("Login", respostaAPILogin);
                            Log.d("setStatus LOGIN", respostaAPIStatus);
                            if(ir.equals("config")){
                                finishAffinity();
                                startActivity(new Intent(ActivityLogin.this, ActivityEditarPerfil.class));
                                finish();
                            }else{
                                finishAffinity();
                                startActivity(new Intent(ActivityLogin.this, ActivityCentral.class));
                                finish();
                            }
                        }
                    }
                }catch(Exception e){
                    Log.d("ERRO", e.toString());
                }
            }
        });
    }
}