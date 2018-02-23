package uk.ac.kent.ml555.flickrviewer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, "favorites", null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE favorites (itemId INTEGER PRIMARY KEY AUTOINCREMENT,itemUrl CHAR(250));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String url){
        SQLiteDatabase database = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put("itemUrl", url);
        database.insert("favorites", null, values);

        database.close();

    }

    public void remove (int itemId){
        SQLiteDatabase db = this.getWritableDatabase();

        String condition = "itemId = " + itemId;
        db.delete("favorites", condition, null);

    }
}
