package lucassbeiler.aplicativo.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.adapter.AdapterMatches;
import lucassbeiler.aplicativo.models.Distancia;
import lucassbeiler.aplicativo.models.OutroUsuario;
import lucassbeiler.aplicativo.models.Perfis;
import lucassbeiler.aplicativo.models.Spot;
import lucassbeiler.aplicativo.models.Usuarios;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMatches extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        baixaMatches(new CallsAPI());
    }


    private void baixaMatches(CallsAPI callsAPI){
        SharedPreferences sharp;
        sharp = getSharedPreferences("login", Context.MODE_PRIVATE);
        callsAPI.retrofitBuilder().getMatches("Bearer " + sharp.getString("token", "")).enqueue(new Callback<Usuarios>(){
            @Override
            public void onResponse(Call<Usuarios> call, final Response<Usuarios> response){
                if(response.isSuccessful()){
                    RecyclerView recyclerView = findViewById(R.id.recycler_view);
                    AdapterMatches adapter = new AdapterMatches(response.body(), getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }else{
                    try{
                        Log.d("ERRO GETMATCHES", response.errorBody().string());
                    }catch(Exception e){
                        Log.d("EXCEPTION GETMATCHES", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuarios> call, Throwable t){
                Log.d("EXCEPTION GETMATCHES", t.getMessage());
            }

        });
    }

    private void chamaRecyclerView(){
        RecyclerView rv = findViewById(R.id.recycler_view);
    }
}
