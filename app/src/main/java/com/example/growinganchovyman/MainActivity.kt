package com.example.growinganchovyman

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.growinganchovyman.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val presetBtn: Button = findViewById(R.id.presetBtn)

        presetBtn.setOnClickListener{
            val intent = Intent(this, PresetActivity::class.java)
            startActivity(intent)
        }
    }
}