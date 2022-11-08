package vn.iot.iotgradensystem

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import vn.iot.iotgradensystem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private var humanity: DatabaseReference? = null
    private var soilMoisture: DatabaseReference? = null
    private var temperature: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDatabase()
        fetchData()
    }

    private fun initDatabase() {
        database = Firebase.database.reference
        humanity = database.child("humanity")
        soilMoisture = database.child("soilMoisture")
        temperature = database.child("temperature")
    }

    private fun fetchData() {
        humanity?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.txtHumanity.text = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value humanity.", error.toException())
            }
        })
        soilMoisture?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.txtSoilMoisture.text = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value soil moisture.", error.toException())
            }

        })
        temperature?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.txtTemperature.text = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value temperature.", error.toException())
            }

        })
    }
}