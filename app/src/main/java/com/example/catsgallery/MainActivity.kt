package com.example.catsgallery

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private val uris: MutableList<String> = ArrayList()
    private lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.myLayout)
        this.doGet("https://api.imgur.com/3/gallery/search/?q=cats", "Client-ID 1ceddedc03a5d71")
    }

    /*
    * Get data from imgur
    * */
    private fun doGet(url: String, authorization: String) {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object: StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                this.handleOnUpdateUris(response.toString())
            },
            Response.ErrorListener {  })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = authorization
                return headers
            }
        }
        queue.add(stringRequest)
    }

    /*
    * Get all possible cat images
    * */
    private fun handleOnUpdateUris(data: String) {
        val catsJson = JSONObject(data)
        val catsArray: JSONArray = catsJson.getJSONArray("data")
        for (i in 0 until catsArray.length()) {
            val cat: JSONObject = catsArray.getJSONObject(i)
            if (cat.has("images")) {
                val images: JSONArray = cat.getJSONArray("images")
                for (i in 0 until images.length()) {
                    val image: JSONObject = images.getJSONObject(i)
                    if (!image["link"].toString().endsWith(".mp4")) {
                        this.uris.add(image["link"].toString())
                    }
                }
            }
        }
        this.showImages()
    }

    /*
    * Loop to generate cat images
    * */
    private fun showImages() {
        for (uri in this.uris) {
            this.generateImage(uri)
        }
    }

    private fun generateImage(imageUrl: String) {
        val imageView = ImageView(this)
        Glide.with(getApplicationContext())
            .load(imageUrl)
            .into(imageView)
        this.layout.addView(imageView)
        val params = imageView.layoutParams as LinearLayout.LayoutParams
        params.setMargins(8, 8, 8, 8)
    }
}