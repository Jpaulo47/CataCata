package com.example.catacata.activity.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.catacata.R
import com.example.catacata.activity.fragments.AgendamentoFragment
import com.example.catacata.activity.fragments.AjudaFragment
import com.example.catacata.activity.fragments.ContatosFragment
import com.example.catacata.activity.fragments.HomeFragment
import com.example.catacata.activity.helper.Configuracaofirebase
import com.example.catacata.activity.helper.UsuarioFirebase
import com.example.catacata.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    private lateinit var autenticacao: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ResourceAsColor", "NonConstantResourceId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // configuração de objetos
        autenticacao = Configuracaofirebase.referenciaAutenticacao!!

        configurarToolbar()
        substituirFragment(HomeFragment())
        configurarBottonNavigation()

        val circleImageImagePerfil = findViewById<CircleImageView>(R.id.circleImageToolbar)
        circleImageImagePerfil.setOnClickListener {
            abrirTelaMeuPerfil()

        }
    }

    private fun configurarToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbarPrincipal)
        toolbar.title = ""
        setSupportActionBar(toolbar)
    }

    private fun configurarBottonNavigation() {
        binding.bottonNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> substituirFragment(HomeFragment())
                R.id.menuContatos -> substituirFragment(ContatosFragment())
                R.id.menuAgendamento -> substituirFragment(AgendamentoFragment())
                R.id.menuAjuda -> substituirFragment(AjudaFragment())
            }
            true
        }
    }

    private fun substituirFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_Layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSair -> {
                deslogarUsuario()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            R.id.menu_editar_perfil -> {
                abrirTelaMeuPerfil()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deslogarUsuario() {
        try {
            autenticacao.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun abrirTelaMeusMateriais(view: View) {
        val intent = Intent(this, MeusMateriaisActivity::class.java)
        startActivity(intent)
    }

    fun abrirTelaInfoSeguranca(view: View) {
        val intent = Intent(this, InfoSegurancaActivity::class.java)
        startActivity(intent)
    }

    private fun abrirTelaMeuPerfil() {
        val intent = Intent(this, PerfilActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        atualizarImagePerfil()
        super.onResume()
    }

    private fun atualizarImagePerfil() {
        val usuarioFirebase = UsuarioFirebase
        val circleImageImagePerfil = findViewById<CircleImageView>(R.id.circleImageToolbar)
        usuarioFirebase.getUriImagemPerfil({ uri ->
            Glide.with(this).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH).into(circleImageImagePerfil)
        }, { e -> circleImageImagePerfil.setImageResource(R.drawable.padrao) })
    }
}