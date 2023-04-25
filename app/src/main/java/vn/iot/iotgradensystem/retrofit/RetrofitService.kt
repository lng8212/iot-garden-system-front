package vn.iot.iotgradensystem.retrofit

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import vn.iot.iotgradensystem.model.Predict


interface RetrofitService {

    @GET("/api")
    suspend fun getData(
        @Query("temperature") temperature: String,
        @Query("humidity") humidity: String
    ): Response<Predict>

    companion object {
        var retrofitService: RetrofitService? = null
        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.1.13:8000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }
}
