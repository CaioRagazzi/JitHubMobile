package com.company.JitHub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    String[] mfilesPaths;

    public ImageAdapter(Context context, String[] filesPath) {
        this.context = context;
        mfilesPaths = filesPath;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return mfilesPaths.length;
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_grid_image, parent, false);
            holder = new ViewHolder();
            assert view != null;

            holder.imageView = (ImageView) view.findViewById(R.id.image);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String url = mfilesPaths[position];

        Picasso.get()
                .load("file://" + url)
                .placeholder(R.drawable.ic_launcher_background)
                .fit()
                .into(holder.imageView);

        return view;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
