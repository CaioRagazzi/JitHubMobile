package com.company.JitHub.Retrofit;

import com.company.JitHub.Model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonUsuarioApi {

    @GET("usuarios")
    Call<List<Usuario>> getUsuarios();

    @GET("usuarios")
    Call<List<Usuario>> getUsuarioPorNome(@Query("nome") String nome);
}
