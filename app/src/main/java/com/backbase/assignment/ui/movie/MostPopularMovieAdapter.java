package com.backbase.assignment.ui.movie;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.backbase.assignment.ui.R;
import com.backbase.assignment.ui.model.Results;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MostPopularMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/original/";

    private List<Results> movieResults;
    private Context context;

    private boolean isLoadingAdded = false;

    public MostPopularMovieAdapter(Context context) {
        this.context = context;
        movieResults = new ArrayList<>();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        assert viewHolder != null;
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.movie_item, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Results result = movieResults.get(position); // Movie
        holder.itemView.setTag(result.getId());
        switch (getItemViewType(position)) {
            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;

                movieVH.mMovieTitle.setText(result.getTitle());

                movieVH.mReleaseDate.setText(result.getRelease_date());
                movieVH.mRating.setText(result.getVote_average()+"%");
                Double rating = Double.parseDouble(""+result.getVote_average());
                Drawable drawable;

                int ratingInt  =(int) Math.round(rating);
                if(ratingInt>10){
                    ratingInt = 10;
                }
                Resources res = this.context.getResources();

                if(ratingInt>5){
                    drawable = res.getDrawable(R.drawable.greencircleprogrees);
                }else {
                    drawable = res.getDrawable(R.drawable.yellocircleprogress);
                }

                movieVH.mProgress.setProgress(ratingInt);   // Main Progress

                movieVH.mProgress.setSecondaryProgress(10); // Secondary Progress
                movieVH.mProgress.setMax(10); // Maximum Progress


                movieVH.mProgress.setProgressDrawable(drawable);

                Glide.with(context)
                        .load(BASE_URL_IMG + result.getPoster_path())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                        .centerCrop()
                        .transition(new DrawableTransitionOptions().crossFade())
                        .into(movieVH.mPosterImg);

                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return movieResults == null ? 0 : movieResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    private void add(Results r) {
        movieResults.add(r);
        notifyItemInserted(movieResults.size() - 1);
    }

    public void addAll(List<Results> moveResults) {
        for (Results result : moveResults) {
            add(result);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
         add(new Results());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResults.size() - 1;
        Results result = getItem(position);

        if (result != null) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Results getItem(int position) {
        return movieResults.get(position);
    }

    /**
     * Main list's content ViewHolder
     */
    static private class MovieVH extends RecyclerView.ViewHolder {
        private TextView mMovieTitle;
        private TextView mReleaseDate; // displays "year | language"
        private ImageView mPosterImg;
        private TextView mRating;
        private ProgressBar mProgress;

        private MovieVH(View itemView) {
            super(itemView);

            mMovieTitle = itemView.findViewById(R.id.title);
            mRating     = itemView.findViewById(R.id.textView1);
            mReleaseDate = itemView.findViewById(R.id.releaseDate);
            mPosterImg = itemView.findViewById(R.id.poster);

            mProgress = itemView.findViewById(R.id.rating_progress);
          
        }
    }

    static private class LoadingVH extends RecyclerView.ViewHolder {

         LoadingVH(View itemView) {
            super(itemView);
        }
    }

}
