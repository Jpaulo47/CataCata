package com.example.catacata.activity.helper

import android.net.Uri
import android.util.Log
import com.example.catacata.activity.model.Usuario
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import java.util.*

/**
 * Esta classe contém métodos estáticos para ajudar a gerenciar usuários no Firebase.
 * create by João Paulo - 03/10/2022
 */

object UserFirebase {

    /**
     * Retorna o identificador do usuário atual codificado em Base64.
     * @return O identificador do usuário atual codificado em Base64.
     */

    fun getIdentificadorUsuario(): String {
        val usuario = FirebaseConfig.referenciaAutenticacao
        val email = usuario!!.currentUser!!.email
        return Base64Custom.codificarBase64(email.toString())
    }

    /**
     * Retorna o objeto FirebaseUser que representa o usuário atualmente autenticado.
     * @return O objeto FirebaseUser que representa o usuário atualmente autenticado.
     */

    fun getUsuarioAtual(): FirebaseUser? {
        val usuario = FirebaseConfig.referenciaAutenticacao
        return usuario!!.currentUser
    }

    /**
     * Recupera a URL da imagem de perfil do usuário atual no Firebase Storage e chama o listener de sucesso ou falha.
     * @param successListener O listener que será chamado com a URL da imagem de perfil se a operação for bem-sucedida.
     * @param failureListener O listener que será chamado se ocorrer um erro durante a operação.
     */

    fun getUriImagemPerfil(successListener: OnSuccessListener<Uri>, failureListener: OnFailureListener) {
        val email = Objects.requireNonNull(FirebaseAuth.getInstance().currentUser)!!.email
        val path = "imagens/perfil/$email.jpeg/perfil.jpeg"
        FirebaseStorage.getInstance().getReference(path).downloadUrl
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)
    }

    /**
     * Atualiza o nome do usuário atual no Firebase Authentication.
     * @param nome O novo nome do usuário.
     * @return true se a operação for bem-sucedida, false caso contrário.
     */

    fun atualizarNomeUsuario(nome: String): Boolean {
        return try {
            val user = getUsuarioAtual()
            val profile = UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .build()
            user?.updateProfile(profile)?.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("perfil", "Erro ao atualizar nome de perfil")
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun atualizarFotoUsuario(url: Uri): Boolean {
        return try {
            val user = getUsuarioAtual()
            val profile = UserProfileChangeRequest.Builder()
                .setPhotoUri(url)
                .build()
            user?.updateProfile(profile)?.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("perfil", "Erro ao atualizar foto de perfil")
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    /**
     * Retorna um objeto Usuario que contém os dados do usuário atualmente autenticado.
     * @return Um objeto Usuario que contém os dados do usuário atualmente autenticado.
     */

    fun getDadosUsuarioLogado(): Usuario {
        val firebaseUser = getUsuarioAtual()
        val usuario = Usuario()
        usuario.email = firebaseUser?.email
        usuario.nome = firebaseUser?.displayName
        return usuario
    }
}
