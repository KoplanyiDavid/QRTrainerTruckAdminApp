package com.vicegym.qrtrainertruckadminapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.vicegym.qrtrainertruckadminapp.adapter.TraineesAdapter
import com.vicegym.qrtrainertruckadminapp.databinding.ActivityTraineesBinding

class TraineesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTraineesBinding
    private lateinit var traineesAdapter: TraineesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraineesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        traineesAdapter = TraineesAdapter(this)
        binding.rvTrainees.layoutManager = LinearLayoutManager(this)
        binding.rvTrainees.adapter = traineesAdapter
        initTraineesListener()
    }

    private fun initTraineesListener() {
        val db = Firebase.firestore
        db.collection("trainings")
            .orderBy("date") //TODO: ha date vhogy bugos akkor sorter használata
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> traineesAdapter.addTrainees(dc.document.toObject())
                        DocumentChange.Type.MODIFIED -> Toast.makeText(
                            requireContext(),
                            dc.document.data.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        DocumentChange.Type.REMOVED -> Toast.makeText(
                            requireContext(),
                            dc.document.data.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}