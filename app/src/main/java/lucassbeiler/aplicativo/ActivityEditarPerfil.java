package lucassbeiler.aplicativo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.ybq.android.spinkit.SpinKitView;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import lucassbeiler.aplicativo.requisicoes.FuncoesDeleteJSON;
import lucassbeiler.aplicativo.requisicoes.FuncoesPOSTImagem;
import lucassbeiler.aplicativo.requisicoes.FuncoesPUTJSON;

public class ActivityEditarPerfil extends AppCompatActivity {
    private CircleImageView imagemPersonalizar;
    private SpinKitView emblemaCarregamento;
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
    private FuncoesJSON fJSON = new FuncoesJSON();
    private static final int ESCOLHER_IMAGEM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String token = sharp.getString("token", "");
        if (token.isEmpty()) {
            finishAffinity();
            startActivity(new Intent(ActivityEditarPerfil.this, ActivityLogin.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            return;
        }
        setContentView(R.layout.activity_editar_perfil);

        String imagemURL = sharp.getString("imagemURL", "");

        carregandoTela = new ProgressDialog(this);
        imagemPersonalizar = findViewById(R.id.imagemConfig);
        emblemaCarregamento = findViewById(R.id.emblemaCarregamento);
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

        desconectarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor edtr = sharp.edit();
                edtr.clear();
                edtr.apply();
                finishAffinity();
                startActivity(new Intent(ActivityEditarPerfil.this, ActivityLogin.class));
                finish();
                TastyToast.makeText(ActivityEditarPerfil.this, "Logout executado com sucesso!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        });

        apagarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perguntaExclusaoConta();
            }
        });

        imagemPersonalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emblemaCarregamento.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        comecarEdicao();
    }

    private void comecarEdicao() {
        try {
            textoEmail.setText("E-mail: " + sharp.getString("email", ""));
            textoSenha.setText("Senha: " + "*********");
            textoNome.setText("Bio: " + sharp.getString("bio", ""));

            textoEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textoEmail.setVisibility(View.GONE);
                    editarEmail.setHint(sharp.getString("email", ""));
                    editarEmail.setVisibility(View.VISIBLE);

                }
            });
            textoSenha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
            textoNome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textoNome.setVisibility(View.GONE);
                    definirNome.setHint(sharp.getString("bio", ""));
                    definirNome.setVisibility(View.VISIBLE);

                }
            });

            botaoFinalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String[] chavesJSONDadosEditar = {"email", "bio", "oldPassword", "password", "confirmPassword"};
                        String[] valoresJSONDadosEditar = {editarEmail.getText().toString(), definirNome.getText().toString(), editarSenhaAntiga.getText().toString(),
                                editarSenha.getText().toString(), editarSenha2.getText().toString()};
                        Log.d("JSONEditar", fJSON.geraJSON(chavesJSONDadosEditar, valoresJSONDadosEditar).toString());
                        String respostaAPIEditar = new FuncoesPUTJSON().execute("http://34.95.164.190:3333/user/updates", fJSON.geraJSON(chavesJSONDadosEditar, valoresJSONDadosEditar).toString(), sharp.getString("token", "")).get();
                        JSONObject jSONO = new JSONObject(respostaAPIEditar);
                        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edtr = sharp.edit();
                        String alterou = "Alterações concluídas: ";
                        if (jSONO.getString("email").equals(editarEmail.getText().toString()) && !jSONO.has("error")) {
                            alterou += "E-mail\n";
                            edtr.putString("email", jSONO.getString("email"));
                            edtr.apply();
                        }
                        if (jSONO.getString("bio").equals(definirNome.getText().toString()) && !jSONO.has("error")) {
                            alterou += "Bio";
                            edtr.putString("bio", jSONO.getString("bio"));
                            edtr.apply();
                        }
                        TastyToast.makeText(getApplicationContext(), alterou, Toast.LENGTH_SHORT, TastyToast.SUCCESS);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    } catch (Exception e) {
                        //
                    }
                }
            });
        } catch (NullPointerException e) {
            //
        }
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent data) {
        try {
            super.onActivityResult(codigoRequisicao, codigoResultado, data);
            Uri UriImagem = data.getData();
            if (codigoRequisicao == ESCOLHER_IMAGEM && codigoResultado == RESULT_OK && UriImagem != null) {
                CropImage.activity(UriImagem)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if (codigoRequisicao == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (codigoResultado == RESULT_OK) {
                    imagemUrl = result.getUri();
                    imagemPersonalizar.setImageURI(imagemUrl);
                    try {
                        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                        String token = sharp.getString("token", "");
                        String respostaAPI = new FuncoesPOSTImagem().execute("http://34.95.164.190:3333/upload/file", imagemUrl.toString(), token).get();
                        JSONObject jSONO = new JSONObject(respostaAPI);
                        Log.d("RESPOSTA", respostaAPI);
                        if (jSONO.has("filename")) {
                            emblemaCarregamento.setVisibility(View.GONE);
                            TastyToast.makeText(getApplicationContext(), "IMAGEM ATUALIZADA COM SUCESSO!", Toast.LENGTH_SHORT, TastyToast.SUCCESS);
                            sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edtr = sharp.edit();
                            edtr.putString("imagemURL", "http://34.95.164.190:3333/uploads/" + jSONO.getString("filename"));
                            edtr.apply();
                        } else {
                            TastyToast.makeText(getApplicationContext(), "ERRO AO ENVIAR IMAGEM!", Toast.LENGTH_SHORT, TastyToast.ERROR);
                        }
                    } catch (Exception e) {
                        Log.d("ERRO EditarPerfil.java", e.toString());
                    }
                } else if (codigoResultado == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception erro = result.getError();
                }
            }
        } catch (NullPointerException npex) {
            //
        }
    }

    private void carregaImagem(final String imagemURL) {
        try {
            emblemaCarregamento.setVisibility(View.VISIBLE);
            Picasso.get().load(imagemURL).into(imagemPersonalizar, new com.squareup.picasso.Callback() {
                public void onSuccess() {
                    emblemaCarregamento.setVisibility(View.GONE);
                }

                public void onError(Exception ex) {
                    TastyToast.makeText(ActivityEditarPerfil.this, getString(R.string.erro_carregar_imagem) + imagemURL, Toast.LENGTH_LONG, TastyToast.ERROR);
                    emblemaCarregamento.setVisibility(View.GONE);
                }
            });
        } catch (IllegalArgumentException iae) {
            TastyToast.makeText(ActivityEditarPerfil.this, getString(R.string.erro_carregar_imagem), Toast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    private void perguntaExclusaoConta() {
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
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface d, int id) {
                        return;
                    }
                });
        b.setNegativeButton(
                "Sim, prossiga!",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface d, int id) {
                        apagaConta(senhaExclusao.getText().toString());
                    }
                });
        AlertDialog alerta = b.create();
        alerta.show();
    }

    private void apagaConta(String senhaExclusao){
        try {
            String[] chavesJSONApagar = {"email", "password"};
            String[] valoresJSONApagar = {sharp.getString("email", ""), senhaExclusao};
            String respostaAPIApagar = new FuncoesDeleteJSON().execute("http://34.95.164.190:3333/delete/account", fJSON.geraJSON(chavesJSONApagar, valoresJSONApagar).toString(), sharp.getString("token", "")).get();
            JSONObject jSONO = new JSONObject(respostaAPIApagar);
            if(jSONO.has("ok")) {
                SharedPreferences.Editor edtr = sharp.edit();
                edtr.clear();
                edtr.apply();
                finishAffinity();
                startActivity(new Intent(ActivityEditarPerfil.this, ActivityLogin.class));
                TastyToast.makeText(ActivityEditarPerfil.this, "Conta encerrada com sucesso!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                finish();
            }else{
                TastyToast.makeText(ActivityEditarPerfil.this, jSONO.getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
                return;
            }
        }catch (Exception e){
            Log.d("Exception", e.getMessage());
        }
    }
}
