package org.example.survey;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableESM_data extends SQLiteOpenHelper 
{
	private static final String TABLE_NAME = "ESM_data";
	private static final String DATABASE_NAME = "user_data.db";
	private static final int DATABASE_VERSION = 1;	
	private static final String USER_ID = "user_ID";
	private static final String TIMESTAMP = "timestamp";
	private static final String GEOLOCATION = "geolocation";
	private static final String GENERAL_INTEREST = "general_interest";
	private static final String ATTRIBUTE = "attribute";
	private static final String ATTRIBUTE_TYPE = "attribute_type";
	private static final String INTERESTED = "interested";
	private static final String REASON_SIMILARITY = "reason_similarity";
	private static final String REASON_PLACE = "reason_place";
	private static final String REASON_ACTIVITY = "reason_activity";
	private static final String REASON_ENTOURAGE = "reason_entourage";
	private static final String PLACE_TYPE = "place_type";
	private static final String SOCIABILITY = "sociability";
	private static final String RARITY = "rarity";
	private static final String FAMILIARITY_PPL = "familiarity_ppl";
	private static final String CROWDEDNESS = "crowdedness";
	private static final String BUSYNESS = "busyness";
	//private static final String INTEREST_STRENGTH = "interest_strength";
	private static final String FAMILIARITY_PLACE = "familiarity_place";
	private static final String SYNC = "SYNC";
			
	//create a helper object for the Events database
	public TableESM_data(Context ctx)
	{
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_ID + " INTEGER NOT NULL, " + TIMESTAMP + " TEXT NOT NULL, " + GEOLOCATION + " TEXT NOT NULL, " + GENERAL_INTEREST + " INTEGER NOT NULL, " + ATTRIBUTE +" TEXT NOT NULL, " + ATTRIBUTE_TYPE + " TEXT NOT NULL, " + INTERESTED + " INTEGER NOT NULL, "  + REASON_SIMILARITY + " INTEGER NOT NULL, " + REASON_PLACE + " INTEGER NOT NULL, " + REASON_ACTIVITY + " INTEGER NOT NULL, " + REASON_ENTOURAGE + " INTEGER NOT NULL, " + PLACE_TYPE + " INTEGER NOT NULL, " + SOCIABILITY + " INTEGER NOT NULL, " + RARITY + " INTEGER NOT NULL, " + FAMILIARITY_PPL + " INTEGER NOT NULL, " + CROWDEDNESS + " INTEGER NOT NULL, " + BUSYNESS + " INTEGER NOT NULL, " + FAMILIARITY_PLACE + " INTEGER NOT NULL, " + SYNC + " INTEGER NOT NULL)");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}		
}
