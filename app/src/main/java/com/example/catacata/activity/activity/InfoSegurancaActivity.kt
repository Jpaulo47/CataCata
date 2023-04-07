package com.example.catacata.activity.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.catacata.R
import com.example.catacata.activity.helper.UsuarioFirebase
import com.example.catacata.databinding.ActivityDetalhesInfoBinding
import com.example.catacata.databinding.ActivityInfoSegurancaBinding
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class InfoSegurancaActivity : AppCompatActivity() {

    lateinit var binding: ActivityInfoSegurancaBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoSegurancaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarAlternativa)
        toolbar.title = "Info de Segurança"
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_voltar)
        toolbar.setTitleTextColor(resources.getColor(R.color.verdeIcons))

        //Recuperando dados do usuario no firebaseUser
        val circleImageToolbarAlternativa = findViewById<CircleImageView>(R.id.circleImageToolbar)
        val usuarioPerfil = UsuarioFirebase.getUsuarioAtual()
        val url = usuarioPerfil!!.photoUrl
        if (url != null) {
            Glide.with(this ).load(url).into(circleImageToolbarAlternativa)
        } else {
            circleImageToolbarAlternativa.setImageResource(R.drawable.padrao)
        }

        //Adicionando método de click na imagem de perfil
        circleImageToolbarAlternativa.setOnClickListener{
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }


        val goToDetalhesInfoBinding = Intent(this, DetalhesInfoActivity::class.java)

        //Métodos que aciona evento de click a cada botao
        binding.buttonCapuz.setOnClickListener{
            goToDetalhesInfoBinding.putExtra("name", getString(R.string.capuz))
            goToDetalhesInfoBinding.putExtra("image", R.drawable.capuz_aba_epi)
            goToDetalhesInfoBinding.putExtra("text", getString(R.string.capuz_text))
            goToDetalhesInfoBinding.putExtra("description", getString(R.string.capuz_image))
            startActivity(goToDetalhesInfoBinding)
        }

        binding.buttonOculos.setOnClickListener{
            goToDetalhesInfoBinding.putExtra("name", getString(R.string.oculos))
            goToDetalhesInfoBinding.putExtra("image", R.drawable.oculos_epi)
            goToDetalhesInfoBinding.putExtra("text", getString(R.string.oculos_text))
            goToDetalhesInfoBinding.putExtra("description", getString(R.string.oculos_image))
            startActivity(goToDetalhesInfoBinding)
        }

        binding.buttonProtetorOuvido.setOnClickListener{
            goToDetalhesInfoBinding.putExtra("name", getString(R.string.protetor_ouvido))
            goToDetalhesInfoBinding.putExtra("image", R.drawable.protetor_ouvido_epi)
            goToDetalhesInfoBinding.putExtra("text", getString(R.string.p_ouvido_text))
            goToDetalhesInfoBinding.putExtra("description", getString(R.string.p_ouvido_image))
            startActivity(goToDetalhesInfoBinding)
        }

        binding.buttonMangote.setOnClickListener{
            goToDetalhesInfoBinding.putExtra("name", getString(R.string.mangote))
            goToDetalhesInfoBinding.putExtra("image", R.drawable.mangote_epi)
            goToDetalhesInfoBinding.putExtra("text", getString(R.string.mangote_text))
            goToDetalhesInfoBinding.putExtra("description", getString(R.string.mangote_image))
            startActivity(goToDetalhesInfoBinding)
        }

        binding.buttonLuvas.setOnClickListener{
            goToDetalhesInfoBinding.putExtra("name", getString(R.string.luvas))
            goToDetalhesInfoBinding.putExtra("image", R.drawable.luvas_epi)
            goToDetalhesInfoBinding.putExtra("text", getString(R.string.luvas_text))
            goToDetalhesInfoBinding.putExtra("description", getString(R.string.luvas_image))
            startActivity(goToDetalhesInfoBinding)
        }

        binding.buttonBotina.setOnClickListener{
            goToDetalhesInfoBinding.putExtra("name", getString(R.string.botina))
            goToDetalhesInfoBinding.putExtra("image", R.drawable.botina_epi)
            goToDetalhesInfoBinding.putExtra("text", getString(R.string.botina_text))
            goToDetalhesInfoBinding.putExtra("description", getString(R.string.botina_image))
            startActivity(goToDetalhesInfoBinding)
        }

        binding.buttonCreme.setOnClickListener{
            goToDetalhesInfoBinding.putExtra("name", getString(R.string.creme_protecao))
            goToDetalhesInfoBinding.putExtra("image", R.drawable.download)
            goToDetalhesInfoBinding.putExtra("text", getString(R.string.creme_text))
            goToDetalhesInfoBinding.putExtra("description", getString(R.string.creme_image))
            startActivity(goToDetalhesInfoBinding)
        }
    }

    override fun onSupportNavigateUp(): Boolean { //Método para finalizar activity atual
        finish()
        return false
    }

}