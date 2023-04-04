package com.example.catacata.activity.model;

import com.example.catacata.activity.helper.Configuracaofirebase;
import com.example.catacata.activity.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String cep;
    private String bairro;
    private String municipio;
    private String estado;
    private String logradouro;

    private String senha;
    private String caminhoFoto;

    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = Configuracaofirebase.getFirebase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child( getId() );

        usuario.setValue( this );
    }

    public void atualizar(){

        String  identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = Configuracaofirebase.getFirebase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child( identificadorUsuario );

        Map<String, Object>valoresUsuario = converterParaMap();

        usuariosRef.updateChildren( valoresUsuario );

    }

    @Exclude
    public HashMap<String, Object> converterParaMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail() );
        usuarioMap.put("nome", getNome() );
        usuarioMap.put("id", getId() );
        usuarioMap.put("caminhoFoto", getCaminhoFoto() );
        usuarioMap.put("cep", getCep() );
        usuarioMap.put("municipio", getMunicipio() );
        usuarioMap.put("bairro", getBairro() );
        usuarioMap.put("estado", getEstado() );
        usuarioMap.put("logradouro", getLogradouro() );

        return usuarioMap;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }
}
