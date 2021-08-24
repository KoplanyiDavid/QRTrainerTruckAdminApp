package com.vicegym.qrtrainertruckadminapp.popupwindow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.vicegym.qrtrainertruckadminapp.R
import com.vicegym.qrtrainertruckadminapp.adapter.TraineesAdapter
import com.vicegym.qrtrainertruckadminapp.data.TraineeData
import java.util.*

class PopupWindowActivity : AppCompatActivity() {

    private lateinit var traineesAdapter: TraineesAdapter
    private var traineesList: ArrayList<TraineeData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_window)

        init()
    }

    private fun init() {
        traineesAdapter = TraineesAdapter(applicationContext)
        val recyclerView = findViewById<RecyclerView>(R.id.rvTrainees)
        recyclerView.adapter = traineesAdapter
        val bundle = intent.extras
        traineesList = bundle?.getParcelableArrayList("traineeList")
        for (trainee in traineesList!!) {
            traineesAdapter.addTrainees(trainee)
        }
    }
}