package lucassbeiler.aplicativo.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import io.socket.client.Socket;
import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.UI.ActivityLogin;
import lucassbeiler.aplicativo.models.EncerramentoConta;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import lucassbeiler.aplicativo.utilitarias.ClasseApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentApagarConta extends AppCompatDialogFragment{
    private Socket socket;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apagar_conta, container, false);

        if(getDialog() != null){
            getDialog().setCancelable(true);
            getDialog().setCanceledOnTouchOutside(true);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Button botaoSalvar;

        final EditText etSenhaExclusao = view.findViewById(R.id.senha_exclusao);
        botaoSalvar = view.findViewById(R.id.botao_salvar_senha);

        botaoSalvar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences sharp = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                CallsAPI callsAPI = new CallsAPI();
                callsAPI.retrofitBuilder().encerraConta(new EncerramentoConta(etSenhaExclusao.getText().toString()), "Bearer " + sharp.getString("token", "")).enqueue(new Callback<EncerramentoConta>(){
                    @Override
                    public void onResponse(Call<EncerramentoConta> call, Response<EncerramentoConta> resposta){
                        if(resposta.isSuccessful()){
                            SharedPreferences sharp = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edtr = sharp.edit();
                            edtr.clear();
                            edtr.apply();
                            ClasseApplication app = (ClasseApplication) getActivity().getApplication();
                            socket = app.getSocket();
                            socket.disconnect();
                            socket = app.anulaSocket();
                            getActivity().finishAffinity();
                            startActivity(new Intent(getActivity(), ActivityLogin.class));
                            TastyToast.makeText(getActivity(), "Conta encerrada com sucesso!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                            getActivity().finish();
                        }else{
                            try{
                                TastyToast.makeText(getActivity(), new JSONObject(resposta.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
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
        });

    }


}