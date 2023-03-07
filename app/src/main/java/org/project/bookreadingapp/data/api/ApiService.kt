package org.project.bookreadingapp.data.api

import org.project.bookreadingapp.data.Tales
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

const val BASE_URL = "http://10.0.2.2:5000/"
interface ApiService {

    @GET("get_tales")
    fun getTales(): Call<List<Tales>>

    companion object{

        operator fun invoke():ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}