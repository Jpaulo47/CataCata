package com.example.catacata.activity.helper;

import android.net.Uri;
import android.util.Log;

import com.example.catacata.activity.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario(){
        FirebaseAuth usuario = Configuracaofirebase.getReferenciaAutenticacao();
        String email = Objects.requireNonNull(usuario.getCurrentUser()).getEmail();

        return Base64Custom.codificarBase64(Objects.requireNonNull(email));
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = Configuracaofirebase.getReferenciaAutenticacao();
        return usuario.getCurrentUser();
    }

    public static void getUriImagemPerfil(OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        String path = "imagens/perfil/" + email + ".jpeg/perfil.jpeg";
        FirebaseStorage.getInstance().getReference(path).getDownloadUrl()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
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