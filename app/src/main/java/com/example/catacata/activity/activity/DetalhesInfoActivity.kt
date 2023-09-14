package com.example.catacata.activity.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.catacata.R
import com.example.catacata.activity.helper.UserFirebase
import com.example.catacata.databinding.ActivityDetalhesInfoBinding
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class DetalhesInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetalhesInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarAlternativa)
        toolbar.title = "Info de Segurança"
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_voltar)
        toolbar.setTitleTextColor(resources.getColor(R.color.verdeIcons))

        //Recuperando dados do usuario no firebaseUser
        val circleImageToolbarAlternativa = findViewById<CircleImageView>(R.id.circleImageToolbar)
        val usuarioPerfil = UserFirebase.getUsuarioAtual()
        val url = usuarioPerfil!!.photoUrl
        if (url != null) {
            Glide.with(this).load(url).into(circleImageToolbarAlternativa)
        } else {
            circleImageToolbarAlternativa.setImageResource(R.drawable.padrao)
        }

        //Adicionando método de click na imagem de perfil
        circleImageToolbarAlternativa.setOnClickListener{
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }

        //Configurando button para substituição do conteudo da tela
        val data = intent.extras

        binding.textEpiNome.text = data?.getString("name")
        binding.textEpiDescricao.text = data?.getString("text")
        data?.getInt("image")?.let{ binding.imageEpiDetalhes.setImageResource(it)}
        binding.imageEpiDetalhes.contentDescription = data?.getString("description")
    }

    override fun onSupportNavigateUp(): Boolean { //Método para finalizar activity atual
        finish()
        return false
    }

}