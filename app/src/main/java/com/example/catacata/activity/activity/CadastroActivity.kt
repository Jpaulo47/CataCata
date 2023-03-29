package com.example.catacata.activity.activity

import Notificador.Companion.showToast
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.catacata.activity.helper.Base64Custom
import com.example.catacata.activity.helper.Configuracaofirebase
import com.example.catacata.activity.helper.UsuarioFirebase
import com.example.catacata.activity.model.Usuario
import com.example.catacata.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

/**
 * Classe Facilitadora para criar Notificações no display do usuário
 * ela é um wrapper das notificações do Android e um facade para os mesmos
 */

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    private var usuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cadastrarUsuario()

    }

    fun cadastrarUsuario() {
        //Cadastrar usuario
        binding.progressBarCadastro.visibility = ProgressBar.GONE
        binding.buttonCadastrar.setOnClickListener {
            //Verificação se os campos foram preenchidos
            val textoNome = binding.editNomeUsuario.text.toString()
            val textoEmail = binding.editCadastroEmail.text.toString()
            val textoSenha = binding.textInputSenhaCadastro.text.toString()
            val textoConfirmaSenha = binding.textInputConfirmaSenha.text.toString()

            if (textoNome.isNotEmpty()) {
                if (textoEmail.isNotEmpty()) {
                    if (textoSenha.isNotEmpty()) {
                        if (textoSenha == textoConfirmaSenha) {
                            if (binding.checkBoxTermosServico.isChecked) {

                                usuario = Usuario()
                                usuario?.nome = textoNome
                                usuario?.email = textoEmail
                                usuario?.senha = textoSenha
                                cadastrar(usuario!!)

                            } else {
                                showToast("Aceite nossos termos de serviço e condições de uso para continu")
                            }
                        } else {
                            showToast("Suas senhas não são iguais!")
                        }
                    } else {
                        showToast("Preencha a senha!")
                    }
                } else {
                    showToast("Preencha o E-mail!")
                }
            } else {
                showToast("Preencha o nome!")
            }
        }
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
}
