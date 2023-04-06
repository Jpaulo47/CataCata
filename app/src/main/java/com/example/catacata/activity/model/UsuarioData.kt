import java.util.Date

data class Usuario(
    var nome: String? = null,
    var email: String? = null,
    var telefone: String? = null,
    var dataNascimento: Date? = null,
    var cep: String? = null,
    var estado: String? = null,
    var municipio: String? = null,
    var bairro: String? = null,
    var logradouro: String? = null,
    var sexo: String? = null,
    var ocupacao: String? = null,

)
