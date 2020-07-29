package com.example.flightsimapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
// this is a test activity just to check the activity flow.
class simulator:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simulator_screen)
    }
}