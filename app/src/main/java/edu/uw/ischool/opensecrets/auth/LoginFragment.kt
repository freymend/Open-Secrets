package edu.uw.ischool.opensecrets.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import edu.uw.ischool.opensecrets.R
import edu.uw.ischool.opensecrets.SecretApp
import edu.uw.ischool.opensecrets.databinding.FragmentLoginBinding
import edu.uw.ischool.opensecrets.util.Request

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)


        val isFilled = { binding.username.text.isNotEmpty() && binding.password.text.isNotEmpty() }

        binding.username.addTextChangedListener {
            Log.d("LoginActivity", "username changed")
            Log.d("LoginActivity", isFilled().toString())
            binding.login.isEnabled = isFilled()
        }
        binding.password.addTextChangedListener {
            Log.d("LoginActivity", "password changed")
            Log.d("LoginActivity", isFilled().toString())
            binding.login.isEnabled = isFilled()
        }

        binding.signUp.setOnClickListener {
            if (isAdded) {
                parentFragmentManager.setFragmentResult("signup", Bundle.EMPTY)
            }
        }
        binding.login.setOnClickListener {
            Thread {
                val response = Request.post(
                    "https://not-open-secrets.fly.dev/login", """{
                        "username": "${binding.username.text}",
                        "password": "${binding.password.text}"
                    }""".trimIndent()
                )

                activity?.runOnUiThread {
                    if (response.getBoolean("authenticated")) {
                        (activity?.application as SecretApp).optionManager.updateUsername(binding.username.text.toString())
                        (activity?.application as SecretApp).optionManager.updatePassword(binding.password.text.toString())
                        if (isAdded) {
                            parentFragmentManager.setFragmentResult("login", Bundle.EMPTY)
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }.start()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}