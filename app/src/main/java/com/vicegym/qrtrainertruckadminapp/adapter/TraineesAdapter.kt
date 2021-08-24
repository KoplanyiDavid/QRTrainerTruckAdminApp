package com.vicegym.qrtrainertruckadminapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vicegym.qrtrainertruckadminapp.data.TraineeData
import com.vicegym.qrtrainertruckadminapp.databinding.CardTraineeBinding

class TraineesAdapter(private val context: Context) :
    androidx.recyclerview.widget.ListAdapter<TraineeData, TraineesAdapter.TraineesViewHolder>(ItemCallback) {

    private val traineeList: MutableList<TraineeData> = mutableListOf()
    private var lastPosition = -1

    class TraineesViewHolder(binding: CardTraineeBinding) : RecyclerView.ViewHolder(binding.root) {
        val traineeName = binding.tvTraineeName
        val traineeMobile = binding.tvTraineeMobile
        val traineeEmail = binding.tvTraineeEmail
        val traineeRank = binding.tvTraineeRank
    }

    fun addTrainees(trainee: TraineeData?) {
        trainee ?: return

        traineeList += (trainee)
        submitList((traineeList))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TraineesViewHolder(
            CardTraineeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TraineesViewHolder, position: Int) {
        val tmpTrainee = traineeList[position]
        holder.traineeName.text = tmpTrainee.name
        holder.traineeMobile.text = tmpTrainee.mobile
        holder.traineeEmail.text = tmpTrainee.email
        holder.traineeRank.text = tmpTrainee.rank
        setAnimation(holder.itemView, position)
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