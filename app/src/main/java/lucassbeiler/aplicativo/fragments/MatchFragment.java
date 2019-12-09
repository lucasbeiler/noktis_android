package lucassbeiler.aplicativo.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import lucassbeiler.aplicativo.R;
import lucassbeiler.aplicativo.UI.ActivityCentral;
import lucassbeiler.aplicativo.UI.ActivityConversa;

public class MatchFragment extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SimpleDraweeView foto1, foto2;
        Button botaoSair;
        FloatingActionButton botaoChatMatch;

        View view = inflater.inflate(R.layout.fragment_match, container, false);
        foto1 = view.findViewById(R.id.fotoUsuario1);
        foto2 = view.findViewById(R.id.fotoUsuario2);
        botaoSair = view.findViewById(R.id.botao_sair);
        botaoChatMatch = view.findViewById(R.id.botao_match_chat);

        Bundle bundle = getArguments();
        String imagemURLUsuario = bundle.getString("usuarioImagemURL", "");
        String imagemURLMatch = bundle.getString("filenameImagemMatch", "");
        final String idMatch = bundle.getString("idMatch", "");

        Uri uriFotoUsuario = Uri.parse(imagemURLUsuario);
        Uri uriFotoMatch = Uri.parse(imagemURLMatch);

        foto1.setImageURI(uriFotoUsuario);
        foto2.setImageURI(uriFotoMatch);

        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DISMISSOU", "DISMISSOU");
                dismiss();
            }
        });



        botaoChatMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                startActivity(new Intent(getActivity(), ActivityConversa.class).putExtra("idMatch", idMatch));
            }
        });

        if(getDialog() != null && getDialog().getWindow() != null && !getDialog().isShowing()){
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
        return view;
    }
}