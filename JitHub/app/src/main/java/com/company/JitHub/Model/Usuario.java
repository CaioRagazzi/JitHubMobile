package com.company.JitHub.Model;

import android.content.Context;
import android.widget.Toast;

import com.company.JitHub.Activity.LoginActivity;
import com.company.JitHub.DAO.UsuarioDAO;
import com.company.JitHub.Retrofit.JsonUsuarioApi;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Usuario {

    @SerializedName("id")
    private int _id;
    @SerializedName("nome")
    private String _nome;
    @SerializedName("senha")
    private String _senha;
    @SerializedName("email")
    public String _email;
    @SerializedName("grupo")
    public String _grupo;

    public int get_id() {
        return _id;
    }

    public String get_nome() {
        return _nome;
    }

    public String get_senha() {
        return _senha;
    }

    public String get_email() {
        return _email;
    }

    public String get_grupo() {
        return _grupo;
    }

    public Usuario(String login, String senha){
        _nome = login;
        _senha = senha;
    }

    public Usuario GetUsuarioFromDatabase(Context context){

        Usuario usuario = new UsuarioDAO(context).GetUsuario(_nome, _senha);

        return usuario;
    }

    public boolean ValidaUsuarioESenhaNaBaseInterna(Context context){

        Usuario usuario = new UsuarioDAO(context).GetUsuario(_nome, _senha);

        if (usuario != null){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean ValidaUsuarioESenhaNaApi(Context context){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jithubapi.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonUsuarioApi jsonUsuarioApi = retrofit.create(JsonUsuarioApi.class);

        Call<List<Usuario>> call =  jsonUsuarioApi.getUsuarioPorNome(_nome);

        try {
            Response<List<Usuario>> response = call.execute();

            List<Usuario> usuarios = response.body();

            if (!usuarios.isEmpty()){

                Usuario usuario1 = usuarios.get(0);

                AtualizaSenhaUsuarioNaBaseInterna(usuario1 ,context);

                if (usuario1.get_nome().equals(_nome) && usuario1.get_senha().equals(_senha)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void AtualizaSenhaUsuarioNaBaseInterna(Usuario usuario, Context context){

        new UsuarioDAO(context).UpdateUsuario(usuario);
    }
}
