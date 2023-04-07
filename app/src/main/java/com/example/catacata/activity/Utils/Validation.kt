import android.content.Context
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import java.util.*
import java.util.regex.Pattern

/**
 * Uma classe de validação que pode ser usada para validar campos de entrada, incluindo CPF,
 * data de nascimento, e-mail, nome e número de telefone.
 * create by João paulo - 28/03/2023
 */

class Validator {
    private val mValidators = mutableMapOf<Any, FieldValidator>()

    interface FieldValidator {
        fun validateField(): Boolean
        fun getErrorMessage(): String
    }

    fun addFieldValidator(field: Any, validator: FieldValidator) {
        mValidators[field] = validator
    }

    fun validateFields(): Boolean {
        var allFieldsValid = true
        mValidators.forEach { (field, validator) ->
            if (!validator.validateField()) {
                allFieldsValid = false
                if (field is EditText) {
                    field.error = validator.getErrorMessage()
                }
            }
        }
        return allFieldsValid
    }

    inner class RequiredFieldValidator(
        private val field: EditText,
        private val errorMessage: String,
        private val isRequired: Boolean = true
    ) : FieldValidator {
        override fun validateField(): Boolean {
            return if (isRequired) {
                field.text.isNotBlank()
            } else {
                true
            }
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    inner class DateValidator(
        private val field: EditText,
        private val errorMessage: String,
        private val isRequired: Boolean
    ) : FieldValidator {
        override fun validateField(): Boolean {
            if (isRequired && field.text.isBlank()) {
                return false
            }
            val date = field.text.toString()
            val regex = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}\$")
            if (!regex.matcher(date).matches()) {
                return false
            }
            val day = date.substring(0, 2).toInt()
            val month = date.substring(3, 5).toInt()
            val year = date.substring(6).toInt()
            val calendar = Calendar.getInstance()
            calendar.isLenient = false
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.YEAR, year)
            return try {
                calendar.time
                day == calendar.get(Calendar.DAY_OF_MONTH) &&
                        month == calendar.get(Calendar.MONTH) + 1 &&
                        calendar.get(Calendar.YEAR) >= Calendar.getInstance().get(Calendar.YEAR) - 130 &&
                        calendar.get(Calendar.YEAR) <= Calendar.getInstance().get(Calendar.YEAR)
            } catch (e: IllegalArgumentException) {
                false
            }
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    inner class EmailValidator(
        private val field: EditText,
        private val errorMessage: String,
        private val isRequired: Boolean
    ) : FieldValidator {
        override fun validateField(): Boolean {
            if (isRequired && field.text.isBlank()) {
                return false
            }
            val email = field.text.toString()
            val regex = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
            return regex.matcher(email).matches()
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    inner class NameValidator(
        private val field: EditText,
        private val errorMessage: String,
        private val isRequired: Boolean
    ) : FieldValidator {
        override fun validateField(): Boolean {
            if (isRequired && field.text.isBlank()) {
                return false
            }
            val name = field.text.toString().trim()
            val regex = Pattern.compile("^([\\p{L}\\s']+)( [\\p{L}\\s']{1,})+\$")
            return regex.matcher(name).matches()
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    inner class PhoneNumberValidator(
        private val field: EditText,
        private val errorMessage: String,
        private val isRequired: Boolean
    ) : FieldValidator {
        override fun validateField(): Boolean {
            if (isRequired && field.text.isBlank()) {
                return false
            }
            val phoneNumber = field.text.toString()
            val regex = Pattern.compile("^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}\$")
            return regex.matcher(phoneNumber).matches()
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    inner class CpfValidator(
        private val field: EditText,
        private val errorMessage: String,
        private val isRequired: Boolean
    ) : FieldValidator {
        override fun validateField(): Boolean {
            if (isRequired && field.text.isBlank()) {
                return false
            }
            val cpf = field.text.toString().replace(".", "").replace("-", "")
            if (cpf.length != 11 || cpf.toLongOrNull() == null) {
                return false
            }
            val digito1 = calcularDigito(cpf.substring(0, 9))
            val digito2 = calcularDigito(cpf.substring(0, 9) + digito1)
            return cpf == cpf.substring(0, 9) + digito1.toString() + digito2.toString()
        }

        private fun calcularDigito(cpfParcial: String): Int {
            var total = 0
            for (i in 0 until cpfParcial.length) {
                total += cpfParcial[i].toString().toInt() * (cpfParcial.length + 1 - i)
            }
            val resto = total % 11
            return if (resto < 2) 0 else 11 - resto
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    class ConfirmPasswordValidator(
        private val field: EditText,
        private val confirmField: EditText,
        private val errorMessage: String
    ) : FieldValidator {
        override fun validateField(): Boolean {
            val password = field.text.toString()
            val confirmPassword = confirmField.text.toString()
            return password == confirmPassword
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    inner class CompareFieldsValidator(
        private val field1: EditText,
        private val field2: EditText,
        private val errorMessage: String
    ) : FieldValidator {
        override fun validateField(): Boolean {
            return field1.text.toString() == field2.text.toString()
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    inner class PasswordValidator(
        private val field: EditText,
        private val errorMessage: String,
        private val isRequired: Boolean
    ) : FieldValidator {
        override fun validateField(): Boolean {
            if (isRequired && field.text.isBlank()) {
                return false
            }

            val password = field.text.toString()

            if (password.length < 6) {
                return false
            }

            val regex = Pattern.compile(".*\\d.*")
            return regex.matcher(password).matches()
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    inner class CheckBoxValidator(
        private val checkBox: CheckBox,
        private val context: Context,
        private val errorMessage: String

    ) : FieldValidator {
        override fun validateField(): Boolean {
            return checkBox.isChecked
        }

        override fun getErrorMessage(): String {
            if (!validateField()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            return errorMessage
        }
    }

    inner class SpinnerValidator(
        private val spinner: Spinner,
        private val context: Context,
        private val errorMessage: String
    ) : FieldValidator {
        override fun validateField(): Boolean {
            val isValid = spinner.selectedItemPosition != 0
            if (!isValid) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            return isValid
        }

        override fun getErrorMessage(): String {
            return errorMessage
        }
    }

    fun addCheckBoxValidator(checkBox: CheckBox, context: Context, errorMessage: String) {
        addFieldValidator(checkBox, CheckBoxValidator(checkBox, context, errorMessage))
    }

    fun addRequiredFieldValidator(field: EditText, errorMessage: String) {
        addFieldValidator(field, RequiredFieldValidator(field, errorMessage))
    }

    fun addDateValidator(field: EditText, errorMessage: String, isRequired: Boolean) {
        addFieldValidator(field, DateValidator(field, errorMessage, isRequired))
    }

    fun addEmailValidator(field: EditText, errorMessage: String, isRequired: Boolean) {
        addFieldValidator(field, EmailValidator(field, errorMessage, isRequired))
    }

    fun addNameValidator(field: EditText, errorMessage: String, isRequired: Boolean) {
        addFieldValidator(field, NameValidator(field, errorMessage, isRequired))
    }

    fun addPhoneNumberValidator(field: EditText, errorMessage: String, isRequired: Boolean) {
        addFieldValidator(field, PhoneNumberValidator(field, errorMessage, isRequired))
    }

    fun addCpfValidator(field: EditText, errorMessage: String, isRequired: Boolean) {
        addFieldValidator(field, CpfValidator(field, errorMessage, isRequired))
    }

    fun addConfirmPasswordValidator(field: EditText, confirmField: EditText, errorMessage: String) {
        addFieldValidator(field, ConfirmPasswordValidator(field, confirmField, errorMessage))
    }

    fun addPasswordValidator(field: EditText, errorMessage: String, isRequired: Boolean) {
        addFieldValidator(field, PasswordValidator(field, errorMessage, isRequired))
    }

    fun addSpinnerValidator(spinner: Spinner, context: Context, errorMessage: String) {
        addFieldValidator(spinner, SpinnerValidator(spinner, context,  errorMessage))
    }

}
