package vn.iot.iotgradensystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.iot.iotgradensystem.databinding.ActivityMainBinding
import vn.iot.iotgradensystem.repository.MainRepository
import vn.iot.iotgradensystem.retrofit.RetrofitService
import vn.iot.iotgradensystem.viewmodel.MainViewModel
import vn.iot.iotgradensystem.viewmodel.MyViewModelFactory

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var databaseESP: DatabaseReference
    private lateinit var databaseAndroid: DatabaseReference
    private lateinit var isPump: DatabaseReference
    private lateinit var isAuto: DatabaseReference
    private var soilMoisturePump: DatabaseReference? = null
    private var humanity: DatabaseReference? = null
    private var soilMoisture: DatabaseReference? = null
    private var temperature: DatabaseReference? = null
    private var localIsPump = false
    private var currentHumanity: Long? = null
    private var humanityPre: String? = null
    private var temperaturePre: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDatabase()
        fetchData()
        initForecast()
        setOnClick()

    }


    private fun initForecast() {
        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)
        viewModel = ViewModelProvider(
            this,
            MyViewModelFactory(mainRepository)
        )[MainViewModel::class.java]
        viewModel.data.observe(this) {
            binding.txtForecast.text = "Today's weather forecast may be ${it.weather}"
        }

    }

    private fun setOnClick() {
        lifecycleScope.launch {
            delay(3000)
            viewModel.getData(temperaturePre ?: "0", humanityPre ?: "0")
        }
        binding.btnPump.setOnClickListener {
            if (!localIsPump) {
                isPump.setValue(true)
            } else {

                isPump.setValue(false)
            }
        }
        binding.swAuto.setOnClickListener {
            if (binding.swAuto.isChecked) {
                isAuto.setValue(true)
            } else isAuto.setValue(false)
        }

        binding.btnSetAutoPump.setOnClickListener {
            val level = try {
                binding.txtSoilMoistureInfo.text.toString().toInt()
            } catch (e: Exception) {
                1
            }
            soilMoisturePump?.setValue(level)

        }

    }

    private fun initDatabase() {
        databaseAndroid = Firebase.database.reference.child("Android")
        isPump = databaseAndroid.child("isPump")
        isAuto = databaseAndroid.child("isAuto")
        soilMoisturePump = databaseAndroid.child("soilMoisturePump")
        databaseESP = Firebase.database.reference.child("ESP")
        humanity = databaseESP.child("humanity")
        soilMoisture = databaseESP.child("soilMoisture")
        temperature = databaseESP.child("temperature")
    }

    private fun fetchData() {
        isPump.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                localIsPump = snapshot.value as Boolean
                if (localIsPump) {
                    binding.btnPump.text = resources.getString(R.string.stop_pump)
                } else binding.btnPump.text = resources.getString(R.string.pump)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        humanity?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentHumanity = snapshot.value as Long
                binding.txtHumanity.text = currentHumanity.toString()
                humanityPre = currentHumanity.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to read value humanity.",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
        soilMoisture?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.txtSoilMoisture.text = snapshot.value.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to read value soil moisture.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        temperature?.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.txtTemperature.text = "${snapshot.value.toString()}°C"
                temperaturePre = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to read value temperature.",
                    Toast.LENGTH_LONG
                ).show()
                Log.e(TAG, "Failed to read value temperature.", error.toException())
            }
        })
    }
}