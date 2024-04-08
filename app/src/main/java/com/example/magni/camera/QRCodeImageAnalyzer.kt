package com.example.magni.camera

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer

class QRCodeImageAnalyzer(private val onQrCodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val barcodeReader = MultiFormatReader().apply {
        val hints = mapOf<DecodeHintType, Any>(DecodeHintType.POSSIBLE_FORMATS to listOf(
            BarcodeFormat.QR_CODE))
        setHints(hints)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val data = imageProxy.planes[0].buffer.let { buffer ->
            val data = ByteArray(buffer.remaining())
            buffer.get(data)
            data
        }
        val source = PlanarYUVLuminanceSource(data, imageProxy.width, imageProxy.height, 0, 0, imageProxy.width, imageProxy.height, false)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = barcodeReader.decode(binaryBitmap)
            val delimiter = """{"signatures"""
            Log.d("QRCodeAnalyzer", "QR Code Detected: ${result.text}")
            if (result.text.startsWith(delimiter)) {
                onQrCodeDetected(result.text)
            }
        } catch (e: Exception) {
            // Not a QR Code
        } finally {
            imageProxy.close()
        }
    }
}
