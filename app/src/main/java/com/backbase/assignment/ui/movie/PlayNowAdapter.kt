package com.backbase.assignment.ui.movie


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.backbase.assignment.ui.R
import com.backbase.assignment.ui.model.Results
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PlayNowAdapter(private val results: ArrayList<Results>): RecyclerView.Adapter<PlayNowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.playing_now_movie,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(results[position])

    override fun getItemCount() = results.size

    fun addUsers(rows: List<Results>) {
        this.results.apply {
            clear()
            addAll(rows)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var moviePoster : ImageView = itemView.findViewById(R.id.moviePoster)
        fun bind(results: Results) {
            itemView.apply {
                Glide.with(moviePoster.context)
                    .load("https://image.tmdb.org/t/p/original/${results.poster_path}")
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // cache both original & resized image
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher_round)
                    .into(moviePoster)
            }
        }

    }
}