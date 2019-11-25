package lucassbeiler.aplicativo.fragments;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;
import lucassbeiler.aplicativo.ActivityEditarPerfil;
import lucassbeiler.aplicativo.R;

public class MatchFragment extends SupportBlurDialogFragment {
    private SharedPreferences sharp;
    private CircleImageView foto1, foto2;
    private Button botaoSair;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match, container, false);
        foto1 = view.findViewById(R.id.fotoUsuario1);
        foto2 = view.findViewById(R.id.fotoUsuario2);
        botaoSair = view.findViewById(R.id.botaoSair);
        Bundle bundle = getArguments();
        sharp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String imagemURLUsuario = sharp.getString("imagemURL", "");
        String imagemURLMatch = "http://34.95.164.190:3333/uploads/" + bundle.getString("filenameImagem", "");

        Picasso.get().load(imagemURLMatch).into(foto1, new com.squareup.picasso.Callback() {
            public void onSuccess() {
                //
            }

            public void onError(Exception ex) {
                //TastyToast.makeText(MatchFragment.this, getString(R.string.erro_carregar_imagem) + imagemURL, Toast.LENGTH_LONG, TastyToast.ERROR);
            }
        });

        Picasso.get().load(imagemURLUsuario).into(foto2, new com.squareup.picasso.Callback() {
            public void onSuccess() {
                //
            }

            public void onError(Exception ex) {
               // TastyToast.makeText(MatchFragment.this, getString(R.string.erro_carregar_imagem) + imagemURL, Toast.LENGTH_LONG, TastyToast.ERROR);
            }
        });


        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }
    @Override
    protected float getDownScaleFactor() {
        return 6.5f;
    }
    @Override
    protected boolean isRenderScriptEnable() {
        return true;
    }
}
