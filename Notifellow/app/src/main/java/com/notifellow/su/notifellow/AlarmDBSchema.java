package com.notifellow.su.notifellow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class AlarmDBSchema extends SQLiteOpenHelper{
    private static AlarmDBSchema schemaInstance;

    private static final String DATABASE_NAME = "ALARMS_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "ALARM_TABLE";
    private static final String ID = "ID";
    private static final String TITLE = "title";
    private static final String START_DATE = "start_date";
    private static final String START_TIME = "start_time";
    private static final String END_DATE = "end_date";
    private static final String END_TIME = "end_time";
    private static final String REMIND_DATE = "remind_date";
    private static final String REMIND_TIME = "remind_time";
    private static final String LOCATION = "location";
    private static final String WIFINAME = "wifiname";
    private static final String NOTES = "notes";
    private static final String EMAIL = "email";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            ID + " TEXT, " +
            TITLE + " TEXT, " +
            START_DATE + " TEXT, " +
            START_TIME + " TEXT, " +
            END_DATE + " TEXT, " +
            END_TIME + " TEXT, " +
            REMIND_DATE + " TEXT, " +
            REMIND_TIME + " TEXT, " +
            LOCATION + " TEXT, " +
            WIFINAME + " TEXT, " +
            NOTES + " TEXT, " +
            EMAIL + " TEXT);";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public AlarmDBSchema(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static AlarmDBSchema getInstance(Context context) {
        if (schemaInstance == null) {
            schemaInstance = new AlarmDBSchema(context.getApplicationContext());
        }
        return schemaInstance;
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public long insertData(String id, String title, String start_date, String start_time, String end_date, String end_time, String remind_date, String remind_time, String location, String wifiname, String notes, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(TITLE, title);
        contentValues.put(START_DATE, start_date);
        contentValues.put(START_TIME, start_time);
        contentValues.put(END_DATE, end_date);
        contentValues.put(END_TIME, end_time);
        contentValues.put(REMIND_DATE, remind_date);
        contentValues.put(REMIND_TIME, remind_time);
        contentValues.put(LOCATION, location);
        contentValues.put(WIFINAME, wifiname);
        contentValues.put(NOTES, notes);
        contentValues.put(EMAIL, email);
        return db.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {ID, TITLE, START_DATE, START_TIME, END_DATE, END_TIME, REMIND_DATE, REMIND_TIME, LOCATION, WIFINAME, NOTES};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public Cursor getAllRowsForEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {ID, TITLE, START_DATE, START_TIME, END_DATE, END_TIME, REMIND_DATE, REMIND_TIME, LOCATION, WIFINAME, NOTES, EMAIL};
        Cursor cursor = db.query(TABLE_NAME, columns, REMIND_DATE + " != ?", new String[]{"NA"}, null, null, null);
        return cursor;
    }

    public long getRowCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return count;
    }

    public long getRowCountOfEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME, REMIND_DATE + " != ?", new String[]{"NA"});
        return count;
    }

    public long getRowCountByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME, EMAIL + " = ?", new String[]{email});
        return count;
    }

    public Cursor getAlarmByID(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {TITLE, START_DATE, START_TIME, END_DATE, END_TIME, REMIND_DATE, REMIND_TIME, LOCATION, WIFINAME, NOTES};
        Cursor cursor = db.query(TABLE_NAME, columns, ID + " = ? ", new String[]{id}, null, null, null);
        return cursor;
    }

    public Cursor getAlarmCodeByEmail(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {ID};
        Cursor cursor = db.query(TABLE_NAME, columns, EMAIL + " = ? ", new String[]{email}, null, null, null);
        return cursor;
    }

    public String getTitle(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{TITLE}, this.ID + " = ? ", new String[]{ID}, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        String title = cursor.getString(cursor.getColumnIndex(TITLE));
        return title;
    }

    public String getStartTime(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{START_TIME}, this.ID + " = ? ", new String[]{ID}, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        String startTime = cursor.getString(cursor.getColumnIndex(START_TIME));
        return startTime;
    }

    public String getEndTime(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{END_TIME}, this.ID + " = ? ", new String[]{ID}, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        String endTime = cursor.getString(cursor.getColumnIndex(END_TIME));
        return endTime;
    }

    public String getWifiName(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{WIFINAME}, this.ID + " = ? ", new String[]{ID}, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        String wifiName = cursor.getString(cursor.getColumnIndex(WIFINAME));
        return wifiName;
    }

    public String getLocation(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{LOCATION}, this.ID + " = ? ", new String[]{ID}, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        String location = cursor.getString(cursor.getColumnIndex(LOCATION));
        return location;
    }

    public String getEmail(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{EMAIL}, this.ID + " = ? ", new String[]{ID}, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        String email = cursor.getString(cursor.getColumnIndex(EMAIL));
        return email;
    }

    public long insertForWifiListener(String alarmCode, String title, String wifiName, String note, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, alarmCode);
        contentValues.put(TITLE, title);
        contentValues.put(REMIND_DATE, "NA");
        contentValues.put(REMIND_TIME, "NA");
        contentValues.put(LOCATION, "NA");
        contentValues.put(WIFINAME, wifiName);
        contentValues.put(NOTES, note);
        contentValues.put(EMAIL, email);
        return db.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getTasksForWifiListener(){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {ID, TITLE, WIFINAME, NOTES, EMAIL};
        Cursor cursor = db.query(TABLE_NAME, columns, REMIND_DATE + " = ? " + " AND " + LOCATION + " = ? ", new String[]{"NA", "NA"}, null, null, null);
        return cursor;
    }

    public int deleteRowForWifiListener(String wifiName){
        SQLiteDatabase db = this.getWritableDatabase();
        int num = db.delete(TABLE_NAME, WIFINAME + " = ? AND " + REMIND_DATE + " = ? AND " + LOCATION + " = ? ", new String[]{wifiName, "NA", "NA"});
        return num;
    }

    /*  *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   */

    public long insertForLocationListener(String alarmCode, String title, String location, String note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, alarmCode);
        contentValues.put(TITLE, title);
        contentValues.put(REMIND_DATE, "NA");
        contentValues.put(REMIND_TIME, "NA");
        contentValues.put(LOCATION, location);
        contentValues.put(WIFINAME, "NA");
        contentValues.put(NOTES, note);
        return db.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getTasksForLocationListener(){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {TITLE, LOCATION, NOTES};
        Cursor cursor = db.query(TABLE_NAME, columns, REMIND_DATE + " = ? " + " AND " + WIFINAME + " = ? ", new String[]{"NA", "NA"}, null, null, null);
        return cursor;
    }

    public int deleteRowForLocationListener(String location){
        SQLiteDatabase db = this.getWritableDatabase();
        int num = db.delete(TABLE_NAME, LOCATION + " = ? AND " + REMIND_DATE + " = ? AND " + WIFINAME + " = ? ", new String[]{location, "NA", "NA"});
        return num;
    }

    /*  *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   */

    public int deleteRow(String wifiName, String location, String remind_date, String remind_time){
        SQLiteDatabase db = this.getWritableDatabase();
        int num = db.delete(TABLE_NAME, WIFINAME + " = ? AND " + LOCATION + " = ? AND " + REMIND_DATE + " = ? AND " + REMIND_TIME + " = ? ", new String[]{wifiName, location, remind_date, remind_time});
        return num;
    }

    public int deleteByID(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        int num = db.delete(TABLE_NAME, ID + " = ? ", new String[]{id});
        return num;
    }

    public int deleteByEmail(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        int num = db.delete(TABLE_NAME, EMAIL + " = ? ", new String[]{email});
        return num;
    }

    public void deleteAllInstances(){
        Log.i("Schema","Going to delete all rows");

        if(getRowCount() > 0){
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, null, null);
            Log.i("Schema", "All rows have deleted!");
        }
    }

    public long updateEmailAddresses(String oldMail, String newMail){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(EMAIL, newMail);
        Log.i("AlarmDBSchema: ", "Next statement is Update EMAIL!!!");
        return db.update(TABLE_NAME, contentValues, EMAIL + " = ? ", new String[]{oldMail});
    }
}
