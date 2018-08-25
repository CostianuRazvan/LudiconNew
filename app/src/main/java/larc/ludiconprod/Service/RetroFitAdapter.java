package larc.ludiconprod.Service;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;

import larc.ludiconprod.Adapters.EditProfile.HistoryFullAdapter;
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
    private static Retrofit retrofitInstance;
    String authKey;
    RecyclerView recyclerView;

    public RetroFitAdapter(String authKey, RecyclerView recyclerView) {
        this.authKey = authKey;
        this.recyclerView = recyclerView;
    }

    public static Retrofit getInstance() {
        return retrofitInstance;
    }


    private OkHttpClient getClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("authKey", authKey)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);

            }
        });

        httpClient.interceptors().add(logging);

        return httpClient.build();


    }

    private void initializeRetroFit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://207.154.236.13/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();

        retrofitInstance = retrofit;
    }

    public void getEvents(String userId, int pageNumber) {
        initializeRetroFit();
        LudiconAPI service = retrofit.create(LudiconAPI.class);

        Call<HistoryResponse> request = service.pastEvents(authKey, userId, pageNumber + "", "GMT +3");
        request.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("PASTEVENTS", response.body().getHistoryArrayList().get(0).getEventDate() + "");

                        ((HistoryFullAdapter) recyclerView.getAdapter()).addAllItems(response.body().getHistoryArrayList());
                    }
                } else {
                    Log.d("FAIL", response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                Log.e("FAIL", t.getMessage() + " ", t);
            }
        });

    }
}