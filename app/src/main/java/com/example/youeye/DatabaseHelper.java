package com.example.youeye;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PW = "pw";

    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;

        // 데이터베이스 파일 복사
        if (!checkDatabaseExists()){
            copyDatabaseFromAssets();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 테이블이 존재하지 않을 때에만 CREATE TABLE 구문 실행
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " ("
                + COLUMN_ID + " TEXT PRIMARY KEY, "
                + COLUMN_PW + " TEXT)");

        String createTableQuery = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_PW + " TEXT)";
        db.execSQL(createTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // onUpgrade()에서는 테이블을 업그레이드하는 작업을 수행할 수 있습니다.
        // 현재는 아무 작업도 수행하지 않습니다.
    }

    private boolean checkDatabaseExists() {
        SQLiteDatabase checkDB = null;
        try {
            String dbPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.d("DatabaseHelper", "Failed to open database: " + e.getMessage());
            // 데이터베이스가 존재하지 않음
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDatabaseFromAssets() {
        try {
            InputStream inputStream = mContext.getAssets().open(DATABASE_NAME);
            String outFileName = mContext.getDatabasePath(DATABASE_NAME).getPath();
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            Log.d("DatabaseHelper", "Database copied successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DatabaseHelper", "Failed to copy database: " + e.getMessage());
        }
    }
    public boolean authenticateUser(String id, String pw) {
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        boolean result = false;

        try {
            cursor = db.query(
                    TABLE_USERS,
                    new String[]{COLUMN_PW},
                    COLUMN_ID + " = ?",
                    new String[]{id},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_PW);

                if (columnIndex != -1) {
                    String storedPW = cursor.getString(columnIndex);
                    result = pw.equals(storedPW);
                } else {
                    Log.e("LoginActivity", "Column index is -1. Column not found.");
                }
            } else {
                Log.e("LoginActivity", "No results found for id: " + id);
            }
        } catch (Exception e) {
            Log.e("LoginActivity", "Error querying database: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

}
