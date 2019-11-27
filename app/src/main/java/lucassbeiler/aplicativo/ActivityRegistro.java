package lucassbeiler.aplicativo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lucassbeiler.aplicativo.fragments.EscolhaDataFragment;
import lucassbeiler.aplicativo.requisicoes.FuncoesPOSTJSON;

import static android.view.View.GONE;

public class ActivityRegistro extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private EditText enderecoEmaill, Senhaa, nomee, descricaoPerfill;
    private Button botaoRegistrar, botaoDataNascimento;
    private TextView cancelar, dataNascimentoTxt;
    private SharedPreferences sharp;
    private FuncoesJSON fJSON = new FuncoesJSON();
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private SpinKitView carregaSKV;
    private Localizador localiza = new Localizador();
    private Calendar cal;
    private DatePickerDialog.OnDateSetListener dsl;
    private Long timestampNascimento = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        enderecoEmaill = findViewById(R.id.endereco_de_email);
        Senhaa = findViewById(R.id.senha_login);
        nomee = findViewById(R.id.nome_registro);
        dataNascimentoTxt = findViewById(R.id.dataNascimentoTxt);
        descricaoPerfill = findViewById(R.id.descricao_registro);
        botaoRegistrar = findViewById(R.id.registrar);
        botaoDataNascimento = findViewById(R.id.dataNascimentoBt);
        radioGroup = findViewById(R.id.radio_group);
        botaoRegistrar.setTextColor(ContextCompat.getColor(ActivityRegistro.this, R.color.textoBotoes));
        cancelar = findViewById(R.id.cancelar);
        carregaSKV = findViewById(R.id.barraLoading);
        final TextInputLayout txtEmail = findViewById(R.id.layout_entrada_texto);
        final TextInputLayout txtSenha = findViewById(R.id.layout_entrada_texto2);
        final TextInputLayout txtNome = findViewById(R.id.layout_entrada_texto3);
        final TextInputLayout txtDescricaoPerfil = findViewById(R.id.layout_entrada_texto4);

        txtEmail.setHintEnabled(true);
        txtSenha.setHintEnabled(true);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityRegistro.this, ActivityCentral.class));
                finish();
            }
        });

        dataNascimentoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment escolhaData = new EscolhaDataFragment();
                escolhaData.show(getSupportFragmentManager(), "teste");

            }
        });

        botaoDataNascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment escolhaData = new EscolhaDataFragment();
                escolhaData.show(getSupportFragmentManager(), "teste");

            }
        });

        botaoRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                carregaSKV.setVisibility(View.VISIBLE);
                int idRadioBt = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(idRadioBt);
                String email = enderecoEmaill.getText().toString();
                String senha = Senhaa.getText().toString();
                String nome = nomee.getText().toString();
                String sexo = "";
                if(radioGroup.getCheckedRadioButtonId() != -1) {
                    sexo = radioButton.getText().toString().substring(0, 1).toLowerCase();
                }
                String descricaoPerfil = descricaoPerfill.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    txtEmail.setError(getString(R.string.digite_email));
                    return;
                } else {
                    txtEmail.setError(null);
                }
                if (TextUtils.isEmpty(senha)) {
                    txtSenha.setError(getString(R.string.digite_senha));
                    return;
                } else {
                    txtSenha.setError(null);
                }
                sharp = getSharedPreferences("local", Context.MODE_PRIVATE);
                Double longitude = Double.valueOf(sharp.getString("longitude", "0.0"));
                Double latitude = Double.valueOf(sharp.getString("latitude", "0.0"));

                String[] chavesJSONRegistro = {"name", "email", "password", "bio", "sex", "birth_timestamp"};
                String[] valoresJSONRegistro = {nome, email, senha, descricaoPerfil, sexo, timestampNascimento.toString()};

                String[] chavesJSONCoords = {"latitude", "longitude"};
                String[] valoresJSONCoords = {latitude.toString(), longitude.toString()};

                String[] chavesJSONStatus = {"online"};
                String[] valoresJSONStatus = {"true"};

                try{
                    sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    String respostaAPIRegistro = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/user/create", fJSON.geraJSON(chavesJSONRegistro, valoresJSONRegistro).toString(), "").get();
                    JSONObject jSONO = new JSONObject(respostaAPIRegistro);
                    if(jSONO.has("error")){
                        TastyToast.makeText(getApplicationContext(), jSONO.getString("error"), Toast.LENGTH_SHORT, TastyToast.ERROR);
                        return;
                    }else if(jSONO.has("token")){
                        SharedPreferences.Editor edtr = sharp.edit();
                        edtr.putString("token", jSONO.getString("token"));
                        edtr.putString("email", jSONO.getJSONObject("user").getString("email"));
                        edtr.putString("bio", jSONO.getJSONObject("user").getString("bio"));
                        edtr.putString("uid", jSONO.getJSONObject("user").getString("id"));
                        edtr.putString("senha", senha);
                        edtr.putString("imagemURL", "http://34.95.164.190:3333/uploads/" + jSONO.getJSONObject("user").getString("filename"));
                        edtr.apply();
                        String respostaAPIStatus = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/set/status", fJSON.geraJSON(chavesJSONStatus, valoresJSONStatus).toString(), jSONO.getString("token")).get();
                        String respostaAPICoords = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/location/send", fJSON.geraJSON(chavesJSONCoords, valoresJSONCoords).toString(), jSONO.getString("token")).get();
                        if(respostaAPICoords.contains("true")){
                            Log.d("Registro", respostaAPIRegistro);
                            Log.d("setStatus REGISTRO", respostaAPIStatus);
                            carregaSKV.setVisibility(GONE);
                            startActivity(new Intent(ActivityRegistro.this, ActivityCentral.class));
                            finish();
                        }
                    }
                }catch(Exception e){
                    Log.d("ERRO", e.toString());
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int ano, int mes, int diaDoMes) {
        try {
            mes += 1;
            String data = diaDoMes + "/" + mes + "/" + ano;
            timestampNascimento = new SimpleDateFormat("dd/MM/yyyy").parse(data).getTime();
            Log.d("NASCEU", String.valueOf(timestampNascimento));
            Log.d("NASCEU DATA", data);
            botaoDataNascimento.setVisibility(GONE);
            dataNascimentoTxt.setText("Data de nascimento: " + data);
            dataNascimentoTxt.setVisibility(View.VISIBLE);
        }catch(Exception ex){
            Log.d("EXCEPTION", ex.getMessage());
        }
    }
}