package edu.uw.ischool.opensecrets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import edu.uw.ischool.opensecrets.databinding.OptionBinding


class OptionActivity : Fragment() {
    private var _binding: OptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = OptionBinding.inflate(inflater, container, false)
        Log.i("OptionActivity", "onCreateView")
        binding.minTimeInput.setText(
            (activity?.application as SecretApp).optionManager.getMinTime().toString()
        )
        binding.saveOptions.setOnClickListener {
            (activity?.application as SecretApp).optionManager.updateMinTime(
                binding.minTimeInput.text.toString().toFloat()
            )
            Toast.makeText(context, getString(R.string.saved_option), Toast.LENGTH_SHORT).show()
            if (isAdded) {
                parentFragmentManager.setFragmentResult("option", Bundle.EMPTY)
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}