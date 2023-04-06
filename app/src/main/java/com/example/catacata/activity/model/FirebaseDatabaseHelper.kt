import com.google.firebase.database.*

data class FirebaseDatabaseHelper(
    var estado: String? = null,
    var logradouro: String? = null,
    var municipio: String? = null,
    var nome: String? = null,
    var ocupacao: String? = null,
    var sexo: String? = null,
    var telefone: String? = null
) {
    companion object {
        private const val USUARIOS_NODE = "usuarios"
    }

    fun obterDadosUsuario(idUsuario: String, callback: (FirebaseDatabaseHelper?) -> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference(USUARIOS_NODE).child(idUsuario)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val usuario = dataSnapshot.getValue(FirebaseDatabaseHelper::class.java)
                    callback(usuario)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    fun obterEmailUsuario(idUsuario: String, callback: (String?) -> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference(USUARIOS_NODE).child(idUsuario).child("email")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val email = dataSnapshot.getValue(String::class.java)
                callback(email)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }
}
