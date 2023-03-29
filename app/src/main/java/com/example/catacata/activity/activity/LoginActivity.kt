package com.example.catacata.activity.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.catacata.R
import com.example.catacata.activity.helper.Configuracaofirebase
import com.example.catacata.activity.model.Usuario
import com.example.catacata.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import java.util.Objects

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

        //TODO: Ver método para colocar chave no gradle do projeto
        //Método para login com conta google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("778403556608-i66i8ltmjaik8ipseqg2tn6an5bcgmlh.apps.googleusercontent.com") //Token google cloud
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Fazer login do usuario
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
                    Toast.makeText(
                        this@LoginActivity,
                        "Preencha a senha!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //Ir para tela de cadastro
        binding.textCadastrar.setOnClickListener {
            startActivity(Intent(this@LoginActivity, CadastroActivity::class.java))
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
                Toast.makeText(
                    this@LoginActivity,
                    "Nenhum usuário Google logado no aparelho",
                    Toast.LENGTH_SHORT
                ).show()
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
                        this@LoginActivity,
                        "Login efetuado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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
                    Toast.makeText(
                        this@LoginActivity,
                        erroExcecao,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //Método para login com conta Google
    private fun loginComGoogle(idToken: String?) {
        val credencial: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        autenticacao.signInWithCredential(credencial).addOnSuccessListener(this) {

            val user = autenticacao.currentUser
            binding.progressBarLogin.visibility = View.INVISIBLE
            Toast.makeText(
                this@LoginActivity, "Login efetuado com sucesso!", Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }.addOnFailureListener(this) { e ->
            binding.progressBarLogin.visibility = View.INVISIBLE
            Toast.makeText(
                this@LoginActivity, "Erro ao efetuar login: ${e.message}", Toast.LENGTH_SHORT
            ).show()
        }
    }

    //Método para verificar se o usuário está logado
    private fun verificarUsuarioLogado() {
        if (autenticacao.currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}

