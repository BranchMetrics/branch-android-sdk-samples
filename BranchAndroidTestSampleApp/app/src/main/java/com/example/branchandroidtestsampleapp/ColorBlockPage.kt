package com.example.branchandroidtestsampleapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import io.branch.referral.Branch
import io.branch.referral.util.BranchEvent

class ColorBlockPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_block_page)
    }

    override fun onStart() {
        super.onStart()

        val sessionParams = Branch.getInstance().latestReferringParams
        val colorBlockImageView = findViewById<ImageView>(R.id.colorBlock)
        val yourColorBlockString = findViewById<TextView>(R.id.yourBlockColorString)

        // Check if session parameters contain the "blockColor" parameter. If it does, modify the block's color.

        if (sessionParams.has("blockColor")) {
            val blockColor = sessionParams["blockColor"]
            val stringColor = getString(R.string.blockColorString, blockColor)

            if (sessionParams["blockColor"] == "Yellow") {
                colorBlockImageView.setBackgroundColor(Color.YELLOW)
                yourColorBlockString.text = stringColor

            } else if (sessionParams["blockColor"] == "Blue") {
                colorBlockImageView.setBackgroundColor(Color.BLUE)
                yourColorBlockString.text = stringColor

            } else if (sessionParams["blockColor"] == "Red") {
                colorBlockImageView.setBackgroundColor(Color.RED)
                yourColorBlockString.text = stringColor
            } else {
                return
            }

        } else {
            yourColorBlockString.text = "Your color block is: White"
        }

        // Home button to return to MainActivity

        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
        }

    }
}