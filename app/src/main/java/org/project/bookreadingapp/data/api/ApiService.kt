package org.project.bookreadingapp.data.api

import okhttp3.OkHttpClient
import org.project.bookreadingapp.data.Embed
import org.project.bookreadingapp.data.Tales
import org.project.bookreadingapp.data.Wav
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


const val BASE_URL = "http://10.0.2.2:5000/"
interface ApiService {

    @GET("get_tales")
    fun getTales(): Call<List<Tales>>

    @POST("/api-embed")
    fun getEmbed(@Body wav: Wav): Call<Embed>



    companion object{

        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        operator fun invoke():ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}