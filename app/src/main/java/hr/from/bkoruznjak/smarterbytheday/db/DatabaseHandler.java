package hr.from.bkoruznjak.smarterbytheday.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hr.from.bkoruznjak.smarterbytheday.db.enums.RiddleParameterEnum;

/**
 * Created by borna on 20.09.15..
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String TAG = "RIDDLES";
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 10;
    // Database Name
    private static final String DATABASE_NAME = "riddleManager";

    // Riddle table name
    private static final String TABLE_RIDDLES = "riddles";

    // Version table name
    private static final String TABLE_VERSION = "version_table";

    // Riddle Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_RIDDLE_TEXT = "riddle_text";
    private static final String KEY_RIDDLE_ANWSER = "riddle_anwser";
    private static final String KEY_VIEW_COUNT = "view_count";
    private static final String KEY_FAVORITE = "favorite";

    // Version Table Column names
    private static final String KEY_RIDDLE_DATABASE_VERSION = "riddle_version";

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
                + KEY_VIEW_COUNT + " INTEGER,"
                + KEY_FAVORITE + " INTEGER"
                + ")";

        String CREATE_VERSION_TABLE = "CREATE TABLE "
                + TABLE_VERSION
                + "("
                + KEY_ID + " VARCHAR(10) PRIMARY KEY,"
                + KEY_RIDDLE_DATABASE_VERSION + " INTEGER"
                + ")";

        db.execSQL(CREATE_RIDDLE_TABLE);
        db.execSQL(CREATE_VERSION_TABLE);

        ContentValues values = new ContentValues();
        values.put(KEY_ID, "1"); // Version id
        values.put(KEY_RIDDLE_DATABASE_VERSION, DATABASE_VERSION);
        db.insert(TABLE_VERSION, null, values);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDDLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSION);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Get table value of riddle database version
    public int getRiddleVersion() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_VERSION, new String[]{KEY_ID,
                            KEY_RIDDLE_DATABASE_VERSION}, KEY_ID + "=?",
                    new String[]{"1"}, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();

            int version = Integer.parseInt(cursor.getString(1));
            // return riddle
            return version;
        } catch (Exception e) {
            Log.e(TAG, "" + e);
        } finally {
            cursor.close();
        }
        return 0;
    }

    // Change table value of riddle database version
    public void changeRiddleVersion(int version) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RIDDLE_DATABASE_VERSION, version);

        // updating row
        db.update(TABLE_VERSION, values, KEY_ID + " = ?",
                new String[]{"1"});
    }

    // Adding new riddle
    public void addRiddle(Riddle riddle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, riddle.getId()); // Riddle id
        values.put(KEY_RIDDLE_TEXT, riddle.getRiddleText()); // Riddle text
        values.put(KEY_RIDDLE_ANWSER, riddle.getRiddleAnwser()); // Riddle anwser
        values.put(KEY_VIEW_COUNT, riddle.getViewCount()); // Riddle view count
        values.put(KEY_FAVORITE, riddle.getFavorite()); // 1 if riddle is favorite, 0 if not

        // Inserting Row
        db.insert(TABLE_RIDDLES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single riddle
    public Riddle getRiddle(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RIDDLES, new String[]{KEY_ID,
                            KEY_RIDDLE_TEXT, KEY_RIDDLE_ANWSER, KEY_VIEW_COUNT, KEY_FAVORITE}, KEY_ID + "=?",
                    new String[]{id}, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();

            Riddle riddle = new Riddle(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    Integer.parseInt(cursor.getString(3)),
                    Integer.parseInt(cursor.getString(4)));
            // return riddle
            return riddle;
        } catch (Exception e) {
            Log.e(TAG, "" + e);
        } finally {
            cursor.close();
        }
        return null;
    }

    // Getting All Riddles
    public List<Riddle> getAllRiddles(RiddleParameterEnum parameter) {
        String selectQuery = "";
        if (parameter == RiddleParameterEnum.DEFAULT) {
            // Select All Query
            selectQuery = "SELECT  * FROM " + TABLE_RIDDLES;
        } else if (parameter == RiddleParameterEnum.FAVORITE) {
            // Select Only favorites
            selectQuery = "SELECT  * FROM " + TABLE_RIDDLES + " WHERE " + KEY_FAVORITE + " = 1";
        } else if (parameter == RiddleParameterEnum.SEEN) {
            // Select Only seen riddles
            selectQuery = "SELECT  * FROM " + TABLE_RIDDLES + " WHERE " + KEY_VIEW_COUNT + " > 0";
        }
        List<Riddle> riddleList = new ArrayList<Riddle>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Riddle riddle = new Riddle();
                    riddle.setId(cursor.getString(0));
                    riddle.setRiddleText(cursor.getString(1));
                    riddle.setRiddleAnwser(cursor.getString(2));
                    riddle.setViewCount(Integer.parseInt(cursor.getString(3)));
                    riddle.setFavorite(Integer.parseInt(cursor.getString(4)));
                    // Adding riddles to list
                    riddleList.add(riddle);
                } while (cursor.moveToNext());
            }

            // return riddle list
            return riddleList;
        } catch (Exception e) {
            Log.e(TAG, "" + e);
        } finally {
            cursor.close();
        }
        return null;
    }

    // Updating single riddle
    public int updateRiddle(Riddle riddle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RIDDLE_TEXT, riddle.getRiddleText());
        values.put(KEY_RIDDLE_ANWSER, riddle.getRiddleAnwser());
        values.put(KEY_VIEW_COUNT, riddle.getViewCount());
        values.put(KEY_FAVORITE, riddle.getFavorite());

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
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
            return cursor.getCount();
        } catch (Exception ex) {
            Log.e(TAG, "" + ex);
        } finally {
            cursor.close();
        }
        return 0;
    }

    // Check if record already exists
    public boolean recordIdExistsInDb(String idValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_RIDDLES + " where " + KEY_ID + " = " + idValue;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}

