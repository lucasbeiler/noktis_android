package lucassbeiler.aplicativo.requisicoes;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FuncoesPUTJSON extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... parametros) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String rota = parametros[0];
        String objetoJSON = parametros[1];
        String token = parametros[2];

        RequestBody corpoRequisicao = RequestBody.create(JSON, objetoJSON);

        Request requisicao = new Request.Builder().url(rota).addHeader("Authorization", "Bearer " + token).put(corpoRequisicao).build();
        Log.d("Requisicao " + rota, requisicao.toString());
        try (Response resposta = client.newCall(requisicao).execute()) {
            return resposta.body().string();
        }catch(Exception e){
            return e.toString();
        }
    }
}
