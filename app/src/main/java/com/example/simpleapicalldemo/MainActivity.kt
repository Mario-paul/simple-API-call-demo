package com.example.simpleapicalldemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.simpleapicalldemo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonApiCall.setOnClickListener {

            val apiCaller = GetStringFromURL(this)

            apiCaller.setAddressListener(object : GetStringFromURL.StringListener {

                override fun onStringObtained(string: String?) {
                    binding.textViewResult.text = string // add result to UI
                    binding.textViewResult.visibility = View.VISIBLE
                    binding.textViewHelloWorld.visibility = View.GONE
                    binding.textViewInstructions.visibility = View.GONE
                }

                override fun onError() {
                    Log.e("error", "Something went wrong")
                }

            })

            lifecycleScope.launch(Dispatchers.IO) {
                apiCaller.getResult()
            }

        }
    }

}