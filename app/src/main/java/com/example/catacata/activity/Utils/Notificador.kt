import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.catacata.activity.Utils.MyApp
import com.google.android.material.snackbar.Snackbar

/**
 * Classe Facilitadora para criar Notificações no display do usuário
 * create by João paulo - 28/03/2023
 */

class Notificador {

    companion object {

        private lateinit var applicationContext: Context

        fun showToast(mensagem: String, duracao: Int = Toast.LENGTH_SHORT, context: Context? = null) {
            context?.let {
                Toast.makeText(it, mensagem, duracao).show()
            } ?: run {
                // caso o contexto seja nulo, exibir o Toast usando o contexto da aplicação
                Toast.makeText(getContext(), mensagem, duracao).show()
            }
        }

        private fun getContext(): Context {
            return MyApp.getInstance().applicationContext
        }

        fun initialize(context: Context) {
            applicationContext = context.applicationContext
        }

        fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(context, message, duration).show()
        }

        fun showToastLong(context: Context, message: String) {
            showToast(context, message, Toast.LENGTH_LONG)
        }

        fun showToast(context: Context, resId: Int, duration: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(context, resId, duration).show()
        }

        fun showToastLong(context: Context, resId: Int) {
            showToast(context, resId, Toast.LENGTH_LONG)
        }

        fun showToast(context: Context, message: String) {
            showToast(context, message, Toast.LENGTH_SHORT)
        }

        fun showToast(context: Context, resId: Int, vararg args: Any) {
            showToast(context, context.getString(resId, *args))
        }

        fun showDialog(context: Context, title: String, message: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton(android.R.string.ok, null)
            builder.show()
        }

        fun showSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
            Snackbar.make(view, message, duration).show()
        }

        fun showConfirmationDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonLabel: String,
            negativeButtonLabel: String,
            onPositiveButtonClick: (() -> Unit)? = null,
            onNegativeButtonClick: (() -> Unit)? = null
        ) {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonLabel) { _, _ ->
                    onPositiveButtonClick?.invoke()
                }
                .setNegativeButton(negativeButtonLabel) { _, _ ->
                    onNegativeButtonClick?.invoke()
                }
                .show()
        }
    }
}
