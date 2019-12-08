package lucassbeiler.aplicativo.UI;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.jem.rubberpicker.RubberRangePicker;

import org.jetbrains.annotations.NotNull;

import lucassbeiler.aplicativo.R;

public class ActivityConfiguracoes extends AppCompatActivity {
    private RubberRangePicker rangeIdadeView;
    private TextView rangeIdadeTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_configuracoes);

//        rangeIdadeView = findViewById(R.id.idade_range);
//        rangeIdadeTextView = findViewById(R.id.idade_range_textview);
        rangeIdadeView.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(@NotNull RubberRangePicker rubberRangePicker, int i, int i1, boolean b) {
                rangeIdadeTextView.setText("Entre " + rubberRangePicker.getCurrentStartValue() + " e " + rubberRangePicker.getCurrentEndValue() + " anos");
            }

            @Override
            public void onStartTrackingTouch(@NotNull RubberRangePicker rubberRangePicker, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull RubberRangePicker rubberRangePicker, boolean b) {

            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}