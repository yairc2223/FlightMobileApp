package com.example.flightsimapp

import kotlin.properties.Delegates
import com.squareup.moshi.Json
// this is a data class that represents the command object and holds its data in JSON form.
data class Command(
    @Json(name = "aileron") val aileron: Double,
    @Json(name = "rudder") val rudder: Double,
    @Json(name = "elevator") val elevator: Double,
    @Json(name = "throttle") val throttle: Double

)