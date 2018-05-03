//package com.notifellow.su.notifellow.notes;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.DatabaseUtils;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
///**
// * Created by egealpay on 25.03.2018.
// */
//
//public class Schema extends SQLiteOpenHelper {
//    private static Schema schemaInstance;
//
//    private static final String DATABASE_NAME = "NOTES_DB";
//    private static final int DATABASE_VERSION = 1;
//    private static final String TABLE_NAME = "NOTES_TABLE";
//    private static final String TITLE = "title";
//    private static final String TEXTS = "texts";
//    private static final String EMAIL = "email";
//    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
//            EMAIL + " TEXT, " +
//            TITLE + " TEXT, " +
//            TEXTS + " TEXT, " +
//            "PRIMARY KEY (" + EMAIL + ", " + TITLE + "));";
//    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
//
//    private Schema(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    public static Schema getInstance(Context context) {
//        if (schemaInstance == null) {
//            schemaInstance = new Schema(context.getApplicationContext());
//        }
//        return schemaInstance;
//    }
//
//
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_TABLE);
//    }
//
//
//    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
//        db.execSQL(DROP_TABLE);
//        onCreate(db);
//    }
//
//    public long insertData(String email, String title, String message) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(EMAIL, email);
//        contentValues.put(TITLE, title);
//        contentValues.put(TEXTS, message);
//        return db.insert(TABLE_NAME, null, contentValues);
//    }
//
//    public void updateData(String email, String title, String message){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(TITLE, title);
//        contentValues.put(TEXTS, message);
//        db.update(TABLE_NAME, contentValues, EMAIL + " = ? AND " + TITLE + " = ? ", new String[]{email, title});
//    }
//
//    public Cursor getAllFields() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String[] columns = {TITLE, TEXTS};
//        return db.query(TABLE_NAME, columns, null, null, null, null, null);
//    }
//
//    public long getRowCount() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
//        return count;
//    }
//
//    public String getNote(String title){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_NAME, new String[]{TEXTS}, TITLE + " = ?", new String[]{title}, null, null, null);
//
//        if(cursor != null)
//            cursor.moveToFirst();
//
//        return cursor.getString(cursor.getColumnIndex(TEXTS));
//    }
//
//    public long updateEmailAddresses(String oldMail, String newMail){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues= new ContentValues();
//        contentValues.put(EMAIL, newMail);
//        Log.i("NoteDB: ", "Next statement is Update EMAIL!!!");
//        return db.update(TABLE_NAME, contentValues, EMAIL + " = ? ", new String[]{oldMail});
//    }
//}
