package com.backbase.assignment.api

import DetailModel
import NowPlaying
import com.backbase.assignment.model.MovieList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MovieService {
    @GET("movie/popular")
    fun getMovies(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pageIndex: Int
    ): Call<MovieList?>?

    @GET("movie/{id}")
    fun getDetails(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String?

    ): Call<DetailModel?>?

    @GET("movie/now_playing")
    fun getNowPlaying(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pageIndex: Int

    ): Call<NowPlaying?>?

}