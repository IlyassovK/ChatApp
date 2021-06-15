package com.example.messenger.view.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.messenger.R
import com.example.messenger.databinding.FragmentLoginBinding
import com.example.messenger.viewmodel.LoginSignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val loginSignupViewModel: LoginSignupViewModel by viewModels()

    private lateinit var binding: FragmentLoginBinding

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(loginSignupViewModel.isUserSignedIn()){
            print("USER HERE")
            view.findNavController().navigate(R.id.action_loginFragment_to_userListFragment)
        }

        binding = FragmentLoginBinding.bind(view)

        subscribeToObservers(view)

        var isRegistration = binding.tvLoginPageTitle.text == "Create Account"


        binding.buttonLoginPage.setOnClickListener{
            val fullName: String = binding.etFullName.text.toString()
            val email: String = binding.etEmail.text.toString().trim()
            val password: String = binding.etPassword.text.toString().trim()
            val confirmPassword: String = binding.etConfirmPassword.text.toString().trim()


            if(isRegistration){
                if(email.isEmpty() || password.isEmpty() || fullName.isEmpty() || confirmPassword.isEmpty()){
                    Toast.makeText(context, "Fill required fields!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(password.length < 6){
                    Toast.makeText(context, "Password must be longer than 6!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if(password != confirmPassword){
                    Toast.makeText(context, "Passwords are not same!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                loginSignupViewModel.register(email, password, fullName)

            }else{

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(context, "Fill required fields!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                loginSignupViewModel.login(email, password)
            }
        }

        binding.buttonToggleLoginSignup.setOnClickListener {
            if(!isRegistration){
                binding.tvLoginPageTitle.text = "Create Account"
                binding.buttonLoginPage.text = "Sing Up"
                binding.tvToggleButtonLabel.text = "Already have an account?"
                binding.buttonToggleLoginSignup.text = "Sing In"
                binding.etFullName.isVisible = true
                binding.etConfirmPassword.isVisible = true
                isRegistration = true
            }
            else{
                binding.tvLoginPageTitle.text = "Log In"
                binding.buttonLoginPage.text = "Login"
                binding.tvToggleButtonLabel.text = "Don't have an account?"
                binding.buttonToggleLoginSignup.text = "Sing Up"
                binding.etFullName.isVisible = false
                binding.etConfirmPassword.isVisible = false
                isRegistration = false
            }
        }

    }

    private fun subscribeToObservers(view: View) {
        loginSignupViewModel.userLiveData.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()
                view.findNavController().navigate(R.id.action_loginFragment_to_userListFragment)
            }
        }
    }
}