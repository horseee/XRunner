package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class DBHelper extends SQLiteOpenHelper{

    public static final String PointDatabase = "Point_Info";
    public static final String PointDate = "DateID";
    public static final String PointID = "RecordID";
    public static final String PointLa = "Latitude";
    public static final String PointLo = "Longitude";
    public static final String PointSpeed = "Speed";

    public static final String UserRecordDatabase = "User_Record";
    public static final String User = "UserID";
    public static final String Date = "DateID";
    public static final String RunDis = "Distance";
    public static final String Runtime = "Time";
    public static final String Runspeed = "AverageSpeed";

    public static final String UserDatabase = "User_Info";
    public static final String UserID = "UserID";
    public static final String Password = "Password";
    public static final String Gender = "Gender";
    public static final String Age = "Age";

    public static final String LevelDatabase="User_Level";

    public static final String TAG = "Database: ";



    public DBHelper(Context context) {
        super(context, "PointInfo", null, 1);
    }

    // onCreate function is called the first time DBHelper is created.
    // It means that if one database is created, than this function will not be called again
    // So if you want to create a new database, put it in onOpen.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists Point_Info(" +
                "RecordID integer, " +
                "DateID varchar," +
                "Latitude double, " +
                "Longitude double, " +
                "Speed float," +
                "primary key (RecordId,DateId)" +
                ");"
        );

        db.execSQL("create table if not exists User_Photo(" +
                "UserID varchar primary key," +
                "Image BLOB" +
                ");"
        );

        db.execSQL("create table if not exists User_Record(" +
                "UserID varchar," +
                "DateID varchar, "+
                "Distance float, "+
                "Time String, "+
                "AverageSpeed float," +
                "primary key(UserID, DateID)" +
                ");"
        );

        db.execSQL("create table if not exists Running_Photo(" +
                "UserID varchar," +
                "DateID varchar, "+
                "Image BLOB," +
                "primary key(UserID, DateID)" +
                ");"
        );

        db.execSQL("create table if not exists User_Info(" +
                "UserID varchar primary key, " +
                "Password varchar, " +
                "Gender int, " +
                "Age int" +
                ")"
        );

        db.execSQL("create table if not exists User_Level(" +
                "UserID varchar primary key," +
                "level int, "+
                "exp int," +
                "dis float" +
                ")"
        );

        db.execSQL("create table if not exists User_Model(" +
                "UserID varchar," +
                "ModelID int," +
                "ModelCount int," +
                "primary key(UserID, ModelID)" +
                ")"
        );
    }

    // onOpen will be called when the database is open.
    // So most of the create database SQL is put in this function (Solving the problem in onCreate).
    public void onOpen(SQLiteDatabase db) {
        db.execSQL("create table if not exists Point_Info(" +
                "RecordID integer, " +
                "DateID varchar," +
                "Latitude double, " +
                "Longitude double, " +
                "Speed float," +
                "primary key (RecordId,DateId)" +
                ");"
        );

        db.execSQL("create table if not exists User_Photo(" +
                "UserID varchar primary key," +
                "Image BLOB" +
                ");"
        );

        db.execSQL("create table if not exists User_Info(" +
                "UserID varchar primary key, " +
                "Password varchar, " +
                "Gender int, " +
                "Age int" +
                ")"
        );

        db.execSQL("create table if not exists User_Level(" +
                "UserID varchar primary key," +
                "level int, "+
                "exp int," +
                "dis float" +
                ")"
        );


        db.execSQL("create table if not exists User_Record(" +
                "UserID varchar," +
                "DateID varchar, "+
                "Distance float, "+
                "Time String, "+
                "AverageSpeed float," +
                "primary key(UserID, DateID)" +
                ");"
        );

        db.execSQL("create table if not exists Running_Photo(" +
                "UserID varchar," +
                "DateID varchar, "+
                "Image BLOB," +
                "primary key(UserID, DateID)" +
                ");"
        );

        db.execSQL("create table if not exists User_Model(" +
                "UserID varchar," +
                "ModelID int," +
                "ModelCount int," +
                "primary key(UserID, ModelID)" +
                ")"
        );
    }

    //Insert Operation for run point
    // Get position(latitude, longitude) from the amaplocation.
    // Use ContentValues class to insert data.
    public void initUserModel(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i=0; i<=6; i++) {
            cv.put("UserID", name);
            cv.put("ModelID", i);
            cv.put("ModelCount", 0);
            db.insert("User_Model", null, cv);
        }
    }

    public void insertPointPosition(String DateID, int Recordptr, double la, double lo, float spe) {
        SQLiteDatabase db = getWritableDatabase();      // get the database list.
        ContentValues cv = new ContentValues();
        cv.put(PointDate, DateID);
        cv.put(PointID, Recordptr);
        cv.put(PointLa, la);
        cv.put(PointLo, lo);
        cv.put(PointSpeed, spe);
        db.insert(PointDatabase, null, cv);
    }

    //insert distance for running record
    //Get DateID and dis from the amaplocation.
    //DateID is the primary key for user's one running record.
    public void insertDistance(String name, String DateID, float dis, String time, float speed) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(User, name);
        cv.put(Date, DateID);
        cv.put(RunDis, dis);
        cv.put(Runtime, time);
        cv.put(Runspeed, speed);
        db.insert(UserRecordDatabase, null, cv);
    }

    //insert user's infomation
    //Get ID , password , isMale , age FROM SignUpActivity
    public void insertUser(String ID, String key, int isMale, int age) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(UserID, ID);
        cv.put(Password, key);
        cv.put(Gender, isMale);
        cv.put(Age, age);
        insertUserLevel(ID);
        db.insert(UserDatabase, null, cv);
    }

    public void insertUserLevel(String ID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("UserID", ID);
        cv.put("level", 1);
        cv.put("exp", 0);
        cv.put("dis", 0.0f);
        Log.e("levelinsert", "1");
        db.insert("User_Level", null, cv);
    }

    //insert user's image
    public void insertUserImage(String ID, Bitmap bmp)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        cv.put("Image", os.toByteArray());
        cv.put("UserID",ID);
        db.insert("User_Photo", null, cv);
        Log.e("insert ", "success");
    }

    //insert user's running path image
    public void insertUserPathImage(String UserID, String DateID, Bitmap bmp)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        cv.put("Image", os.toByteArray());
        cv.put("UserID",UserID);
        cv.put("DateID", DateID);
        db.insert("Running_Photo", null, cv);
        Log.e("path image insert ", "success");
    }

    public void updateLevelAndDistance(String ID, float distance) {
        float tempDis = 0.0f;
        int tempExp = 0, level = 0;

        String selection = "UserID = ?";
        String[] selectionArgs = {ID};

        SQLiteDatabase db = getWritableDatabase();
        ContentValues updatedValues = new ContentValues();

        // (String table, String[] columns, String selection, String[] selectionArgs,String groupBy, String having, String orderBy, String limit)
        Cursor cursor = db.query(LevelDatabase, null, selection, selectionArgs, null, null, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            tempDis = cursor.getFloat(cursor.getColumnIndex("dis"));
            tempExp = cursor.getInt(cursor.getColumnIndex("exp"));
            Log.e("tempdis", ""+tempDis);
        }
        tempDis += distance;
        tempExp += distance;
        if (distance > 100) tempExp += 50;

        if (tempExp < 500) level = 1;
        else if (tempExp < 2000) level = 2;
        else if (tempExp < 4000) level = 3;
        else if (tempExp < 6000) level = 4;
        else if (tempExp < 8000) level = 5;
        else if (tempExp < 10000) level = 6;
        else level = (tempExp + 11000) / 3000;

        Log.e("TempLevel", ""+level);
        Log.e("TempExp", ""+tempExp);
        Log.e("Tempdis", ""+tempDis);
        updatedValues.put("dis", tempDis);
        updatedValues.put("exp", tempExp);
        updatedValues.put("level", level);
        String where = UserID + "=" + ID;
        // Update the row with the specified index with the new values.
        db.update("User_Level", updatedValues, selection, selectionArgs);
    }

    public void updateExpandLevel(String ID, int ModelID)
    {
        int tempExp = 0;
        int level;
        float tempDis;
        float[] ModelExp = {30, 70, 130, 150, 200, 250, 280, 350, 500, 700, 800};
        String selection = "UserID = ?";
        String[] selectionArgs = {ID};

        SQLiteDatabase db = getWritableDatabase();

        // (String table, String[] columns, String selection, String[] selectionArgs,String groupBy, String having, String orderBy, String limit)
        Cursor cursor = db.query(LevelDatabase, null, selection, selectionArgs, null, null, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            tempDis = cursor.getFloat(cursor.getColumnIndex("dis"));
            tempExp = cursor.getInt(cursor.getColumnIndex("exp"));
            Log.e("tempEXP", ""+tempExp);
        }
        else return;

        tempExp += ModelExp[ModelID];
        if (tempExp < 500) level = 1;
        else if (tempExp < 2000) level = 2;
        else if (tempExp < 4000) level = 3;
        else if (tempExp < 6000) level = 4;
        else if (tempExp < 8000) level = 5;
        else if (tempExp < 10000) level = 6;
        else level = ((tempExp + 11000) / 3000);

        ContentValues updatedValues = new ContentValues();
        updatedValues.put("dis", tempDis);
        updatedValues.put("exp", tempExp);
        updatedValues.put("level", level);
        // Update the row with the specified index with the new values.
        db.update("User_Level", updatedValues, selection, selectionArgs);

    }

    public void updateModelData(String ID, int ModelID, boolean add, int number)
    {
        SQLiteDatabase db = getWritableDatabase();

        String selection = "UserID = ? and ModelID = ?";
        String[] selectionArgs = {ID, String.valueOf(ModelID)};
        Cursor cursor = db.query("User_Model", null, selection, selectionArgs, null, null, null);
        if (cursor!= null) {
            cursor.moveToFirst();
            int Count = cursor.getInt(cursor.getColumnIndex("ModelCount"));
            if (add) {
                Count += number;
            } else {
                Count -= number;
            }
            ContentValues cv = new ContentValues();
            cv.put("UserID",ID);
            cv.put("ModelID", ModelID);
            cv.put("ModelCount", Count);
            db.update("User_Model", cv, selection , selectionArgs);
        }

        cursor = db.query("User_Model", null, selection, selectionArgs, null, null, null);
        if (cursor!= null) {
            cursor.moveToFirst();
            int Count = cursor.getInt(cursor.getColumnIndex("ModelCount"));
            Log.e("ModelCount", Count + "");
            Log.e("ModelNumber", ModelID + "");
        }
    }


    public void updateUserImage(String ID, Bitmap bm)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        String selection = "UserID = ?";
        String[] selectionArgs = {ID};

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, os);
        cv.put("Image", os.toByteArray());
        cv.put("UserID",ID);
        String where = "UserID=" + ID;
        db.update("User_Photo", cv, selection , selectionArgs);
        Log.e("UPDATE", "success");
    }


    //get the point info for specific date
    public Cursor getOnePointData(String dateID) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = "DateID = ?";
        String[] selectionArgs = {dateID};
        Log.e(TAG, "Start Querying");
        return db.query(PointDatabase, null, selection, selectionArgs, null, null, "RecordID ASC");
    }

    public int GetModelData(String ID, int ModelID) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = "UserID = ? and ModelID = ?";
        String[] selectionArgs = {ID, String.valueOf(ModelID)};
        Cursor cursor = db.query("User_Model", null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int Count = cursor.getInt(cursor.getColumnIndex("ModelCount"));
            return Count;
        }
        return -1;
    }

    public Cursor getUserInfo(String username){
        SQLiteDatabase db = getReadableDatabase();
        String selection = "UserID = ?";
        String[] selectionArgs = {username};
        Log.e(TAG, "Start Querying");
        return db.query(UserDatabase, null, selection, selectionArgs, null, null, null);
    }

    public int getUserLevel(String ID) {
        SQLiteDatabase db=getWritableDatabase();
        String selection = "UserID = ?";
        String[] selectionArgs = {ID};
        Cursor cursor = db.query(LevelDatabase, null, selection, selectionArgs, null, null, null);
        if (cursor!=null){
            cursor.moveToFirst();
            String User = cursor.getString(cursor.getColumnIndex("UserID"));
            Log.e("name", User);
            int level = cursor.getInt(cursor.getColumnIndex("level"));
            Log.e("level", ""+level);
            return level;
        }
        return -1;
    }

    public float getUserDis(String ID) {
        SQLiteDatabase db=getWritableDatabase();
        String selection = "UserID = ?";
        String[] selectionArgs = {ID};
        Cursor cursor = db.query(LevelDatabase, null, selection, selectionArgs, null, null, null);
        if (cursor!=null){
            cursor.moveToFirst();
            String User = cursor.getString(cursor.getColumnIndex("UserID"));
            Log.e("name", User);
            float dis= cursor.getInt(cursor.getColumnIndex("dis"));
            Log.e("dis", ""+dis);
            return dis;
        }
        return -1.0f;
    }


    //get running record for one day
    public Cursor getDateDis(String username, String dateID) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = "DateID = ?";
        String[] selectionArgs = {dateID};
        return db.query(UserRecordDatabase, null, selection, selectionArgs, null, null, null);
    }

    //get one's running record
    public Cursor getPersonRecord(String username) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = "UserID = ?";
        String[] selectionArgs = {username};
        return db.query(UserRecordDatabase, null, selection, selectionArgs, null, null, null);
    }


    //judge whether there already exits a username named ID in User_info
    public boolean canUserInsert(String ID){
        SQLiteDatabase db = getReadableDatabase();
        String selection = "UserID = ?";
        String[] selectionArgs = {ID};
        Log.e(TAG, "Start Querying");
        Cursor cursor = db.query(UserDatabase, new String[]{"COUNT(UserID)"}, selection, selectionArgs, null, null, null);
        if(cursor.moveToFirst()){
            int count = cursor.getInt(0);
            if(count == 0)
                return true;
        }
        return false;
    }

    public Cursor getUserModelInf(String ID) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = "UserID = ?";
        String[] selectionArgs = {ID};
        Cursor cursor = db.query("User_Model", null, selection, selectionArgs, null, null, null);
        return cursor;
    }

    //check if the password is correct
    public boolean IsPasswordCorrect(String ID, String key) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = "UserID = ?";  // "?" is important .If no "?", query will not call the selectionArgs
        String[] selectionArgs = {ID};
        //Cursor is the pointer to header of the first record that meet the condition.
        //Cursor query  (String table, String[] columns, String selection, String[] selectionArgs,String groupBy, String having, String orderBy, String limit)
        Cursor cursor = db.query(UserDatabase, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String CorrectKey = cursor.getString(cursor.getColumnIndex(Password));
                Log.e(TAG, "GETKEY = " + CorrectKey);       //Log is to show the record in the android monitor
                return  CorrectKey.equals(key);
            }
        }
        return false;
    }

    //get user's image
    public Bitmap getUserImage(String ID)
    {
        SQLiteDatabase db = getWritableDatabase();
        String selection = "UserID = ?";
        String[] selectionArgs = {ID};
        Log.e("get image id", ID);
        Cursor cursor = db.query("User_Photo", null, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        if (cursor == null) {
            Log.e("Image", "null");
            return null;
        }
        Log.e("image id", cursor.getString(cursor.getColumnIndex("UserID")));
        byte[] in = cursor.getBlob(cursor.getColumnIndex("Image"));
        Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
        return bmpout;
    }

    public Bitmap getRunningPathImage(String UserID, String DateID)
    {
        SQLiteDatabase db = getWritableDatabase();
        String selection = "DateID = ?";
        String[] selectionArgs = {DateID};
        Log.e("get image id", DateID);
        Cursor cursor = db.query("Running_Photo", null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            Log.e("image id","");
            cursor.moveToFirst();
            Log.e("image id", cursor.getString(cursor.getColumnIndex("UserID")));
            byte[] in = cursor.getBlob(cursor.getColumnIndex("Image"));
            Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
            return bmpout;
        }
        else {
            Log.e("Image", "null");
            return null;
        }

    }

    public void DropUserInfo() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE USER_INFO");
    }

    public void DropUserLevel() {
        SQLiteDatabase db = getWritableDatabase();
    }

    public void DropData() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from Point_Info");
        db.execSQL("delete from User_Record");
        db.execSQL("delete from User_Level");
        db.execSQL("delete from User_Info");
        db.execSQL("delete from User_Photo");
        db.execSQL("delete from Running_Photo");
    }





    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }
}
