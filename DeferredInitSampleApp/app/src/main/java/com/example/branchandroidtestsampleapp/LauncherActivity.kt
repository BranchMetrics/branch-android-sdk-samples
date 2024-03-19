package com.example.branchandroidtestsampleapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("LauncherActivity", "onCreate")

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }
}
