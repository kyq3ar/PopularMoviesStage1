package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoriteContract;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteAdapterViewHolder> {

    private String[] mFavoriteData;
    private static final String TAG = MainActivity.class.getSimpleName();

    private final FavoriteAdapterOnClickHandler mClickHandler;
    private Context context;
    private Cursor mCursor;

    public interface FavoriteAdapterOnClickHandler {
        void onClick(String favorite);
    }
    public FavoriteAdapter(FavoriteAdapterOnClickHandler clickHandler, Context context, Cursor cursor) {
        mClickHandler = clickHandler;
        this.context = context;
        mCursor = cursor;
    }

    public FavoriteAdapter(FavoriteAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class FavoriteAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mFavoriteImageView;

        public FavoriteAdapterViewHolder(View view) {
            super(view);
            mFavoriteImageView = (ImageView) view.findViewById(R.id.movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String favorite = mFavoriteData[adapterPosition];
            mClickHandler.onClick(favorite);
        }
    }

    @Override
    public FavoriteAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new FavoriteAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteAdapterViewHolder favoriteAdapterViewHolder, int position) {
        if(!mCursor.moveToPosition(position))
            return;

        String favorite = mCursor.getString(mCursor.getColumnIndex(
                FavoriteContract.FavoriteEntry.COLUMN_TITLE));

        String [] data = favorite.split("\\n");

        String base_url = "http://image.tmdb.org/t/p/w185";
        Picasso.with(context).load(base_url + data[0]).into(favoriteAdapterViewHolder.mFavoriteImageView);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setFavoriteData(String[] favoriteData) {
        mFavoriteData = favoriteData;
        notifyDataSetChanged();
    }
}
