package com.example.catacata.activity.model

import com.example.catacata.activity.helper.Configuracaofirebase
import com.example.catacata.activity.helper.UsuarioFirebase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.Exclude
import java.io.Serializable
import java.util.*

class Usuario : Serializable {
    @get:Exclude
    var id: String? = null
    var nome: String? = null
    var email: String? = null
    var cep: String? = null
    var bairro: String? = null
    var municipio: String? = null
    var estado: String? = null
    var logradouro: String? = null

    @get:Exclude
    var senha: String? = null
    var telefone: String? = null
    var sexo: String? = null
    var ocupacao: String? = null
    var isTermosdeUso = false
    var dataNascimento: Date? = null

    fun salvar() {
        val firebaseRef = Configuracaofirebase.firebase
        val usuario = firebaseRef?.child("usuarios")!!.child(id!!)
        usuario.setValue(this)
    }

    fun atualizar(listener: OnCompleteListener<Void?>?) {
        val identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario()
        val database = Configuracaofirebase.firebase
        val usuariosRef = database!!.child("usuarios")
            .child(identificadorUsuario)
        val valoresUsuario: Map<String, Any?> = converterParaMap()
        usuariosRef.updateChildren(valoresUsuario).addOnCompleteListener(listener!!)
    }

    @Exclude
    fun converterParaMap(): HashMap<String, Any?> {
        val usuarioMap = HashMap<String, Any?>()
        usuarioMap["email"] = email
        usuarioMap["nome"] = nome
        usuarioMap["id"] = id
        usuarioMap["cep"] = cep
        usuarioMap["municipio"] = municipio
        usuarioMap["bairro"] = bairro
        usuarioMap["estado"] = estado
        usuarioMap["logradouro"] = logradouro
        usuarioMap["telefone"] = telefone
        usuarioMap["dataNascimento"] = dataNascimento
        usuarioMap["sexo"] = sexo
        usuarioMap["ocupacao"] = ocupacao
        return usuarioMap
    }
}