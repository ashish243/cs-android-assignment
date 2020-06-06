package com.backbase.assignment.ui.movie

import Genres
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.backbase.assignment.ui.R

class GenresAdapter (private val users: ArrayList<Genres>) : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       private var movieGenres: Button = itemView.findViewById(R.id.buttonGenres)
        fun bind(genres: Genres) {
            itemView.apply {
                movieGenres.text = genres.name

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.genres_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    fun addUsers(rows: List<Genres>) {
        this.users.apply {
            clear()
            addAll(rows)
        }

    }
}