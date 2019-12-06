package lucassbeiler.aplicativo.utilitarias;

import java.util.List;

import lucassbeiler.aplicativo.models.AlteracaoConta;
import lucassbeiler.aplicativo.models.EncerramentoConta;
import lucassbeiler.aplicativo.models.Localizacao;
import lucassbeiler.aplicativo.models.Login;
import lucassbeiler.aplicativo.models.Reacao;
import lucassbeiler.aplicativo.models.Registro;
import lucassbeiler.aplicativo.models.Usuarios;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface Rotas {
    @POST("sessions")
    Call<Login> fazLogin(@Body Login login);

    @POST("/user/create")
    Call<Registro> criaConta(@Body Registro registro);

    @POST("/users/likes")
    Call<Reacao> like(@Body Reacao reacao, @Header("Authorization") String token);

    @POST("/users/dislikes")
    Call<Reacao> dislike(@Body Reacao reacao, @Header("Authorization") String token);

    @POST("/location/send")
    Call<Localizacao> localizaUsuario(@Body Localizacao localizacao, @Header("Authorization") String token);

    @HTTP(method = "DELETE", path = "/delete/account", hasBody = true)
    Call<EncerramentoConta> encerraConta(@Body EncerramentoConta encerramentoConta, @Header("Authorization") String token);

    @PUT("/user/updates")
    Call<AlteracaoConta> alteraConta(@Body AlteracaoConta alteracaoConta, @Header("Authorization") String token);

    @GET("/users/online")
    Call<Usuarios> getUsuarios(@Header("Authorization") String token);

    @Multipart
    @POST("/upload/file")
    Call<ResponseBody> uploadFoto(@Header("Authorization") String token, @Part MultipartBody.Part file);
}
