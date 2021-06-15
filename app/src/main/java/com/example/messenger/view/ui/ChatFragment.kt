package com.example.messenger.view.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.R
import com.example.messenger.databinding.FragmentChatBinding
import com.example.messenger.model.adapters.MessageListAdapter
import com.example.messenger.model.dataModel.User
import com.example.messenger.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding

    @Inject
    lateinit var messagesAdapter: MessageListAdapter

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receiverUser: User = arguments?.get("receiverUser") as User

        binding = FragmentChatBinding.bind(view)

        binding.tvReceiverUserName.text = receiverUser.fullName

        chatViewModel.getMessages(receiverUser.id!!)


        subscribeToObservers()
        setupRecyclerView()


        binding.etNewMessage.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonSendMessage.isEnabled = s != null && s.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.buttonSendMessage.setOnClickListener {
            val messageText: String = binding.etNewMessage.text.toString()

            lifecycleScope.launch {
                chatViewModel.sendMessage(messageText, receiverUser.id!!)
            }

            binding.etNewMessage.text.clear()
        }
    }


    private fun subscribeToObservers() {
        messagesAdapter.clear()
        chatViewModel.messagesLiveData.observe(viewLifecycleOwner){
            it?.let {
                messagesAdapter.messages = it
            }
        }
    }

    private fun setupRecyclerView() = binding.rvMessages.apply {
        adapter = messagesAdapter
        val myLayoutManager = LinearLayoutManager(requireContext())
        myLayoutManager.stackFromEnd = true
        layoutManager = myLayoutManager
    }
}