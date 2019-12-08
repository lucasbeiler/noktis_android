package lucassbeiler.aplicativo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.adapter.AdapterFeedUsuarioAtual;
import lucassbeiler.aplicativo.adapter.AdapterSessoes;
import lucassbeiler.aplicativo.models.Posts;
import lucassbeiler.aplicativo.models.Sessoes;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSessoes extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        baixaSessoes(new CallsAPI());
        return inflater.inflate(R.layout.fragment_sessoes, container, false);
    }

    private void baixaSessoes(CallsAPI callsAPI){
        SharedPreferences sharp;
        sharp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        callsAPI.retrofitBuilder().baixaSessoes("Bearer " + sharp.getString("token", "")).enqueue(new Callback<Sessoes>(){
            @Override
            public void onResponse(Call<Sessoes> call, final Response<Sessoes> response){
                if(response.isSuccessful()){
                    Log.d("SESSAO", "SESSAO " + response.body().getSessions().get(0).getIp());
                    RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_sessoes);
                    AdapterSessoes adapter = new AdapterSessoes(response.body(), getActivity());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }else{
                    try{
                        Log.d("POSTS ERRO", "ERRO: " + response.errorBody().string());
                    }catch(Exception e){
                        Log.d("POSTS EXCEPTION", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Sessoes> call, Throwable t){
                Log.d("POSTS EXCEPTION2", t.getMessage());
            }

        });
    }
}
