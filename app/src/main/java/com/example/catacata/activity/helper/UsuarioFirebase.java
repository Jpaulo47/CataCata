package com.example.catacata.activity.helper;

import android.net.Uri;
import android.util.Log;

import com.example.catacata.activity.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario(){
        FirebaseAuth usuario = Configuracaofirebase.getReferenciaAutenticacao();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64( email );

        return identificadorUsuario;
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = Configuracaofirebase.getReferenciaAutenticacao();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarNomeUsuario(String nome){

        try{

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName( nome )
                    .build();

            user.updateProfile( profile ).addOnCompleteListener(task -> {

                if ( !task.isSuccessful()) {
                    Log.d("perfil", "Erro ao atualizar nome de perfil");
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    public static boolean atualizarFotoUsuario(Uri url){

        try{

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri( url )
                    .build();

            user.updateProfile( profile ).addOnCompleteListener(task -> {

                if ( !task.isSuccessful()) {
                    Log.d("perfil", "Erro ao atualizar foto de perfil");
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;

        }
    }

    public static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail( firebaseUser.getEmail() );
        usuario.setNome( firebaseUser.getDisplayName());

        if ( firebaseUser.getPhotoUrl() == null){
            usuario.setCaminhoFoto("");

        }else{
            usuario.setCaminhoFoto( firebaseUser.getPhotoUrl().toString());
        }

        return usuario;

    }
}