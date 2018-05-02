//package com.example.notes;
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
// * Created by berk aktug on 27/05/2017.
// */
//
//public class NotesDBSchema extends SQLiteOpenHelper {
//
//    private static NotesDBSchema schemaInstance;
////    private SharedPreferences shared;
//
//    private static final String TAG = NotesDBSchema.class.getSimpleName();
//    private static final String DATABASE_NAME = "NOTES_DB";
//    private static final String TABLE_NAME = "note_table";
//    private static final int DATABASE_VERSION = 1;
//    private static final String ID = "ID";
////    private static final String EMAIL = "email";
//    private static final String TITLE = "title";
//    private static final String NOTE = "note";
//    private static final String IMAGE_PATH = "image_path";
//    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
//            ID + " TEXT, " +
//            TITLE + " TEXT, " +
////            EMAIL + " TEXT";
//            NOTE + " TEXT, " +
//            IMAGE_PATH + " TEXT);";
//    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
//
//    NotesDBSchema(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    public static NotesDBSchema getInstance(Context context) {
//        if (schemaInstance == null) {
//            schemaInstance = new NotesDBSchema(context.getApplicationContext());
//        }
//        return schemaInstance;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_TABLE);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
//    }
//
//
//
//    public boolean addData(String title, String note, String itemPath) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
////        contentValues.put(EMAIL, email);
//        contentValues.put(TITLE, title);
//        contentValues.put(NOTE, note);
//        contentValues.put(IMAGE_PATH, itemPath);
//
//        Log.d(TAG, "addData: Adding " + title + "\n" + note + "\nand \n" + itemPath + "\nto " + TABLE_NAME);
//
//        //if date as inserted incorrectly it will return -1
//        long result = db.insert(TABLE_NAME, null, contentValues);
//
//        return (int) result != -1;
//    }
//
//    public long getRowCount() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
//        return count;
//    }
//
//    public long getRowCountOfEvents() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME, null, null);
//        return count;
//    }
//
//    public String getTitle(String ID){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_NAME, new String[]{TITLE}, this.ID + " = ? ", new String[]{ID}, null, null, null);
//
//        if(cursor != null)
//            cursor.moveToFirst();
//
//        String title = cursor.getString(cursor.getColumnIndex(TITLE));
//        return title;
//    }
//
//
//    public String getNote(String ID){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_NAME, new String[]{NOTE}, this.ID + " = ? ", new String[]{ID}, null, null, null);
//
//        if(cursor != null)
//            cursor.moveToFirst();
//
//        String note = cursor.getString(cursor.getColumnIndex(NOTE));
//        return note;
//    }
//
//    public String getImagePath(String ID) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_NAME, new String[]{IMAGE_PATH}, this.ID + " = ? ", new String[]{ID}, null, null, null);
//
//        if (cursor != null) cursor.moveToFirst();
//
//        String image_path = cursor.getString(cursor.getColumnIndex(IMAGE_PATH));
//        return image_path;
//    }
//
////    public String getEmail(String ID){
////        SQLiteDatabase db = this.getReadableDatabase();
////        Cursor cursor = db.query(TABLE_NAME, new String[]{EMAIL}, this.ID + " = ? ", new String[]{ID}, null, null, null);
////
////        if(cursor != null)
////            cursor.moveToFirst();
////
////        String email = cursor.getString(cursor.getColumnIndex(EMAIL));
////        return email ;
////    }
//
//
//    /**
//     * Returns all the data from database
//     *
//     * @return
//     */
//    public Cursor getData() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT * FROM " + TABLE_NAME;
//        return db.rawQuery(query, null);
//    }
//


//
//    /**
//     * Returns only the ID that matches the name passed in
//     *
//     * @param title
//     * @return
//     */
//    public Cursor getItemID(String title) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT " + ID + " FROM " + TABLE_NAME + " WHERE " + TITLE + " = '" + title + "'";
//        return db.rawQuery(query, null);
//    }
//
////    /**
////     * Returns only the Image Path that matches the id passed in
////     *
////     * @param id
////     * @return
////     */
////    public Cursor getItemImagePath(String id) {
////        SQLiteDatabase db = this.getWritableDatabase();
////        String query = "SELECT " + IMAGE_PATH + " FROM " + TABLE_NAME + " WHERE " + ID + " = '" + id + "'";
////        return db.rawQuery(query, null);
////    }
//
//    /**
//     * Updates the NOTE field
//     *
//     * @param id
//     * @param newTitle
//     * @param newNote
//     * @param newImagePath
//     */
//    public void updateAllFields(int id, String newTitle, String newNote, String newImagePath) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query =
//                "UPDATE " + TABLE_NAME  + " " +
//                "SET "    + TITLE + " = '" + newTitle + "' "
//                          + NOTE + " = '" + newNote  + "'"
//                          + IMAGE_PATH + " = '" + newImagePath + "'" +
//                "WHERE "  + ID + " = '" + id + "'" ;
////                + " AND " + TITLE + " = '" + oldNote + "'";
//        Log.d(TAG, "updateName: query: " + query);
//        Log.d(TAG, "updateName: Setting name to " + newTitle);
//        db.execSQL(query);
//    }
//
//    /**
//     * Delete from database
//     *
//     * @param id        int
//     * @param title     String
//     * @param note      String
//     * @param imagePath String
//     */
//    public void deleteName(int id, String title, String note, String imagePath) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = new StringBuilder()
//                .append("DELETE FROM ")
//                .append(TABLE_NAME)
//                .append(" WHERE ")
//                .append(ID)
//                .append(" = '")
//                .append(id)
//                .append("' AND ")
//                .append(TITLE)
//                .append(" = '")
//                .append(title)
//                .append("' AND ")
//                .append(NOTE)
//                .append(" = ' ")
//                .append(note)
//                .append(" ' AND ")
//                .append(IMAGE_PATH)
//                .append(" = ' ")
//                .append(imagePath)
//                .append("'").toString();
//        Log.d(TAG, "deleteName: query: " + query);
//        Log.d(TAG, "deleteName: Deleting item with id: " + id + " from database.");
//        db.execSQL(query);
//    }
//}
package com.notifellow.su.notifellow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by User on 2/28/2017.
 */

