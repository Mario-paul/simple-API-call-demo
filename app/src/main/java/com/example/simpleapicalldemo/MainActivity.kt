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

            apiCaller.setResultListener(object : GetStringFromURL.ResultListener {

                override fun onResultObtained(result: String?) {
                    binding.textViewResult.text = result // add result to UI
                    binding.textViewResult.visibility = View.VISIBLE
                    binding.textViewHelloWorld.visibility = View.GONE
                    binding.textViewInstructions.visibility = View.GONE
                }

                override fun onError() {
                    Log.e("error", "Something went wrong")
                }

            })

//            Start background task
            lifecycleScope.launch(Dispatchers.IO) {
                apiCaller.getResult()
            }

        }
    }

}