package com.example.hammerox.oxmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


public class DetailsActivity extends AppCompatActivity
        implements DetailsFragment.OnFragmentInteractionListener,
                    OfflineFragment.OnFragmentInteractionListener {

    public static String movieID;
    public static String bundleTag = "movieID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        movieID = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        int sortOrder = getIntent().getIntExtra(Intent.EXTRA_DOCK_STATE, 0);

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString(bundleTag, movieID);

            switch (sortOrder) {
                case 0:
                case 1:
                    DetailsFragment detailsFrag = new DetailsFragment();
                    detailsFrag.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_fragment_details, detailsFrag, null)
                            .commit();
                    break;
                case 2:
                    OfflineFragment offlineFrag = new OfflineFragment();
                    offlineFrag.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_fragment_details, offlineFrag, null)
                            .commit();
                    break;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void favourite(View v) {
        Utility.setFavourite(DetailsActivity.this, this, DetailsFragment.movie, v);
    }


    public void showTrailer(View view) {
        Utility.showTrailer(this, DetailsFragment.trailerList, view);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
