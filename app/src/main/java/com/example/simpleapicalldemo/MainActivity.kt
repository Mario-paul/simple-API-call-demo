package com.example.simpleapicalldemo

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.simpleapicalldemo.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
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

            CallAPILoginAsyncTask("jane", "mypassword123456").getResult() // call our custom class

        }
        binding.buttonReset.setOnClickListener { resetUI() }
    }

    private inner class CallAPILoginAsyncTask(val username: String, val password: String) {

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

                val url = URL("https://run.mocky.io/v3/04a586e7-3861-42de-a7e8-09675fd7347b")
                // secret delete link -- warning, clicking this link will delete the above Mocky:
                // https://designer.mocky.io/manage/delete/cf6e606a-6e63-4bf4-9a6e-b10dfeaf15db/LPjwBvWmFYf5xSpUCdq778Z9dq4jpmzPQWnb
                // https://designer.mocky.io/manage/delete/52f3c0cd-d7d5-4e27-8f4b-36beee205187/Vfy67YzAyud80nYh757dFE0J5fQ5keRM9d6p
                // https://designer.mocky.io/manage/delete/04a586e7-3861-42de-a7e8-09675fd7347b/Tf51D1UdMuU3Pi33YJg1AXr2MSyVZLP9IWUy

                // Attempt connection with remote server url
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true

                // POST / login code. This code is unusable due to not having a real server to login to
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST" // can set any request method, eg. GET, POST, etc
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.useCaches = false

                val writeDataOutputStream = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                jsonRequest.put("username", username)
                jsonRequest.put("password", password)
                writeDataOutputStream.writeBytes(jsonRequest.toString())
                writeDataOutputStream.flush()
                writeDataOutputStream.close()

                // Continue get json code
                val httpResult: Int =
                    connection.responseCode // gets response code from remote connection
                Log.e("Connection code: ", httpResult.toString())

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
                    result =
                        connection.responseMessage // return response code in case of negative response from server
//                    Log.e("ERROR", result) // debugging
                    cancelProgressDialog()
                }

                // Runs code on UI thread after background job is done
                runOnUiThread {

                    cancelProgressDialog()
                    Log.i("JSON Response Result", result) // print whole json object in logcat

                    binding.textViewResult.text = result // print whole json object to UI
                    binding.textViewResult.visibility = View.VISIBLE
                    binding.textViewHelloWorld.visibility = View.GONE
                    binding.textViewInstructions.visibility = View.GONE
                    binding.buttonApiCall.visibility = View.GONE
                    binding.buttonReset.visibility = View.VISIBLE

                    // Read and deconstruct JSON objects (manual/traditional way, without libraries)
                    val jsonObject = JSONObject(result)

                    val message = jsonObject.optString("message") // get String json object
                    Log.i("Message", message)
                    val userId = jsonObject.optInt("user_id")
                    Log.i("User ID", "$userId") // get Int json object
                    val name = jsonObject.optString("name")
                    Log.i("User name", name)

                    val profileDetailsObject =
                        jsonObject.optJSONObject("profile_details") // get Object json object
                    val isProfileCompleted =
                        profileDetailsObject?.optBoolean("is_profile_completed") // get Boolean json object from our previous object
                    Log.i("Is profile completed", "$isProfileCompleted")

                    val dataListArray =
                        jsonObject.optJSONArray("data_list") // get Array (list) json object
                    Log.i("Data List Size", "${dataListArray?.length()}") // get size of above array
                    if (dataListArray != null) {
                        for (item in 0 until dataListArray.length()) {
                            // print the whole array element
                            Log.i("Element $item", "${dataListArray[item]}")

                            val dataItemObject: JSONObject =
                                dataListArray[item] as JSONObject // get each array item

                            // access and print values inside each array element
                            val id = dataItemObject.optInt("id")
                            val value = dataItemObject.optString("value")
                            Log.i("id", "$id")
                            Log.i("value", value)
                        }
                    }

                    Log.i("", "******************************************")

                    // Read and deconstruct JSON objects (gson library way)
                    val responseData = Gson().fromJson(result, ResponseData::class.java)
                    Log.i("Message", responseData.message)
                    Log.i("user_id", "${responseData.user_id}")
                    Log.i("name", responseData.name)
                    Log.i("email", responseData.email)
                    Log.i("mobile", "${responseData.mobile}")

                    Log.i(
                        "is profile completed",
                        "${responseData.profile_details.is_profile_completed}"
                    )
                    Log.i("rating", "${responseData.profile_details.rating}")

                    Log.i("data_list size", "${responseData.data_list.size}")

                    for (item in responseData.data_list.indices) {
                        Log.i("element $item", "${responseData.data_list[item]}")

                        Log.i("id", "${responseData.data_list[item].id}")
                        Log.i("value", responseData.data_list[item].value)

                    }

                }

            } catch (e: SocketTimeoutException) {
                result = "Error: Connection Timeout"
                cancelProgressDialog()
                Log.e("catch (e: SocketTim...)", result)
                runOnUiThread {
                    Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                result = "Error: " + e.message
                cancelProgressDialog()
                Log.e("catch (e: Exception)", result)
//                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show() // CRASHES APP, RUN ON UI THREAD AS BELOW
                runOnUiThread {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                }
            } finally {
                connection?.disconnect() // close connection
            }

//            Log.e("result", result) // debugging
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

    private fun resetUI() {
        binding.textViewResult.text = "" // print whole json object to UI
        binding.textViewResult.visibility = View.GONE
        binding.textViewHelloWorld.visibility = View.VISIBLE
        binding.textViewInstructions.visibility = View.VISIBLE
        binding.buttonReset.visibility = View.GONE
        binding.buttonApiCall.visibility = View.VISIBLE
    }

}