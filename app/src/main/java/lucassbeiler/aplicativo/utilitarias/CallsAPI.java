package lucassbeiler.aplicativo.utilitarias;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallsAPI{
    public static String enderecoServidor = "http://34.95.164.190:3333/";
    public static String uploadsDir = enderecoServidor + "uploads/";

    public <T> Rotas retrofitBuilder() {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(enderecoServidor).addConverterFactory(GsonConverterFactory.create());
        return builder.build().create(Rotas.class);
    }
}