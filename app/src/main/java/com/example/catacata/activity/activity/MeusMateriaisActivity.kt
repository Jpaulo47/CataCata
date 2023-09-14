package com.example.catacata.activity.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.catacata.R
import com.example.catacata.activity.helper.UserFirebase
import com.example.catacata.databinding.ActivityMeusMateriaisBinding
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class MeusMateriaisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeusMateriaisBinding
    private lateinit var circleImageToolbarAlternativa: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeusMateriaisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbarAlternativa)
        toolbar.title = "Meus Materiais"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_voltar)
        toolbar.setTitleTextColor(resources.getColor(R.color.verdeIcons))

        circleImageToolbarAlternativa = findViewById(R.id.circleImageToolbar)

        circleImageToolbarAlternativa.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        val usuarioPerfil: FirebaseUser? = UserFirebase.getUsuarioAtual()
        val url: Uri? = usuarioPerfil?.photoUrl
        if (url != null) {
            Glide.with(this).load(url).into(circleImageToolbarAlternativa)
        } else {
            circleImageToolbarAlternativa.setImageResource(R.drawable.padrao)
        }
    }

    override fun onSupportNavigateUp(): Boolean { // método para finalizar activity atual
        finish()
        return false
    }

    override fun onResume() {
        super.onResume()

        if (!binding.switch1.isChecked){
            binding.textField.error = "fhfdshfd hgdfshgdfhdgt gdjgtfdjgfjgf gfjgfdjgf fgjfg jgfjgfj td jfgd jgfj "
        }else{
            binding.textField.error == null
        }
    }
}
