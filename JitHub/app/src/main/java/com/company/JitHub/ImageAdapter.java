package com.company.JitHub;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<String> f = new ArrayList<String>();

    public ImageAdapter(Context c) {

        mContext = c;
    }

    @Override
    public int getCount() {

        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    public void showImageList() {

        File[] listFile;

        File file2 = new File(android.os.Environment.getExternalStorageDirectory(),"");
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (file.isDirectory()){

            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++)
            {
                f.add(listFile[i].getAbsolutePath());
            }
        }
    }

    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };
}
