package com.example.messenger.model.dataModel

data class Message(
    var id: String? = "",
    var text: String? = "",
    var sendTime: String? = "",
    var receiverUserIdAndSenderUserId: String? = "",
    var senderUserName: String? = ""
)