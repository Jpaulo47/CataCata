package com.example.catacata.activity.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.catacata.R;
import com.example.catacata.activity.helper.Configuracaofirebase;
import com.example.catacata.activity.model.Usuario;
import com.example.catacata.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail;
    private TextView loginGoogle, textRecuperarSenha;
    private TextInputEditText campoSenha;
    private Button botaoLogin;
    private ProgressBar progressBar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    ActivityLoginBinding binding;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_CataCata);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        autenticacao = FirebaseAuth.getInstance();
        verificarUsuarioLogado();
        inicializarComponentes();

        //TODO: Ver método para colocar chave no gradle do projeto
        //Método para login com conta google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("778403556608-i66i8ltmjaik8ipseqg2tn6an5bcgmlh.apps.googleusercontent.com")//Token google cloud
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Fazer login do usuario
        progressBar.setVisibility( View.INVISIBLE );
        botaoLogin.setOnClickListener(view -> {

            String textoEmail = campoEmail.getText().toString();
            String textoSenha = Objects.requireNonNull(campoSenha.getText()).toString();

            if ( !textoEmail.isEmpty()){
                if ( !textoSenha.isEmpty()){

                    usuario = new Usuario();
                    usuario.setEmail( textoEmail );
                    usuario.setSenha( textoSenha );
                    validarLogin( usuario );

                }else {

                    Toast.makeText(LoginActivity.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(LoginActivity.this,
                        "Preencha o e-mail!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //ativação do textview "login com google"
        loginGoogle.setOnClickListener(view -> {
            signiIn();

        });

        //ativação do TextView "Recuperar Senha"
        textRecuperarSenha.setOnClickListener(view -> {
            recuperarSenha();
        });

    }

    //Método para recuperar senha
    private void recuperarSenha(){

        String textoEmail = campoEmail.getText().toString();
        if ( textoEmail.isEmpty()){
            Toast.makeText(LoginActivity.this,
                    "Preencha o campo e-mail para Recuperar sua senha!",
                    Toast.LENGTH_SHORT).show();
        }else {
            enviarEmail(textoEmail);
        }

    }

    //Método que envia email para recuperação de senha
    private void enviarEmail(String textoEmail){

        autenticacao.sendPasswordResetEmail(textoEmail).addOnSuccessListener(unused -> {
            Toast.makeText(getBaseContext(),"Enviamos um Link de redefinição de senha para o seu e-mail.",
                    Toast.LENGTH_LONG).show();

        }).addOnFailureListener(e -> {
            Toast.makeText(getBaseContext(),"Erro ao enviar e-mail",
                    Toast.LENGTH_LONG).show();
        });
    }

    //Método para abrir tela de login com conta google
    private void signiIn(){
        Intent intent = googleSignInClient.getSignInIntent();
       //startActivityForResult(intent, 1);
        abreActivity.launch(intent);
    }

    //Método para abrir tela de login com conta google
    ActivityResultLauncher<Intent> abreActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent( intent );//Método para ver conta que está logada no aparelho.
                    try {
                        GoogleSignInAccount conta = task.getResult(ApiException.class);
                        loginComGoogle(conta.getIdToken());
                    }catch (ApiException exception){
                        Toast.makeText(LoginActivity.this,
                                "Nenhum usuário Google logado no aparelho",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    //Método para login com conta google
    private void loginComGoogle(String token){
        AuthCredential credencial = GoogleAuthProvider.getCredential(token, null);
        autenticacao.signInWithCredential( credencial ).addOnCompleteListener(this, task -> {

            if (task.isSuccessful()){
                Toast.makeText(LoginActivity.this,
                        "Login com Google efetuado com sucesso ",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }else{
                Toast.makeText(LoginActivity.this,
                        "Erro ao efetuar login com Google",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1 ){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent( intent );//Método para ver conta que está logada no aparelho.
            try {
                GoogleSignInAccount conta = task.getResult(ApiException.class);
                loginComGoogle(conta.getIdToken());
            }catch (ApiException exception){
                Toast.makeText(LoginActivity.this,
                        "Nenhum usuário Google logado no aparelho",
                        Toast.LENGTH_LONG).show();
                Log.d("Erro", exception.toString());
            }
        }
    }

    public void verificarUsuarioLogado(){
        autenticacao = Configuracaofirebase.getReferenciaAutenticacao();
         //autenticacao.signOut();//Deslogar usuário
        if ( autenticacao.getCurrentUser() != null ){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void validarLogin( Usuario usuario){

        progressBar.setVisibility( View.VISIBLE);
        autenticacao = Configuracaofirebase.getReferenciaAutenticacao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(task -> {

            if ( task.isSuccessful() ){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }else {

                String erroExcecao = "";
                try {
                    throw Objects.requireNonNull(task.getException());
                }catch (FirebaseAuthInvalidCredentialsException e) {
                    erroExcecao = "E-mail ou senha está incorreto";
                }catch (FirebaseAuthInvalidUserException e){
                    erroExcecao = "Usuário não está cadastrado.";
                }catch (Exception e){
                    erroExcecao = "Erro ao logar usuário: " + e.getMessage();
                    e.printStackTrace();
                }

                Toast.makeText(LoginActivity.this,
                        "Erro ao fazer login",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility( View.INVISIBLE);
            }
        });

    }

    public void abrirCadastro(View view){
        Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(i);
    }

    public void inicializarComponentes(){

        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.textInputSenhaLogin);
        botaoLogin = findViewById(R.id.buttonEntrar);
        progressBar = findViewById(R.id.progressBarLogin);
        loginGoogle = findViewById(R.id.textLoginGoogle);
        textRecuperarSenha = findViewById(R.id.textRecuperarSenha);

        campoEmail.requestFocus();
    }
}
