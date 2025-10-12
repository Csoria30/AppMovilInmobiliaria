package com.ulp.appinmobiliaria.request;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ulp.appinmobiliaria.model.PropietarioModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public class ApiClient {
    public static final String URLBASE = "https://inmobiliariaulp-amb5hwfqaraweyga.canadacentral-01.azurewebsites.net/";

    //Retrofit
    public static InmobiliariaService getApiInmobiliaria(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("YYYY-MM-dd'T'HH:mm:ss").create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLBASE)
                //.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(InmobiliariaService.class);
    }

    public static void guardarToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String leerToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("token.xml", Context.MODE_PRIVATE);
        return sp.getString("token", null);
    }

    public interface InmobiliariaService{
        @FormUrlEncoded
        @POST("api/Propietarios/login")
        Call<String> login(@Field("Usuario") String u, @Field("Clave") String c);

        //Obtener perfil
        @GET("api/propietarios")
        Call<PropietarioModel> obtenerPerfil(@Header("Authorization") String token);

        //Actualizar Perfil
        @PUT("api/Propietarios/actualizar")
        Call<PropietarioModel> actualizarPerfil(@Header("Authorization") String token, @Body PropietarioModel propietario);
    }
}
