package com.example.catacata.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.catacata.R;
import com.example.catacata.activity.fragments.HomeFragment;
import com.example.catacata.activity.helper.Configuracaofirebase;
import com.example.catacata.activity.helper.UsuarioFirebase;
import com.example.catacata.activity.model.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    private static final int SELECAO_GALERIA = 200;
    private CircleImageView circleImageImagePerfil;
    private StorageReference storageReference;
    private String identificadorUsuario;
    private Usuario usuarioLogado;

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPerfil);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.verdeIcons));

        //Inicializar componentes
        ImageButton imageButtonAlterarFoto = findViewById(R.id.imageAlterarFoto);
        circleImageImagePerfil = findViewById(R.id.circleImageToolbar);
        TextView textNomeUsuario = findViewById(R.id.textNomeUsuarioPerfil);
        TextView textEmailUsuario = findViewById(R.id.textEmailUsuarioLogado);
        storageReference = Configuracaofirebase.getFirebaseStorage();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        //Recuperando dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuarioPerfil.getPhotoUrl();
        if ( url != null ){
            Glide.with(PerfilActivity.this).load( url ).into( circleImageImagePerfil );

        }else {
            circleImageImagePerfil.setImageResource(R.drawable.padrao);
        }

        textNomeUsuario.setText( usuarioPerfil.getDisplayName() );
        textEmailUsuario.setText( usuarioPerfil.getEmail() );

        //Método para alterar foto do usuario
        imageButtonAlterarFoto.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (i.resolveActivity(getPackageManager()) != null )
                startActivityForResult(i, SELECAO_GALERIA);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){
            Bitmap imagem = null;

            try {

                //Seleção da imagem da galeria
                if (requestCode == SELECAO_GALERIA) {
                    assert data != null;
                    Uri localImagemSelecionada = data.getData();
                    imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                }

                //para Implementar botao de atualizar nome: Aula 231
                //Carregar imagem ao ser escolhida
                if ( imagem != null ){
                    circleImageImagePerfil.setImageBitmap( imagem );

                    //recupera dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //salvar imagem no firebase
                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(identificadorUsuario + ".jpeg")
                            .child("perfil.jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(e -> {
                        Toast.makeText(PerfilActivity.this, "Erro ao fazer upload da imagem",
                                Toast.LENGTH_SHORT).show();

                    }).addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(PerfilActivity.this, "Sucesso ao fazer upload da imagem",
                                Toast.LENGTH_SHORT).show();

                       imagemRef.getDownloadUrl().addOnSuccessListener(this::atualizarFotoUsuario);

                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //Método responsavel por atualizar foto do usuario
    public void atualizarFotoUsuario(Uri url){
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario( url );
        if ( retorno ){
            usuarioLogado.setCaminhoFoto( url.toString() );
            usuarioLogado.atualizar();

            Toast.makeText(PerfilActivity.this,
                    "Sucesso ao alterar sua foto",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {//Método para finalizar activity atual
        finish();
        return false;
    }
}