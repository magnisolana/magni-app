package com.example.magni.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.magni.R
import com.example.magni.databinding.FragmentKeyInfoBinding
import com.example.magni.model.KeyEntity
import com.example.magni.viewmodel.KeyViewModel
import com.solana.core.Message
import com.solana.vendor.bip39.Mnemonic
import com.solana.vendor.bip39.WordCount

class KeyInfoFragment : Fragment() {

    private var _binding: FragmentKeyInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: KeyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val key = generateRandomMnemonic().toString()
        binding.btnGenerateKey.setOnClickListener {
            binding.tvGeneratedKey.text = key
            binding.tvGeneratedKey.visibility = View.VISIBLE
            binding.btnSaveKey.visibility = View.VISIBLE
        }

        binding.btnSaveKey.setOnClickListener {
            if (key.isNotEmpty()) {
                viewModel.insertKey(KeyEntity(name = key))
                findNavController().navigate(R.id.action_keyInfoFragment_to_mainScreenFragment)
            }
        }
    }

    private fun generateRandomMnemonic(): List<String> {
        return Mnemonic(WordCount.COUNT_24).phrase
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
