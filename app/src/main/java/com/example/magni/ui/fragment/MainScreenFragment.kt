package com.example.magni.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.magni.R
import com.example.magni.databinding.FragmentMainScreenBinding
import com.example.magni.viewmodel.KeyViewModel
import com.solana.core.DerivationPath
import com.solana.core.HotAccount

class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: KeyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.firstKey.observe(viewLifecycleOwner) { keyEntity ->
            val phrase24 = keyEntity?.name
                ?.removePrefix("[")
                ?.removeSuffix("]")
                ?.split(", ")
                ?.map { it.trim() }
            val account = HotAccount.fromMnemonic(phrase24!!, "",  DerivationPath.BIP44_M_44H_501H_0H)
            binding.tvKey.text = getString(R.string.key, account.publicKey)
        }

        binding.btnScanQR.setOnClickListener {
            findNavController().navigate(R.id.action_mainScreenFragment_to_scanQRCodeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


