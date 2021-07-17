package aditi.sensor_based_har;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIsInterface {

    //Here we will define the request method for particular php script

    @FormUrlEncoded
    @POST("save_user.php")
    Call<ResponseModel> sendData(
            @Field("ACTIVITY") String activity
    );


}

