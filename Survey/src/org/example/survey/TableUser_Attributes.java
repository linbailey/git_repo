package org.example.survey;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableUser_Attributes extends SQLiteOpenHelper 
{
	private static final String TABLE_NAME = "user_attributes";
	private static final String DATABASE_NAME = "user_data2.db";
	private static final int DATABASE_VERSION = 1;	
	private static final String GENDER = "gender";
	private static final String GENDER_IMP = "gender_imp";
	private static final String AGE = "age";
	private static final String AGE_IMP = "age_imp";
	private static final String NATIONALITY = "nationality";
	private static final String NATIONALITY_IMP = "nationality_imp";
	private static final String GREWUP = "grewup";
	private static final String GREWUP_IMP = "grewup_imp";
	private static final String HOME = "home";
	private static final String HOME_IMP = "home_imp";
	private static final String SCHOOL = "school";
	private static final String SCHOOL_IMP = "school_imp";
	private static final String MAJOR = "major";
	private static final String MAJOR_IMP = "major_imp";
	private static final String MINOR = "minor";
	private static final String MINOR_IMP = "minor_imp";
	private static final String YEAR = "year";
	private static final String YEAR_IMP = "year_imp";
	private static final String WORK_FIELD = "work_field";
	private static final String WORK_FIELD_IMP = "work_field_imp";
	private static final String POSITION = "position";
	private static final String POSITION_IMP = "position_imp";
	private static final String EMPLOYER = "employer";
	private static final String EMPLOYER_IMP = "employer_imp";
	private static final String SEXUAL_ORIENTATION = "sexual_orientation";
	private static final String SEXUAL_ORIENTATION_IMP = "sexual_orientation_imp";
	private static final String RELATIONSHIP = "relationship";
	private static final String RELATIONSHIP_IMP = "relationship_imp";
	private static final String RELIGIOUS = "religious";
	private static final String RELIGIOUS_IMP = "religious_imp";
	private static final String INTEREST1 = "interest1";
	private static final String INTEREST1_IMP = "interest1_imp";
	private static final String INTEREST2 = "interest2";
	private static final String INTEREST2_IMP = "interest2_imp";
	private static final String INTEREST3 = "interest3";
	private static final String INTEREST3_IMP = "interest3_imp";
	private static final String INTEREST4 = "interest4";
	private static final String INTEREST4_IMP = "interest4_imp";
	private static final String INTEREST5 = "interest5";
	private static final String INTEREST5_IMP = "interest5_imp";
			
	//create a helper object for the Events database
	public TableUser_Attributes(Context ctx)
	{
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GENDER + " TEXT NOT NULL, " + GENDER_IMP + " INTEGER NOT NULL, " + AGE +" INTEGER NOT NULL, " + AGE_IMP + " INTEGER NOT NULL, " + NATIONALITY + " TEXT NOT NULL, "  + NATIONALITY_IMP + " INTEGER NOT NULL, " + GREWUP + " TEXT NOT NULL, " + GREWUP_IMP + " INTEGER NOT NULL, " + HOME + " TEXT NOT NULL, " + HOME_IMP + " INTEGER NOT NULL, " + SCHOOL + " TEXT NOT NULL, " + SCHOOL_IMP + " INTEGER NOT NULL, " + MAJOR + " TEXT NOT NULL, " + MAJOR_IMP + " INTEGER NOT NULL, " + MINOR + " TEXT NOT NULL, " + MINOR_IMP + " INTEGER NOT NULL, " + YEAR + " TEXT NOT NULL, " + YEAR_IMP + " INTEGER NOT NULL, " + WORK_FIELD + " TEXT NOT NULL, " + WORK_FIELD_IMP + " INTEGER NOT NULL, " + POSITION + " TEXT NOT NULL, " + POSITION_IMP + " INTEGER NOT NULL, " + EMPLOYER + " TEXT NOT NULL, " + EMPLOYER_IMP + " INTEGER NOT NULL, " + SEXUAL_ORIENTATION + " TEXT NOT NULL, " + SEXUAL_ORIENTATION_IMP + " INTEGER NOT NULL, " + RELATIONSHIP + " TEXT NOT NULL, " + RELATIONSHIP_IMP + " INTEGER NOT NULL, " + RELIGIOUS + " TEXT NOT NULL, " + RELIGIOUS_IMP + " INTEGER NOT NULL, " + INTEREST1 + " TEXT NOT NULL, " + INTEREST1_IMP + " INTEGER NOT NULL, " + INTEREST2 + " TEXT NOT NULL, " + INTEREST2_IMP + " INTEGER NOT NULL, " + INTEREST3 + " TEXT NOT NULL, " + INTEREST3_IMP + " INTEGER NOT NULL, " + INTEREST4 + " TEXT NOT NULL, " + INTEREST4_IMP + " INTEGER NOT NULL, " + INTEREST5 + " TEXT NOT NULL, " + INTEREST5_IMP + " INTEGER NOT NULL)");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}		
}
