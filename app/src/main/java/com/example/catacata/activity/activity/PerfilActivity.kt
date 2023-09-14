package com.example.catacata.activity.activity

import Notificador
import UsuarioRepository
import Validator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.catacata.R
import com.example.catacata.activity.helper.FirebaseConfig
import com.example.catacata.activity.helper.UserFirebase
import com.example.catacata.activity.model.Usuario
import com.example.catacata.databinding.ActivityPerfilBinding
import com.example.utils.UtilsView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
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
        UtilsView.setEditTextsEnabled(false, binding.linearPerfil, binding.linearCep, binding.linearEstado)
        preencherSpinner()
        adicionarMascaras()

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val usuarioPerfil = UserFirebase.getUsuarioAtual()
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
                    atualizarFotoUsuario(url, usuarioPerfil!!.email.toString())
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
        val usuarioPerfil = UserFirebase.getUsuarioAtual()
        val imagemRef = storageReference
            ?.child("imagens")
            ?.child("perfil")
            ?.child("${usuarioPerfil?.email}.jpeg")
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

    fun atualizarFotoUsuario(url: Uri,  email: String) {

        val storageRef = FirebaseStorage.getInstance().getReference("imagens/perfil/$email.jpeg/perfil.jpeg")
        storageRef.putFile(url).addOnSuccessListener {}.addOnFailureListener {}
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    companion object {
        private const val SELECAO_GALERIA = 200
    }

    fun atualizarImagemPerfil(){
        UserFirebase.getUriImagemPerfil({ uri: Uri? ->
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

        val usuarioPerfil = UserFirebase.getUsuarioAtual()

        val usuarioRepository = UsuarioRepository()
        val idUsuario = UserFirebase.getIdentificadorUsuario()
        usuarioRepository.getUsuario(idUsuario) { usuario ->
            if (usuario != null) {
                binding.editNomeUsuario.setText(usuario.nome)
                binding.editCadastroTelefone.setText(usuario.telefone)
                binding.editCadastroCep.setText(usuario.cep)
                binding.editCadastroMunicipio.setText(usuario.municipio)
                binding.editCadastroBairro.setText(usuario.bairro)
                binding.editCadastroLogradouro.setText(usuario.logradouro)
                binding.editCadastroUf.setText(usuario.estado)
                binding.editCadastroOcupacao.setText(usuario.ocupacao)
                binding.editCadastroSexo.setText(usuario.sexo)
                binding.editCadastroNascimento.setText(usuario.dataNascimento?.let {
                    Utils.formatDate(
                        it, "dd/MM/yyyy")
                })

                UtilsView.setShowComponents(View.GONE, binding.containerSpinner, binding.buttonCadastrar, binding.buscarCep)

            } else {
                binding.textNomeUsuarioPerfil.text = "usuario não encontrado"
            }
        }

        usuarioLogado = Usuario()
        binding.textNomeUsuarioPerfil.text = usuarioPerfil!!.displayName
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

    val aoClicarAlterarDados = View.OnClickListener {
        UtilsView.setEditTextsEnabled(true, binding.linearPerfil, binding.linearCep, binding.linearEstado)
        UtilsView.setShowComponents(View.VISIBLE, binding.containerSpinner, binding.buttonCadastrar, binding.buscarCep)
        UtilsView.setShowComponents(View.GONE, binding.inputCadastroSexo, binding.inputCadastroOcupacao)
    }

    private fun inicializarObjetos() {
        storageReference = FirebaseConfig.firebaseStorage
        usuarioLogado = UserFirebase.getDadosUsuarioLogado()
        identificadorUsuario = UserFirebase.getIdentificadorUsuario()

        binding.imageAlterarFoto.setOnClickListener(aoClicarImageAlterarFoto)
        binding.buttonAlterarDados.setOnClickListener(aoClicarAlterarDados)
        binding.buscarCep.setOnClickListener {buscarCep(binding.editCadastroCep.text.toString())}
        binding.buttonCadastrar.setOnClickListener{atualizarDados()}

    }

    fun preencherSpinner() {

        val ocupacoes = arrayOf("Selecione sua ocupação", "Morador", "Catador", "Cooperativa")
        val adapterOcupacao = ArrayAdapter(this, android.R.layout.simple_spinner_item, ocupacoes)
        adapterOcupacao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOcupacao.adapter = adapterOcupacao

        val sexos = arrayOf("Informe seu sexo", "Masculino", "Feminino", "Não quero informar")
        val adapterSexo = ArrayAdapter(this, android.R.layout.simple_spinner_item, sexos)
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSexo.adapter = adapterSexo

    }

    private fun buscarCep(cep: String) {
        lifecycleScope.launch {
            val endereco = CepApi.buscaCep(cep)

            if (endereco != null) {
                binding.editCadastroLogradouro.setText(endereco.logradouro)
                binding.editCadastroBairro.setText(endereco.bairro)
                binding.editCadastroMunicipio.setText(endereco.cidade)
                binding.editCadastroUf.setText(endereco.estado)
            } else {
                Notificador.showToast("CEP inválido ou não encontrado.")
            }
        }
    }

    private fun atualizarDados() {

        val textoNome = binding.editNomeUsuario.text.toString()
        val textoCep = binding.editCadastroCep.text.toString()
        val textoMunicipio = binding.editCadastroMunicipio.text.toString()
        val textoEstado = binding.editCadastroUf.text.toString()
        val textoLogradouro = binding.editCadastroLogradouro.text.toString()
        val textoBairro = binding.editCadastroBairro.text.toString()
        val textoTelefone = binding.editCadastroTelefone.text.toString()
        val dataNascimento = binding.editCadastroNascimento.text.toString()
        val textoSexo = binding.spinnerSexo.selectedItem.toString()
        val textoOcupacao = binding.spinnerOcupacao.selectedItem.toString()

        val validator = Validator()

        validator.addDateValidator(binding.editCadastroNascimento, "Data de nascimento inválida!", true)
        validator.addNameValidator(binding.editNomeUsuario, "Nome Inválido, informe seu nome e sobrenome!", true)
        validator.addPhoneNumberValidator(binding.editCadastroTelefone, "Campo obrigatório", true)
        validator.addRequiredFieldValidator(binding.editCadastroCep, "Campo obrigatório")
        validator.addRequiredFieldValidator(binding.editCadastroBairro, "Campo obrigatório")
        validator.addRequiredFieldValidator(binding.editCadastroMunicipio, "Campo obrigatório")
        validator.addRequiredFieldValidator(binding.editCadastroUf, "Campo obrigatório")
        validator.addRequiredFieldValidator(binding.editCadastroLogradouro, "Campo obrigatório")
        validator.addSpinnerValidator(binding.spinnerOcupacao, this, "Informe sua ocupação!")
        validator.addSpinnerValidator(binding.spinnerSexo, this, "Informe seu sexo!")

        if (validator.validateFields()) {
           val  usuario = Usuario()
            usuario.nome = textoNome
            usuario.cep = textoCep
            usuario.municipio = textoMunicipio
            usuario.bairro = textoBairro
            usuario.logradouro = textoLogradouro
            usuario.estado = textoEstado
            usuario.telefone = textoTelefone
            usuario.dataNascimento = Utils.stringToDate(dataNascimento)
            usuario.sexo = textoSexo
            usuario.ocupacao = textoOcupacao
            UserFirebase.atualizarNomeUsuario(textoNome)

            binding.progressBarCadastro.visibility = View.VISIBLE
            binding.buttonCadastrar.text = ""
            usuario.atualizar { task ->
                if (task.isSuccessful) {
                    Notificador.showToast("Atualização realizada com sucesso!")
                    finish()
                } else {
                    Notificador.showToast("Erro ao atualizar os dados: " + task.exception?.message)
                    binding.progressBarCadastro.visibility = View.GONE
                    binding.buttonCadastrar.text = getString(R.string.salvar)
                }
            }

        }
    }

    fun adicionarMascaras() {
        Utils.addMaskToEditText(binding.editCadastroNascimento, "##/##/####")
        Utils.addMaskToEditText(binding.editCadastroTelefone, "(##) #####-####")
        Utils.addMaskToEditText(binding.editCadastroCep, "#####-###")
    }
}
