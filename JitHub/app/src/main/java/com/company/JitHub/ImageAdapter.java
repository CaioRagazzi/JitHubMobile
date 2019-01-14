package com.company.JitHub;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    String[] mfilesPaths;

    public ImageAdapter(Context c, String[] filesPath) {
        mContext = c;
        mfilesPaths = filesPath;
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

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        //Mostrar thumbnail
        //Bitmap bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mfilesPaths[position]), 64, 64);
        //Mostrar imagem inteira
        Bitmap bmp = BitmapFactory.decodeFile(mfilesPaths[position], options);
        //Matrix matrix = new Matrix();
        //matrix.postRotate(90);
        //bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        imageView.setImageBitmap(bmp);

        return imageView;
    }
}
