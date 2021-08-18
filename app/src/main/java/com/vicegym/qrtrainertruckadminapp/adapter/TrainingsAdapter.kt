package com.vicegym.qrtrainertruckadminapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vicegym.qrtrainertruckadminapp.TraineesActivity
import com.vicegym.qrtrainertruckadminapp.data.TrainingData
import com.vicegym.qrtrainertruckadminapp.databinding.CardTrainingBinding

class TrainingsAdapter(private val context: Context) :
    ListAdapter<TrainingData, TrainingsAdapter.TrainingsViewHolder>(ItemCallback) {

    private val trainingsList: MutableList<TrainingData> = mutableListOf()
    private var lastPosition = -1

    class TrainingsViewHolder(binding: CardTrainingBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTrainingType = binding.tvTrainingType
        val tvTrainer: TextView = binding.tvTrainer
        val tvGymPlace: TextView = binding.tvGymPlace
        val tvDate: TextView = binding.tvGymDate
        val trainingCard = binding.cardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TrainingsViewHolder(CardTrainingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TrainingsViewHolder, position: Int) {
        val tmpTraining = trainingsList[position]
        holder.tvTrainingType.text = tmpTraining.title
        holder.tvTrainer.text = tmpTraining.trainer
        holder.tvGymPlace.text = tmpTraining.location
        holder.tvDate.text = tmpTraining.date
        holder.trainingCard.setOnClickListener { startTraineesActivity() }
        setAnimation(holder.itemView, position)
    }

    private fun startTraineesActivity() {
        context.startActivity(Intent(context, TraineesActivity::class.java))
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