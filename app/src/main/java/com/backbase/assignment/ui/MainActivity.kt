package com.backbase.assignment.ui

import NowPlaying
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.backbase.assignment.api.MovieService
import com.backbase.assignment.model.MovieList
import com.backbase.assignment.ui.api.MovieApi
import com.backbase.assignment.ui.model.Results
import com.backbase.assignment.ui.movie.MostPopularMovieAdapter
import com.backbase.assignment.ui.movie.PlayNowAdapter
import com.backbase.assignment.ui.util.RecyclerTouchListener
import com.backbase.assignment.util.CheckNetworkConnection
import com.backbase.assignment.util.PaginationScrollListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var totalPage: Int = 1
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarTop: ProgressBar
    private lateinit var moviesAdapter: MostPopularMovieAdapter
    private lateinit var playNowMoviesAdapter: PlayNowAdapter
    private lateinit var recyclerViewPlayNow: RecyclerView
    private lateinit var recyclerViewMovie: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var movieService: MovieService
    private lateinit var cd: CheckNetworkConnection
    private val PAGE_START = 1

    private var currentPage: Int = PAGE_START
    var isLastPage: Boolean = false
    var isLoading: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_main)

        cd = CheckNetworkConnection()
        supportActionBar?.hide()

        progressBar = findViewById(R.id.main_progress)
        progressBarTop  = findViewById(R.id.top_progress)
        recyclerViewMovie = findViewById(R.id.rvVerticalMovieList)
        recyclerViewPlayNow = findViewById(R.id.rvHorizontal)

        recyclerViewPlayNow.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        playNowMoviesAdapter = PlayNowAdapter(arrayListOf())
        recyclerViewPlayNow.adapter = playNowMoviesAdapter

        listOfMovieInit()

        //init service and load data
        if (cd.isNetworkConnected(this)) {
            movieService = MovieApi.getClient().create(MovieService::class.java)
            loadFirstPage()
            loadNowPlayingList()
        }else {
            Toast.makeText(this,"Please check your internet connection..", Toast.LENGTH_LONG).show()
        }
    }

    private fun listOfMovieInit(){

        linearLayoutManager =
            LinearLayoutManager(this)
        recyclerViewMovie.layoutManager = linearLayoutManager
        moviesAdapter = MostPopularMovieAdapter(this)
        recyclerViewMovie.adapter = moviesAdapter

        recyclerViewMovie.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                if (cd.isNetworkConnected(this@MainActivity)) {
                    isLoading = true
                    currentPage += 1
                    //you have to call loadmore items to get more data
                    // mocking network delay for API call
                    // mocking network delay for API call
                    Handler().postDelayed(Runnable {
                        loadNextPage()
                    }, 1000)
                }else {
                    Toast.makeText(this@MainActivity,"Please check your internet connection..",Toast.LENGTH_LONG).show()
                }
            }
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        recyclerViewMovie.addOnItemTouchListener(
            RecyclerTouchListener(this,
                recyclerViewMovie, object : ClickListener {
                    override fun onClick(
                        view: View,
                        position: Int
                    ) { //Values are passing to activity & to fragment as well

                        val intent = Intent(this@MainActivity,MovieDetail::class.java)
                        intent.putExtra("MOVIE_ID","${view.getTag()}")
                        // start your next activity
                        startActivity(intent)
                    }

                    override fun onLongClick(view: View, position: Int) {

                    }
                })
        )
    }

    fun loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage)

        callTopRatedMoviesApi()!!.enqueue(object : Callback<MovieList?> {
            override fun onResponse(
                call: Call<MovieList?>,
                response: Response<MovieList?>
            ) {
                moviesAdapter.removeLoadingFooter()
                isLoading = false
                val results: List<Results?>? = fetchResults(response)
                moviesAdapter.addAll(results)
                if (currentPage !== totalPage) moviesAdapter.addLoadingFooter() else isLastPage = true
            }

            override fun onFailure(
                call: Call<MovieList?>,
                t: Throwable
            ) {
                t.printStackTrace()
                // TODO: 08/11/16 handle failure
            }
        })
    }

    private fun loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ")
        callTopRatedMoviesApi()?.enqueue(object : Callback<MovieList?> {
            override fun onResponse(call: Call<MovieList?>, response: Response<MovieList?>
            ) { // Got data. Send it to adapter
                val results: List<Results?>? = fetchResults(response)
                progressBar.visibility = View.GONE
                Log.d(TAG, "loadFirstPage: ${results}")
                totalPage = response.body()?.total_pages ?: 1
                moviesAdapter.addAll(results)
                if (currentPage <= totalPage) moviesAdapter.addLoadingFooter() else isLastPage = true
            }

            override fun onFailure(
                call: Call<MovieList?>?,
                t: Throwable
            ) {
                t.printStackTrace()
                progressBar.visibility = View.GONE
                // TODO: 08/11/16 handle failure
            }

        })

    }

    private fun loadNowPlayingList() {
        Log.d(TAG, "loadNowPlayingList: ")
        callNowPlayingMoviesApi()?.enqueue(object : Callback<NowPlaying?> {
            override fun onResponse(call: Call<NowPlaying?>, response: Response<NowPlaying?>
            ) { // Got data. Send it to adapter
                //val results: List<Results?>? = fetchNowPlayingResults(response)
                progressBarTop.visibility = View.GONE
                Log.d(TAG, "Success: ")

                response.body()?.results?.let { results -> retrieveNowPlayingList(results) }


            }

            override fun onFailure(
                call: Call<NowPlaying?>?,
                t: Throwable
            ) {
                t.printStackTrace()
                progressBarTop.visibility = View.GONE
                Log.d(TAG, "Error: ")
                // TODO: 08/11/16 handle failure
            }

        })

    }

    private fun fetchResults(response: Response<MovieList?>): List<Results?>? {
        val topRatedMovies: MovieList? = response.body()
        return topRatedMovies?.results
    }

    private fun retrieveNowPlayingList(moviePlaying: List<Results>) {
        playNowMoviesAdapter.apply {
            addUsers(moviePlaying)
            notifyDataSetChanged()
        }
    }
    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As [.currentPage] will be incremented automatically
     * by @[PaginationScrollListener] to load next page.
     */
    private fun callTopRatedMoviesApi(): Call<MovieList?>? {
        return movieService.getMovies(
            getString(R.string.my_api_key),
            "en_US",
            currentPage
        )
    }

    private fun callNowPlayingMoviesApi(): Call<NowPlaying?>? {
        return movieService.getNowPlaying(
            getString(R.string.my_api_key),
            "en_US",
            1
        )
    }
}
