package com.vicegym.qrtrainertruckadminapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vicegym.qrtrainertruckadminapp.data.TraineeData
import com.vicegym.qrtrainertruckadminapp.databinding.CardTraineeBinding

class TraineesAdapter(private val context: Context) :
    ListAdapter<TraineeData, TraineesAdapter.TraineesViewHolder>(ItemCallback) {

    private val traineesList: MutableList<TraineeData> = mutableListOf()
    private var lastPosition = -1

    class TraineesViewHolder(binding: CardTraineeBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTrainingType = binding.tvTrainingType
        val tvTrainer: TextView = binding.tvTrainer
        val tvGymPlace: TextView = binding.tvGymPlace
        val tvDate: TextView = binding.tvGymDate
        val trainingCard = binding.cardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TraineesAdapter.TraineesViewHolder(CardTraineeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TraineesAdapter.TraineesViewHolder, position: Int) {
        val tmpTraining = traineesList[position]
        holder.tvTrainingType.text = tmpTraining.title
        holder.tvTrainer.text = tmpTraining.trainer
        holder.tvGymPlace.text = tmpTraining.location
        holder.tvDate.text = tmpTraining.date
        setAnimation(holder.itemView, position)
    }

    fun addTrainees(trainee: TraineeData?) {
        trainee ?: return

        traineesList += (trainee)
        submitList((traineesList))
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    companion object {
        object ItemCallback : DiffUtil.ItemCallback<TraineeData>() {
            override fun areItemsTheSame(oldItem: TraineeData, newItem: TraineeData): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: TraineeData, newItem: TraineeData): Boolean {
                return oldItem == newItem
            }
        }
    }
}