import com.google.firebase.database.*

/**
 * Classe que representa um repositório de usuários no Firebase Realtime Database.
 * create by João paulo - 07/04/2023
 */

class UsuarioRepository {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usuariosRef: DatabaseReference = firebaseDatabase.getReference("usuarios")

    /**
     * Obtém um usuário do Firebase Realtime Database com base no seu ID.
     *
     * @param idUsuario O ID do usuário a ser recuperado.
     * @param onComplete Uma função de retorno que é chamada quando a operação é concluída.
     * Recebe um objeto [Usuario] se a operação for bem-sucedida, ou null se ocorrer um erro.
     */

    fun getUsuario(idUsuario: String, onComplete: (Usuario?) -> Unit) {
        usuariosRef.child(idUsuario).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                onComplete(usuario)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(null)
            }
        })
    }
}
