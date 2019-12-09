package lucassbeiler.aplicativo.fragments;

import android.content.Context;
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
import androidx.fragment.app.DialogFragment;

import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONObject;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.models.AlteracaoConta;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAlterarSenha extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alterar_senha, container, false);

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

        final EditText etSenhaAtual = view.findViewById(R.id.senha_atual);
        final EditText etSenhaNova = view.findViewById(R.id.senha_nova);
        final EditText etSenhaNova2 = view.findViewById(R.id.senha_nova_2);
        botaoSalvar = view.findViewById(R.id.botao_salvar_senha);

        botaoSalvar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences sharp = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                CallsAPI callsAPI = new CallsAPI();
                AlteracaoConta alteracaoConta = new AlteracaoConta("", etSenhaNova.getText().toString(), etSenhaNova2.getText().toString(), etSenhaAtual.getText().toString(), "", null, null);
                callsAPI.retrofitBuilder().alteraConta(alteracaoConta, "Bearer " + sharp.getString("token", "")).enqueue(new Callback<AlteracaoConta>(){
                    @Override
                    public void onResponse(Call<AlteracaoConta> call, Response<AlteracaoConta> resposta){
                        if(resposta.isSuccessful() && !etSenhaAtual.getText().toString().isEmpty() && !etSenhaNova.getText().toString().isEmpty() && !etSenhaNova2.getText().toString().isEmpty()){
                            TastyToast.makeText(getActivity(), "Senha atualizada com sucesso!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                            dismiss();
                        }else{
                            try{
                                TastyToast.makeText(getActivity(), new JSONObject(resposta.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
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
        });

    }


}