package com.example.catacata.activity.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.catacata.R;
import com.example.catacata.activity.fragments.AgendamentoFragment;
import com.example.catacata.activity.fragments.AjudaFragment;
import com.example.catacata.activity.fragments.ContatosFragment;
import com.example.catacata.activity.fragments.HomeFragment;
import com.example.catacata.activity.helper.Configuracaofirebase;
import com.example.catacata.activity.helper.UsuarioFirebase;
import com.example.catacata.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    ActivityMainBinding binding;

    @SuppressLint({"ResourceAsColor", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_CataCata);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //configuração de objetos
        autenticacao = Configuracaofirebase.getReferenciaAutenticacao();
        CircleImageView circleImageImagePerfil = findViewById(R.id.circleImageToolbar);

        //Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        substituirFragment(new HomeFragment());

        //Configuração do botton navigation
        binding.bottonNavigation.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.menuHome:
                    substituirFragment(new HomeFragment());
                    break;
                case R.id.menuContatos:
                    substituirFragment(new ContatosFragment());
                    break;
                case R.id.menuAgendamento:
                    substituirFragment(new AgendamentoFragment());
                    break;
                case R.id.menuAjuda:
                    substituirFragment(new AjudaFragment());
                    break;

            }

            return true;
        });

        //Adicionando método de click na imagem de perfil
        binding.include.circleImageToolbar.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity( intent );
        });

        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuarioPerfil.getPhotoUrl();
        if ( url != null ){
            Glide.with(MainActivity.this).load( url ).into(circleImageImagePerfil);

        }else {
            circleImageImagePerfil.setImageResource(R.drawable.padrao);
        }

    }

    private void substituirFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_Layout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.menuInfoApp:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario(){
        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void abrirTelaMeusMateriais(View view){
        Intent intent = new Intent(MainActivity.this, MeusMateriaisActivity.class);
        startActivity( intent );
    }

    public void abrirTelaInfoSeguranca(View view){
        Intent intent = new Intent(MainActivity.this, InfoSegurancaActivity.class);
        startActivity( intent );
    }
}