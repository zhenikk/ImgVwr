package com.eugens.imgvwr;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Eugen on 09.03.2015.
 */
public class ImageCursorLoaderAdapter extends CursorAdapter {
    Context mContext;

    public ImageCursorLoaderAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.single_image_item, parent, false);
        bindView(v, context, cursor);
        return v;

    }

    public int getScreenResolution(){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x; //returning width of display

    }



    public int getImSize(){
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = getScreenResolution();


        int ImSize;
        int rotation = display.getRotation();

        if(rotation == Surface.ROTATION_0 ){

            ImSize  = width/2;
        }
        else if(rotation==  Surface.ROTATION_90||rotation== Surface.ROTATION_270){
            ImSize = width/3;

        }
        else ImSize = 0;
        Log.d("Rotation ", "Rotation" +rotation+"SIze"+ImSize);
        return ImSize;

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //int width = getScreenResolution();
        int padding = 0;
        int imSize = getImSize();

        ImageView img = (ImageView)view.findViewById(R.id.imageViewItem);

        img.setLayoutParams(new LinearLayout.LayoutParams(imSize,imSize));
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setPadding(padding,padding,padding,padding);

        String path = cursor.getString(1);
        Bitmap b = ImageUtils.decodeSampledBitmapFromResource(path,imSize,imSize);
        img.setImageBitmap(b);

    }
}
