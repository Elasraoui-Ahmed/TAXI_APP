package com.example.figma

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.common.BitMatrix

class fragmentProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load profile image
        val imgProfil = view.findViewById<ImageView>(R.id.img)
        Glide.with(this).load(R.drawable.profil).circleCrop().into(imgProfil)

        // Play Lottie animation
        val lottieDriver = view.findViewById<LottieAnimationView>(R.id.lottie_driver)
        lottieDriver.playAnimation() // Start the animation

        // Generate QR code
        val qrCodeImageView = view.findViewById<ImageView>(R.id.qrCodeImageView)

        // Get driver information from strings.xml
        val driverName = getString(R.string.driver)
        val driverAge = getString(R.string.age)
        val driverLicense = getString(R.string.License)

        // Combine the driver information into a single string
        val driverInfo = "$driverName\n$driverAge\n$driverLicense"

        // Generate the QR code
        val qrCodeBitmap = generateQRCode(driverInfo)

        // Display QR code in the ImageView
        qrCodeImageView.setImageBitmap(qrCodeBitmap)

        // Optionally, set the QR label
        val qrLabel = view.findViewById<TextView>(R.id.txt_strong)
        qrLabel.text = getString(R.string.qr)
    }

    // Function to generate QR code from a string
    private fun generateQRCode(data: String): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val hints = hashMapOf<EncodeHintType, Any>()
        hints[EncodeHintType.MARGIN] = 1 // optional, set margin of the QR code

        // Generate the QR code as a BitMatrix
        val bitMatrix: BitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 500, 500, hints)

        // Convert the BitMatrix to a Bitmap
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) -0x1000000 else -0x1)
            }
        }

        return bitmap
    }
}
