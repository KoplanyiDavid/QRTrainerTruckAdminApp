package com.vicegym.qrtrainertruckadminapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vicegym.qrtrainertruckadminapp.data.TraineeData
import com.vicegym.qrtrainertruckadminapp.data.TrainingData
import com.vicegym.qrtrainertruckadminapp.databinding.CardTrainingBinding
import com.vicegym.qrtrainertruckadminapp.popupwindow.PopupWindowActivity


class TrainingsAdapter(private val context: Context) :
    ListAdapter<TrainingData, TrainingsAdapter.TrainingsViewHolder>(ItemCallback) {

    private val db = Firebase.firestore
    private val trainingsList: MutableList<TrainingData> = mutableListOf()
    private var lastPosition = -1
    private var traineesDataArray: ArrayList<TraineeData> = arrayListOf()
    private var traineesIdArray: List<String>? = null

    class TrainingsViewHolder(binding: CardTrainingBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTrainingType = binding.tvTrainingType
        val tvTrainer: TextView = binding.tvTrainer
        val tvGymPlace: TextView = binding.tvGymPlace
        val tvDate: TextView = binding.tvGymDate
        val trainingCard = binding.cardViewTraining
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TrainingsViewHolder(
            CardTrainingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TrainingsViewHolder, position: Int) {
        val tmpTraining = trainingsList[position]
        holder.tvTrainingType.text = tmpTraining.title
        holder.tvTrainer.text = tmpTraining.trainer
        holder.tvGymPlace.text = tmpTraining.location
        holder.tvDate.text = tmpTraining.date
        holder.trainingCard.setOnClickListener { getTraineesId(tmpTraining.id!!) }
        setAnimation(holder.itemView, position)
    }

    private fun getTraineesId(id: String) {
        db.collection("trainings").document(id).get().addOnSuccessListener { document ->
            if (document != null) {
                traineesIdArray = document.data?.get("trainees") as List<String>?
                if (!(traineesIdArray.isNullOrEmpty()))
                    getUserFromId()
            } else {
                Log.d("TraineesList", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TraineesList", "get failed with ", exception)
            }
    }

    private fun getUserFromId() {
        for (id in traineesIdArray!!) {
            db.collection("users").document(id).get().addOnSuccessListener { document ->
                if (document != null) {
                    val traineeData = TraineeData(
                        document.data?.get("name") as String?,
                        document.data?.get("mobile") as String?,
                        document.data?.get("email") as String?,
                        document.data?.get("rank") as String?
                    )
                    if (!(traineesDataArray.contains(traineeData)))
                        traineesDataArray.add(traineeData)

                    if (traineesDataArray.size == traineesIdArray!!.size)
                        startTraineesDataPopupWindow()
                }
            }
        }
    }

    private fun startTraineesDataPopupWindow() {
        val intent = Intent(context, PopupWindowActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList("traineeList", traineesDataArray)
        intent.putExtras(bundle)
        context.startActivity(intent)
    }


    fun addTrainings(training: TrainingData?) {
        training ?: return

        trainingsList += (training)
        submitList((trainingsList))
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    companion object {
        object ItemCallback : DiffUtil.ItemCallback<TrainingData>() {
            override fun areItemsTheSame(oldItem: TrainingData, newItem: TrainingData): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: TrainingData, newItem: TrainingData): Boolean {
                return oldItem == newItem
            }
        }
    }
}