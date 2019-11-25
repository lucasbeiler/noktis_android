package lucassbeiler.aplicativo.requisicoes;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FuncoesPOSTImagem extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... parametros) {
        String rota = parametros[0];
        String caminho = parametros[1].substring(parametros[1].lastIndexOf(":")+1);;
        String token = parametros[2];

        File arquivoImagem = new File(caminho);

        //OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", caminho, RequestBody.create(MediaType.parse("image/*"), arquivoImagem))
                .build();

        Request requisicao = new Request.Builder().url(rota).addHeader("Authorization", "Bearer " + token).post(body).build();
        try (Response resposta = client.newCall(requisicao).execute()) {
            return resposta.body().string();
        } catch (Exception e) {
            return e.toString();
        }
    }
}
