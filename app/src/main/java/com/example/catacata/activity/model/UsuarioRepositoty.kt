import com.google.firebase.database.*

class UsuarioRepository {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usuariosRef: DatabaseReference = firebaseDatabase.getReference("usuarios")

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
