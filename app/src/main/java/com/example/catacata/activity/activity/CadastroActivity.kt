package com.example.catacata.activity.activity

import Notificador.Companion.showToast
import Utils
import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.catacata.activity.helper.Base64Custom
import com.example.catacata.activity.helper.Configuracaofirebase
import com.example.catacata.activity.helper.UsuarioFirebase
import com.example.catacata.activity.model.Usuario
import com.example.catacata.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    private var usuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cadastrarUsuario()
        preencherSpinner()
        adicionarMascaras()
        binding.buscarCep.setOnClickListener {
            buscarCep(binding.editCadastroCep.text.toString())
        }

    }

    fun cadastrarUsuario() {
        //Cadastrar usuario
        binding.progressBarCadastro.visibility = ProgressBar.GONE
        binding.buttonCadastrar.setOnClickListener {


            if (!camposPreenchidos()) return@setOnClickListener
            usuario = Usuario()
            cadastrar(usuario!!)

        }
    }

    private fun camposPreenchidos(): Boolean {
        val textoNome = binding.editNomeUsuario.text.toString()
        val textoEmail = binding.editCadastroEmail.text.toString()
        val textoSenha = binding.textInputSenhaCadastro.text.toString()
        val textoCep = binding.editCadastroCep.text.toString()
        val textoMunicipio = binding.editCadastroMunicipio.text.toString()
        val textoEstado = binding.editCadastroUf.text.toString()
        val textoLogradouro = binding.editCadastroLogradouro.text.toString()
        val textoBairro = binding.editCadastroBairro.text.toString()
        val textoConfirmaSenha = binding.textInputConfirmaSenha.text.toString()

        if (textoNome.isEmpty()) {
            binding.editNomeUsuario.setError("Preencha o nome")
            binding.editNomeUsuario.requestFocus()

            return false
        }

        if (textoEmail.isEmpty()) {
            binding.editCadastroEmail.setError("Preencha o E-mail")
            return false
        }

        if (textoSenha.isEmpty()) {
            binding.textInputSenhaCadastro.setError("Preencha a senha")
            return false
        }

        if (textoSenha != textoConfirmaSenha) {
            binding.textInputConfirmaSenha.setError("Suas senhas não são iguais")
            return false
        }

        if (binding.checkBoxTermosServico.isChecked) {
            showToast("Aceite nossos termos de serviço e condições de uso para continuar")
            return false
        }

        if (textoCep.isEmpty()) {
            binding.editCadastroCep.setError("Preencha o CEP")
            return false
        }

        if (textoMunicipio.isEmpty()) {
            binding.editCadastroMunicipio.setError("Preencha o município")
            return false
        }

        if (textoEstado.isEmpty()) {
            binding.editCadastroUf.setError("Preencha o estado")
            return false
        }

        if (textoLogradouro.isEmpty()) {
            binding.editCadastroLogradouro.setError("Preencha o logradouro")
            return false
        }

        if (textoBairro.isEmpty()) {
            binding.editCadastroBairro.setError("Preencha o bairro")
            return false
        }

        return true
    }


    fun cadastrar(usuario: Usuario) {
        binding.progressBarCadastro.visibility = View.VISIBLE
        val autenticacao = Configuracaofirebase.getReferenciaAutenticacao()

        autenticacao.createUserWithEmailAndPassword(
            usuario.email, usuario.senha
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("usuario", usuario)
                }
                startActivity(intent)

                binding.progressBarCadastro.visibility = View.GONE
                showToast("Cadastro com sucesso")
                UsuarioFirebase.atualizarNomeUsuario(usuario.nome)
                finish()

                try {
                    val identificadorUsuario = Base64Custom.codificarBase64(usuario.email)
                    usuario.id = identificadorUsuario
                    usuario.salvar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding.progressBarCadastro.visibility = View.GONE

                val erroExcecao = when (val exception = task.exception) {
                    is FirebaseAuthWeakPasswordException -> "Digite uma senha mais forte!"
                    is FirebaseAuthInvalidCredentialsException -> "Por favor, digite um e-mail válido"
                    is FirebaseAuthUserCollisionException -> "Esta conta já tem cadastro!"
                    else -> "Ao cadastrar usuário: ${exception?.message}"
                }

                showToast("Erro: $erroExcecao")
            }
        }
    }

    fun preencherSpinner() {

        val ocupacoes = arrayOf("Selecione sua ocupação", "Morador", "Catador", "Cooperativa")
        val adapterOcupacao = ArrayAdapter(this, R.layout.simple_spinner_item, ocupacoes)
        adapterOcupacao.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerOcupacao.adapter = adapterOcupacao

        val sexos = arrayOf("Informe seu sexo", "Masculino", "Feminino", "Não quero informar")
        val adapterSexo = ArrayAdapter(this, R.layout.simple_spinner_item, sexos)
        adapterSexo.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerSexo.adapter = adapterSexo

    }

    fun adicionarMascaras() {
        Utils.addMaskToEditText(binding.editCadastroNascimento, "##/##/####")
        Utils.addMaskToEditText(binding.editCadastroTelefone, "(##) #####-####")
        Utils.addMaskToEditText(binding.editCadastroCep, "#####-###")
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
                showToast("CEP inválido ou não encontrado.")
            }
        }
    }

}
