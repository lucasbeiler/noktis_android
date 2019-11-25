package lucassbeiler.aplicativo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdsmdg.tastytoast.TastyToast;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import lucassbeiler.aplicativo.fragments.MatchFragment;
import lucassbeiler.aplicativo.requisicoes.FuncoesGET;
import lucassbeiler.aplicativo.requisicoes.FuncoesPOSTJSON;

public class ActivityCentral extends AppCompatActivity implements CardStackListener {

    private DrawerLayout drawerLayout;
    private CardStackLayoutManager manager;
    private TextView acabouAviso, acabouDesc;
    private CardStackAdapter adapter;
    private CardStackView csvw;
    private SharedPreferences sharp;
    private FuncoesJSON fJSON = new FuncoesJSON();
    private Localizador localiza = new Localizador();
    private Socket socket;
    private FloatingActionButton botaoChats, botaoMapa, botaoFeed, botaoRecarregar, botaoLike, botaoDislike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localiza.atualizaLocalizacao(ActivityCentral.this, this);
        setContentView(R.layout.activity_central);
        //startActivity(new Intent(this, Intro.class));
        verificaLogin();
        atualizaLocalizacaoRemota();
        enviaSocket();
        botaoChats = findViewById(R.id.botao_chats);
        botaoMapa = findViewById(R.id.botao_mapa);
        botaoFeed = findViewById(R.id.botao_feed);
        acabouAviso = findViewById(R.id.acabouAviso);
        acabouDesc = findViewById(R.id.acabouDesc);
        botaoRecarregar = findViewById(R.id.botao_recarregar);
        botaoLike = findViewById(R.id.like_button);
        botaoDislike = findViewById(R.id.skip_button);

        botaoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TastyToast.makeText(getApplicationContext(), "Mapa indisponível nessa versão (" + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE + "-" + BuildConfig.VERSION_CODE + ")", Toast.LENGTH_SHORT, TastyToast.DEFAULT);
            }
        });

        botaoChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TastyToast.makeText(getApplicationContext(), "Ainda estamos trabalhando no sistema de matches/chats...", Toast.LENGTH_SHORT, TastyToast.DEFAULT);
            }
        });

        botaoRecarregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        botaoFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TastyToast.makeText(getApplicationContext(), "Ainda estamos trabalhando no sistema de feeds/timelines...", Toast.LENGTH_SHORT, TastyToast.DEFAULT);
            }
        });
        setupCardStackView();
        setupButton();
        verificaUsuarios();
    }
    
    @Override
    public void onCardDragging(Direction direction, float ratio) {
        Log.d("CardStackView", "Arrastando: d = " + direction.name() + ", r = " + ratio);
    }

    @Override
    public void onCardSwiped(Direction direcao) {
        Log.d("CardStackView", "Passou: p = " + (manager.getTopPosition()-1) + ", d = " + direcao);
        List<Spot> spots = adapter.getSpots();
        try {
            String id = spots.get(manager.getTopPosition()-1).usuarioID; // causa IndexOutOfBoundsException
            String nome = spots.get(manager.getTopPosition()-1).nome;
            verificaUsuarios();
            Log.d("ID_NOME", id + " " + nome);
            if (direcao.toString().equals("Left")) {
               enviaDislike(id);
            } else if (direcao.toString().equals("Right")) {
               enviaLike(id);
            }
        }catch(Exception e){
            Log.d("ERRO LIKE", e.toString());
        }
    }

    @Override
    public void onCardRewound() {
        Log.d("CardStackView", "Voltou: " + manager.getTopPosition());
    }

    @Override
    public void onCardCanceled() {
        Log.d("CardStackView", "Cancelou:" + manager.getTopPosition());
    }

    @Override
    public void onCardAppeared(View view, int position) {
        TextView textView = view.findViewById(R.id.item_name);
        Log.d("CardStackView", "Spawn: (" + position + ") " + textView.getText());
    }

    @Override
    public void onCardDisappeared(View view, int position) {
        TextView textView = view.findViewById(R.id.item_name);
        Log.d("CardStackView", "Unspawn: (" + position + ") " + textView.getText());
    }

    private void setupCardStackView() {
        inicializa();
    }

    private JSONArray baixaUsuarios(){
        JSONArray jArra = null;
        try {
            sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
            String token = sharp.getString("token", "");
            String usuarios = new FuncoesGET().execute("http://34.95.164.190:3333/users/online", token).get();
            JSONObject jSONO = new JSONObject(usuarios);
            jArra = jSONO.getJSONArray("usersValues");
            Log.d("TAMANHO REAL", String.valueOf(jArra.length()));
        }catch(Exception e){
            Log.d("USUÁRIOS EXC", e.toString());
        }
        return jArra;
    }

    private void setupButton() {
        View skip = findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCentral.this, ActivityEditarPerfil.class));
            }
        });

        View like = findViewById(R.id.like_button);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void inicializa() {
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
        adapter = new CardStackAdapter(this, alimentaCards());
        csvw = findViewById(R.id.card_stack_view);
        csvw.setLayoutManager(manager);
        csvw.setAdapter(adapter);
    }

    private List<Spot> alimentaCards() {
        List<Spot> cards = new ArrayList<>();
        try {
            JSONArray jArra = baixaUsuarios();
            Log.d("TAMANHO", String.valueOf(jArra.length()));
            for (int i = 0; i < jArra.length(); i++) {
                JSONObject jSONO = jArra.getJSONObject(i);
                String[] chavesJSONDistancia = {"id"};
                String[] valoresJSONDistancia = {jSONO.getString("id")};
                sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
                String token = sharp.getString("token", "");
                String respostaAPIDistancia = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/users/distancia", fJSON.geraJSON(chavesJSONDistancia, valoresJSONDistancia).toString(), token).get();
                JSONObject jSONODistancia = new JSONObject(respostaAPIDistancia);
                Log.d("JSON IDADE", jSONO.toString());
                String idade = String.valueOf((System.currentTimeMillis() - jSONO.getLong("birth_timestamp"))/31556952000L);
                //String idade = String.valueOf((System.currentTimeMillis() - 1006473600000L)/31556952000L);
                cards.add(new Spot(jSONO.getString("name") + ", " + idade, "a " + jSONODistancia.getString("distancia") + "km daqui", "http://34.95.164.190:3333/uploads/" + jSONO.getString("filename"), jSONO.getString("id")));
            }
        }catch (Exception jex){
            //valorJSON
        }
        return cards;
    }
    
    private void verificaLogin(){
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        if(sharp.getString("token", "").isEmpty()){
            startActivity(new Intent(ActivityCentral.this, ActivityLogin.class));
            finish();
        }
    }

    private void enviaLike(String id){
        String[] chavesJSONLike = {"id"};
        String[] valoresJSONLike = {id};
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String token = sharp.getString("token", "");
        try {
            String respostaAPILike = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/users/likes", fJSON.geraJSON(chavesJSONLike, valoresJSONLike).toString(), token).get();
            Log.d("RESPOSTA LIKE", respostaAPILike);
        }catch(Exception e){
            Log.d("ERRO", e.toString());
        }
    }
    private void enviaDislike(String id){
        String[] chavesJSONDislike = {"id"};
        String[] valoresJSONDislike = {id};
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String token = sharp.getString("token", "");
        try {
            String respostaAPIDislike = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/users/dislikes", fJSON.geraJSON(chavesJSONDislike, valoresJSONDislike).toString(), token).get();
            Log.d("RESPOSTA DISLIKE", respostaAPIDislike);
        }catch(Exception e){
            Log.d("ERRO", e.toString());
        }
    }
    private void atualizaLocalizacaoRemota(){
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String token = sharp.getString("token", "");
        sharp = getSharedPreferences("local", Context.MODE_PRIVATE);
        String[] chavesJSONCoords = {"latitude", "longitude"};
        String[] valoresJSONCoords = {sharp.getString("latitude", "e"), sharp.getString("longitude", "e")};
        try {
            String respostaAPICoords = new FuncoesPOSTJSON().execute("http://34.95.164.190:3333/location/send", fJSON.geraJSON(chavesJSONCoords, valoresJSONCoords).toString(), token).get();
        }catch (Exception e){
            //
        }
        }

    private void enviaSocket() {
        try {
            sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            opts.query = "token=" + sharp.getString("token", "") + "&user=" + sharp.getString("uid", "");
            socket = IO.socket("http://34.95.164.190:3333/", opts);
        } catch (Exception e) {
            //
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("CONECTADO", "CONECTOU");
            }

        }).on("match", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    final JSONObject jSONO = new JSONObject(args[0].toString());
                    final String nomeMatch = jSONO.getString("name").toString();
                    // try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //TastyToast.makeText(getApplicationContext(), "Match com " + nomeMatch, Toast.LENGTH_SHORT, TastyToast.SUCCESS);
                            try {
                                carregaTelaMatch(jSONO.getString("filename"));
                            }catch(JSONException jex){
                                //
                            }
                        }
                    });
                    Log.d("SOCKET MATCH", jSONO.toString());
                    //}catch (Exception e){
                    //Log.d("MATCH EXCEPTION", e.getMessage());
                    // }
                } catch (Exception e) {
                    Log.d("SOCKET EXCEPTION", e.getMessage());
                }
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SOCKET DESCONECTADO", args[0].toString());
            }
        }).on("connection", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SOCKET CONECTOU", "aa");
            }
        });
        socket.connect();
    }

    private void carregaTelaMatch(String filenameImagemMatch){
        MatchFragment matchFragment = new MatchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("filenameImagem", filenameImagemMatch);
        matchFragment.setArguments(bundle);
        matchFragment.show(getSupportFragmentManager(), matchFragment.getClass().getSimpleName());
    }

    private void verificaUsuarios(){
        List<Spot> spots = adapter.getSpots();
        if(spots.size()-manager.getTopPosition() < 1){
            acabouAviso.setVisibility(View.VISIBLE);
            acabouDesc.setVisibility(View.VISIBLE);
            botaoRecarregar.setVisibility(View.VISIBLE);

            botaoLike.setVisibility(View.GONE);
            botaoDislike.setVisibility(View.GONE);

        }
    }
}