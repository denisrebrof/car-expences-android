package com.upreality.car.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.upreality.car.R
import com.upreality.car.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.root.let(this::setContentView)
    }
}