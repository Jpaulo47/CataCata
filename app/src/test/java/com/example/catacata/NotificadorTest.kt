package com.example.catacata

import Notificador
import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@Config(manifest=Config.NONE)
@RunWith(AndroidJUnit4::class)
class NotificadorTest {

    private lateinit var context: Context
    private lateinit var view: View

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        view = View(context)
        Notificador.initialize(context)
    }

    @Test
    fun testShowToast() {
        Notificador.showToast(context, "Test toast message")
        // Check if toast message is displayed by checking the last toast message.
        assertEquals("Test toast message", ShadowToast.getTextOfLatestToast())
    }
}
