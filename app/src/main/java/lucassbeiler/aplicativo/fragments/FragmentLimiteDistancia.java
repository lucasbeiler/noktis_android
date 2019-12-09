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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jem.rubberpicker.RubberSeekBar;
import com.sdsmdg.tastytoast.TastyToast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.UI.ActivityCentral;
import lucassbeiler.aplicativo.models.AlteracaoConta;
import lucassbeiler.aplicativo.utilitarias.AtualizaUsuario;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentLimiteDistancia extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_limite_distancia, container, false);
        AtualizaUsuario atualizaUsuario = new AtualizaUsuario();
        atualizaUsuario.atualizaDadosUsuario(new CallsAPI(), getContext());
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
        SharedPreferences sharp = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        final RubberSeekBar limiteDistanciaView = view.findViewById(R.id.limite_distancia);
        botaoSalvar = view.findViewById(R.id.botao_salvar);
        limiteDistanciaView.setCurrentValue(sharp.getInt("distanciaMax", 50));
        final TextView limiteDistanciaAtualTextView = view.findViewById(R.id.limite_atual_distancia);
        limiteDistanciaAtualTextView.setText("Limite atual: " + limiteDistanciaView.getCurrentValue() + "km");

        limiteDistanciaView.setOnRubberSeekBarChangeListener(new RubberSeekBar.OnRubberSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@NotNull RubberSeekBar rubberSeekBar, int i, boolean b) {
                limiteDistanciaAtualTextView.setText("Limite atual: " + limiteDistanciaView.getCurrentValue() + "km");
            }

            @Override
            public void onStartTrackingTouch(@NotNull RubberSeekBar rubberSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull RubberSeekBar rubberSeekBar) {

            }
        });

        botaoSalvar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences sharp = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                CallsAPI callsAPI = new CallsAPI();
                AlteracaoConta alteracaoConta = new AlteracaoConta("", "", "", "", "", null, limiteDistanciaView.getCurrentValue());
                callsAPI.retrofitBuilder().alteraConta(alteracaoConta, "Bearer " + sharp.getString("token", "")).enqueue(new Callback<AlteracaoConta>(){
                    @Override
                    public void onResponse(Call<AlteracaoConta> call, Response<AlteracaoConta> resposta){
                        if(resposta.isSuccessful()){
                            TastyToast.makeText(getActivity(), "Alteração concluída com sucesso!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
                            dismiss();
                            getActivity().finishAffinity();
                            startActivity(new Intent(getActivity(), ActivityCentral.class));
                            getActivity().finish();
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