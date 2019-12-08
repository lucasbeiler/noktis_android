package lucassbeiler.aplicativo.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jem.rubberpicker.RubberRangePicker;
import com.sdsmdg.tastytoast.TastyToast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.UI.ActivityCentral;
import lucassbeiler.aplicativo.UI.ActivityConversa;
import lucassbeiler.aplicativo.UI.ActivityEditarPerfil;
import lucassbeiler.aplicativo.UI.ActivityMatches;
import lucassbeiler.aplicativo.models.AlteracaoConta;
import lucassbeiler.aplicativo.utilitarias.CallsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentRangeIdade extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_range_idade, container, false);

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

        final RubberRangePicker rangeIdadeView = view.findViewById(R.id.range_idade);
        botaoSalvar = view.findViewById(R.id.botao_salvar_bio);
        final TextView rangeIdadeAtualTextView = view.findViewById(R.id.range_atual_idade);
        rangeIdadeAtualTextView.setText("Entre " + rangeIdadeView.getCurrentStartValue() + " e " + rangeIdadeView.getCurrentEndValue());

        rangeIdadeView.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(@NotNull RubberRangePicker rubberRangePicker, int i, int i1, boolean b) {
                rangeIdadeAtualTextView.setText("Entre " + rangeIdadeView.getCurrentStartValue() + " e " + rangeIdadeView.getCurrentEndValue());
            }

            @Override
            public void onStartTrackingTouch(@NotNull RubberRangePicker rubberRangePicker, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull RubberRangePicker rubberRangePicker, boolean b) {

            }
        });

        botaoSalvar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences sharp = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                CallsAPI callsAPI = new CallsAPI();
                AlteracaoConta alteracaoConta = new AlteracaoConta("", "", "", "", "", new Integer[]{rangeIdadeView.getCurrentStartValue(), rangeIdadeView.getCurrentEndValue()}, null);
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