package com.example.flightsimapp
import Api
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.gson.GsonBuilder
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_dashboard.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.cos
import kotlin.math.sin
import android.util.Log

// this is the class of the simulator where al the fun happens live images and control of the flight simulator.
class Dashboard : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    var rudderSeekBar: SeekBar? = null
    var throttleSeekBar: SeekBar? = null

    var commandToSend: Command? = null

    //var img: ImageView? = null
    var currAileron: Double? = null
    var currElevator: Double? = null
    var currRudder: Int? = null
    var currThrottle: Int? = null
    var isConnected = true
    var countEpost =0
    var countEget =0
    var serverAddress = "http://10.0.2.2:5001/"
// on create method that takes car eof creating ths screen.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        serverAddress = intent.getStringExtra("url")
        //rudderSeekbar
        onStart()
        val seekBarRudder:SeekBar? = findViewById<SeekBar>(R.id.rudderSeekbar)
        // initiate  views
        rudderSeekBar = findViewById(R.id.rudderSeekbar) as SeekBar
        // perform seek bar change listener event used for getting the progress value
        rudderSeekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                progressChangedValue = progress
                println(progress.toDouble() / 1000)
                if(currRudder != progress){
                    currRudder = progress
                    val rudderCommand = progress.toDouble() / 100
                    val commandToSend = Command(aileron = 0.0, throttle = 0.0, rudder = rudderCommand, elevator = 0.0 )

                    postCommand(commandToSend)
                }


                //if seekbar value changed then change send the command otherwise don't send
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        //Throttle seekbar
        val seekBarThrottle:SeekBar? = findViewById<SeekBar>(R.id.throttleSeekbar)
        // initiate  views
        throttleSeekBar = findViewById(R.id.throttleSeekbar) as SeekBar
        // perform seek bar change listener event used for getting the progress value
        throttleSeekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                progressChangedValue = progress
                if(currThrottle != progressChangedValue){
                    currThrottle = progress
                    val throttleCommand = progress.toDouble() / 100
                    println(throttleCommand)
                    val commandToSend = Command(aileron = 0.0, throttle = throttleCommand, rudder = 0.0, elevator = 0.0 )
                    postCommand(commandToSend)
                }


            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })



        //joystick
        val joystickLeft = findViewById<JoystickView>(R.id.joystickView_left) as JoystickView
        joystickLeft.setOnMoveListener { angle, strength ->


            val aileronCommand = cos(angle.toDouble()) * strength
            val elevatorCommand = sin(angle.toDouble()) * strength

            val commandToSend = Command(aileron = aileronCommand, throttle = 0.0, rudder = 0.0, elevator = elevatorCommand)
            postCommand(commandToSend)

        }

        //orientation handler command
        fun onConfigurationChanged(newConfig: Configuration) {
            super.onConfigurationChanged(newConfig)

            // Checks the orientation of the screen
            if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {
                //retrieving resources for the variations
                setContentView(R.layout.activity_dashboard)
            } else if (newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {
                //retrieving resources for the variations
                setContentView(R.layout.activity_dashboard)
            }
        }

    }
// this method sends an update post for the given server whenever the values are changed by the pilot.
    fun postCommand(command: Command){
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.postCommand(command).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println(response.body())
                countEpost=0

            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                countEpost+=1
                if(countEpost>5) {
                    senBadMessagepost()
                    isConnected=false
                    this@Dashboard.finish()
                }
            }
        })
    }

// this method are responsible for getting th image from the mediator server.
    fun getImage() {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(serverAddress)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if(response.isSuccessful) {
                    countEget=0
                    //img = findViewById(R.id.cockpitView) as ImageView
                    val I = response?.body()?.byteStream()
                    val B = BitmapFactory.decodeStream(I)
                    runOnUiThread{
                        cockpitView.setImageBitmap(B)
                    }
                    //ImageView img = findViewById<>("cockpitView")
                    //setImageBitmap(body)
                }
            }
            // whenever there is an error
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                countEget += 1
                if(countEget>5) {
                    senBadMessageget()
                    isConnected = false
                    this@Dashboard.finish()
                }
            }
        })
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    fun senBadMessageget() {
        Toast.makeText(this,"Connection to Simulator broke", Toast.LENGTH_SHORT).show()
    }
    fun senBadMessagepost() {
        Toast.makeText(this,"Connection to Simulator broke", Toast.LENGTH_SHORT).show()
    }
    // lifecycle methord that starts the activity.
    override fun onStart() {
        super.onStart()
        isConnected=true
        runThread()

    }
    //thread running method that keep requesting images as long as the server is on.
    private fun  runThread()
    {
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    while (isConnected) {
                        sleep(500)
                        runOnUiThread {
                            getImage()
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }

        thread.start()
    }
    // o resume lifecycle method
    override fun onResume() {
        super.onResume()
        isConnected=true
    }
// on pause lifecycle method
    override fun onPause() {
        super.onPause()
        isConnected=false
    }
    // on stop lifecycle method
    override fun onStop() {
        super.onStop()
        isConnected=false
    }
    // on restart lifecycle method
    override fun onRestart() {
        super.onRestart()
        isConnected=true
    }


}