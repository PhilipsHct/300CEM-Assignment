package com.example.assignment_footage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.assignment_footage.models.Footage;
import com.example.assignment_footage.models.LocationRecord;
import com.example.assignment_footage.models.Note;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class FootageHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Assignment";

    private static final String TABLE_FOOTAGES = "Footage";
    private static final String TABLE_LOCATIONRECORDS = "LocationRecord";
    private static final String TABLE_NOTES = "Note";

    private static FootageHelper sInstance = null;

    // Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_FOOTAGEDATE = "footageDate";
    private static final String KEY_REMARK = "remark";

    // Location Record

    private static final String KEY_FOOTAGEID = "footageId";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_RECORDTIME = "recordTime";

    // Note

    private static final String KEY_LOCATIONRECORDID = "locationRecordId";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_PHOTOPATH = "photoPath";

    public FootageHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized FootageHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new FootageHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table Footage
        String createStatements = "CREATE TABLE IF NOT EXISTS " + TABLE_FOOTAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " VARCHAR(255) NOT NULL,"
                + KEY_FOOTAGEDATE + " DATE NOT NULL,"
                + KEY_REMARK + " VARCHAR(1000)"
                + ")";
        db.execSQL(createStatements);
        // Table LocationRecord
        createStatements = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATIONRECORDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_FOOTAGEID + " INTEGER NOT NULL, "
                + KEY_LONGITUDE + " FLOAT NOT NULL, "
                + KEY_LATITUDE + " FLOAT NOT NULL, "
                + KEY_RECORDTIME + " DATETIME NOT NULL"
                + ")";
        db.execSQL(createStatements);
        // Table Note
        createStatements = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_LOCATIONRECORDID + " INTEGER NOT NULL,"
                + KEY_CONTENT + " VARCHAR(1000) DEFAULT NULL,"
                + KEY_PHOTOPATH + " VARCHAR(1000) DEFAULT NULL"
                + ")";
        db.execSQL(createStatements);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOTAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONRECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        // Create tables
        onCreate(db);
    }

    public int addFootage(Footage footage){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, footage.getName());
        values.put(KEY_FOOTAGEDATE, footage.getFootageDate().toString());
        values.put(KEY_REMARK, footage.getRemark());

        db.insert(TABLE_FOOTAGES, null, values);

        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        if(cursor != null)
            cursor.moveToFirst();
        int row_id = cursor.getInt(0);
        db.close();
        return row_id;
    }

    public Footage getFootage(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FOOTAGES, new String[] {
                KEY_ID, KEY_NAME, KEY_FOOTAGEDATE, KEY_REMARK
        }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();

        Footage footage = new Footage(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                Date.valueOf(cursor.getString(2)),
                cursor.getString(2)
        );
        return footage;
    }

    public List<Footage> getAllFootages(){
        List<Footage> footages = new ArrayList<Footage>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FOOTAGES, new String[] {
                KEY_ID, KEY_NAME, KEY_FOOTAGEDATE, KEY_REMARK
        }, null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                footages.add(new Footage(
                        cursor.getInt(0),
                        cursor.getString(1),
                        Date.valueOf(cursor.getString(2)),
                        cursor.getString(2)
                ));
            } while (cursor.moveToNext());
        }
        return footages;
    }

    public int getFootagesCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] tableColumns = new String[]{
                "COUNT(*)"
        };
        String conds = null;
        String[] condsArgs = null;
        String group = null;
        String having = null;
        String order = null;
        String limit = null;
        Cursor cursor = db.query(TABLE_FOOTAGES,
                tableColumns, conds, condsArgs, group, having, order, limit);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int updateFootage(Footage footage){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, footage.getName());
        values.put(KEY_FOOTAGEDATE, footage.getFootageDate().toString());
        values.put(KEY_REMARK, footage.getRemark());

        return db.update(TABLE_FOOTAGES, values, KEY_ID + "=?",
                new String[] { String.valueOf(footage.getId()) });
    }

    public void deleteFootage(Footage footage){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOOTAGES, KEY_ID + "=?",
                new String[] { String.valueOf(footage.getId()) });
        db.close();
    }

    public int addLocationRecord(LocationRecord locationRecord){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FOOTAGEID, locationRecord.getFootageId());
        values.put(KEY_LONGITUDE, locationRecord.getLongitude());
        values.put(KEY_LATITUDE, locationRecord.getLatitude());
        values.put(KEY_RECORDTIME, locationRecord.getRecordTime().toString());

        db.insert(TABLE_LOCATIONRECORDS, null, values);

        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        if(cursor != null)
            cursor.moveToFirst();
        int row_id = cursor.getInt(0);
        db.close();
        return row_id;
    }

    public LocationRecord getLocationRecord(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOCATIONRECORDS, new String[] {
                KEY_ID, KEY_FOOTAGEID, KEY_LONGITUDE, KEY_LATITUDE, KEY_RECORDTIME
        }, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        LocationRecord locationRecord = new LocationRecord(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getFloat(2),
                cursor.getFloat(3),
                Time.valueOf(cursor.getString(4))
        );
        return locationRecord;
    }

    public List<LocationRecord> getAllLocationRecords(int footageId){
        List<LocationRecord> locationRecords = new ArrayList<LocationRecord>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor  = db.query(TABLE_LOCATIONRECORDS, new String[]{
                KEY_ID, KEY_FOOTAGEID, KEY_LONGITUDE, KEY_LATITUDE, KEY_RECORDTIME
        }, KEY_FOOTAGEID + "=?", new String[]{ String.valueOf(footageId) }, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                locationRecords.add(new LocationRecord(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getFloat(2),
                        cursor.getFloat(3),
                        Time.valueOf(cursor.getString(4))
                ));
            } while (cursor.moveToNext());
        }
        return locationRecords;
    }
    public int getLocationRecordCount(int footageId){
        SQLiteDatabase db = this.getReadableDatabase();
        String [] tableColumns = new String[]{
                "COUNT(*)"
        };
        String conds = KEY_FOOTAGEID + "=?";
        String[] condsArgs = {String.valueOf(footageId)};
        String group = null;
        String having = null;
        String order = null;
        String limit = null;
        Cursor cursor = db.query(TABLE_LOCATIONRECORDS,
                tableColumns, conds, condsArgs, group, having, order, limit);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int updateLocationRecord(LocationRecord locationRecord){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LONGITUDE, locationRecord.getLongitude());
        values.put(KEY_LATITUDE, locationRecord.getLatitude());
        values.put(KEY_RECORDTIME, locationRecord.getRecordTime().toString());

        return db.update(TABLE_LOCATIONRECORDS, values, KEY_ID + "=?",
                new String[] { String.valueOf(locationRecord.getId()) } );
    }

    public void deleteLocationRecord(LocationRecord locationRecord){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONRECORDS, KEY_ID + "=?",
                new String[] { String.valueOf(locationRecord.getId()) } );
        db.close();
    }

    public void deleteLocationRecord(Footage footage){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOOTAGES, KEY_ID + "=?",
                new String[] { String.valueOf(footage.getId()) } );
    }

    public int addNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATIONRECORDID, note.getLocationRecordId());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_PHOTOPATH, note.getPhotoPath());

        db.insert(TABLE_NOTES, null, values);

        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        if(cursor != null)
            cursor.moveToFirst();
        int row_id = cursor.getInt(0);
        db.close();
        return row_id;
    }

    public List<Note> getAllNotes(int locationRecordId){
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor  = db.query(TABLE_NOTES, new String[]{
                KEY_ID, KEY_LOCATIONRECORDID, KEY_CONTENT, KEY_PHOTOPATH
        }, KEY_LOCATIONRECORDID + "=?", new String[]{ String.valueOf(locationRecordId) }, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                notes.add(new Note(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            }while(cursor.moveToNext());
        }
        return notes;
    }

    public Note getNote(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES, new String[] {
                KEY_ID, KEY_LOCATIONRECORDID, KEY_CONTENT, KEY_PHOTOPATH
        }, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        Note note = new Note(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3)
        );
        return note;
    }

    public int getNoteCount(int locatioRecordId){
        SQLiteDatabase db = this.getReadableDatabase();
        String [] tableColumns = new String[]{
                "COUNT(*)"
        };
        String conds = KEY_LOCATIONRECORDID + "=?";
        String[] condsArgs = {String.valueOf(locatioRecordId)};
        String group = null;
        String having = null;
        String order = null;
        String limit = null;
        Cursor cursor = db.query(TABLE_NOTES,
                tableColumns, conds, condsArgs, group, having, order, limit);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int updateNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATIONRECORDID, note.getLocationRecordId());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_PHOTOPATH, note.getPhotoPath());

        return db.update(TABLE_NOTES, values, KEY_ID + "=?",
                new String[] { String.valueOf(note.getId()) } );
    }

    public void deleteNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + "=?",
                new String[] { String.valueOf(note.getId()) } );
        db.close();
    }

    public void deleteNote(LocationRecord locationRecord){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_LOCATIONRECORDID + "=?",
                new String[] { String.valueOf(locationRecord.getId()) } );
        db.close();
    }
}
