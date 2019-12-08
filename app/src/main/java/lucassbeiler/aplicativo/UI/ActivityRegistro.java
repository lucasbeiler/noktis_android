package lucassbeiler.aplicativo.UI;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.textfield.TextInputLayout;
import com.sdsmdg.tastytoast.TastyToast;

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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.fragments.EscolhaDataFragment;
import lucassbeiler.aplicativo.models.Registro;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class ActivityRegistro extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private EditText enderecoEmaill, Senhaa, nomee, descricaoPerfill;
    private Button botaoRegistrar, botaoDataNascimento;
    private TextView cancelar, dataNascimentoTxt;
    private SharedPreferences sharp;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private SpinKitView emblemaLoading;
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
        emblemaLoading = findViewById(R.id.barraLoading);
        final TextInputLayout txtEmail = findViewById(R.id.layout_entrada_texto);
        final TextInputLayout txtSenha = findViewById(R.id.layout_entrada_texto2);
        final TextInputLayout txtNome = findViewById(R.id.layout_entrada_texto3);
        final TextInputLayout txtDescricaoPerfil = findViewById(R.id.layout_entrada_texto4);

        txtEmail.setHintEnabled(true);
        txtSenha.setHintEnabled(true);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityRegistro.this, ActivityLogin.class));
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
                emblemaLoading.setVisibility(View.VISIBLE);
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

                try{
                    String celular = Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ")";
                    enviaRegistro(new Registro(nome, email, senha, descricaoPerfil, sexo, timestampNascimento.toString(), celular), new CallsAPI());
                }catch(Exception e){
                    Log.d("REGISTRO EXCEPTION", e.getMessage());
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

    private void enviaRegistro(final Registro registro, final CallsAPI callsAPI){
        callsAPI.retrofitBuilder().criaConta(registro).enqueue(new Callback<Registro>() {
            @Override
            public void onResponse(Call<Registro> call, Response<Registro> resposta) {
                if(resposta.isSuccessful()){
                    if(!resposta.body().getToken().isEmpty()){
                        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edtr = sharp.edit();
                        edtr.putString("token", resposta.body().getToken());
                        edtr.putInt("uid", resposta.body().getUser().getId());
                        edtr.putString("nome", resposta.body().getUser().getName());
                        edtr.putString("email", resposta.body().getUser().getEmail());
                        edtr.putString("senha", Senhaa.getText().toString());
                        edtr.putString("bio", resposta.body().getUser().getBio());
                        edtr.putString("imagemURL", callsAPI.uploadsDir + resposta.body().getUser().getFilename());
                        edtr.apply();
                        emblemaLoading.setVisibility(View.GONE);
                        finishAffinity();
                        startActivity(new Intent(ActivityRegistro.this, ActivityCentral.class));
                        finish();
                    }
                }else{
                    emblemaLoading.setVisibility(View.GONE);
                    try {
                        TastyToast.makeText(ActivityRegistro.this, new JSONObject(resposta.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
                    }catch (Exception e){
                        Log.d("REGISTRO EXCEPTION", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Registro> call, Throwable t) {
                Log.d("RESPOSTA", "FALHA");
            }
        });
    }
}