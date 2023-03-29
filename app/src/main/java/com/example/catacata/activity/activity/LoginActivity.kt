package com.example.catacata.activity.activity

import Keys
import Notificador
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.catacata.R
import com.example.catacata.activity.model.Usuario
import com.example.catacata.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var usuario: Usuario
    private lateinit var autenticacao: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CataCata)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        autenticacao = FirebaseAuth.getInstance()
        verificarUsuarioLogado()

        //Método para login com conta google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Keys.GOOGLE_SIGNIN_API_KEY) //Token google cloud
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.progressBarLogin.visibility = View.INVISIBLE
        binding.buttonEntrar.setOnClickListener {
            val textoEmail = binding.editLoginEmail.text.toString()
            val textoSenha = Objects.requireNonNull(binding.textInputSenhaLogin.text).toString()
            if (textoEmail.isNotEmpty()) {
                if (textoSenha.isNotEmpty()) {
                    usuario = Usuario()
                    usuario.email = textoEmail
                    usuario.senha = textoSenha
                    validarLogin(usuario)
                } else {
                    Notificador.showToast("Preencha a senha!")
                }
            } else {
                Notificador.showToast("Preencha o email!")
            }
        }

        //Ir para tela de cadastro
        binding.textCadastrar.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        //Login com conta Google
        binding.textLoginGoogle.setOnClickListener {
            binding.progressBarLogin.visibility = View.VISIBLE
            val signInIntent = googleSignInClient.signInIntent
            abreActivity.launch(signInIntent)
        }
    }

    private val abreActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val conta = task.getResult(ApiException::class.java)
                loginComGoogle(conta.idToken)
            } catch (e: ApiException) {
                Notificador.showToast("Nenhum usuário Google logado no aparelho")
            }
        }
    }

    //Método para validar o login do usuário
    private fun validarLogin(usuario: Usuario) {
        binding.progressBarLogin.visibility = View.VISIBLE
        autenticacao.signInWithEmailAndPassword(usuario.email, usuario.senha)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    binding.progressBarLogin.visibility = View.VISIBLE
                    Toast.makeText(
                        this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                } else {
                    binding.progressBarLogin.visibility = View.INVISIBLE
                    val erroExcecao = try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        "Usuário não cadastrado."
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        "E-mail e/ou senha não correspondem a um usuário cadastrado."
                    } catch (e: Exception) {
                        "Erro ao efetuar login: ${e.message}"
                    }
                    Notificador.showToast(erroExcecao)
                }
            }
    }

    private fun loginComGoogle(idToken: String?) {
        val credencial: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        autenticacao.signInWithCredential(credencial).addOnSuccessListener(this) {

            val user = autenticacao.currentUser
            binding.progressBarLogin.visibility = View.INVISIBLE
            Notificador.showToast("Login efetuado com sucesso!")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener(this) { e ->
            binding.progressBarLogin.visibility = View.INVISIBLE
            Notificador.showToast("Erro ao efetuar login: ${e.message}")
        }
    }

    //Método para verificar se o usuário está logado
    private fun verificarUsuarioLogado() {
        if (autenticacao.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

