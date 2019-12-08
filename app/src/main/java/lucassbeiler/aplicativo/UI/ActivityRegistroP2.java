package lucassbeiler.aplicativo.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jem.rubberpicker.RubberRangePicker;
import com.jem.rubberpicker.RubberSeekBar;

import org.jetbrains.annotations.NotNull;

import lucassbeiler.aplicativo.R;

public class ActivityRegistroP2 extends AppCompatActivity {
    private SimpleDraweeView imagemRegistro2;
    private RubberRangePicker rangeIdadeView;
    private RubberSeekBar limiteDistanciaView;
    private TextView limiteDistanciaAtualView, rangeIdadeAtualView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_p2);

        imagemRegistro2 = findViewById(R.id.imagem_registro);
        rangeIdadeView = findViewById(R.id.range_idade);
        limiteDistanciaView = findViewById(R.id.limite_distancia);
        limiteDistanciaAtualView = findViewById(R.id.limite_atual_distancia);
        rangeIdadeAtualView = findViewById(R.id.range_atual_idade);

        rangeIdadeAtualView.setText("Entre " + rangeIdadeView.getCurrentStartValue() + " e " + rangeIdadeView.getCurrentEndValue() + " anos");
        limiteDistanciaAtualView.setText("Limite de " + limiteDistanciaView.getCurrentValue() + "km");

        rangeIdadeView.setOnRubberRangePickerChangeListener(new RubberRangePicker.OnRubberRangePickerChangeListener() {
            @Override
            public void onProgressChanged(@NotNull RubberRangePicker rubberRangePicker, int i, int i1, boolean b) {
                rangeIdadeAtualView.setText("Entre " + rubberRangePicker.getCurrentStartValue() + " e " + rubberRangePicker.getCurrentEndValue() + " anos");
            }

            @Override
            public void onStartTrackingTouch(@NotNull RubberRangePicker rubberRangePicker, boolean b) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull RubberRangePicker rubberRangePicker, boolean b) {

            }
        });

        limiteDistanciaView.setOnRubberSeekBarChangeListener(new RubberSeekBar.OnRubberSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@NotNull RubberSeekBar rubberSeekBar, int i, boolean b) {
                rangeIdadeAtualView.setText("Limite de " + rubberSeekBar.getCurrentValue() + "km");
            }

            @Override
            public void onStartTrackingTouch(@NotNull RubberSeekBar rubberSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(@NotNull RubberSeekBar rubberSeekBar) {

            }
        });


    }
}
