package vn.iot.iotgradensystem.repository

import retrofit2.Response
import vn.iot.iotgradensystem.model.Predict
import vn.iot.iotgradensystem.retrofit.RetrofitService

class MainRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun getData(temperature: String, humidity: String): Response<Predict> {
//        val params: HashMap<String, String> = HashMap()
//        params["temperature"] = temperature
//        params["humidity"] = humidity
        return retrofitService.getData(temperature, humidity)
    }

}
