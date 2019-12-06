package lucassbeiler.aplicativo.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.tastytoast.TastyToast;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import lucassbeiler.aplicativo.BuildConfig;
import lucassbeiler.aplicativo.models.Distancia;
import lucassbeiler.aplicativo.models.Perfis;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import lucassbeiler.aplicativo.utilitarias.ClasseApplication;
import lucassbeiler.aplicativo.utilitarias.Localizador;
import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.fragments.MatchFragment;
import lucassbeiler.aplicativo.models.OutroUsuario;
import lucassbeiler.aplicativo.models.Reacao;
import lucassbeiler.aplicativo.models.Spot;
import lucassbeiler.aplicativo.models.Usuarios;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCentral extends AppCompatActivity implements CardStackListener{
    private DrawerLayout drawerLayout;
    private CardStackLayoutManager manager;
    private TextView acabouAviso, acabouDesc;
    private CardStackAdapter adapter;
    private CardStackView csvw;
    private SharedPreferences sharp;
    private Localizador localiza = new Localizador();
    private Socket socket;
    private FloatingActionButton botaoChats, botaoMapa, botaoFeed, botaoRecarregar, botaoLike, botaoDislike;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
        permissao();
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String token = sharp.getString("token", "");
        ClasseApplication app = (ClasseApplication) getApplication();
        socket = app.getSocket();
        if(socket != null){
            socket.connect();
        }
        localiza.atualizaLocalizacao(this, token);
        botaoChats = findViewById(R.id.botao_chats);
        botaoMapa = findViewById(R.id.botao_mapa);
        botaoFeed = findViewById(R.id.botao_feed);
        acabouAviso = findViewById(R.id.acabouAviso);
        acabouDesc = findViewById(R.id.acabouDesc);
        botaoRecarregar = findViewById(R.id.botao_recarregar);
        botaoLike = findViewById(R.id.like_button);
        botaoDislike = findViewById(R.id.skip_button);

        botaoMapa.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                TastyToast.makeText(getApplicationContext(), "Mapa indisponível nessa versão (" + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE + "-" + BuildConfig.VERSION_CODE + ")", Toast.LENGTH_SHORT, TastyToast.DEFAULT);
            }
        });

        botaoChats.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                TastyToast.makeText(getApplicationContext(), "Ainda estamos trabalhando no sistema de matches/chats...", Toast.LENGTH_SHORT, TastyToast.DEFAULT);
            }
        });

        botaoRecarregar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                inicializa();
            }
        });

        botaoFeed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                TastyToast.makeText(getApplicationContext(), "Ainda estamos trabalhando no sistema de feeds/timelines...", Toast.LENGTH_SHORT, TastyToast.DEFAULT);
            }
        });
        setupCardStackView();
        setupButton();
        //verificaUsuarios();
    }

    @Override
    public void onCardDragging(Direction direction, float ratio){
        Log.d("CardStackView", "Arrastando: d = " + direction.name() + ", r = " + ratio);
    }

    @Override
    public void onCardSwiped(Direction direcao){
        Log.d("CardStackView", "Passou: p = " + (manager.getTopPosition() - 1) + ", d = " + direcao);
        List<Spot> spots = adapter.getSpots();
        try{
            Integer id = spots.get(manager.getTopPosition() - 1).usuarioID; // causa IndexOutOfBoundsException
            String nome = spots.get(manager.getTopPosition() - 1).nome;
            verificaUsuarios();
            Log.d("ID_NOME", id + " " + nome);
            if(direcao.toString().equals("Left")){
                enviaDislike(id, new CallsAPI());
            }else if(direcao.toString().equals("Right")){
                enviaLike(id, new CallsAPI());
            }
        }catch(Exception e){
            Log.d("CRASH MATCH", e.toString());
        }
    }

    @Override
    public void onCardRewound(){
        Log.d("CardStackView", "Voltou: " + manager.getTopPosition());
    }

    @Override
    public void onCardCanceled(){
        Log.d("CardStackView", "Cancelou:" + manager.getTopPosition());
    }

    @Override
    public void onCardAppeared(View view, int position){
        TextView textView = view.findViewById(R.id.item_name);
        Log.d("CardStackView", "Spawn: (" + position + ") " + textView.getText());
    }

    @Override
    public void onCardDisappeared(View view, int position){
        TextView textView = view.findViewById(R.id.item_name);
        Log.d("CardStackView", "Unspawn: (" + position + ") " + textView.getText());
    }

    private void setupCardStackView(){
        inicializa();
    }

    private void alimentaCards(CallsAPI callsAPI){
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        callsAPI.retrofitBuilder().getUsuarios("Bearer " + sharp.getString("token", "")).enqueue(new Callback<Usuarios>(){
            @Override
            public void onResponse(Call<Usuarios> call, final Response<Usuarios> response){
                if(response.isSuccessful()){
                    List<Spot> cards = new ArrayList<>();
                    cards.clear();
                    for(int i = 0; i < response.body().getUserFilter().size(); i++){
                        OutroUsuario usuario = response.body().getUserFilter().get(i);
                        Perfis perfil = usuario.getProfiles();
                        Distancia distancia = usuario.getLocations();
                        cards.add(new Spot(perfil.getName() + ", " + perfil.getAge(), "a " + distancia.getDistance() + "km daqui", CallsAPI.uploadsDir + perfil.getFilename(), usuario.getId()));
                    }if(cards.size() > 0){
                        adapter = new CardStackAdapter(ActivityCentral.this, cards);
                        csvw.setAdapter(adapter);
                        acabouAviso.setVisibility(View.GONE);
                        acabouDesc.setVisibility(View.GONE);
                        botaoRecarregar.setVisibility(View.GONE);

                        botaoLike.setVisibility(View.VISIBLE);
                        botaoDislike.setVisibility(View.VISIBLE);
                    }else{
                        acabouAviso.setVisibility(View.VISIBLE);
                        acabouDesc.setVisibility(View.VISIBLE);
                        botaoRecarregar.setVisibility(View.VISIBLE);

                        botaoLike.setVisibility(View.GONE);
                        botaoDislike.setVisibility(View.GONE);

                    }
                }else{
                    try{
                        Log.d("USUARIOS", "erro" + response.errorBody().string());
                        inicializa();
                    }catch(Exception e){
                        //
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuarios> call, Throwable t){
                Log.d("USUARIOS", "erro" + t.getMessage());
            }
        });
    }

    private void setupButton(){
        View skip = findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                csvw.swipe();
            }
        });

        View rewind = findViewById(R.id.rewind_button);
        rewind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(ActivityCentral.this, ActivityEditarPerfil.class));
            }
        });

        View like = findViewById(R.id.like_button);
        like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                csvw.swipe();
            }
        });
    }

    private void inicializa(){
        manager = new CardStackLayoutManager(this, this);
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        csvw = findViewById(R.id.card_stack_view);
        csvw.setLayoutManager(manager);
        if(socket != null){
            escutaMatch();
        }
        alimentaCards(new CallsAPI());
    }


    private void verificaLogin(){
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        if(sharp.getString("token", "").isEmpty()){
            startActivity(new Intent(ActivityCentral.this, ActivityLogin.class));
            finish();
        }
    }

    private void enviaLike(Integer id, CallsAPI callsAPI){
        Reacao reacao = new Reacao(id);
        callsAPI.retrofitBuilder().like(reacao, "Bearer " + sharp.getString("token", "")).enqueue(new Callback<Reacao>(){
            @Override
            public void onResponse(Call<Reacao> call, final Response<Reacao> response){
                if(!response.isSuccessful()){
                    TastyToast.makeText(getApplicationContext(), "Erro ao enviar like", Toast.LENGTH_SHORT, TastyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<Reacao> call, Throwable t){
                TastyToast.makeText(getApplicationContext(), "Erro ao enviar like", Toast.LENGTH_SHORT, TastyToast.ERROR);
            }
        });
    }

    private void enviaDislike(Integer id, CallsAPI callsAPI){
        Reacao reacao = new Reacao(id);
        callsAPI.retrofitBuilder().dislike(reacao, "Bearer " + sharp.getString("token", "")).enqueue(new Callback<Reacao>(){
            @Override
            public void onResponse(Call<Reacao> call, final Response<Reacao> response){
                if(!response.isSuccessful()){
                    TastyToast.makeText(getApplicationContext(), "Erro ao enviar dislike", Toast.LENGTH_SHORT, TastyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<Reacao> call, Throwable t){
                TastyToast.makeText(getApplicationContext(), "Erro ao enviar dislike", Toast.LENGTH_SHORT, TastyToast.ERROR);
            }
        });
    }

    private void escutaMatch(){ // protected
        socket.on("match", new Emitter.Listener(){
            @Override
            public void call(Object... args){
                try{
                    final JSONObject jSONO = new JSONObject(args[0].toString());
                    final String nomeMatch = jSONO.getString("name");
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            try{
                                carregaTelaMatch(jSONO.getString("filename"), jSONO.getString("id"));
                            }catch(JSONException jex){
                                //
                            }
                        }
                    });
                    Log.d("SOCKET MATCH", jSONO.toString());
                }catch(Exception e){
                    Log.d("SOCKET EXCEPTION", e.getMessage());
                }
            }
        });
    }

    private void carregaTelaMatch(String filenameImagemMatch, String idMatch){
        try{
            MatchFragment matchFragment = new MatchFragment();
            Bundle bundle = new Bundle();
            sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
            bundle.putString("filenameImagemMatch", CallsAPI.uploadsDir + filenameImagemMatch);
            bundle.putString("usuarioImagemURL", sharp.getString("imagemURL", ""));
            bundle.putString("idMatch", idMatch);
            matchFragment.setArguments(bundle);
            matchFragment.show(getSupportFragmentManager(), matchFragment.getClass().getSimpleName()); // possível bug
        }catch(Exception e){
            Log.d("CRASH MATCH", e.getMessage());
        }
    }

    private void verificaUsuarios(){
        List<Spot> spots = adapter.getSpots();
        Log.d("USUARIOS", "TAMANHO SPOTS " + spots.size());
        if(spots.size() - manager.getTopPosition() < 1){
            spots.clear();
            acabouAviso.setVisibility(View.VISIBLE);
            acabouDesc.setVisibility(View.VISIBLE);
            botaoRecarregar.setVisibility(View.VISIBLE);

            botaoLike.setVisibility(View.GONE);
            botaoDislike.setVisibility(View.GONE);

        }else{
            acabouAviso.setVisibility(View.GONE);
            acabouDesc.setVisibility(View.GONE);
            botaoRecarregar.setVisibility(View.GONE);

            botaoLike.setVisibility(View.VISIBLE);
            botaoDislike.setVisibility(View.VISIBLE);
        }
    }

    private void permissao(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                verificaLogin();
            }else{
                verificaLogin();
                finishAffinity();
                System.exit(0);
                finish();
            }
        }
    }
}