package com.example.catacata.activity.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.catacata.R
import com.example.catacata.activity.helper.Base64Custom
import com.example.catacata.activity.helper.Configuracaofirebase
import com.example.catacata.activity.helper.UsuarioFirebase
import com.example.catacata.activity.model.Usuario
import com.example.catacata.databinding.ActivityCadastroBinding
import com.example.catacata.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.util.*

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
                                Toast.makeText(
                                    this@CadastroActivity,
                                    "Aceite nossos termos de serviço e condições de uso para continuar!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@CadastroActivity,
                                "Suas senhas não são iguais!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@CadastroActivity, "Preencha a senha!", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@CadastroActivity, "Preencha o E-mail!", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@CadastroActivity, "Preencha o nome!", Toast.LENGTH_SHORT
                ).show()
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
                val intent = Intent(this@CadastroActivity, MainActivity::class.java).apply {
                    putExtra("usuario", usuario)
                }
                startActivity(intent)

                binding.progressBarCadastro.visibility = View.GONE
                Toast.makeText(this@CadastroActivity, "Cadastro com sucesso", Toast.LENGTH_SHORT)
                    .show()

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

                Toast.makeText(this@CadastroActivity, "Erro: $erroExcecao", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
