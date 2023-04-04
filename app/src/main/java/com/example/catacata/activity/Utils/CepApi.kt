import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class CepApi {
    companion object {
        private const val BASE_URL = "https://viacep.com.br/ws/"

        suspend fun buscaCep(cep: String): Endereco? {
            return withContext(Dispatchers.IO) {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("$BASE_URL$cep/json/")
                    .build()

                return@withContext try {
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        throw Exception("Erro ao buscar o CEP: ${response.code}")
                    }

                    val jsonString = response.body?.string() ?: throw Exception("Resposta vazia")
                    val json = JSONObject(jsonString)

                    if (json.has("erro")) {
                        throw Exception("CEP não encontrado")
                    }

                    Endereco(
                        logradouro = json.optString("logradouro"),
                        bairro = json.optString("bairro"),
                        cidade = json.optString("localidade"),
                        estado = json.optString("uf")
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

    }
}

data class Endereco(
    val logradouro: String,
    val bairro: String,
    val cidade: String,
    val estado: String
)
