package com.project.restaurand_android

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.project.restaurand_android.ui.theme.RestauRandAndroidTheme

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    lateinit var searchView: SearchView
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.layout)
        searchView = findViewById(R.id.searchView)
        textView = findViewById(R.id.textView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    lifecycleScope.launch {
                        val response = generateChatGPTResponse(it)
                        Log.d("TEST CHAT GPT RESPONSE", response)
                        textView.text = response
                    }
                }
                return false
            }

            // This function probably not needed
            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we
                // are filtering our adapter with
                // new text on below line.
                Log.d("TEST TAG: onQueryTextChange", "TEST MSG: onQueryTextChange")
                return false
            }
        })
    }
}



suspend fun generateChatGPTResponse(prompt: String): String {
    //https://platform.openai.com/account/api-keys
    val apiKey = "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"
    val apiUrl = "https://api.openai.com/v1/completions"

    val requestBodyJson = JSONObject()
        .put("model", "text-davinci-003")
        .put("prompt", prompt)
        .put("max_tokens", 100) // Adjust the desired response length as needed
        .put("temperature", 0.8) // Adjust the temperature for response randomness

    val client = OkHttpClient()
    val mediaType = "application/json".toMediaType()
    val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url(apiUrl)
        .post(requestBody)
        .header("Authorization", "Bearer $apiKey")
        .build()

    return withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        Log.d("TEST response: ", response.toString())
        Log.d("TEST responseBody: ", responseBody.toString())

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            val jsonResponse = JSONObject(responseBody)
            val choices = jsonResponse.getJSONArray("choices")

            if (choices.length() > 0) {
                val firstChoice = choices.getJSONObject(0)
                val text = firstChoice.getString("text")
                return@withContext text
            }
        }

        // Return an error message if something went wrong
        return@withContext "Failed to generate a response."
    }.replace("\\r", "").replace("\\n", "\n")
}





@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RestauRandAndroidTheme {
        Greeting("Android")
    }
}