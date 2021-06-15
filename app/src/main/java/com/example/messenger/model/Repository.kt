package com.example.messenger.model

import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.messenger.model.dataModel.Message
import com.example.messenger.model.dataModel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class Repository @Inject constructor(
    private val application: Application
) {
    private var auth: FirebaseAuth = Firebase.auth

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersReference = database.reference.child("users")
    private val messagesReference = database.reference.child("messages")

    val userLiveData = MutableLiveData<FirebaseUser?>()
    val loggOutLiveData = MutableLiveData<Boolean>()
    val allUsersLiveData = MutableLiveData<List<User>>()
    val messagesLiveData = MutableLiveData<MutableList<Message>>()

    var allUsers = mutableListOf<User>()
    var allMessages = mutableListOf<Message>()

    suspend fun addMessageToDb(messageText: String, receiverUserId: String){
        // Соединил receiverUserId и SenderUserId, так как Firebase Realtime Database не поддерживает возможность использовать фильтр на несколько аттрубутов
        val receiverUserIdAndSenderUserId: String = "${receiverUserId}_${auth.currentUser!!.uid}"

        val id: String = messagesReference.push().key!!

        val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
        val time: String = dateFormat.format(Date())

        val message = Message(id ,messageText, time, receiverUserIdAndSenderUserId )
        messagesReference.child(id).setValue(message)

        if(allMessages.isNotEmpty()){
            allMessages.add(message)
            messagesLiveData.postValue(allMessages)
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

      fun getMessages(receiverUserId: String){
//        val filter: String = "${receiverUserId}_${auth.currentUser!!.uid}"
//        var query: Query = messagesReference.orderByChild("receiverUserIdAndSenderUserId").equalTo(filter)

        messagesReference.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    allMessages.clear()
                    for (ds in snapshot.children) {
                        val value = ds.value as Map<String, String>
                        value.let {
                            val message = Message(
                                id = it["id"],
                                text = it["text"],
                                sendTime = it["sendTime"],
                                receiverUserIdAndSenderUserId = it["receiverUserIdAndSenderUserId"]
                            )
                            val filter: String = "${receiverUserId}_${auth.currentUser!!.uid}"
                            val filterSecond: String = "${auth.currentUser!!.uid}_${receiverUserId}"

                            if(message.receiverUserIdAndSenderUserId == filter || message.receiverUserIdAndSenderUserId == filterSecond){
                                if(allUsers.isNullOrEmpty()){
                                    GlobalScope.launch {
                                        getAllUsers()
                                    }
                                }
                                val senderUserId: String = message.receiverUserIdAndSenderUserId!!.split('_')[1]

                                var senderUser = allUsers.find { user ->
                                    user.id == senderUserId
                                }
                                message.senderUserName = senderUser?.fullName
                                allMessages.add(message)
                                messagesLiveData.postValue(allMessages)
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
        )
    }

    suspend fun getAllUsers(){
        usersReference.addListenerForSingleValueEvent(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        var value = ds.value as Map<String?, String?>
                        value.let {
                            val user = User(
                                id = it["id"],
                                fullName = it["fullName"],
                                email = it["email"]
                            )
                            if(user.id != auth.currentUser?.uid){
                                allUsers.add(user)
                            }
                        }
                    }
                   allUsersLiveData.postValue(allUsers)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            }
        )

    }

    fun addUserToDatabase(id: String, email: String, fullName: String){
        val user = User(id, email, fullName)
        usersReference.child(id).setValue(user)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun register(email: String, password: String, fullName: String) {
         auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(application.mainExecutor) { task ->
                    if(task.isSuccessful){
                        Toast.makeText(application, "Registration succeeded", Toast.LENGTH_SHORT).show()
                        userLiveData.postValue(auth.currentUser)
                        addUserToDatabase(
                            id = auth.currentUser!!.uid,
                            email = email,
                            fullName = fullName
                        )
                    }else{
                        Toast.makeText(application, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(application.mainExecutor){ task ->
                if(task.isSuccessful){
                    userLiveData.postValue(auth.currentUser)
                    loggOutLiveData.postValue(false)
                }else{
                    Toast.makeText(application, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun loggOut(){
        auth.signOut()
        loggOutLiveData.postValue(true)
    }
}