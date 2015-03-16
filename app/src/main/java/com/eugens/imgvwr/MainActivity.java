package com.eugens.imgvwr;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.BufferUnderflowException;

import static android.view.View.OnClickListener;


public class MainActivity extends ActionBarActivity implements OnClickListener  {
    private final static String DBNAME = "ImagesDB";
    MyDBHelper mdb;
    SQLiteDatabase db;
    Cursor c;
    ImageView imageView;
    Button buttonNext;
    Button buttonPrevious;
   // byte[] img1;
    int position;
    final String LOG_TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonNext=(Button)findViewById(R.id.buttonNext);
        buttonPrevious=(Button)findViewById(R.id.buttonPrevious);
        buttonNext.setOnClickListener(this);
        buttonPrevious.setOnClickListener(this);
      //  position = 1;

        //Intent i = getIntent();
        Bundle extras = getIntent().getExtras();

        position = extras.getInt("pos");
        imageView = (ImageView)findViewById(R.id.imageView);


        mdb = new MyDBHelper(getApplicationContext(),DBNAME,null,1);
        db = mdb.getWritableDatabase();
        updateContext();
        if(c!=null&&c.getCount()>0&&position==0){
            c.moveToFirst();
            RetrieveImage();
            enableButtons();
        }
        else if(c!=null&&c.getCount()>0&&position<c.getCount()){
            c.moveToPosition(position);
            RetrieveImage();
            enableButtons();
        }
        else {
            showEmpty();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,0);

        }
        if (id == R.id.action_delete_this){

            deleteRecord();
        }
        if (id == R.id.action_delete_all) {

            db.execSQL("delete from images");
            //  this.deleteDatabase(DBNAME);
            Log.d(LOG_TAG,"Database successfully was DELETED)");
            Toast.makeText(this,"Database cleared successfully!",Toast.LENGTH_SHORT).show();
            //  onCreate(new Bundle());
            showEmpty();
        }
        if (id == R.id.action_gridView){
            Intent i = new Intent(this,GridViewActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(buttonNext.isPressed()&&position<c.getCount()-1&&c.getCount()>0){

            c.moveToNext();
            Toast.makeText(this,"C= "+c.getCount(),Toast.LENGTH_SHORT).show();
            RetrieveImage();
            position++;
            setTitle("Position: " + position);

        }
        else if(buttonPrevious.isPressed()&&position>0&&c.getCount()>0){

            c.moveToPrevious();
            RetrieveImage();
            position--;
            setTitle("Position: " + position);
        }
       /* else{
            Toast.makeText(this,"Can't move!",Toast.LENGTH_SHORT).show();
        }*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String realPath;


        if(resultCode==RESULT_OK) {
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());// NOT TESTED!!!!!

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

  /*  t.setText(realPath);
    Toast.makeText(this,"real path: "+realPath,Toast.LENGTH_LONG).show();
    check if path is null
      Uri uriFromPath = Uri.fromFile(new File(realPath));
      Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uriFromPath),null,options);
            //BitmapFactory.decodeFile(path, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            if(imageWidth > imageHeight) {
                options.inSampleSize = calculateInSampleSize(options,1280,720);//if landscape
            } else{
                options.inSampleSize = calculateInSampleSize(options,720,1280);//if portrait
            }
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriFromPath),null,options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    decodeAndAdd(bitmap);
*/
            Add(realPath);

            c.moveToLast();
            position = c.getCount();
            setTitle("Position: " + position);
            RetrieveImage();
            enableButtons();
        }

    }

    private void RetrieveImage() {

        int px = getResources().getDimensionPixelSize(R.dimen.image_size);
        String path = c.getString(1);

        //    File file = new File();

        Bitmap bitmap = ImageUtils.decodeSampledBitmapFromResource(path, px, px);
        Log.d("log", String.format("Required size = %s, bitmap size = %sx%s, byteCount = %s",
                px, bitmap.getWidth(), bitmap.getHeight(), bitmap.getByteCount()));
        imageView.setImageBitmap(bitmap);

       /*String path = c.getString(1);
        Toast.makeText(this,"exists "+ path,Toast.LENGTH_LONG).show();

        imageView.setImageDrawable(Drawable.createFromPath(path));

     /* Uri uriFromPath = Uri.fromFile(new File(path));

         imageView.setImageURI(uriFromPath);//
        /*img1 = c.getBlob(1);
        Bitmap b1 = BitmapFactory.decodeByteArray(img1, 0, img1.length);
        imageView.setImageBitmap(b1);*/
    }
    private void showEmpty() {
        setTitle("The database is empty");
        imageView.setImageResource(R.drawable.no_available_image);
        //Toast.makeText(this, "Table is empty", Toast.LENGTH_LONG).show();
        buttonPrevious.setEnabled(false);
        buttonNext.setEnabled(false);
    }

    private void enableButtons() {
        buttonPrevious.setEnabled(true);
        buttonNext.setEnabled(true);
    }

    //Операції з базами даних
    public void updateContext(){
        String[] col = {"_id", "image"};
        c = db.query("images", col, null, null, null, null, null);

    }
    public void Add(String path){

        ContentValues cv = new ContentValues();
        // cv.put("_id",position);
        cv.put("image",path);
        db.insert("images",null,cv);

        updateContext();

    }
    public void deleteRecord(){

        if(c.getCount()!=0) {
            db = mdb.getWritableDatabase();
            // db.execSQL("delete from images where _id='"+position+"'");

            Log.d(LOG_TAG, "--- Delete from mytable: ---");
            // удаляем по id
            int delCount = db.delete("images", "_id = " + c.getInt(c.getColumnIndex("_id")), null);
            Log.d(LOG_TAG, "deleted rows count = " + delCount);


            // db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'images'");
            Toast.makeText(this,"Deleted successfully!",Toast.LENGTH_SHORT).show();
            updateContext();

            int x = c.getCount(); //this will return number of records in current cursor
            if (x == 0) {
                // there are no records
                showEmpty();
            }
            else if (position<x)
            {
                // if postion is in center of all records
                c.move(position);
                //  position = c.getCount();
                setTitle("Position: " + position);
                RetrieveImage();
            }
            else
            {
                c.moveToLast();
                position = c.getCount();
                setTitle("Position: " + position);
                RetrieveImage();
            }

        }
        else{
            showEmpty();
        }
    }
 /*   public void decodeAndAdd(Bitmap bitmap){

        //CONverting Image to Blob
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),id);
        enableButtons();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
        byte[] img = bos.toByteArray();
        ContentValues cv = new ContentValues();
        // cv.put("_id",position);
        cv.put("image",img);
        db.insert("images",null,cv);

        updateContext();

    }*/




}
