package com.example.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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

    /**
     * Desabilita todos os EditText passando o layout pai
     */
    fun setEditTextsEnabled(enabled: Boolean, vararg parentViews: ViewGroup) {
        for (parentView in parentViews) {
            for (i in 0 until parentView.childCount) {
                val childView = parentView.getChildAt(i)
                if (childView is EditText || childView is TextInputEditText) {
                    childView.isEnabled = enabled
                } else if (childView is TextInputLayout) {
                    // Desabilita o TextInputLayout, mas habilita o campo de texto interno
                    childView.isEnabled = enabled
                    childView.getChildAt(0).isEnabled = true
                }
            }
        }
    }



}
