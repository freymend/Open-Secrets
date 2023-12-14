package edu.uw.ischool.opensecrets.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import edu.uw.ischool.opensecrets.R
import edu.uw.ischool.opensecrets.databinding.ActivitySignUpBinding
import edu.uw.ischool.opensecrets.util.Request

class SignUpActivity : Fragment() {
    private var _binding: ActivitySignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySignUpBinding.inflate(inflater, container, false)

        val isFilled =
            { binding.username.text.isNotEmpty() && binding.password.text.isNotEmpty() && binding.verifyPassword.text.isNotEmpty() }

        binding.username.addTextChangedListener {
            binding.signUp.isEnabled = isFilled()
        }
        binding.password.addTextChangedListener {
            binding.signUp.isEnabled = isFilled()
        }
        binding.verifyPassword.addTextChangedListener {
            binding.signUp.isEnabled = isFilled()
        }

        val passwordIsVerified = { binding.password.text.toString() == binding.verifyPassword.text.toString() }

        binding.signUp.setOnClickListener {
            if (!passwordIsVerified()) {
                Toast.makeText(context, getString(R.string.check_password), Toast.LENGTH_SHORT).show()
            }
            Thread {
                val response = Request.post(
                    "https://not-open-secrets.fly.dev/register", """{
                        "username": "${binding.username.text}",
                        "password": "${binding.password.text}"
                    }""".trimIndent()
                )

                activity?.runOnUiThread {
                    if (response.getBoolean("registered")) {
                        if (isAdded) {
                            parentFragmentManager.setFragmentResult("login", Bundle.EMPTY)
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.username_taken), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }.start()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}