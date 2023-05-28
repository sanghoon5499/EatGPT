package com.project.restaurand_android

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.ComponentActivity
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

// override is used very often as Android provides a framework with many pre-defined classes
//   devs can extend or implement to fit our needs. To stay safe, we override these to use them.


class MainActivity : ComponentActivity() {
    lateinit var searchView: SearchView
    lateinit var textView: TextView

    // savedInstanceState is a Bundle type, which contains the saved state of the activity.
    // This is data saved from a previous instance of the activity (screen), such as when the
    //   the activity is destroyed and recreated (screen rotation), or other changes.
    override fun onCreate(savedInstanceState: Bundle?) {
        // "super" is in reference to the page we want to initialize. "super" = "superclass"
        // This line is only called once
        super.onCreate(savedInstanceState)

        // sets the UI layout as specified in the corresponding layout.xml file
        setContentView(R.layout.layout)
        searchView = findViewById(R.id.searchView)
        textView = findViewById(R.id.textView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // check if query exists, and set it as "it"
                query?.let {
                    lifecycleScope.launch {
                        val response = generateChatGPTResponse(it)
                        textView.text = response
                    }
                }
                return false
            }

            // This function probably not needed
            override fun onQueryTextChange(newText: String?): Boolean {
                // runs each time a change in the text field occurs
                return false
            }
        })
    }
}

// the API call takes time, so we pause and resume the function at a later time without
//   blocking the thread it's running on.
suspend fun generateChatGPTResponse(prompt: String): String {
    //https://platform.openai.com/account/api-keys
    val apiKey = "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"
    val apiUrl = "https://api.openai.com/v1/completions"

    // Build the response to send to the model
    val requestBodyJson = JSONObject()
        .put("model", "text-davinci-003")
        .put("prompt", prompt)
        .put("max_tokens", 100) // response length (char count I think)
        .put("temperature", 0.8) // response randomness (lower = more accurate)

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