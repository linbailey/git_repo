package org.example.survey;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableAttType extends SQLiteOpenHelper 
{
	private static final String TABLE_NAME = "AttType";
	private static final String DATABASE_NAME = "user_data3.db";
	private static final int DATABASE_VERSION = 1;
	private static final String GENDER = "gender";
	private static final String AGE = "age";
	private static final String NATIONALITY = "nationality";
	private static final String GREWUP = "grewup";
	private static final String HOME = "home";
	private static final String SCHOOL = "school";
	private static final String MAJOR = "major";
	private static final String MINOR = "minor";
	private static final String YEAR = "year";
	private static final String WORK_FIELD = "work_field";
	private static final String POSITION = "position";
	private static final String EMPLOYER = "employer";
	private static final String SEXUAL_ORIENTATION = "sexual_orientation";
	private static final String RELATIONSHIP = "relationship";
	private static final String RELIGIOUS = "religious";
	private static final String INTEREST1 = "interest1";
	private static final String INTEREST2 = "interest2";
	private static final String INTEREST3 = "interest3";
	private static final String INTEREST4 = "interest4";
	private static final String INTEREST5 = "interest5";	
	
	//create a helper object for the Events database
	public TableAttType(Context ctx)
	{
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GENDER + " TEXT NOT NULL, " + AGE + " INTEGER NOT NULL, " + NATIONALITY + " TEXT NOT NULL, "  + GREWUP + " TEXT NOT NULL, " +  HOME + " TEXT NOT NULL, " + SCHOOL + " TEXT NOT NULL, " + MAJOR + " TEXT NOT NULL, " + MINOR + " TEXT NOT NULL, " + YEAR + " TEXT NOT NULL, " + WORK_FIELD + " TEXT NOT NULL, " + POSITION + " TEXT NOT NULL, " + EMPLOYER + " TEXT NOT NULL, " + SEXUAL_ORIENTATION + " TEXT NOT NULL, " + RELATIONSHIP + " TEXT NOT NULL, " + RELIGIOUS + " TEXT NOT NULL, " + INTEREST1 + " TEXT NOT NULL, " + INTEREST2 + " TEXT NOT NULL, " + INTEREST3 + " TEXT NOT NULL, " + INTEREST4 + " TEXT NOT NULL, " + INTEREST5 + " TEXT NOT NULL)");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}		
}
