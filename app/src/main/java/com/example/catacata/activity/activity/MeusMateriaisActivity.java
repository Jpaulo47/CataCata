package com.example.catacata.activity.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.catacata.R;
import com.example.catacata.activity.helper.UsuarioFirebase;
import com.example.catacata.databinding.ActivityMeusMateriaisBinding;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeusMateriaisActivity extends AppCompatActivity {

    ActivityMeusMateriaisBinding binding;
    private CircleImageView circleImageToolbarAlternativa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeusMateriaisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbarAlternativa);
        toolbar.setTitle("Meus Materiais");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.verdeIcons));

        //inicializando componentes
        circleImageToolbarAlternativa = findViewById(R.id.circleImageToolbar);

        //Adicionando método de click na imagem de perfil
        circleImageToolbarAlternativa.setOnClickListener(view -> {

            Intent intent = new Intent(MeusMateriaisActivity.this, PerfilActivity.class);
            startActivity( intent );

        });

        //Recuperando dados do usuario no firebaseUser
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuarioPerfil.getPhotoUrl();
        if ( url != null ){
            Glide.with(MeusMateriaisActivity.this).load( url ).into( circleImageToolbarAlternativa );

        }else {
            circleImageToolbarAlternativa.setImageResource(R.drawable.padrao);
        }
    }



    @Override
    public boolean onSupportNavigateUp() {//Método para finalizar activity atual
        finish();
        return false;
    }
}