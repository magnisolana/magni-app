package com.example.magni.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.magni.R
import com.example.magni.camera.QRCodeImageAnalyzer
import com.example.magni.databinding.FragmentScanQrCodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class ScanQRCodeFragment : Fragment() {

    private var _binding: FragmentScanQrCodeBinding? = null
    private val binding get() = _binding!!
    private var qrCodeDetected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnAction.setOnClickListener {
            if (!qrCodeDetected) {
                // this state should never be reached as button action changes after QR detected
            } else {
                displaySignedTransactionQRCode()
            }
        }

        binding.btnConfirmScan.setOnClickListener {
            findNavController().navigate(R.id.action_scanQRCodeFragment_to_mainScreenFragment)
        }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ScanQRCodeFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), QRCodeImageAnalyzer { qrCode ->
                        activity?.runOnUiThread {
                            Log.d("QRCodeAnalyzer", "QR Code Detected: $qrCode")
                            cameraProvider.unbindAll()
                            binding.tvDecodedQR.setBackgroundColor(
                                Color.argb(217, 169, 169, 169) // 50% opaque gray
                            )
                            binding.tvDecodedQR.text = qrCode
                            binding.tvDecodedQR.visibility = View.VISIBLE
                            binding.btnAction.text = getString(R.string.sign_transaction)
                            qrCodeDetected = true
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun displaySignedTransactionQRCode() {
        val signedTransactionQRBitmap = generateQRCode("Transaction is signed")
        binding.ivGeneratedQR.setImageBitmap(signedTransactionQRBitmap)
        binding.ivGeneratedQR.visibility = View.VISIBLE
        binding.tvDecodedQR.visibility = View.GONE
        binding.btnAction.text = getString(R.string.finish)

        // Update button click listener to navigate back
        binding.btnAction.setOnClickListener {
            findNavController().navigate(R.id.action_scanQRCodeFragment_to_mainScreenFragment)
        }
    }

    private fun generateQRCode(content: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }
}
