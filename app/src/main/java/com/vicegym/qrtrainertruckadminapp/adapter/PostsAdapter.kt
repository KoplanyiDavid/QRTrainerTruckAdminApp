package com.vicegym.qrtrainertruckadminapp.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vicegym.qrtrainertruckadminapp.data.Post
import com.vicegym.qrtrainertruckadminapp.databinding.CardPostBinding

class PostsAdapter(private val context: Context) :
    ListAdapter<Post, PostsAdapter.PostViewHolder>(ItemCallback) {

    private val postList: MutableList<Post> = mutableListOf()
    private var lastPosition = -1

    class PostViewHolder(binding: CardPostBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivProfilePicture = binding.ivPostCardProfilePicture
        val tvAuthor: TextView = binding.tvAuthor
        val tvTime: TextView = binding.tvDailyChallengeTime
        val tvDescription: TextView = binding.tvDailyChallengeDescription
        val imgPost: ImageView = binding.imgPost
        val card = binding.cardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostViewHolder(CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val tmpPost = postList[position]
        Glide.with(context).load(tmpPost.profilePic).into(holder.ivProfilePicture)
        holder.tvAuthor.text = tmpPost.author
        holder.tvTime.text = tmpPost.time
        holder.tvDescription.text = tmpPost.description
        holder.imgPost.rotation = 90f
        holder.card.setOnClickListener {
            deletePost(tmpPost.sorter)
        }

        Glide.with(context).load(tmpPost.imageUrl).into(holder.imgPost)

        setAnimation(holder.itemView, position)
    }

    private fun deletePost(sorter: Long?) {
        if (sorter != null) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setMessage("Biztosan törölni szeretnéd a posztot?")
            alertDialog.setPositiveButton("Törlés") { dialog, _ ->
                Firebase.firestore.collection("posts").document(sorter.toString()).delete()
                    .addOnSuccessListener {
                        dialog.dismiss()
                        val successDialog = AlertDialog.Builder(context)
                        successDialog.setMessage("A poszt törlése sikeresen megtörtént!")
                        successDialog.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        successDialog.create().show()
                    }
                    .addOnFailureListener {
                        dialog.dismiss()
                        val failureDialog = AlertDialog.Builder(context)
                        failureDialog.setMessage("A poszt törlése sikertelen volt!")
                        failureDialog.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        failureDialog.create().show()
                    }
            }
            alertDialog.setNegativeButton("Mégse") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.create().show()
        }
    }

    fun addPost(post: Post?) {
        post ?: return

        postList += (post)
        submitList(postList)
    }

    fun removePost(post: Post?) {
        post ?: return

        postList -= (post)
        submitList(postList)

    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    companion object {
        object ItemCallback : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
        }
    }
}