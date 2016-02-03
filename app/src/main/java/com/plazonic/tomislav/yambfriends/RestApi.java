package com.plazonic.tomislav.yambfriends;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Tomislav on 2/3/2016.
 */
public interface RestApi {
    @FormUrlEncoded
    @POST("/createNewAccount.php")
    void createNewAccount(
            @Field("username") String username,
            @Field("password") String password,
            Callback<Response> callback
    );
}
