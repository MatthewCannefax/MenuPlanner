package com.matthewcannefax.menuplanner.arrayAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.activity.MainActivity;
import com.matthewcannefax.menuplanner.model.Enums.ActivityNavEnum;

public class MainActivityViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images = {R.drawable.dinner_portrait, R.drawable.ingredients_portrait, R.drawable.shopping_cart};


    public MainActivityViewPagerAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.swipe_layout, null);
        ImageView imageViewMain = view.findViewById(R.id.imageView2);

        imageViewMain.setImageBitmap(getSampledBitmap(images[position], true));

        imageViewMain.setScaleType(ImageView.ScaleType.FIT_XY);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    private Bitmap getSampledBitmap(int imageID, boolean isMainIMG){
        if(isMainIMG){
            return loadSampledResource(imageID, 400, 400);
        }else {
            return loadSampledResource(imageID, 200, 200);
        }
    }

    private Bitmap loadSampledResource(int imageId, int targetHeight, int targetWidth){

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(context.getResources(), imageId, options);

        int originalHeight = options.outHeight;
        int originalWidth = options.outWidth;
        int inSampleSize = 1;

        while ((originalHeight / (inSampleSize * 2)) > targetHeight && (originalWidth/(inSampleSize * 2)) > targetWidth){
            inSampleSize *= 2;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(context.getResources(), imageId, options);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
