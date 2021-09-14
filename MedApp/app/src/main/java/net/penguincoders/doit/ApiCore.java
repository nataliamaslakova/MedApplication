package net.penguincoders.doit;
import com.google.gson.JsonArray;
import java.util.concurrent.TimeUnit;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class ApiCore {

    private static String BASE_URL = "https://med-photo.herokuapp.com/";

    public static Retrofit getRetrofit() {

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .build();


        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public interface GenericDefinitionInterface{

        @Multipart
        @POST("imgt")
        Call<JsonArray> uploadFile(@Part MultipartBody.Part image);}}


//        Call<JsonArray> because the response is in ArrayFormat
//          If possible please change to object format with API headers and Success code
