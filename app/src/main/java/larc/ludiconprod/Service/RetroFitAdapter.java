package larc.ludiconprod.Service;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import larc.ludiconprod.Activities.UserProfileActivity;
import larc.ludiconprod.Controller.Persistance;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitAdapter {

    private Retrofit retrofit;
    static UserProfileActivity userProfileActivity;


    public static OkHttpClient getClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("authKey", Persistance.getInstance().getUserInfo(userProfileActivity).authKey)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);

            }
        });

        return httpClient.build();

    }

    private void initializeRetroFit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ludicon.ro")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
    }

    public void getEvents(int userId, int pageNumber) {
        initializeRetroFit();
        LudiconAPI service = retrofit.create(LudiconAPI.class);

        Call<History> request = null; // service.pastEvents(userId, pageNumber, "GMT +3");
        request.enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                    }
                } else {
                    Log.d("FAIL", response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                Log.e("FAIL", t.getMessage() + " ", t);
            }
        });

    }
}