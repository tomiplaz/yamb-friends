package com.plazonic.tomislav.yambfriends;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface RestApi {
    String END_POINT = "http://ugodnomjesto.net84.net/yambfriends";

    @FormUrlEncoded
    @POST("/createNewAccount.php")
    void createNewAccount(
            @Field("username") String username,
            @Field("password") String password,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST("/signIn.php")
    void signIn(
            @Field("username") String username,
            @Field("password") String password,
            Callback<Response> callback
    );
}
