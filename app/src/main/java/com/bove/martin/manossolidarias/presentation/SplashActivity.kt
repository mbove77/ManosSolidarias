package com.bove.martin.manossolidarias.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bove.martin.manossolidarias.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({ // This method will be executed once the timer is over
            // Start your app main activity
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)

            // close this activity
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    companion object {
        // Splash screen timer
        private const val SPLASH_TIME_OUT = 1500
    }
}