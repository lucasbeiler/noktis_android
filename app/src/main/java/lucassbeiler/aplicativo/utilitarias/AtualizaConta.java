//package lucassbeiler.aplicativo.utilitarias;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.sdsmdg.tastytoast.TastyToast;
//
//import org.json.JSONObject;
//
//import lucassbeiler.aplicativo.models.AlteracaoConta;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class AtualizaConta {
//    public void alteraConta(SharedPreferences sharp, CallsAPI callsAPI, AlteracaoConta alteracaoConta, final Context context, final String texto) {
//        callsAPI.retrofitBuilder().alteraConta(alteracaoConta, "Bearer " + sharp.getString("token", "")).enqueue(new Callback<AlteracaoConta>() {
//            @Override
//            public void onResponse(Call<AlteracaoConta> call, Response<AlteracaoConta> resposta) {
//                if (resposta.isSuccessful() && !texto.isEmpty()) {
//                    TastyToast.makeText(, "E-mail atualizada com sucesso!", Toast.LENGTH_LONG, TastyToast.SUCCESS);
//                } else {
//                    try {
//                        TastyToast.makeText(, new JSONObject(resposta.errorBody().string()).getString("error"), Toast.LENGTH_LONG, TastyToast.ERROR);
//                    } catch (Exception e) {
//                        Log.d("EDITACONTA EXCEPTION", e.getMessage());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AlteracaoConta> call, Throwable t) {
//                Log.d("EDITACONTA EXCEPTION2", t.getMessage());
//            }
//        });
//    }
//}
