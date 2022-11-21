package vn.iot.iotgradensystem.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.iot.iotgradensystem.model.Predict
import vn.iot.iotgradensystem.repository.MainRepository

class MainViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val _data = MutableLiveData<Predict>()
    val data: MutableLiveData<Predict> = _data


    fun getData(temperature: String, humidity: String) {
        viewModelScope.launch {
            val response = mainRepository.getData(temperature, humidity)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        _data.value = response.body()
                    } else Log.e("getdata", "getData: ${response}")
                } catch (e: Exception) {
                    Log.e("getdata", "getData: ${e.stackTrace}")
                }

            }
        }

    }


}
