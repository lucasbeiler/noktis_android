package lucassbeiler.aplicativo.fragments;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class EscolhaDataFragment extends DialogFragment{
    @NonNull
    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        int ano = cal.get(Calendar.YEAR)-18;
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getContext(), (DatePickerDialog.OnDateSetListener) getActivity(), ano, mes, dia);
    }
}
