package com.example.flightsimapp

import Api
import android.content.Intent
import android.os.AsyncTask
import android.os.AsyncTask.execute
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.text.TextUtils
import android.util.Log
import android.util.Log.e
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONStringer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import androidx.lifecycle.Observer
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
// this class is responsible for the connection screen.
class MainActivity : AppCompatActivity(),OnServerItemClickListener {
    private lateinit var serverViewModel: ServerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ServerListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        serverViewModel = ViewModelProvider(this).get(ServerViewModel::class.java)
        serverViewModel.allWords.observe(this, Observer { servers ->
            servers?.let { adapter.setServers(it) }
        })
        val buttoncon = findViewById<Button>(R.id.connectButton)
        buttoncon.setOnClickListener {
            if (TextUtils.isEmpty(serverEditText.text)) {
                Toast.makeText(this, "Please type address ", Toast.LENGTH_SHORT).show()
            } else {
                val add = serverEditText.text.toString()
                val newserver = ServerEntity()
                newserver.server_url = add
                newserver.server_id = (System.currentTimeMillis() % 10000000).toInt()
                newserver.server_lru = System.currentTimeMillis()
                serverViewModel.insert(newserver)
                val gson = GsonBuilder().setLenient().create()
                val retrofit = Retrofit.Builder().baseUrl(serverEditText.text.toString())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                val api = retrofit.create(Api::class.java)
                val body = api.getImg().enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if(response.isSuccessful) {
                            sendOKMessage()
                            intent = Intent(applicationContext,Dashboard::class.java)
                            intent.putExtra("url", add)
                            startActivity(intent)
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        senBadMessage()
                    }
                })
            }
        }
    }
    /// this method is responsible for handling the click event on an item from the recycler view.
    override fun OnItemClick(item: ServerEntity, position: Int) {

        var nserver = ServerEntity()
        nserver.server_lru=System.currentTimeMillis()
        nserver.server_url = item.server_url
        nserver.server_id = item.server_id
        serverViewModel.deleteserver(item.server_id)
        serverViewModel.insert(nserver)
        val edittext = findViewById<EditText>(R.id.serverEditText)
        edittext.setText(nserver.server_url)
        //Toast.makeText(this,item.server_url,Toast.LENGTH_SHORT).show()
    }
    // this method presents a success message toast.
    fun sendOKMessage() {
        Toast.makeText(this,"Connection Established",Toast.LENGTH_SHORT).show()
    }
    // this method presents an error message toast.
    fun senBadMessage() {
        Toast.makeText(this,"didn't succeed",Toast.LENGTH_SHORT).show()
    }

}