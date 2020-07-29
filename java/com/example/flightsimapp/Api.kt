import com.example.flightsimapp.Command
import okhttp3.ResponseBody import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
// this is an interface for the Api requests for the mediator server.
interface Api {
    // sends a get request the cockpit image.
    @GET("/screenshot")
    fun getImg(): Call<ResponseBody>

    // sends a post request for updating plane values.
    @POST("api/command")
    fun postCommand(@Body command: Command): Call<ResponseBody>;
}