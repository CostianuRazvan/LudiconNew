package larc.ludiconprod.Service;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface LudiconAPI {

    @GET("api/v2/pastEvents")
    Call<HistoryResponse> pastEvents(
            @Header("authKey") String authKey,
            @Query("userId") String userId,
            @Query("pageNumber") String pageNumber,
            @Query("timeZone") String timeZone);

}
