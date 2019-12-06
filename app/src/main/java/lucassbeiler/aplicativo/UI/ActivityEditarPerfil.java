package lucassbeiler.aplicativo.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sdsmdg.tastytoast.TastyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;

import io.socket.client.Socket;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.models.AlteracaoConta;
import lucassbeiler.aplicativo.models.EncerramentoConta;
import lucassbeiler.aplicativo.utilitarias.ClasseApplication;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityEditarPerfil extends AppCompatActivity{
    private static final int ESCOLHER_IMAGEM = 1;
    private SimpleDraweeView imagemPersonalizar;
    private Button botaoFinalizar;
    private Uri imagemUrl = null;
    private ProgressDialog carregandoTela;
    private EditText editarEmail;
    private EditText editarSenha, editarSenha2, editarSenhaAntiga;
    private EditText definirNome;
    private TextView textoEmail;
    private TextView textoSenha;
    private TextView textoNome;
    private TextView apagarConta;
    private TextView desconectarConta;
    private TextView latitudeDebug, longitudeDebug;
    private SharedPreferences sharp;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String token = sharp.getString("token", "");

        if(token.isEmpty()){
            finishAffinity();
            startActivity(new Intent(ActivityEditarPerfil.this, ActivityLogin.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            return;
        }
        final ClasseApplication app = (ClasseApplication) getApplication();
        socket = app.getSocket();
        setContentView(R.layout.activity_editar_perfil);

        String imagemURL = sharp.getString("imagemURL", "");
        Log.d("imagemURL", imagemURL);

        carregandoTela = new ProgressDialog(this);
        imagemPersonalizar = findViewById(R.id.imagemConfig);
        editarEmail = findViewById(R.id.editarEmail);
        editarSenha = findViewById(R.id.editarSenha);
        editarSenha2 = findViewById(R.id.editarSenha2);
        editarSenhaAntiga = findViewById(R.id.senhaAntiga);
        definirNome = findViewById(R.id.definirNome);
        botaoFinalizar = findViewById(R.id.botaoConcluir);
        apagarConta = findViewById(R.id.apagarConta);
        desconectarConta = findViewById(R.id.desconectarConta);
        latitudeDebug = findViewById(R.id.latitudeDebug);
        longitudeDebug = findViewById(R.id.longitudeDebug);


        sharp = getSharedPreferences("local", Context.MODE_PRIVATE);
        latitudeDebug.setText("Debug Lat: " + sharp.getString("latitude", "Erro"));
        longitudeDebug.setText("Debug Long: " + sharp.getString("longitude", "Erro"));
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);

        textoEmail = findViewById(R.id.textoEmail);
        textoSenha = findViewById(R.id.textoSenha);
        textoNome = findViewById(R.id.textoNome);
        carregaImagem(imagemURL);

        desconectarConta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor edtr = sharp.edit();
                edtr.clear();
                edtr.apply();
                socket.disconnect();
                socket = app.anulaSocket();
                finishAffinity();
                startActivity(new Intent(ActivityEditarPerfil.this, ActivityLogin.class));
                finish();
                TastyToast.makeText(ActivityEditarPerfil.this, "Logout executado com sucesso!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        });

        apagarConta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                perguntaExclusaoConta();
            }
        });

        imagemPersonalizar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        comecarEdicao();
    }

    private void comecarEdicao(){
        try{
            textoEmail.setText("E-mail: " + sharp.getString("email", ""));
            textoSenha.setText("Senha: " + "*********");
            textoNome.setText("Bio: " + sharp.getString("bio", ""));

            textoEmail.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    textoEmail.setVisibility(View.GONE);
                    editarEmail.setHint(sharp.getString("email", ""));
                    editarEmail.setVisibility(View.VISIBLE);

                }
            });
            textoSenha.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    textoSenha.setVisibility(View.GONE);
                    editarSenha.setVisibility(View.VISIBLE);
                    editarSenha2.setVisibility(View.VISIBLE);
                    editarSenhaAntiga.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) definirNome.getLayoutParams();
                    params.addRule(RelativeLayout.BELOW, R.id.senhaAntiga);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) textoNome.getLayoutParams();
                    params2.addRule(RelativeLayout.BELOW, R.id.senhaAntiga);

                }
            });
            textoNome.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    textoNome.setVisibility(View.GONE);
                    definirNome.setHint(sharp.getString("bio", ""));
                    definirNome.setVisibility(View.VISIBLE);

                }
            });

            botaoFinalizar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    AlteracaoConta alteracaoConta = new AlteracaoConta(editarEmail.getText().toString(), editarSenha.getText().toString(),
                            editarSenha2.getText().toString(), editarSenhaAntiga.getText().toString(), definirNome.getText().toString());
                    alteraConta(alteracaoConta, new CallsAPI());
                }
            });
        }catch(NullPointerException e){
            //
        }
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent data){
        try{
            super.onActivityResult(codigoRequisicao, codigoResultado, data);
            Uri UriImagem = data.getData();
            if(codigoRequisicao == ESCOLHER_IMAGEM && codigoResultado == RESULT_OK && UriImagem != null){
                CropImage.activity(UriImagem)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if(codigoRequisicao == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(codigoResultado == RESULT_OK){
                    imagemUrl = result.getUri();
                    imagemPersonalizar.setImageURI(imagemUrl);
                    sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    String token = sharp.getString("token", "");
                    enviaArquivo(imagemUrl.toString(), token, new CallsAPI());
                }else if(codigoResultado == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception erro = result.getError();
                }
            }
        }catch(NullPointerException npex){
            Log.d("ERRO EditarPerfil.java", npex.getMessage());
        }
    }

    private void carregaImagem(final String imagemURL){
        try{
            Uri uriImagem = Uri.parse(imagemURL);
            imagemPersonalizar.setImageURI(uriImagem);
        }catch(IllegalArgumentException iae){
            TastyToast.makeText(ActivityEditarPerfil.this, getString(R.string.erro_carregar_imagem), Toast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    private void perguntaExclusaoConta(){
        Context c = this;
        final EditText senhaExclusao = new EditText(this);
        AlertDialog.Builder b = new AlertDialog.Builder(c);
        b.setTitle("Tem certeza que deseja apagar a conta?");
        b.setMessage("Essa ação é irreversível! " + sharp.getString("email", "") + " deixará de existir em nosso servidor.");
        b.setCancelable(true);
        b.setIcon(R.drawable.ic_apagar);
        senhaExclusao.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        senhaExclusao.setHint("Digite a senha desta conta");
        senhaExclusao.setBackgroundResource(R.drawable.campos_texto_arredondados);
        senhaExclusao.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chave, 0, 0, 0);
        b.setView(senhaExclusao);
        b.setNeutralButton(
                "Não, mudei de ideia",
                new DialogInterface.OnClickListener(){
                    public void onClick(final DialogInterface d, int id){
                        return;
                    }
                });
        b.setNegativeButton(
                "Sim, prossiga!",
                new DialogInterface.OnClickListener(){
                    public void onClick(final DialogInterface d, int id){
                        encerraConta(new EncerramentoConta(senhaExclusao.getText().toString()), new CallsAPI());
                    }
                });
        AlertDialog alerta = b.create();
        alerta.show();
    }

    private void encerraConta(final EncerramentoConta encerramentoConta, CallsAPI callsAPI){
        callsAPI.retrofitBuilder().encerraConta(encerramentoConta, "Bearer " + sharp.getString("token", "")).enqueue(new Callback<EncerramentoConta>(){
            @Override
            public void onResponse(Call<EncerramentoConta> call, Response<EncerramentoConta> resposta){
                if(resposta.isSuccessful()){
                    sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edtr = sharp.edit();
                    edtr.clear();
                    edtr.apply();
                    ClasseApplication app = (ClasseApplication) getApplication();
                    socket.disconnect();
                    socket = app.anulaSocket();
                    finishAffinity();
                    startActivity(new Intent(ActivityEditarPerfil.this, ActivityLogin.class));
                    TastyToast.makeText(ActivityEditarPerfil.this, "Conta encerrada com sucesso!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                    finish();
                }else{
                    try{
                        TastyToast.makeText(ActivityEditarPerfil.this, new JSONObject(resposta.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
                    }catch(Exception e){
                        Log.d("ENCERRACONTA EXCEPTION1", e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<EncerramentoConta> call, Throwable t){
                Log.d("ENCERRACONTA EXCEPTION2", t.getMessage());
            }
        });
    }

    private void alteraConta(final AlteracaoConta alteracaoConta, CallsAPI callsAPI){
        callsAPI.retrofitBuilder().alteraConta(alteracaoConta, "Bearer " + sharp.getString("token", "")).enqueue(new Callback<AlteracaoConta>(){
            @Override
            public void onResponse(Call<AlteracaoConta> call, Response<AlteracaoConta> resposta){
                if(resposta.isSuccessful()){
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                    TastyToast.makeText(ActivityEditarPerfil.this, "Dados atualizados com sucesso!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                }else{
                    try{
                        TastyToast.makeText(ActivityEditarPerfil.this, new JSONObject(resposta.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
                    }catch(Exception e){
                        Log.d("EDITACONTA EXCEPTION", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AlteracaoConta> call, Throwable t){
                Log.d("EDITACONTA EXCEPTION2", t.getMessage());
            }
        });
    }

    private void enviaArquivo(String caminho, String token, CallsAPI callsAPI) {
        File arquivoImagem = new File(caminho.substring(caminho.lastIndexOf(":")+1));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), arquivoImagem);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", arquivoImagem.getName(), requestFile);
        callsAPI.retrofitBuilder().uploadFoto("Bearer " + token, body).enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                try{
                    if(response.isSuccessful() && response.body() != null){
                        TastyToast.makeText(ActivityEditarPerfil.this, "Imagem atualizada!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                    }else{
                        TastyToast.makeText(ActivityEditarPerfil.this, new JSONObject(response.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
                    }
                }catch(Exception e){
                    Log.d("Upload", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t){
                Log.d("Upload", t.getMessage());
            }
        });
    }
}
