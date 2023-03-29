package com.example.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup

/**
 * Classe utilitária para operações com Views
 * reate by João paulo - 28/03/2023
 */
object UtilsView {

    /**
     * Define o foco para o componente especificado.
     */
    fun setFocusComponent(component: View) {
        component.requestFocus()
    }

    /**
     * Define a visibilidade de vários componentes de uma vez.
     * @param visibility a visibilidade a ser definida, uma das constantes de View
     * @param views as Views a serem alteradas
     */
    fun setShowComponents(visibility: Int, vararg views: View) {
        views.forEach { it.visibility = visibility }
    }

    /**
     * Habilita ou desabilita vários componentes de uma vez.
     * @param enabled o status de habilitação a ser definido
     * @param views as Views a serem alteradas
     */
    fun setEnabledComponents(enabled: Boolean, vararg views: View) {
        views.forEach { it.isEnabled = enabled }
    }

    /**
     * Obtém a View raiz de uma Activity.
     * @param activity a Activity da qual a View será obtida
     * @return a View raiz da Activity
     */
    fun getRootView(activity: Activity): View {
        return activity.findViewById(android.R.id.content)
    }

    /**
     * Obtém a ViewGroup raiz de uma Activity.
     * @param activity a Activity da qual a ViewGroup será obtida
     * @return a ViewGroup raiz da Activity
     */
    fun getRootViewGroup(activity: Activity): ViewGroup {
        return activity.findViewById(android.R.id.content) as ViewGroup
    }

    /**
     * Remove todas as Views filhas de um ViewGroup.
     * @param viewGroup o ViewGroup do qual as Views serão removidas
     */
    fun removeAllChildren(viewGroup: ViewGroup) {
        viewGroup.removeAllViews()
    }

    /**
     * Esconde o teclado virtual.
     * @param activity a Activity na qual o teclado deve ser escondido
     */
    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus ?: View(activity)
        view.clearFocus()
    }
}
