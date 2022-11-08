package com.example.catacata.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.catacata.R;
import com.example.catacata.activity.helper.Base64Custom;
import com.example.catacata.activity.helper.Configuracaofirebase;
import com.example.catacata.activity.helper.UsuarioFirebase;
import com.example.catacata.activity.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail;
    private TextInputEditText campoConfirmaSenha, campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressBarCadastro;
    private CheckBox checkBoxTermosServico;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();
        //Cadastrar usuario
        progressBarCadastro.setVisibility(View.GONE);
        botaoCadastrar.setOnClickListener(view -> {

            //Verificação se os campos foram preenchidos
            String textoNome = campoNome.getText().toString();
            String textoEmail = campoEmail.getText().toString();
            String textoSenha = Objects.requireNonNull(campoSenha.getText()).toString();
            String textoConfirmaSenha = Objects.requireNonNull(campoConfirmaSenha.getText()).toString();

            if ( !textoNome.isEmpty()){
                if ( !textoEmail.isEmpty()){
                    if ( !textoSenha.isEmpty()){
                        if ( textoSenha.equals(textoConfirmaSenha)){
                            if (  checkBoxTermosServico.isChecked()){

                                usuario = new Usuario();
                                usuario.setNome( textoNome );
                                usuario.setEmail( textoEmail );
                                usuario.setSenha( textoSenha );
                                cadastrar( usuario );

                            }else {
                                Toast.makeText(CadastroActivity.this,
                                        "Aceite nossos termos de serviço e condições de uso para continuar!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(CadastroActivity.this,
                                    "Suas senhas não são iguais!",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(CadastroActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha o nome!",
                        Toast.LENGTH_SHORT).show();
            }

        });

        //CONFIGURAÇÃO DO SPINNER
        Spinner ocupacao = (Spinner) findViewById(R.id.spinnerOcupacao);
        // Cria um ArrayAdapter usando o array de strings e um layout de spinner padrão
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Especifique o layout a ser usado quando a lista de opções aparecer
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Aplica o adaptador no spinner
        ocupacao.setAdapter(adapter);

    }

    public void cadastrar( Usuario usuario ){

        progressBarCadastro.setVisibility(View.VISIBLE);
        FirebaseAuth autenticacao = Configuracaofirebase.getReferenciaAutenticacao();
        //Tratamento de erros no cadastro
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                this,
                task -> {
                    if (task.isSuccessful()){

                        Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
                        startActivity( intent );

                        progressBarCadastro.setVisibility(View.GONE);
                        Toast.makeText(CadastroActivity.this,
                                "Cadastro com sucesso",
                                Toast.LENGTH_SHORT).show();

                        UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );
                        finish();

                        try {

                            String identificadorUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                            usuario.setId( identificadorUsuario );
                            usuario.salvar();

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else {
                        progressBarCadastro.setVisibility( View.GONE);

                        String erroExcecao;
                        try {
                            throw Objects.requireNonNull(task.getException());
                        }catch (FirebaseAuthWeakPasswordException e){
                            erroExcecao = "Digite uma senha mais forte!";
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            erroExcecao = "por favor, digite um e-mail válido";
                        }catch (FirebaseAuthUserCollisionException e) {
                            erroExcecao = "Esta conta já tem cadastro!";
                        }catch (Exception e){
                            erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(CadastroActivity.this,
                                "Erro: " + erroExcecao,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void inicializarComponentes(){

        campoNome = findViewById(R.id.editNomeUsuario);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoConfirmaSenha = findViewById(R.id.textInputConfirmaSenha);
        campoSenha = findViewById(R.id.textInputSenhaCadastro);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);
        progressBarCadastro = findViewById(R.id.progressBarCadastro);
        checkBoxTermosServico = findViewById(R.id.checkBoxTermosServico);

        campoNome.requestFocus();

    }

}