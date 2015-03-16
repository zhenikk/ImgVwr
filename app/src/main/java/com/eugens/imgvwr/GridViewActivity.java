package com.eugens.imgvwr;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


public class GridViewActivity extends ActionBarActivity {
    private final static String DBNAME = "ImagesDB";
    GridView gridView;
    MyDBHelper mdb;
    SQLiteDatabase db;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);

        mdb = new MyDBHelper(getApplicationContext(),DBNAME,null,1);
        db = mdb.getWritableDatabase();
        updateContext();


        gridView = (GridView)findViewById(R.id.gridview);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                gridView.setAdapter(new ImageCursorLoaderAdapter(GridViewActivity.this,c));
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(GridViewActivity.this," "+position,Toast.LENGTH_SHORT).show();
                Intent i = new Intent(GridViewActivity.this, MainActivity.class);
                i.putExtra("pos",(int)position);
                startActivity(i);


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grid_view, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        String realPath;


        if (resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());// NOT TESTED!!!!!

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

            Add(realPath);
            restartActivity();

            // c.moveToLast();
            // position = c.getCount();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_add) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,0);

        }
        if (id == R.id.action_delete_all) {

            db.execSQL("delete from images");
            //  this.deleteDatabase(DBNAME);
            Log.d("Tag","Database successfully was DELETED)");
            Toast.makeText(this,"Database cleared successfully!",Toast.LENGTH_SHORT).show();
            //  onCreate(new Bundle());
            restartActivity();
        }

        return super.onOptionsItemSelected(item);
    }
    public void updateContext(){
        String[] col = {"_id", "image"};
        c = db.query("images", col, null, null, null, null, null);
        Log.d("TAg", "cursor got the data" + c.getCount());
    }
    public void Add(String path){

        ContentValues cv = new ContentValues();
        // cv.put("_id",position);
        cv.put("image",path);
        db.insert("images",null,cv);

        updateContext();

    }

    private void restartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }


}
