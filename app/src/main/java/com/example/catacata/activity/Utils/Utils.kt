import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Classe com funções úteis para a aplicação.
 * create by João paulo - 28/03/2023
 */
object Utils {

    /**
     * Verifica se há conexão com a internet.
     * @return true se há conexão com a internet, false caso contrário.
     */
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    /**
     * Retorna a data formatada de acordo com o formato passado por parâmetro.
     * @param date A data a ser formatada.
     * @param format O formato da data.
     * @return A data formatada.
     */
    fun formatDate(date: Date, format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * Retorna o tamanho da tela em pixels.
     * @param context O contexto da aplicação.
     * @return Um objeto DisplayMetrics com as dimensões da tela.
     */
    fun getScreenSize(context: Context): DisplayMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    /**
     * Retorna a altura da tela em pixels.
     * @param context O contexto da aplicação.
     * @return A altura da tela em pixels.
     */
    fun getScreenHeight(context: Context): Int {
        return getScreenSize(context).heightPixels
    }

    /**
     * Retorna a largura da tela em pixels.
     * @param context O contexto da aplicação.
     * @return A largura da tela em pixels.
     */
    fun getScreenWidth(context: Context): Int {
        return getScreenSize(context).widthPixels
    }

    /**
     * Converte uma quantidade de pixels em dp.
     * @param context O contexto da aplicação.
     * @param px A quantidade de pixels.
     * @return A quantidade de dp correspondente.
     */
    fun pxToDp(context: Context, px: Int): Int {
        val density = context.resources.displayMetrics.densityDpi.toFloat()
        return (px / (density / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    /**
     * Converte uma quantidade de dp em pixels.
     * @param context O contexto da aplicação.
     * @param dp A quantidade de dp.
     * @return A quantidade de pixels correspondente.
     */
    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    /**
     * Verifica se uma string é um endereço de email válido.
     */
    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Retorna a cor correspondente a um recurso de cor.
     */
    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    /**
     * Retorna o drawable correspondente a um recurso de drawable.
     */
    fun getDrawable(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawableRes)
    }


    /**
     * Converte um valor do tipo Date para String utilizando um formato específico.
     * @param value Valor do tipo Date a ser convertido.
     * @param format Formato da data a ser utilizado na conversão.
     * @return Valor do tipo Date convertido para String.
     */
    fun toString(value: Date?, format: String = "dd/MM/yyyy"): String {
        return if (value != null) {
            SimpleDateFormat(format, Locale.getDefault()).format(value)
        } else {
            ""
        }
    }

    /**
     * Converte um valor do tipo Double para String.
     * @param value Valor do tipo Double a ser convertido.
     * @return Valor do tipo Double convertido para String.
     */
    fun toString(value: Double?): String {
        return value?.toString() ?: ""
    }

    /**
     * Converte um valor do tipo Int para String.
     * @param value Valor do tipo Int a ser convertido.
     * @return Valor do tipo Int convertido para String.
     */
    fun toString(value: Int?): String {
        return value?.toString() ?: ""
    }

    /**
     * Converte um valor do tipo Long para String.
     * @param value Valor do tipo Long a ser convertido.
     * @return Valor do tipo Long convertido para String.
     */
    fun toString(value: Long?): String {
        return value?.toString() ?: ""
    }

    /**
     * Converte um valor do tipo Float para String.
     * @param value Valor do tipo Float a ser convertido.
     * @return Valor do tipo Float convertido para String.
     */
    fun toString(value: Float?): String {
        return value?.toString() ?: ""
    }

    /**
     * Converte um valor do tipo Boolean para String.
     * @param value Valor do tipo Boolean a ser convertido.
     * @return Valor do tipo Boolean convertido para String.
     */
    fun toString(value: Boolean?): String {
        return value?.toString() ?: ""
    }

    /**
     * Converte um valor de qualquer tipo para String.
     * @param value Valor a ser convertido.
     * @return Valor convertido para String.
     */
    fun toString(value: Any?): String {
        return value?.toString() ?: ""
    }

    /**
     * Converte um valor do tipo ByteArray para String utilizando Base64.
     * @param value Valor do tipo ByteArray a ser convertido.
     * @return Valor do tipo ByteArray convertido para String utilizando Base64.
     */
    fun toString(value: ByteArray?): String {
        return value?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getEncoder().encodeToString(it)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        } ?: ""
    }

    /**
     * Converte um valor do tipo Array para String.
     * @param value valor do tipo Array.
     * @return valor convertido para String.
     */
    fun toString(value: Array<*>?): String {
        return value?.joinToString(",") ?: ""
    }

    /**
     * Retorna a data atual do dispositivo.
     * @param format Formato da data a ser retornado (exemplo: "dd/MM/yyyy").
     * @return data atual no formato especificado.
     */
    fun getCurrentDate(format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    /**
     * Retorna a idade do usuário a partir da data de nascimento informada.
     * @param birthDate Data de nascimento do usuário.
     * @return Idade do usuário em anos.
     */
    fun getAgeFromDate(birthDate: Date): Int {
        val now = Calendar.getInstance()
        val birth = Calendar.getInstance()
        birth.time = birthDate
        var age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    /**
    Adiciona uma máscara de formatação em um EditText
    @param editText O EditText que receberá a máscara de formatação
    @param mask A máscara de formatação a ser aplicada. Use "#" para os caracteres que serão substituídos pelo valor digitado pelo usuário.
    Exemplo de uso:
    addMaskToEditText(editText, "###.###.###-##")
    Esse exemplo aplicaria a máscara de CPF no EditText, onde "#" seria substituído pelos valores digitados pelo usuário.
     */
    fun addMaskToEditText(editText: EditText, mask: String) {
        val textWatcher = object : TextWatcher {
            var isUpdating = false
            var oldText = ""

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                if (isUpdating || newText == oldText) {
                    return
                }
                isUpdating = true

                var formattedText = ""
                var i = 0
                for (m in mask) {
                    if (m != '#' && newText.length > oldText.length) {
                        formattedText += m
                        continue
                    }
                    try {
                        formattedText += newText[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }

                editText.setText(formattedText)
                editText.setSelection(formattedText.length)
                oldText = formattedText
                isUpdating = false
            }
        }

        editText.addTextChangedListener(textWatcher)
    }
}
    
