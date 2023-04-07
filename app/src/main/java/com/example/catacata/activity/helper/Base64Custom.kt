package com.example.catacata.activity.helper

import android.util.Base64

/**
 * Classe que contém funções para codificar e decodificar strings em Base64.
 * create by João paulo - 03/10/2022
 */

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