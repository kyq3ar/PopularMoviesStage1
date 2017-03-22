package com.example.android.popularmovies.data;


import android.provider.BaseColumns;

public class FavoriteContract implements BaseColumns {

    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME="favorites";
        public static final String ID="favorite_id";
        public static final String COLUMN_TITLE="title";
        //public static final String COLUMN_MOVIE_DATA="data";
        /*public static final String COLUMN_RELEASE_DATE="release";
        public static final String COLUMN_USER_RATING = "rating";
        public static final*/
    }
}
