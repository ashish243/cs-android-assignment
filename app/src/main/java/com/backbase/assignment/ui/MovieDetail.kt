package com.backbase.assignment.ui

import DetailModel
import Genres
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.backbase.assignment.api.MovieService
import com.backbase.assignment.ui.api.MovieApi
import com.backbase.assignment.ui.movie.GenresAdapter
import com.backbase.assignment.util.CheckNetworkConnection
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class MovieDetail : Activity() {
    private val TAG = "DetailActivity"
    private lateinit var MOVIEID :String
    private lateinit var movieService: MovieService
    private lateinit var cd: CheckNetworkConnection
    private lateinit var genresAdapter: GenresAdapter
    private lateinit var recyclerViewGenres: RecyclerView
    private lateinit var movieName: TextView
    private lateinit var duration: TextView
    private lateinit var overview: TextView
    private lateinit var back: ImageView
    private lateinit var posterImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.moviedetail)

        MOVIEID = intent.getStringExtra("MOVIE_ID")
        cd = CheckNetworkConnection()

        init()

        if (cd.isNetworkConnected(this)) {
            //init service and load data
            movieService = MovieApi.getClient().create(MovieService::class.java)

            loadDetails()
        }else {
            Toast.makeText(this,"Please check your internet connection..",Toast.LENGTH_LONG).show()
        }
    }

    fun init (){
        recyclerViewGenres = findViewById(R.id.rvHorizontal)
        movieName          = findViewById(R.id.textViewName)
        duration           = findViewById(R.id.textViewDuration)
        overview           = findViewById(R.id.textViewOverView)
        back               = findViewById(R.id.imageViewBack)
        posterImage        = findViewById(R.id.imagePoster)

        recyclerViewGenres.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        genresAdapter = GenresAdapter(arrayListOf())
        recyclerViewGenres.adapter = genresAdapter


    }

    fun backClick(v: View?) {
        finish()
    }
    private fun loadDetails(){
        Log.d(TAG, "loadFirstPage: ${MOVIEID}")
        callFetchDetailApi()?.enqueue(object : Callback<DetailModel?> {
            override fun onResponse(call: Call<DetailModel?>, response: Response<DetailModel?>
            ) {

                Log.d(TAG, "loadFirstPage:${MOVIEID}"+response.body()?.adult)
                overview.text = response.body()?.overview?: "Null"
                duration.text = response.body()?.release_date?: "Null"
                movieName.text = response.body()?.title?: "Null"

                Glide.with(posterImage.context)
                    .load("https://image.tmdb.org/t/p/w500/${response.body()?.poster_path}")
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher_round)
                    .into(posterImage)
                     response.body()?.genres?.let { genres -> retrieveGenresList(genres) }
            }

            override fun onFailure(
                call: Call<DetailModel?>?,
                t: Throwable
            ) {
                t.printStackTrace()
                Toast.makeText(this@MovieDetail,"Some thing went roung "+t.message,Toast.LENGTH_LONG).show()

                Log.d(TAG, "loadFirstPage Error"+ t.printStackTrace().toString())

            }

        })
    }

    private fun callFetchDetailApi(): Call<DetailModel?>? {
        val movieId: Int  = MOVIEID.toInt()
        return movieService.getDetails(
            movieId,
            getString(R.string.my_api_key))
    }

    private fun retrieveGenresList(moviegenres: List<Genres>) {

        genresAdapter.apply {
            addUsers(moviegenres)
            notifyDataSetChanged()
        }
    }
}