import android.widget.EditText
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
        private val field: EditText, private val errorMessage: String
    ) : FieldValidator {
        override fun validateField(): Boolean {
            return field.text.isNotBlank()
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
            return try {
                val calendar = Calendar.getInstance()
                calendar.setLenient(false)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                calendar.set(Calendar.MONTH, month - 1)
                calendar.set(Calendar.YEAR, year)
                calendar.time
                true
            } catch (e: Exception) {
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
            val name = field.text.toString()
            val regex = Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*\$")
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

}
