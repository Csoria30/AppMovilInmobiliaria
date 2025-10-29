package com.ulp.appinmobiliaria.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.model.PropietarioModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public class ApiClient {
    public static final String URLBASE = "https://inmobiliariaulp-amb5hwfqaraweyga.canadacentral-01.azurewebsites.net/";

    // Configuración de Retrofit
    public static InmobiliariaService getApiInmobiliaria(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("YYYY-MM-dd'T'HH:mm:ss").create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLBASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(InmobiliariaService.class);
    }

    // Interface de servicios
    public interface InmobiliariaService{
        @FormUrlEncoded
        @POST("api/Propietarios/login")
        Call<String> login(@Field("Usuario") String u, @Field("Clave") String c);

        @GET("api/propietarios")
        Call<PropietarioModel> obtenerPerfil(@Header("Authorization") String token);

        @PUT("api/Propietarios/actualizar")
        Call<PropietarioModel> actualizarPerfil(@Header("Authorization") String token, @Body PropietarioModel propietario);

        @FormUrlEncoded
        @PUT("api/Propietarios/changePassword")
        Call<Void> cambiarContrasena(
                @Header("Authorization") String token,
                @Field("currentPassword") String currentPassword,
                @Field("newPassword") String newPassword
        );


        @GET("api/Inmuebles")
        Call<List<InmuebleModel>> obtenerInmuebles(
                @Header("Authorization") String token
        );


    }
}