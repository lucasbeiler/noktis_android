package lucassbeiler.aplicativo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import io.socket.client.Socket;
import lucassbeiler.aplicativo.R;

public class FragmentEditarPerfilMenu extends DialogFragment {
    private Socket socket;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_perfil_menu, container, false);

        if(getDialog() != null){
            getDialog().setCancelable(true);
            getDialog().setCanceledOnTouchOutside(true);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        Button editarBio = view.findViewById(R.id.botao_editar_bio);
        Button botaoApagarConta = view.findViewById(R.id.botao_apagar_conta);
        Button botaoSairConta = view.findViewById(R.id.botao_sair_conta);

        editarBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentEditarBio fragmentEditarBio = new FragmentEditarBio();
                fragmentEditarBio.show(getActivity().getSupportFragmentManager(), fragmentEditarBio.getClass().getSimpleName());
                dismiss();
            }
        });

        botaoApagarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentApagarConta fragmentApagarConta = new FragmentApagarConta();
                fragmentApagarConta.show(getActivity().getSupportFragmentManager(), fragmentApagarConta.getClass().getSimpleName());
                dismiss();
            }
        });

        botaoSairConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSessoes fragmentSessoes = new FragmentSessoes();
                fragmentSessoes.show(getActivity().getSupportFragmentManager(), fragmentSessoes.getClass().getSimpleName());
                dismiss();
                dismiss();
            }
        });
    }


}