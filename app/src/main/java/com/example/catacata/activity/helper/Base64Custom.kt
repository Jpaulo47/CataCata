package com.example.catacata.activity.helper

import android.util.Base64

object Base64Custom {
    @JvmStatic
    fun codificarBase64(texto: String): String {
        return Base64.encodeToString(texto.toByteArray(), Base64.DEFAULT)
            .replace("([\\n\\r])".toRegex(), "")
    }

    fun decodificarBase64(textoCodificado: String?): String {
        return String(Base64.decode(textoCodificado, Base64.DEFAULT))
    }
}