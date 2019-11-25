package lucassbeiler.aplicativo.requisicoes;

import android.os.AsyncTask;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FuncoesGET extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... parametros) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String rota = parametros[0];
        String token = parametros[1];

        Request requisicao = new Request.Builder().url(rota).addHeader("Authorization", "Bearer " + token).get().build();
        try (Response resposta = client.newCall(requisicao).execute()) {
            return resposta.body().string();
        }catch(Exception e){
            return e.toString();
        }
    }
}
