package lucassbeiler.aplicativo.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.tastytoast.TastyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.adapter.AdapterFeedUsuarioAtual;
import lucassbeiler.aplicativo.fragments.FragmentEditarPerfilMenu;
import lucassbeiler.aplicativo.models.Posts;
import lucassbeiler.aplicativo.utilitarias.AtualizaUsuario;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityTimelineUsuarioAtual extends AppCompatActivity{
    private Uri imagemUrl = null;
    private SharedPreferences sharp;
    private SimpleDraweeView fotoFeedUsuario, botaoSelecionarFotoSelecionado;
    private int codigoRequisicaoImutavel;
    private FloatingActionButton botaoEnviarPost, botaoSelecionarFoto;
    private EditText campoPost;
    private TextView nenhumPostView;
    private CardView feedTopo, enviarPostCardView;
    private AtualizaUsuario atualizaUsuario = new AtualizaUsuario();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_usuario_atual);
        final TextView nomeFeedUsuario, bioFeedUsuario, cidadeFeedUsuario, editarPerfil;

        Integer idUsuario = Integer.valueOf(getIntent().getStringExtra("idUsuario"));
        String nomeUsuario = getIntent().getStringExtra("nomeUsuario");
        String urlImagemUsuario = getIntent().getStringExtra("urlImagemUsuario");
        String token = getIntent().getStringExtra("token");
        String bio = getIntent().getStringExtra("bioUsuario");
        String cidade = getIntent().getStringExtra("cidadeUsuario");

        fotoFeedUsuario = findViewById(R.id.foto_feed_usuario);
        feedTopo = findViewById(R.id.card_view_feed_topo);
        bioFeedUsuario = findViewById(R.id.bio_detalhes);
        cidadeFeedUsuario = findViewById(R.id.cidade_detalhes);
        nenhumPostView = findViewById(R.id.nenhum_post_aviso);
        nomeFeedUsuario = findViewById(R.id.nome_usuario_tl);
        botaoEnviarPost = findViewById(R.id.botao_enviar_post);
        botaoSelecionarFoto = findViewById(R.id.botao_foto_enviar_feed);
        botaoSelecionarFotoSelecionado = findViewById(R.id.botao_foto_enviar_feed_selecionado);
        campoPost = findViewById(R.id.escrever_post);
        enviarPostCardView = findViewById(R.id.card_view_enviar_post);
        editarPerfil = findViewById(R.id.editar_perfil);

        Uri uriImagem = Uri.parse(urlImagemUsuario);
        nomeFeedUsuario.setText(nomeUsuario);
        fotoFeedUsuario.setImageURI(uriImagem);

        botaoEnviarPost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sharp = getSharedPreferences("login", MODE_PRIVATE);
                fazPostagem(sharp.getString("token", ""), new CallsAPI(), campoPost.getText().toString());
            }
        });

        if(token != null && !token.isEmpty()) {
            fotoFeedUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            });

            botaoSelecionarFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 24);
                }
            });

            botaoSelecionarFotoSelecionado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 24);
                }
            });

            feedTopo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        FragmentEditarPerfilMenu fragmentEditarBio = new FragmentEditarPerfilMenu();
                        fragmentEditarBio.show(getSupportFragmentManager(), fragmentEditarBio.getClass().getSimpleName());
                    } catch (Exception e) {
                        Log.d("CRASH MATCH", e.getMessage());
                    }
                }
            });
        }else{
            enviarPostCardView.setVisibility(View.GONE);
            editarPerfil.setVisibility(View.GONE);
        }
        bioFeedUsuario.setText(bio);
        if(cidade != null && !cidade.isEmpty()){
            cidadeFeedUsuario.setText(cidade);
        }else{
            cidadeFeedUsuario.setVisibility(View.GONE);
        }
        baixaPosts(new CallsAPI(), idUsuario);
    }

    @Override
    protected void onActivityResult(int codigoRequisicao, int codigoResultado, Intent data){
        try{
            super.onActivityResult(codigoRequisicao, codigoResultado, data);
            Uri UriImagem = data.getData();
            if((codigoRequisicao == 1 || codigoRequisicao == 24) && codigoResultado == RESULT_OK && UriImagem != null){
                codigoRequisicaoImutavel = codigoRequisicao;
                CropImage.activity(UriImagem)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
            if(codigoRequisicao == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d("REQUEST", String.valueOf(codigoRequisicaoImutavel));
                if(codigoResultado == RESULT_OK && codigoRequisicaoImutavel == 1){
                    imagemUrl = result.getUri();
                    fotoFeedUsuario.setImageURI(imagemUrl);
                    sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    String token = sharp.getString("token", "");
                    enviaArquivo(imagemUrl.toString(), token, new CallsAPI());
                }else if(codigoResultado == RESULT_OK && codigoRequisicaoImutavel == 24){
                    imagemUrl = result.getUri();
                    botaoSelecionarFotoSelecionado.setVisibility(View.VISIBLE);
                    botaoSelecionarFotoSelecionado.setImageURI(imagemUrl);
                    botaoSelecionarFoto.setVisibility(View.GONE);
                    sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    final String token = sharp.getString("token", "");
                    botaoEnviarPost.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            fazPostagemComImagem(imagemUrl.toString(), token, new CallsAPI(), campoPost.getText().toString());
                        }
                    });
                }else if(codigoResultado == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception erro = result.getError();
                }
            }
        }catch(NullPointerException npex){
            Log.d("ERRO EditarPerfil.java", npex.getMessage());
        }
    }

    private void enviaArquivo(final String caminho, String token, CallsAPI callsAPI) {
        final File arquivoImagem = new File(caminho.substring(caminho.lastIndexOf(":")+1));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), arquivoImagem);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", arquivoImagem.getName(), requestFile);
        callsAPI.retrofitBuilder().uploadFoto("Bearer " + token, body).enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                try{
                    if(response.isSuccessful() && response.body() != null){
                        TastyToast.makeText(ActivityTimelineUsuarioAtual.this, "Imagem atualizada!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                        atualizaUsuario.atualizaDadosUsuario(new CallsAPI(), getApplicationContext());
                    }else{
                        TastyToast.makeText(ActivityTimelineUsuarioAtual.this, new JSONObject(response.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
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

    private void fazPostagemComImagem(String caminho, String token, CallsAPI callsAPI, String descricao){
        MultipartBody.Part body = null;
        File arquivoImagem = new File(caminho.substring(caminho.lastIndexOf(":") + 1));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), arquivoImagem);
        body = MultipartBody.Part.createFormData("file", arquivoImagem.getName(), requestFile);

        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descricao);
        callsAPI.retrofitBuilder().enviaPostComImagem("Bearer " + token, body, description).enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                try{
                    if(response.isSuccessful() && response.body() != null){
                        botaoSelecionarFotoSelecionado.setVisibility(View.GONE);
                        botaoSelecionarFoto.setVisibility(View.GONE);
                        campoPost.setText("");
                        baixaPosts(new CallsAPI(), sharp.getInt("uid", 0));
                        TastyToast.makeText(ActivityTimelineUsuarioAtual.this, "Publicação enviada!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                    }else{
                        TastyToast.makeText(ActivityTimelineUsuarioAtual.this, new JSONObject(response.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
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

    private void fazPostagem(String token, CallsAPI callsAPI, String descricao){
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descricao);
        callsAPI.retrofitBuilder().enviaPost("Bearer " + token, description).enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                try{
                    if(response.isSuccessful() && response.body() != null){
                        campoPost.setText("");
                        baixaPosts(new CallsAPI(), sharp.getInt("uid", 0));
                        TastyToast.makeText(ActivityTimelineUsuarioAtual.this, "Publicação enviada!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                    }else{
                        TastyToast.makeText(ActivityTimelineUsuarioAtual.this, new JSONObject(response.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
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

    private void baixaPosts(CallsAPI callsAPI, Integer idUsuario){
        SharedPreferences sharp;
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        callsAPI.retrofitBuilder().baixaPosts(new Posts(idUsuario),"Bearer " + sharp.getString("token", "")).enqueue(new Callback<Posts>(){
            @Override
            public void onResponse(Call<Posts> call, final Response<Posts> response){
                if(response.isSuccessful()){
                    Log.d("PERFIL", response.body().getPerfis().getBio());
                    RecyclerView recyclerView = findViewById(R.id.recycler_view_posts_usuario);
                    AdapterFeedUsuarioAtual adapter = new AdapterFeedUsuarioAtual(response.body(), response.body().getPerfis(), getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    if(adapter.getItemCount() < 1){
                        nenhumPostView.setVisibility(View.VISIBLE);
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }else{
                    try{
                        Log.d("POSTS ERRO", "ERRO: " + response.errorBody().string());
                    }catch(Exception e){
                        Log.d("POSTS EXCEPTION", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Posts> call, Throwable t){
                Log.d("POSTS EXCEPTION2", t.getMessage());
            }

        });
    }
}
