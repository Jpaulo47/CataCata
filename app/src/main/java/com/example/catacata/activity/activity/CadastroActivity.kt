package com.example.catacata.activity.activity

//noinspection SuspiciousImport
import CepApi
import Notificador.Companion.showToast
import Utils
import Validator
//noinspection SuspiciousImport
import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.catacata.activity.helper.Base64Custom
import com.example.catacata.activity.helper.Configuracaofirebase
import com.example.catacata.activity.helper.UsuarioFirebase
import com.example.catacata.activity.model.Usuario
import com.example.catacata.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding


    private var usuario: Usuario? = null
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cadastrarUsuario()
        preencherSpinner()
        adicionarMascaras()
        binding.buscarCep.setOnClickListener {
            buscarCep(binding.editCadastroCep.text.toString())
        }

        binding.imagePerfil.setOnClickListener { openGallery() }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    fun cadastrarUsuario() {

        binding.progressBarCadastro.visibility = ProgressBar.GONE
        binding.buttonCadastrar.setOnClickListener { validarDados() }
    }

    private fun validarDados() {
        val textoNome = binding.editNomeUsuario.text.toString()
        val textoEmail = binding.editCadastroEmail.text.toString()
        val textoSenha = binding.textInputSenhaCadastro.text.toString()
        val textoCep = binding.editCadastroCep.text.toString()
        val textoMunicipio = binding.editCadastroMunicipio.text.toString()
        val textoEstado = binding.editCadastroUf.text.toString()
        val textoLogradouro = binding.editCadastroLogradouro.text.toString()
        val textoBairro = binding.editCadastroBairro.text.toString()
        val textoTelefone = binding.editCadastroTelefone.text.toString()
        val dataNascimento = binding.editCadastroNascimento.text.toString()
        val textoSexo = binding.spinnerSexo.selectedItem.toString()
        val textoOcupacao = binding.spinnerOcupacao.selectedItem.toString()
        val termosUso = binding.checkBoxTermosServico.isChecked

        val validator = Validator()

        validator.addDateValidator(
            binding.editCadastroNascimento,
            "Data de nascimento inválida!",
            true
        )
        validator.addEmailValidator(
            binding.editCadastroEmail,
            "Digite um e-mail no formato: nome@email.com",
            true
        )
        validator.addNameValidator(
            binding.editNomeUsuario,
            "Nome Inválido, informe seu nome e sobrenome!",
            true
        )
        validator.addPhoneNumberValidator(binding.editCadastroTelefone, "Campo obrigatório", true)
        validator.addRequiredFieldValidator(binding.editCadastroCep, "Campo obrigatório")
        validator.addRequiredFieldValidator(binding.editCadastroBairro, "Campo obrigatório")
        validator.addRequiredFieldValidator(binding.editCadastroMunicipio, "Campo obrigatório")
        validator.addRequiredFieldValidator(binding.editCadastroUf, "Campo obrigatório")
        validator.addRequiredFieldValidator(binding.editCadastroLogradouro, "Campo obrigatório")
        validator.addSpinnerValidator(binding.spinnerOcupacao, this, "Informe sua ocupação!")
        validator.addSpinnerValidator(binding.spinnerSexo, this, "Informe seu sexo!")
        validator.addPasswordValidator(binding.textInputSenhaCadastro, "Senha inválida!", true)
        validator.addConfirmPasswordValidator(
            binding.textInputConfirmaSenha,
            binding.textInputSenhaCadastro,
            "As senhas tem que ser iguais!"
        )

        if (validator.validateFields()) {
            usuario = Usuario()
            usuario?.nome = textoNome
            usuario?.email = textoEmail
            usuario?.senha = textoSenha
            usuario?.cep = textoCep
            usuario?.municipio = textoMunicipio
            usuario?.bairro = textoBairro
            usuario?.logradouro = textoLogradouro
            usuario?.estado = textoEstado
            usuario?.telefone = textoTelefone
            usuario?.dataNascimento = Utils.stringToDate(dataNascimento)
            usuario?.sexo = textoSexo
            usuario?.ocupacao = textoOcupacao
            usuario?.isTermosdeUso = termosUso

            if (!binding.checkBoxTermosServico.isChecked) {
                showToast("É necessario aceitar os termos de uso!")
                return
            }

            imageUri?.let { saveImageToFirebase(it, textoEmail) }

            binding.progressBarCadastro.visibility = View.VISIBLE
            binding.buttonCadastrar.setText("")
            cadastrar(usuario!!)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data

            Glide.with(this)
                .load(imageUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(binding.imagePerfil)
        }
    }

    private fun saveImageToFirebase(imageUri: Uri, email: String) {

        val storageRef = FirebaseStorage.getInstance().getReference("imagens/perfil/$email.jpeg/perfil.jpeg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(this, "Imagem salva com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show()
            }
    }

    fun cadastrar(usuario: Usuario) {
        binding.progressBarCadastro.visibility = View.VISIBLE
        val autenticacao = Configuracaofirebase.referenciaAutenticacao

        autenticacao!!.createUserWithEmailAndPassword(
            usuario.email.toString(), usuario.senha.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("usuario", usuario)
                }
                startActivity(intent)

                binding.progressBarCadastro.visibility = View.GONE
                showToast("Cadastro com sucesso")
                UsuarioFirebase.atualizarNomeUsuario(usuario.nome.toString())
                finish()

                try {
                    val identificadorUsuario = Base64Custom.codificarBase64(usuario.email.toString())
                    usuario.id = identificadorUsuario
                    usuario.salvar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding.progressBarCadastro.visibility = View.GONE

                val erroExcecao = when (val exception = task.exception) {
                    is FirebaseAuthWeakPasswordException -> "Digite uma senha mais forte!"
                    is FirebaseAuthInvalidCredentialsException -> "Por favor, digite um e-mail válido"
                    is FirebaseAuthUserCollisionException -> "Esta conta já tem cadastro!"
                    else -> "Ao cadastrar usuário: ${exception?.message}"
                }

                showToast("Erro: $erroExcecao")
            }
        }
    }

    fun preencherSpinner() {

        val ocupacoes = arrayOf("Selecione sua ocupação", "Morador", "Catador", "Cooperativa")
        val adapterOcupacao = ArrayAdapter(this, R.layout.simple_spinner_item, ocupacoes)
        adapterOcupacao.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerOcupacao.adapter = adapterOcupacao

        val sexos = arrayOf("Informe seu sexo", "Masculino", "Feminino", "Não quero informar")
        val adapterSexo = ArrayAdapter(this, R.layout.simple_spinner_item, sexos)
        adapterSexo.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerSexo.adapter = adapterSexo

    }

    fun adicionarMascaras() {
        Utils.addMaskToEditText(binding.editCadastroNascimento, "##/##/####")
        Utils.addMaskToEditText(binding.editCadastroTelefone, "(##) #####-####")
        Utils.addMaskToEditText(binding.editCadastroCep, "#####-###")
    }

    private fun buscarCep(cep: String) {
        lifecycleScope.launch {
            val endereco = CepApi.buscaCep(cep)

            if (endereco != null) {
                binding.editCadastroLogradouro.setText(endereco.logradouro)
                binding.editCadastroBairro.setText(endereco.bairro)
                binding.editCadastroMunicipio.setText(endereco.cidade)
                binding.editCadastroUf.setText(endereco.estado)
            } else {
                showToast("CEP inválido ou não encontrado.")
            }
        }
    }

}