public class NotesDBSchema extends SQLiteOpenHelper {

    private static final String TAG = NotesDBSchema.class.getSimpleName();

    private static final String TABLE_NAME = "NOTE_TABLE";
    private static final String ID = "ID";
    private static final String TITLE = "tittle";
    private static final String NOTE = "note";
    private static final String EMAIL = "email";
    private static final String IMAGE_PATH = "image_path";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
//            ID          + " TEXT, " +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE       + " TEXT, " +
            EMAIL       + " TEXT, " +
            NOTE        + " TEXT, " +
            IMAGE_PATH  + " TEXT);";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
//

    NotesDBSchema(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + NOTE + " TEXT, " + IMAGE_PATH + " TEXT );";
//        db.execSQL(createTable);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String tittle, String note, String itemPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TITLE, tittle);
        contentValues.put(NOTE, note);
        contentValues.put(IMAGE_PATH, itemPath);

        Log.d(TAG, new StringBuilder().append("addData: Adding ").append(tittle).append("\n").append(note).append("\nand \n").append(itemPath).append("\nto ").append(TABLE_NAME).toString());

        long result = db.insert(TABLE_NAME, null, contentValues);
        //if date as inserted incorrectly it will return -1
        //return result != -1;
        return (int) result != -1;
    }

    /**
     * Returns all the data from database
     *
     * @return
     */
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    /**
     * Returns only the ID that matches the name passed in
     *
     * @param name
     * @return
     */
    public Cursor getItemID(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID + " FROM " + TABLE_NAME + " WHERE " + TITLE + " = '" + name + "'";
        return db.rawQuery(query, null);
    }

    /**
     * Returns only the Image Path that matches the id passed in
     *
     * @param id
     * @return
     */
    public Cursor getItemImagePath(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + IMAGE_PATH + " FROM " + TABLE_NAME + " WHERE " + ID + " = '" + id + "'";
        return db.rawQuery(query, null);
    }

    /**
     * Updates the name field
     *
     * @param id
     * @param newName
     * @param oldName
     */
    public void updateName(int id, String newName, String oldName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + NOTE + " = '" + newName + "' WHERE " + ID + " = '" + id + "'" + " AND NOT " + NOTE + " = '" + oldName + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting NOTE to " + newName);
        db.execSQL(query);
    }

    /**
     * Updates the IMAGE_PATH field
     *
     * @param id
     * @param newImagePath
//     * @param oldImagePath
     */
    public void updateImagePath(int id, String newImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE_PATH, newImagePath);
        Log.d(TAG, "updateName: Setting imagepath to " + IMAGE_PATH);
        db.update(TABLE_NAME, contentValues, ID + " = ? ", new String[]{String.valueOf(id)});
    }

    /**
     * Updates the NOTE field
     *
     * @param id
     * @param newNote
     * @param oldNote
     */
    public void updateNote(int id, String newNote, String oldNote) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + TITLE + " = '" + newNote + "' WHERE " + ID + " = '" + id + "'" + " AND " + TITLE + " = '" + oldNote + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newNote);
        db.execSQL(query);
    }

//    public Cursor getAllRowsForEvents() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String[] columns = {ID, TITLE, NOTE, IMAGE_PATH};
//        return db.query(TABLE_NAME, columns, null, null, null, null, null);
//    }

    /**
     * Delete from database
     *
     * @param id        int
     * @param tittle     String
     * @param note      String
     * @param imagePath String
     */
    public void deleteName(int id, String tittle, String note, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = new StringBuilder()
                .append("DELETE FROM ")
                .append(TABLE_NAME)
                .append(" WHERE ")
                .append(ID)
                .append(" = '")
                .append(id)
                .append("' AND ")
                .append(TITLE)
                .append(" = '")
                .append(tittle)
                .append("' AND ")
                .append(NOTE)
                .append(" = ' ")
                .append(note)
                .append(" ' AND ")
                .append(IMAGE_PATH)
                .append(" = ' ")
                .append(imagePath)
                .append("'").toString();
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting item with id: " + id + " from database.");
        db.execSQL(query);
    }
}
