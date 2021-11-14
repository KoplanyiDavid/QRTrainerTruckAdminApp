package com.vicegym.qrtrainertruckadminapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vicegym.qrtrainertruckadminapp.data.TrainingData
import com.vicegym.qrtrainertruckadminapp.data.UserData
import com.vicegym.qrtrainertruckadminapp.databinding.CardTrainingBinding


class TrainingsAdapter(private val context: Context) :
    ListAdapter<TrainingData, TrainingsAdapter.TrainingsViewHolder>(ItemCallback) {

    private val db = Firebase.firestore
    private val trainingsList: MutableList<TrainingData> = mutableListOf()
    private val usersList: MutableList<UserData> = mutableListOf()
    private var lastPosition = -1

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
        holder.trainingCard.setOnClickListener {
            buildTrainingAlertDialog(tmpTraining)
        }
        setAnimation(holder.itemView, position)
    }

    private fun buildTrainingAlertDialog(training: TrainingData) {
        var traineesData = ""

        if (training.trainees!!.isNotEmpty()) {
            for (traineeId in training.trainees!!) {
                for (u in usersList) {
                    if (traineeId.contentEquals(u.id)) {
                        traineesData += "${u.name}\n${u.mobile}\n${u.rank}\n\n"
                    }
                }
            }
        }

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setMessage(traineesData)
        alertDialog.setPositiveButton("Bezár") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("Óra törlése") { dialog, _ ->
            db.collection("trainings").document(training.sorter.toString()).delete()

            val data = hashMapOf(
                "title" to training.title,
                "trainer" to training.trainer,
                "date" to training.date,
                "location" to training.location,
                "sorter" to training.sorter
            )

            for (traineeId in training.trainees!!) {
                for (u in usersList) {
                    if (u.id.contentEquals(traineeId)) {
                        db.collection("users").document(u.id!!)
                            .update("trainings", FieldValue.arrayRemove(data))
                    }
                }
            }
        }
        alertDialog.create().show()
    }

    fun addTrainings(training: TrainingData?) {
        training ?: return

        trainingsList += (training)
        submitList((trainingsList))
    }

    fun removeTrainings(training: TrainingData?) {
        training ?: return

        trainingsList -= (training)
        submitList(trainingsList)
    }

    fun addUser(user: UserData?) {
        user ?: return
        usersList += (user)
    }

    fun removeUser(user: UserData?) {
        user ?: return
        usersList -= user
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