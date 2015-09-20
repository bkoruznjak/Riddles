package hr.from.bkoruznjak.myfirstandroidapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by borna on 20.09.15..
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "riddleManager";

    // Riddle table name
    private static final String TABLE_RIDDLES = "riddles";

    // Riddle Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_RIDDLE_TEXT = "riddle_text";
    private static final String KEY_RIDDLE_ANWSER = "riddle_anwser";
    private static final String KEY_VIEW_COUNT = "view_count";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RIDDLE_TABLE = "CREATE TABLE "
                + TABLE_RIDDLES
                + "("
                + KEY_ID + " VARCHAR(10) PRIMARY KEY,"
                + KEY_RIDDLE_TEXT + " TEXT,"
                + KEY_RIDDLE_ANWSER + " TEXT,"
                + KEY_VIEW_COUNT + " INTEGER"
                + ")";
        db.execSQL(CREATE_RIDDLE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDDLES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new riddle
    public void addRiddle(Riddle riddle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, riddle.getId()); // Riddle id
        values.put(KEY_RIDDLE_TEXT, riddle.getRiddleText()); // Riddle text
        values.put(KEY_RIDDLE_ANWSER, riddle.getRiddleAnwser()); // Riddle anwser
        values.put(KEY_VIEW_COUNT, riddle.getViewCount()); // Riddle view count

        // Inserting Row
        db.insert(TABLE_RIDDLES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single riddle
    public Riddle getRiddle(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RIDDLES, new String[]{KEY_ID,
                        KEY_RIDDLE_TEXT, KEY_RIDDLE_ANWSER, KEY_VIEW_COUNT}, KEY_ID + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Riddle riddle = new Riddle(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                Integer.parseInt(cursor.getString(3)));
        // return riddle
        return riddle;
    }

    // Getting All Riddles
    public List<Riddle> getAllRiddles() {
        List<Riddle> riddleList = new ArrayList<Riddle>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RIDDLES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Riddle riddle = new Riddle();
                riddle.setId(cursor.getString(0));
                riddle.setRiddleText(cursor.getString(1));
                riddle.setRiddleAnwser(cursor.getString(2));
                riddle.setViewCount(Integer.parseInt(cursor.getString(3)));
                // Adding riddles to list
                riddleList.add(riddle);
            } while (cursor.moveToNext());
        }

        // return riddle list
        return riddleList;
    }

    // Updating single riddle
    public int updateRiddle(Riddle riddle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RIDDLE_TEXT, riddle.getRiddleText());
        values.put(KEY_RIDDLE_ANWSER, riddle.getRiddleAnwser());
        values.put(KEY_VIEW_COUNT, riddle.getViewCount());

        // updating row
        return db.update(TABLE_RIDDLES, values, KEY_ID + " = ?",
                new String[]{riddle.getId()});
    }

    // Deleting single riddle
    public void deleteRiddle(Riddle riddle) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RIDDLES, KEY_ID + " = ?",
                new String[]{riddle.getId()});
        db.close();
    }


    // Getting Riddle Count
    public int getRiddleCount() {
        String countQuery = "SELECT  * FROM " + TABLE_RIDDLES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // Check if record already exists
    public boolean recordIdExistsInDb(String idValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_RIDDLES + " where " + KEY_ID + " = " + idValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}

