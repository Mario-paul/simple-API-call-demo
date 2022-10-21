package com.example.simpleapicalldemo

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.simpleapicalldemo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonApiCall.setOnClickListener {

            CallAPILoginAsyncTask().getResult() // call our custom class

        }
    }

    private inner class CallAPILoginAsyncTask {

        private lateinit var customProgressDialog: Dialog

        fun getResult() {

            showProgressDialog()

            lifecycleScope.launch(Dispatchers.IO) {
                openConnectionAndGetText() // execute code in the background
            }

        }

        fun openConnectionAndGetText(): String {

            var result: String
            var connection: HttpURLConnection? = null

                try {

                    val url = URL("https://run.mocky.io/v3/cf6e606a-6e63-4bf4-9a6e-b10dfeaf15db")
                    // secret delete link -- warning, clicking this link will delete the above Mocky:
                    // https://designer.mocky.io/manage/delete/cf6e606a-6e63-4bf4-9a6e-b10dfeaf15db/LPjwBvWmFYf5xSpUCdq778Z9dq4jpmzPQWnb

                    // Attempt connection with remote server url
                    connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.doOutput = true

                    val httpResult: Int = connection.responseCode // gets response code from remote connection

                    if (httpResult == HttpURLConnection.HTTP_OK) {

                        val inputStream = connection.inputStream

                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val stringBuilder = StringBuilder() // the entire text will be saved here
                        var line: String? // each line of text will be saved here temporarily

                        try {

                            // Reads a line, then adds it to the stringBuilder in a new line
                            while (reader.readLine().also { line = it } != null) {
                                stringBuilder.append(line + "\n")
                            }

                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            try {
                                inputStream.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        result = stringBuilder.toString()

                    } else {
                        result = connection.responseMessage // return response code in case of negative response from server
                    }

                    // Runs code on UI thread after background job is done
                    runOnUiThread {
                        cancelProgressDialog()
                        Log.i("JSON RESPONSE RESULT", result)
                    }

                } catch (e: SocketTimeoutException) {
                    result = "Error: Connection Timeout"
                } catch (e: Exception) {
                    result = "Error: " + e.message
                } finally {
                    connection?.disconnect() // close connection
                }

            return result

        }

        private fun showProgressDialog() {
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog() {
            customProgressDialog.dismiss()
        }

    }

}