package com.example.figma

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import java.util.*

class LanguageSettingsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var englishButton: Button
    private lateinit var frenchButton: Button
    private lateinit var arabicButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_language, container, false)
        val lottieDriver = view.findViewById<LottieAnimationView>(R.id.lottie_lang)
        lottieDriver.playAnimation() // Start the animation

        // Initialize shared preferences
        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", 0)

        englishButton = view.findViewById(R.id.button_english)
        frenchButton = view.findViewById(R.id.button_french)
        arabicButton = view.findViewById(R.id.button_arabic)

        // Set the text of the buttons based on the saved language
        setButtonText()

        englishButton.setOnClickListener {
            setLocale("en")
            setButtonText() // Update button text after language change
        }

        frenchButton.setOnClickListener {
            setLocale("fr")
            setButtonText() // Update button text after language change
        }

        arabicButton.setOnClickListener {
            setLocale("ar")
            setButtonText() // Update button text after language change
        }

        return view
    }

    private fun setLocale(languageCode: String) {
        // Save the selected language in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.apply()

        // Change the locale of the app
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = requireActivity().resources.configuration
        config.setLocale(locale)
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

        // Restart the activity to apply changes
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setButtonText() {
        // Get the saved language from SharedPreferences
        val languageCode = sharedPreferences.getString("language", "en") ?: "en"

        // Set the button text based on the selected language
        when (languageCode) {
            "en" -> {
                englishButton.text = "English"
                frenchButton.text = "French"
                arabicButton.text = "Arabic"
            }
            "fr" -> {
                englishButton.text = "Anglais"
                frenchButton.text = "Français"
                arabicButton.text = "Arabe"
            }
            "ar" -> {
                englishButton.text = "إنجليزي"
                frenchButton.text = "فرنسي"
                arabicButton.text = "عربي"
            }
        }
    }
}
