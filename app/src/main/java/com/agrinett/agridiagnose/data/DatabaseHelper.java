package com.agrinett.agridiagnose.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.CharacteristicsEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.DiseaseModelEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.DiseaseModelDetailsEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.ScalesEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.QualitativeScaleEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.QualitativeScaleValuesEntry;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "agridiagnose.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper dbInstance;

    //SQL to create Characteristic table
    private static final String TABLE_CHARACTERISTICS_CREATE =
            "CREATE TABLE " + CharacteristicsEntry.TABLE_NAME + " (" +
//                    CharacteristicsEntry._ID + " INTEGER PRIMARY KEY, " +
                    CharacteristicsEntry.CHARACTERISTIC_ID + " INTEGER PRIMARY KEY," +
                    CharacteristicsEntry.COMMON_NAME + " TEXT," +
                    CharacteristicsEntry.SCIENTIFIC_NAME + " TEXT," +
                    CharacteristicsEntry.TYPE + " TEXT," +
                    CharacteristicsEntry.QUESTION + " TEXT," +
                    CharacteristicsEntry.RESPONSE_TYPE + " TEXT," +
                    CharacteristicsEntry.GROUP_PARENT + " TEXT," +
                    CharacteristicsEntry.QUESTION_ORDER + " TEXT)";

    //SQL to create DiseaseModel table
    private static final String TABLE_DISEASE_MODEL_CREATE =
            "CREATE TABLE " + DiseaseModelEntry.TABLE_NAME + " (" +
                    DiseaseModelEntry.DISEASE_ID + " INTEGER PRIMARY KEY," +
                    DiseaseModelEntry.TYPE + " TEXT," +
                    DiseaseModelEntry.DESCRIPTION + " TEXT," +
                    DiseaseModelEntry.SCIENTIFIC_NAME + " TEXT," +
                    DiseaseModelEntry.LEVEL + " INTEGER," +
                    DiseaseModelEntry.PARENT_ID + " TEXT," +
                    DiseaseModelEntry.REFERENCE + " TEXT," +
                    DiseaseModelEntry.NOTES + " TEXT)";

    //SQL to create Scales table
    private static final String TABLE_SCALES_CREATE =
            "CREATE TABLE " + ScalesEntry.TABLE_NAME + " (" +
