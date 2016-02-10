package com.plazonic.tomislav.yambfriends;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

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

    @FormUrlEncoded
    @POST("/uploadImage.php")
    void uploadImage(
            @Field("username") String username,
            @Field("image") String image,
            Callback<Response> callback
    );

    @GET("/getUserId.php")
    void getUserId(
            @Query("username") String username,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST("/insertGame.php")
    void insertGame(
            @Field("username") String username,
            @Field("type") String type,
            @Field("game") String game,
            @Field("result") int result,
            @Field("duration") int duration,
            @Field("latitude") float latitude,
            @Field("longitude") float longitude,
            Callback<Response> callback
    );

}
