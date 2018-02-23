package uk.ac.kent.ml555.flickrviewer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class FavoritesActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ImageListAdapter adapter;
    DatabaseHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);



        recyclerView = (RecyclerView) findViewById(R.id.photoListView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.hasFixedSize();
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);




        adapter = new ImageListAdapter(this);
        recyclerView.setAdapter(adapter);




        loadFavorites();


    }

    private void loadFavorites() {

        adapter.clearList();

        dbHelper = new DatabaseHelper(FavoritesActivity.this);

        // Prepare query
        SQLiteDatabase db  = dbHelper.getReadableDatabase();

        String[] columns = {"itemId", "itemUrl"};
        Cursor cursor = db.query("favorites", columns, null, null, null, null, "itemUrl");

        Log.d("DBDEMO", "" + cursor.getCount());

        // Go through all the entries in the database
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String url = cursor.getString(1);


            // Add data to the array list for the recyclerview
            ImageInfo photo = new ImageInfo();
            photo.url_m = url;

            adapter.imageList.add(photo);

            // Move to next entry
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        adapter.notifyDataSetChanged();
    }

    }
