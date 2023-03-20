package org.project.bookreadingapp.data.api

import okhttp3.OkHttpClient
import org.project.bookreadingapp.data.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor


const val BASE_URL = "http://10.0.2.2:5000/"
interface ApiService {

    @GET("get_tales")
    fun getTales(): Call<List<Tales>>

    @POST("/api-embed")
    fun getEmbed(@Body wav: Wav): Call<Embed>

    @POST("api-synthesize-tale")
    fun getSynVoice(@Body taleEmbed: TaleEmbed):Call<TaleWithSynVoice>


    companion object{

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) // log requests and responses
            .retryOnConnectionFailure(true)
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