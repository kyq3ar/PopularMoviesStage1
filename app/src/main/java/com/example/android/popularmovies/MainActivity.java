package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 4);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        sortOrder = "popular";
        setTitle("Popular");
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if(isOnline())
            loadMovieData();
        else
            showErrorMessage();
    }

    private void loadMovieData() {
        showMovieDataView();
        new FetchMovieTask().execute(sortOrder);
    }

    @Override
    public void onClick(String movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intentToStartDetailActivity);
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            String sortOrder = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortOrder);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                String[] information = MovieJsonUtils
                        .getMovieInformationFromJson(MainActivity.this, jsonMovieResponse);

                return information;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_popular) {
            if(sortOrder.equals("popular")){
                Toast toast = Toast.makeText(getApplicationContext(), "Already sorted by popularity.",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                mMovieAdapter.setMovieData(null);
                sortOrder = "popular";
                setTitle("Popular");
                loadMovieData();
            }
            return true;
        }
        else if (id == R.id.sort_top) {
            if(sortOrder.equals("top_rated")){
                Toast toast = Toast.makeText(getApplicationContext(), "Already sorted by Top-Rated.",
                        Toast.LENGTH_LONG);
                toast.show();

            }
            else {
                mMovieAdapter.setMovieData(null);
                sortOrder = "top_rated";
                setTitle("Top-Rated");
                loadMovieData();
            }
            return true;
        }
        else {
            if(sortOrder.equals("favorites")){
                Toast toast = Toast.makeText(getApplicationContext(), "Already sorted by Favorites.",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                mMovieAdapter.setMovieData(null);
                sortOrder = "favorites";
                setTitle("Favorites");
                SharedPreferences sp = getSharedPreferences("favorites", Activity.MODE_PRIVATE);
                Map<String, ?> favorites = sp.getAll();
                String [] movieData = new String [favorites.size()];
                int count = 0;
                for (Map.Entry<String, ?> entry : favorites.entrySet()) {
                    movieData[count] = entry.getValue().toString();
                    count++;
                }
                mMovieAdapter.setMovieData(movieData);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
