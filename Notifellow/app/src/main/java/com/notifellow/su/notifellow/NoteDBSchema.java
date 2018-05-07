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

public class NoteDBSchema extends SQLiteOpenHelper {

    private static NoteDBSchema schemaInstance;

    private static final String TAG = NoteDBSchema.class.getSimpleName();

    private static final String TABLE_NAME = "NOTE_TABLE";
    private static final String ID = "ID";
    private static final String TITLE = "title";
    private static final String NOTE = "note";
    private static final String EMAIL = "email";
    private static final String IMAGE_PATH = "image_path";
    private static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + " (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            NOTE + " TEXT, " +
            IMAGE_PATH + " TEXT, " +
            EMAIL + " TEXT);";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    NoteDBSchema(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    public static NoteDBSchema getInstance(Context context) {
        if (schemaInstance == null) {
            schemaInstance = new NoteDBSchema(context.getApplicationContext());
        }
        return schemaInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public boolean addData(String title, String note, String itemPath, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TITLE, title);
        contentValues.put(NOTE, note);
        contentValues.put(IMAGE_PATH, itemPath);
        contentValues.put(EMAIL, email);

        Log.d(TAG, "addData: Adding " + title + "\nand\n" + note + "\nand\n" + itemPath + "\nto " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);
        //if date as inserted incorrectly it will return -1
        return (int) result != -1;
    }

    /**
     * Returns all the Notes from database
     *
     * @return
     */
    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    /**
     * Returns only the ID that matches the name passed in
     *
     * @param title
     * @param note
     * @param image_path
     * @param email
     * @return ID
     */
    public String getNoteID(String title, String note, String image_path, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{ID},
                TITLE      + " = ? " + " AND " +
                         NOTE       + " = ? " + " AND " +
                         IMAGE_PATH + " = ? " + " AND " +
                         EMAIL      + " = ?",
                new String[]{title, note, image_path, email},
                null, null, null);
        if(cursor != null)
            cursor.moveToFirst();

        String id = cursor.getString(cursor.getColumnIndex(ID));
        cursor.close();
        return id;
    }

    /**
     * Returns only the Image Path that matches the id passed in
     *
     * @param id
     * @return
     */
    public String getItemImagePath(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT " + IMAGE_PATH + " FROM " + TABLE_NAME + " WHERE " + ID + " = '" + id + "'";
//        return db.rawQuery(query, null);

        Cursor cursor = db.query(TABLE_NAME, new String[]{IMAGE_PATH}, ID + " = ? ",
                new String[]{String.valueOf(id)},null,null,null);

        if(cursor != null)
            cursor.moveToFirst();

        String imagePath = cursor.getString(cursor.getColumnIndex(IMAGE_PATH));
        cursor.close();
        return imagePath;
    }



    /**
     * Updates the name field
     *
     * @param id int
     * @param newTitle String
     */
    public void updateTitleField(int id, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, newTitle);
        Log.d(TAG, "updateName: Setting title to " + TITLE);
        db.update(TABLE_NAME, contentValues, ID + " = ? ", new String[]{String.valueOf(id)});
    }

    /**
     * Updates the ALl fields except email and id
     *
     * @param id int
     * @param newTitle String
     * @param newNote String
     * @param newImagePath String
     */
    public void updateAllButEmailAndId(int id, String newTitle, String newNote, String newImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, newTitle);
        contentValues.put(NOTE, newNote);
        contentValues.put(IMAGE_PATH, newImagePath);
        Log.d(TAG, "updateName: Setting title to " + TITLE);
        Log.d(TAG, "updateName: Setting note to " + NOTE);
        Log.d(TAG, "updateName: Setting image_path to " + IMAGE_PATH);
        db.update(TABLE_NAME, contentValues, ID + " = ? ", new String[]{String.valueOf(id)});
    }

    /**
     * Updates the IMAGE_PATH field
     *
     * @param id
     * @param newImagePath //     * @param oldImagePath
     */
    public void updateImagPathField(int id, String newImagePath) {
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
     */
    public void updateNoteField(int id, String newNote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE, newNote);
        Log.d(TAG, "updateName: Setting note to " + IMAGE_PATH);
        db.update(TABLE_NAME, contentValues, ID + " = ? ", new String[]{String.valueOf(id)});
    }

    public String getTitle(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{TITLE}, NoteDBSchema.ID + " = ? ", new String[]{ID}, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        String title = cursor.getString(cursor.getColumnIndex(TITLE));
        cursor.close();
        return title;
    }

    public String getEmail(String ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{EMAIL}, NoteDBSchema.ID + " = ? ", new String[]{ID}, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        String email = cursor.getString(cursor.getColumnIndex(EMAIL));
        cursor.close();
        return email;
    }

    public int deleteByID(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result_code = db.delete(TABLE_NAME, ID + " = ? ", new String[]{id});
        return result_code;
    }

    public int deleteByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result_code = db.delete(TABLE_NAME, EMAIL + " = ? ", new String[]{email});
        return result_code;
    }


    public long updateEmailAddresses(String oldMail, String newMail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EMAIL, newMail);
        Log.i("AlarmDBSchema: ", "Next statement is Update EMAIL!!!");
        return db.update(TABLE_NAME, contentValues, EMAIL + " = ? ", new String[]{oldMail});
    }
}
