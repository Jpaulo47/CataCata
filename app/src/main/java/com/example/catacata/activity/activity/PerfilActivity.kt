package com.example.catacata.activity.activity

import FirebaseDatabaseHelper
import Notificador
import UsuarioRepository
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.catacata.R
import com.example.catacata.activity.helper.Configuracaofirebase
import com.example.catacata.activity.helper.UsuarioFirebase
import com.example.catacata.activity.model.Usuario
import com.example.catacata.databinding.ActivityPerfilBinding
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private var storageReference: StorageReference? = null
    private var identificadorUsuario: String? = null
    private var usuarioLogado: Usuario? = null

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarObjetos()
        configurartoolbar()
        atualizarImagemPerfil()
        setarInfoView()

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            return
        }

        try {
            val imagem = if (requestCode == SELECAO_GALERIA) {
                data?.data?.let {
                    MediaStore.Images.Media.getBitmap(contentResolver, it)
                }
            } else {
                null
            }

            imagem?.let {
                binding.circleImageToolbar.setImageBitmap(imagem)

                val dadosImagem = compressBitmap(imagem)

                uploadImagem(dadosImagem) { url ->
                    atualizarFotoUsuario(url)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun compressBitmap(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        return baos.toByteArray()
    }

    private fun uploadImagem(dadosImagem: ByteArray, onUploadComplete: (Uri) -> Unit) {
        val imagemRef = storageReference
            ?.child("imagens")
            ?.child("perfil")
            ?.child("${usuarioLogado?.email}.jpeg")
            ?.child("perfil.jpeg")

        val uploadTask = imagemRef?.putBytes(dadosImagem)

        uploadTask?.addOnFailureListener { e: Exception? ->
            Notificador.showToast("Erro ao fazer upload da imagem")

        }?.addOnSuccessListener {

            Notificador.showToast("Sucesso ao fazer upload da imagem")
            imagemRef.downloadUrl.addOnSuccessListener { url: Uri ->
                onUploadComplete(url)
            }
        }
    }

    fun atualizarFotoUsuario(url: Uri) {
        val retorno = UsuarioFirebase.atualizarFotoUsuario(url)
        if (retorno) {
            usuarioLogado?.caminhoFoto = url.toString()
            usuarioLogado?.atualizar()
            Notificador.showToast("Sucesso ao alterar sua foto")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    companion object {
        private const val SELECAO_GALERIA = 200
    }

    fun atualizarImagemPerfil(){
        UsuarioFirebase.getUriImagemPerfil({ uri: Uri? ->
            Glide.with(this).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH).into(binding.circleImageToolbar)
        }) { e: Exception? -> binding.circleImageToolbar.setImageResource(R.drawable.padrao) }
    }

    fun configurartoolbar(){
        val toolbar = binding.toolbarPerfil
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_voltar)
        toolbar.setTitleTextColor(resources.getColor(R.color.verdeIcons))
    }

    @SuppressLint("SetTextI18n")
    fun setarInfoView(){

        val usuarioPerfil = UsuarioFirebase.getUsuarioAtual()

        val usuarioRepository = UsuarioRepository()
        val idUsuario = UsuarioFirebase.getIdentificadorUsuario()
        usuarioRepository.getUsuario(idUsuario) { usuario ->
            if (usuario != null) {
                binding.editNomeUsuario.setText(usuario.nome)
                binding.editCadastroEmail.setText(usuario.email)
                binding.editCadastroTelefone.setText(usuario.telefone)
                binding.editCadastroCep.setText(usuario.cep)
                binding.editCadastroMunicipio.setText(usuario.municipio)
                binding.editCadastroBairro.setText(usuario.bairro)
                binding.editCadastroLogradouro.setText(usuario.logradouro)
                binding.editCadastroUf.setText(usuario.estado)
                binding.editCadastroNascimento.setText(usuario.dataNascimento?.let {
                    Utils.formatDate(
                        it, "dd/MM/yyyy")
                })

                binding.spinnerSexo.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf(usuario.sexo)
                )
                binding.spinnerOcupacao.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf(usuario.ocupacao)
                )
            } else {
                binding.textNomeUsuarioPerfil.text = "usuario não encontrado"
            }
        }

        usuarioLogado = Usuario()
        binding.textNomeUsuarioPerfil.text = usuarioPerfil.displayName
        binding.textEmailUsuarioLogado.text = usuarioPerfil.email

    }

    @SuppressLint("QueryPermissionsNeeded")
    val aoClicarImageAlterarFoto = View.OnClickListener {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (i.resolveActivity(packageManager) != null) startActivityForResult(
            i,
            SELECAO_GALERIA
        )
    }


    private fun inicializarObjetos() {
        storageReference = Configuracaofirebase.getFirebaseStorage()
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado()
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario()

        binding.imageAlterarFoto.setOnClickListener(aoClicarImageAlterarFoto)

    }
}
