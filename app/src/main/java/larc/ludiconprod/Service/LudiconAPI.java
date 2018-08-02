package larc.ludiconprod.Service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LudiconAPI {

    @GET("api/v2/pastEvents")
    Call<History> pastEvents(
            @Query("userId") String userId,
            @Query("pageNumber") String pageNumber,
            @Query("timeZone") String timeZone);

}