//                    ScalesEntry._ID + " INTEGER PRIMARY KEY, " +
                    ScalesEntry.SCALES_ID + " INTEGER PRIMARY KEY," +
                    ScalesEntry.SCALES_TYPE + " TEXT," +
                    ScalesEntry.DESCRIPTION + " TEXT," +
                    ScalesEntry.CONTEXT + " TEXT)";

    //SQL to create DiseaseModelDetails table
    private static final String TABLE_DISEASE_MODEL_DETAILS_CREATE =
            "CREATE TABLE " + DiseaseModelDetailsEntry.TABLE_NAME + " (" +
                    DiseaseModelDetailsEntry.DISEASE_ID + " INTEGER NOT NULL," +
                    DiseaseModelDetailsEntry.CHARACTERISTIC_ID + " INTEGER NOT NULL," +
                    DiseaseModelDetailsEntry.SCALE_ID + " INTEGER NOT NULL," +
                    DiseaseModelDetailsEntry.PROBABILITY_VALUE + " REAL," +
                    DiseaseModelDetailsEntry.SCALE_MODEL + " TEXT," +
                    DiseaseModelDetailsEntry.SCALE_EQUATION + " TEXT," +
                    DiseaseModelDetailsEntry.BEST + " INTEGER," +
                    DiseaseModelDetailsEntry.WORST + " INTEGER," +
                    DiseaseModelDetailsEntry.REASON + " TEXT," +
                    DiseaseModelDetailsEntry.UNITS + " TEXT," +
                    DiseaseModelDetailsEntry.AHP + " TEXT," +
                    DiseaseModelDetailsEntry.SORT_STAGE + " INTEGER," +
                    "PRIMARY KEY (" + DiseaseModelDetailsEntry.DISEASE_ID + "," + DiseaseModelDetailsEntry.CHARACTERISTIC_ID + "," + DiseaseModelDetailsEntry.SCALE_ID + ")," +
                    "FOREIGN KEY (" + DiseaseModelDetailsEntry.DISEASE_ID + ") REFERENCES " + DiseaseModelEntry.TABLE_NAME +"(" + DiseaseModelEntry.DISEASE_ID +")," +
                    "FOREIGN KEY (" + DiseaseModelDetailsEntry.CHARACTERISTIC_ID + ") REFERENCES " + CharacteristicsEntry.TABLE_NAME + "(" + CharacteristicsEntry.CHARACTERISTIC_ID + ")," +
                    "FOREIGN KEY (" + DiseaseModelDetailsEntry.SCALE_ID + ") REFERENCES " + ScalesEntry.TABLE_NAME + "(" + ScalesEntry.SCALES_ID + "));";


    private static final String TABLE_QUALITATIVE_SCALES_CREATE =
            "CREATE TABLE " + QualitativeScaleEntry.TABLE_NAME + " (" +
                    QualitativeScaleEntry.SCALES_ID + " INTEGER NOT NULL," +
                    QualitativeScaleEntry.PROPERTY_ID + " INTEGER NOT NULL," +
                    QualitativeScaleEntry.PROPERTY + " TEXT," +
                    "PRIMARY KEY (" + QualitativeScaleEntry.PROPERTY_ID + ", " + QualitativeScaleEntry.SCALES_ID +")," +
                    "FOREIGN KEY (" + QualitativeScaleEntry.SCALES_ID + ") REFERENCES " + ScalesEntry.TABLE_NAME + "(" + ScalesEntry.SCALES_ID + "));";

    private static final String TABLE_QUALITATIVE_SCALES_VALUES_CREATE =
            "CREATE TABLE " + QualitativeScaleValuesEntry.TABLE_NAME + " (" +
                    QualitativeScaleValuesEntry.CHARACTERISTIC_ID + " INTEGER NOT NULL," +
                    QualitativeScaleValuesEntry.SCALES_ID + " INTEGER NOT NULL," +
                    QualitativeScaleValuesEntry.DISEASE_ID + " INTEGER NOT NULL," +
                    QualitativeScaleValuesEntry.PROPERTY_ID + " INTEGER NOT NULL," +
                    QualitativeScaleValuesEntry.PROPERTY + " TEXT," +
                    QualitativeScaleValuesEntry.PROPERTY_VALUE + " INTEGER," +
                    "PRIMARY KEY (" + QualitativeScaleValuesEntry.CHARACTERISTIC_ID + "," + QualitativeScaleValuesEntry.SCALES_ID + "," + QualitativeScaleValuesEntry.DISEASE_ID + "," + QualitativeScaleValuesEntry.PROPERTY_ID + ")," +
                    "FOREIGN KEY (" + QualitativeScaleValuesEntry.CHARACTERISTIC_ID + ") REFERENCES " + CharacteristicsEntry.TABLE_NAME + "(" + CharacteristicsEntry.CHARACTERISTIC_ID + ")," +
                    "FOREIGN KEY (" + QualitativeScaleValuesEntry.DISEASE_ID + ") REFERENCES " + DiseaseModelEntry.TABLE_NAME + "(" + DiseaseModelEntry.DISEASE_ID + ")," +
                    "FOREIGN KEY (" + QualitativeScaleValuesEntry.PROPERTY_ID + ") REFERENCES " + QualitativeScaleEntry.TABLE_NAME + "(" + QualitativeScaleEntry.PROPERTY_ID + ")," +
                    "FOREIGN KEY (" + QualitativeScaleValuesEntry.SCALES_ID + ") REFERENCES " + ScalesEntry.TABLE_NAME + "(" + ScalesEntry.SCALES_ID + "));";

    static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CHARACTERISTICS_CREATE);
        db.execSQL(TABLE_DISEASE_MODEL_CREATE);
        db.execSQL(TABLE_SCALES_CREATE);
        db.execSQL(TABLE_DISEASE_MODEL_DETAILS_CREATE);
        db.execSQL(TABLE_QUALITATIVE_SCALES_CREATE);
        db.execSQL(TABLE_QUALITATIVE_SCALES_VALUES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAndCreateDB(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAndCreateDB(db);
    }

    private void dropAndCreateDB(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + QualitativeScaleValuesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QualitativeScaleEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DiseaseModelDetailsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ScalesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DiseaseModelEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CharacteristicsEntry.TABLE_NAME);
        onCreate(db);
    }
}
