package lucassbeiler.aplicativo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import lucassbeiler.aplicativo.R;

public class FragmentConfiguracoes extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracoes, container, false);

        if(getDialog() != null){
            getDialog().setCancelable(true);
            getDialog().setCanceledOnTouchOutside(true);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        Button botaoAlterarEmail = view.findViewById(R.id.botao_alterar_email);
        Button botaoAlterarSenha = view.findViewById(R.id.botao_alterar_senha);

        Button botaoDefinirRangeIdade = view.findViewById(R.id.botao_definir_range_idade);
        Button botaoDefinirLimiteDistancia = view.findViewById(R.id.botao_definir_distancia_maxima);

        Button botaoSessoes = view.findViewById(R.id.botao_sessoes_ativas);

        botaoAlterarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAlterarEmail fragmentAlterarEmail = new FragmentAlterarEmail();
                fragmentAlterarEmail.show(getActivity().getSupportFragmentManager(), fragmentAlterarEmail.getClass().getSimpleName());
                dismiss();
            }
        });

        botaoAlterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAlterarSenha fragmentAlterarSenha = new FragmentAlterarSenha();
                fragmentAlterarSenha.show(getActivity().getSupportFragmentManager(), fragmentAlterarSenha.getClass().getSimpleName());
                dismiss();
            }
        });

        botaoDefinirRangeIdade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentRangeIdade fragmentRangeIdade = new FragmentRangeIdade();
                fragmentRangeIdade.show(getActivity().getSupportFragmentManager(), fragmentRangeIdade.getClass().getSimpleName());
                dismiss();
            }
        });

        botaoDefinirLimiteDistancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLimiteDistancia fragmentLimiteDistancia = new FragmentLimiteDistancia();
                fragmentLimiteDistancia.show(getActivity().getSupportFragmentManager(), fragmentLimiteDistancia.getClass().getSimpleName());
                dismiss();
            }
        });

        botaoSessoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSessoes fragmentSessoes = new FragmentSessoes();
                fragmentSessoes.show(getActivity().getSupportFragmentManager(), fragmentSessoes.getClass().getSimpleName());
                dismiss();
            }
        });
    }


}