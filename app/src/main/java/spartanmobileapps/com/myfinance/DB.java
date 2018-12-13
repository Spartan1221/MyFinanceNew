package spartanmobileapps.com.myfinance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=7;
    public static final String DATABSE_NAME="financeDb";

    public static final String TABLE_NAME1="tProfit";
    public static final String TABLE_NAME2="tSpend";
    public static final String TABLE_NAME3="tPayment";

    public static final String KEY_ID="_id";
    public static final String KEY_DATE1="dateMonth";
    public static final String KEY_DATE2="dateDay";
    public static final String KEY_DATE3="dateForPayment";
    public static final String KEY_TYPE="type";
    public static final String KEY_SUM="sum";

    SQLiteDatabase db;
    DB mDBHelper;

    public void delCount(String table){

        db=mDBHelper.getWritableDatabase();
        db.delete(table, KEY_ID + "= (SELECT MAX(_id) FROM "+ table+")", null);
    }



    public DB(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME1+"("
                +KEY_ID+" integer primary key,"+KEY_DATE1+" numeric,"+KEY_DATE2+" numeric,"+KEY_DATE3+" numeric,"+KEY_TYPE+" text,"+KEY_SUM+" real"+")");

        db.execSQL("create table "+TABLE_NAME2+"("
                +KEY_ID+" integer primary key,"+KEY_DATE1+" numeric,"+KEY_DATE2+" numeric,"+KEY_DATE3+" numeric,"+KEY_TYPE+" text,"+KEY_SUM+" real"+")");

        db.execSQL("create table "+TABLE_NAME3+"("
                +KEY_ID+" integer primary key,"+KEY_DATE1+" numeric,"+KEY_DATE2+" numeric,"+KEY_DATE3+" numeric,"+KEY_TYPE+" text,"+KEY_SUM+" real"+")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists "+TABLE_NAME1);
        db.execSQL("drop table if exists "+TABLE_NAME2);
        db.execSQL("drop table if exists "+TABLE_NAME3);

        onCreate(db);
    }




}


