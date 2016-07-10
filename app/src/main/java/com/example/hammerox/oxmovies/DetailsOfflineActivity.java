package com.example.hammerox.oxmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hammerox.oxmovies.data.Movie;
import com.example.hammerox.oxmovies.data.MovieDatabase;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailsOfflineActivity extends AppCompatActivity {

    String movieID;
    private Movie movie = new Movie();

    private int width = 0;
    private int height = 0;

    private List<Pair<String, String>> trailerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Display display = getWindowManager().getDefaultDisplay();
        width = Utility.getPosterWidth(display);
        height = Utility.getPosterHeight(display);
        Utility.setPosterIntoView(this, width, height);

        movieID = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView titleView = (TextView) findViewById(R.id.details_title);
        ImageView posterView = (ImageView) findViewById(R.id.details_poster);
        TextView synopsysView = (TextView) findViewById(R.id.details_synopsys);
        CheckBox favouriteView = (CheckBox) findViewById(R.id.details_favourite);
        TextView ratingView = (TextView) findViewById(R.id.details_rating);
        TextView releaseDateView = (TextView) findViewById(R.id.details_releasedate);
        LinearLayout trailersView = (LinearLayout) findViewById(R.id.details_trailers);
        LinearLayout reviewsView = (LinearLayout) findViewById(R.id.details_reviews);

        MovieDatabase database = new MovieDatabase(this);
        Query query = Query.select().from(Movie.TABLE).where(Movie.MOVIE_ID.eq(movieID));
        SquidCursor<Movie> cursor = database.query(Movie.class, query);

        try {
            Movie movie = new Movie();
            while (cursor.moveToNext()) {
                movie.readPropertiesFromCursor(cursor);
                this.movie = movie;

                titleView.setText(movie.getTitle());
                posterView.setImageBitmap(Utility.loadPosterImage(movieID));
                synopsysView.setText(movie.getSynopsys());
                favouriteView.setChecked(Utility.isFavourite(DetailsOfflineActivity.this, movieID));
                favouriteView.setVisibility(View.VISIBLE);
                ratingView.setText(movie.getRating().toString());
                releaseDateView.setText(movie.getReleaseDate());

                // Set up trailers
                JSONObject trailersJSON = new JSONObject(movie.getTrailersJson());
                JSONArray allTrailers = trailersJSON.getJSONArray("results");

                int trailersCount = allTrailers.length();
                if (trailersCount > 0) {

                    trailerList = new ArrayList<>();

                    for (int i = 0; i < trailersCount; i++) {
                        JSONObject trailerObject = allTrailers.getJSONObject(i);
                        String trailerTitle = trailerObject.getString("name");
                        String trailerKey = trailerObject.getString("key");
                        Pair<String, String> trailerPair = new Pair<>(trailerTitle, trailerKey);
                        trailerList.add(trailerPair);

                        View custom = inflater.inflate(R.layout.item_trailer, null);

                        TextView trailerTitleView = (TextView) custom.findViewById(R.id.item_trailer_title);
                        trailerTitleView.setText(trailerTitle);

                        trailersView.addView(custom);
                    }
                } else {
                    View custom = inflater.inflate(R.layout.item_trailer_empty, null);
                    trailersView.addView(custom);
                }

                // Set up reviews
                JSONObject reviewsJSON = new JSONObject(movie.getReviewsJson());
                JSONArray allReviews = reviewsJSON.getJSONArray("results");

                int reviewsCount = allReviews.length();
                if (reviewsCount > 0) {
                    for (int i = 0; i < reviewsCount; i++) {
                        JSONObject reviewObject = allReviews.getJSONObject(i);
                        String reviewAuthor = reviewObject.getString("author");
                        String reviewComment = reviewObject.getString("content");

                        View custom = inflater.inflate(R.layout.item_review, null);

                        TextView reviewAuthorView = (TextView) custom.findViewById(R.id.item_review_author);
                        reviewAuthorView.setText(reviewAuthor);

                        TextView reviewCommentView = (TextView) custom.findViewById(R.id.item_review_comment);
                        reviewCommentView.setText(reviewComment);

                        reviewsView.addView(custom);
                    }
                } else {
                    View custom = inflater.inflate(R.layout.item_review_empty, null);
                    reviewsView.addView(custom);
                }

            }

        } catch (JSONException e) {

        } finally {
            cursor.close();
        }

    }


    public void favourite(View v) {
        Utility.setFavourite(DetailsOfflineActivity.this, this, movie, v);
    }


    public void showTrailer(View view) {
        Utility.showTrailer(this, trailerList, view);
    }

}
