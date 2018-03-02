package com.agrinett.agridiagnose.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.CharacteristicsEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.DiseaseModelEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.DiseaseModelDetailsEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.ScalesEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.QualitativeScaleEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.QualitativeScaleValuesEntry;
import com.agrinett.agridiagnose.models.Characteristic;
import com.agrinett.agridiagnose.models.DiseaseModel;
//import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
//import com.google.common.collect.FluentIterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository implements IRepository {

    private DatabaseHelper databaseHelper;

    public Repository(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public boolean QueryIsSynced() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + CharacteristicsEntry.TABLE_NAME + " limit 1";
        Cursor cursor = db.rawQuery(query, new String[]{});
        boolean results = false;
        if (cursor.moveToFirst()) {
            results = true;
        }
        cursor.close();
        return results;
    }

    @Override
    public void NonQuerySyncData(HashMap<String, JSONObject> data) throws JSONException {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            // Delete old data
            db.delete(QualitativeScaleValuesEntry.TABLE_NAME, null, null);
            db.delete(QualitativeScaleEntry.TABLE_NAME, null, null);
            db.delete(DiseaseModelDetailsEntry.TABLE_NAME, null, null);
            db.delete(ScalesEntry.TABLE_NAME, null, null);
            db.delete(DiseaseModelEntry.TABLE_NAME, null, null);
            db.delete(CharacteristicsEntry.TABLE_NAME, null, null);

            // Perform inserts
            for (Map.Entry<String, JSONObject> entry : data.entrySet()) {
                JSONArray result = entry.getValue().getJSONArray("result").getJSONObject(0).getJSONArray("aaData");
                switch (entry.getKey()) {
                    case CharacteristicsEntry.TABLE_NAME:
                        for (int i = 0; i < result.length(); i++) {
                            JSONArray characteristics = result.getJSONArray(i);
                            ContentValues values = new ContentValues();
                            values.put(CharacteristicsEntry.CHARACTERISTIC_ID, characteristics.getString(0));
                            values.put(CharacteristicsEntry.COMMON_NAME, characteristics.getString(1));
                            values.put(CharacteristicsEntry.SCIENTIFIC_NAME, characteristics.getString(2));
                            values.put(CharacteristicsEntry.TYPE, characteristics.getString(3));
                            values.put(CharacteristicsEntry.QUESTION, characteristics.getString(4));
                            values.put(CharacteristicsEntry.RESPONSE_TYPE, characteristics.getString(5));
                            // Skipped Background and Reference
                            values.put(CharacteristicsEntry.GROUP_PARENT, characteristics.getString(8));
                            values.put(CharacteristicsEntry.QUESTION_ORDER, characteristics.getString(9));
                            db.insertOrThrow(CharacteristicsEntry.TABLE_NAME, null, values);
                        }
                        break;
                    case DiseaseModelEntry.TABLE_NAME:
                        for (int i = 0; i < result.length(); i++) {
                            JSONArray characteristics = result.getJSONArray(i);
                            ContentValues values = new ContentValues();
                            values.put(DiseaseModelEntry.DISEASE_ID, characteristics.getString(0));
                            values.put(DiseaseModelEntry.TYPE, characteristics.getString(1));
                            values.put(DiseaseModelEntry.DESCRIPTION, characteristics.getString(2));
                            values.put(DiseaseModelEntry.SCIENTIFIC_NAME, characteristics.getString(3));
                            values.put(DiseaseModelEntry.LEVEL, characteristics.getString(4));
                            values.put(DiseaseModelEntry.PARENT_ID, characteristics.getString(5));
                            values.put(DiseaseModelEntry.REFERENCE, characteristics.getString(6));
                            values.put(DiseaseModelEntry.NOTES, characteristics.getString(7));
                            db.insertOrThrow(DiseaseModelEntry.TABLE_NAME, null, values);
                        }
                        break;
                    case ScalesEntry.TABLE_NAME:
                        for (int i = 0; i < result.length(); i++) {
                            JSONArray scales = result.getJSONArray(i);
                            ContentValues values = new ContentValues();
                            values.put(ScalesEntry.SCALES_ID, scales.getString(0));
                            values.put(ScalesEntry.SCALES_TYPE, scales.getString(1));
                            values.put(ScalesEntry.DESCRIPTION, scales.getString(2));
                            values.put(ScalesEntry.CONTEXT, scales.getString(3));
                            db.insertOrThrow(ScalesEntry.TABLE_NAME, null, values);
                        }
                        break;
                    case DiseaseModelDetailsEntry.TABLE_NAME:
                        for (int i = 0; i < result.length(); i++) {
                            JSONArray modelDetails = result.getJSONArray(i);
                            ContentValues values = new ContentValues();
                            values.put(DiseaseModelDetailsEntry.DISEASE_ID, modelDetails.getString(0));
                            values.put(DiseaseModelDetailsEntry.CHARACTERISTIC_ID, modelDetails.getString(1));
                            values.put(DiseaseModelDetailsEntry.SCALE_ID, modelDetails.getString(2));
                            values.put(DiseaseModelDetailsEntry.PROBABILITY_VALUE, modelDetails.getString(3));
                            values.put(DiseaseModelDetailsEntry.SCALE_MODEL, modelDetails.getString(4));
                            values.put(DiseaseModelDetailsEntry.SCALE_EQUATION, modelDetails.getString(5));
                            values.put(DiseaseModelDetailsEntry.BEST, modelDetails.getString(6));
                            values.put(DiseaseModelDetailsEntry.WORST, modelDetails.getString(7));
                            values.put(DiseaseModelDetailsEntry.REASON, modelDetails.getString(8));
                            values.put(DiseaseModelDetailsEntry.UNITS, modelDetails.getString(9));
                            values.put(DiseaseModelDetailsEntry.AHP, modelDetails.getString(10));
                            values.put(DiseaseModelDetailsEntry.SORT_STAGE, modelDetails.getString(11));
                            db.insertOrThrow(DiseaseModelDetailsEntry.TABLE_NAME, null, values);
                        }
                        break;
                    case QualitativeScaleEntry.TABLE_NAME:
                        for (int i = 0; i < result.length(); i++) {
                            JSONArray qualitativeScales = result.getJSONArray(i);
                            ContentValues values = new ContentValues();
                            values.put(QualitativeScaleEntry.SCALES_ID, qualitativeScales.getString(0));
                            values.put(QualitativeScaleEntry.PROPERTY_ID, qualitativeScales.getString(1));
                            values.put(QualitativeScaleEntry.PROPERTY, qualitativeScales.getString(2));
                            db.insertOrThrow(QualitativeScaleEntry.TABLE_NAME, null, values);
                        }
                        break;
                    case QualitativeScaleValuesEntry.TABLE_NAME:
                        for (int i = 0; i < result.length(); i++) {
                            JSONArray modelDetails = result.getJSONArray(i);
                            ContentValues values = new ContentValues();
                            values.put(QualitativeScaleValuesEntry.CHARACTERISTIC_ID, modelDetails.getString(0));
                            values.put(QualitativeScaleValuesEntry.SCALES_ID, modelDetails.getString(1));
                            values.put(QualitativeScaleValuesEntry.DISEASE_ID, modelDetails.getString(2));
                            values.put(QualitativeScaleValuesEntry.PROPERTY_ID, modelDetails.getString(3));
                            values.put(QualitativeScaleValuesEntry.PROPERTY, modelDetails.getString(4));
                            values.put(QualitativeScaleValuesEntry.PROPERTY_VALUE, modelDetails.getString(5));
                            db.insertOrThrow(QualitativeScaleValuesEntry.TABLE_NAME, null, values);
                        }
                        break;
                    default:
                        break;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    @Override
    public List<Characteristic> QueryFilterQuestions() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sql = "SELECT DISTINCT C.CharacteristicId, C.Question, C.Type, C.ResponseType, C.GroupParent, C.QuestionOrder " +
                "FROM Characteristics C " +
                "INNER JOIN DiseaseModelDetails DMD " +
                "ON C.CharacteristicId = DMD.CharacteristicId " +
                "WHERE DMD.SortStage IN ('-1','0') " +
                "ORDER BY DMD.SortStage";

        String args[] = new String[]{};
        Cursor cursor = db.rawQuery(sql, args);
        List<Characteristic> results = new ArrayList<>();

        while (cursor.moveToNext()) {
            Characteristic characteristic = new Characteristic();
            characteristic.CharacteristicId(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.CHARACTERISTIC_ID)));
            characteristic.Type(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.TYPE)));
            characteristic.Question(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.QUESTION)));
            characteristic.ResponseType(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.RESPONSE_TYPE)));
            characteristic.GroupParent(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.GROUP_PARENT)));
            characteristic.QuestionOrder(cursor.getInt(cursor.getColumnIndexOrThrow(CharacteristicsEntry.QUESTION_ORDER)));
            results.add(characteristic);
        }
        cursor.close();
        return results;
    }

    @Override
    public List<DiseaseModel> QueryDiseaseModels(String characteristicId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

//        String sql = "SELECT CharacteristicId, DiseaseId, ScaleId, ProbabilityValue, Reason, SortStage " +
//                "FROM " + DiseaseModelDetailsEntry.TABLE_NAME;

        String sql = "SELECT DiseaseId, CharacteristicId, ScaleId, ProbabilityValue, Reason, SortStage " +
                "FROM DiseaseModelDetails " +
                "WHERE DiseaseId IN (SELECT DiseaseId " +
                "FROM DiseaseModelDetails " +
                "WHERE CharacteristicId = ?)";

        String args[] = new String[]{ characteristicId };
        Cursor cursor = db.rawQuery(sql, args);

        List<DiseaseModel> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            DiseaseModel diseaseModel = new DiseaseModel();
            String diseaseId = cursor.getString(cursor.getColumnIndexOrThrow(DiseaseModelDetailsEntry.DISEASE_ID));
            diseaseModel.DiseaseId(diseaseId);
            diseaseModel.CharacteristicId(cursor.getString(cursor.getColumnIndexOrThrow(DiseaseModelDetailsEntry.CHARACTERISTIC_ID)));
            diseaseModel.ScaleId(cursor.getString(cursor.getColumnIndexOrThrow(DiseaseModelDetailsEntry.SCALE_ID)));
            diseaseModel.ProbabilityValue(cursor.getDouble(cursor.getColumnIndexOrThrow(DiseaseModelDetailsEntry.PROBABILITY_VALUE)));
            diseaseModel.Reason(cursor.getString(cursor.getColumnIndexOrThrow(DiseaseModelDetailsEntry.REASON)));
            diseaseModel.SortStage(cursor.getInt(cursor.getColumnIndexOrThrow(DiseaseModelDetailsEntry.SORT_STAGE)));

            results.add(diseaseModel);
//            if (results.containsKey(diseaseId)) {
//                results.get(diseaseId).add(diseaseModel);
//            } else {
//                List<DiseaseModel> list = new ArrayList<>();
//                list.add(diseaseModel);
//                results.put(diseaseId, list);
//            }
        }
        cursor.close();
        return results;
    }

    @Override
    public Map<String, Double> QueryUtilities() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT DISTINCT DiseaseId FROM DiseaseModelDetails";
        Cursor cursor = db.rawQuery(query, new String[]{});
        Map<String, Double> results = new HashMap<>();
        while (cursor.moveToNext()) {
            results.put(cursor.getString(cursor.getColumnIndexOrThrow(DiseaseModelDetailsEntry.DISEASE_ID)), 0.0);
        }
        cursor.close();
        return results;
    }

    @Override
    public List<Characteristic> QueryQuestions(String diseaseId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String sql = "SELECT DISTINCT C.CharacteristicId, C.Question, C.Type, C.ResponseType, C.GroupParent, C.QuestionOrder " +
                "FROM Characteristics C " +
                "INNER JOIN DiseaseModelDetails DM " +
                "ON C.CharacteristicId = DM.CharacteristicId " +
                "WHERE DM.DiseaseId = ? " +
                "AND DM.SortStage NOT IN (-1, 0, 100) " +
                "ORDER BY DM.SortStage";

        String args[] = new String[]{ diseaseId };
        Cursor cursor = db.rawQuery(sql, args);
        List<Characteristic> results = new ArrayList<>();

        while (cursor.moveToNext()) {
            Characteristic characteristic = new Characteristic();
            characteristic.CharacteristicId(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.CHARACTERISTIC_ID)));
            characteristic.Type(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.TYPE)));
            characteristic.Question(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.QUESTION)));
            characteristic.ResponseType(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.RESPONSE_TYPE)));
            characteristic.GroupParent(cursor.getString(cursor.getColumnIndexOrThrow(CharacteristicsEntry.GROUP_PARENT)));
            characteristic.QuestionOrder(cursor.getInt(cursor.getColumnIndexOrThrow(CharacteristicsEntry.QUESTION_ORDER)));
            results.add(characteristic);
        }
        cursor.close();
        return results;
    }

    @Override
    public String QueryScaleId(String characteristicId, String diseaseId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String results = null;
        String query = "SELECT DISTINCT ScaleId FROM QualitativeScaleValues WHERE CharacteristicId = ? AND DiseaseId = ?";
        String args [] = new String[] { characteristicId, diseaseId };
        Cursor cursor = db.rawQuery(query, args);
        if(cursor.moveToFirst()) {
            results = cursor.getString(cursor.getColumnIndexOrThrow(QualitativeScaleValuesEntry.SCALES_ID));
        }
        cursor.close();
        return results;
    }

    @Override
    public String QueryReason(String characteristicId, String diseaseId, String scaleId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String results = null;
        // Scale is needed here
        String query = "SELECT Reason FROM DiseaseModelDetails WHERE CharacteristicId = ? AND DiseaseId = ? AND ScaleId = ?";
        String args [] = new String[] { characteristicId, diseaseId, scaleId };
        Cursor cursor = db.rawQuery(query, args);
        if(cursor.moveToFirst()) {
            results = cursor.getString(cursor.getColumnIndexOrThrow(DiseaseModelDetailsEntry.REASON));
        }
        cursor.close();
        return results;
    }

    @Override
    public List<String> QueryResponses(String characteristicId, String diseaseId) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<String> results = new ArrayList<>();

        String sql = "SELECT QS.Property " +
                "FROM QualitativeScale QS " +
                "INNER JOIN QualitativeScaleValues QSV " +
                "ON QS.ScaleId = QSV.ScaleId " +
                "AND QS.PropertyId = QSV.PropertyId " +
                "WHERE QSV.CharacteristicId = ? " +
                "AND QSV.DiseaseId = ? " +
                "ORDER BY QS.Property ASC";
        String args [] = new String[] { characteristicId, diseaseId };
        Cursor cursor = db.rawQuery(sql, args);
        while(cursor.moveToNext()) {
            results.add(cursor.getString(cursor.getColumnIndexOrThrow(QualitativeScaleEntry.PROPERTY)));
        }
        cursor.close();
        results = new ArrayList<>(Collections2.filter(results, Predicates.<String>notNull()));
//        Predicate<String> emptyString = new Predicate<String>() {
//            @Override
//            public boolean apply(String input) {
//                return !input.isEmpty();
//            }
//        };
        return results;//FluentIterable.from(results).filter(emptyString).toList();
    }

    @Override
    public double QueryPropertyValue(String property, String characteristicId, String diseaseId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        double results = 0.0;
        String sql = "SELECT QSV.PropertyValue " +
                "FROM QualitativeScale QS " +
                "INNER JOIN QualitativeScaleValues QSV " +
                "ON QS.PropertyId = QSV.PropertyId " +
                "AND QS.ScaleId = QSV.ScaleId " +
                "WHERE QS.Property = ? " +
                "AND QSV.CharacteristicId = ? " +
                "AND QSV.DiseaseId = ?";
        String args [] = new String[] { property, characteristicId, diseaseId };
        Cursor cursor = db.rawQuery(sql, args);
        if(cursor.moveToFirst()) {
            results = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(QualitativeScaleValuesEntry.PROPERTY_VALUE)));
        }
        cursor.close();
        return results;
    }

    @Override
    public double QueryMinPropertyValue(String characteristicId, String diseaseId, String scaleId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        double results = 0.0;
        String query = "SELECT MIN(PropertyValue) PropertyValue FROM QualitativeScaleValues WHERE characteristicId = ? AND diseaseId = ? AND scaleId = ?";
        String args [] = new String[] { characteristicId, diseaseId, scaleId };
        Cursor cursor = db.rawQuery(query, args);
        if(cursor.moveToFirst()) {
            results = cursor.getDouble(cursor.getColumnIndexOrThrow(QualitativeScaleValuesEntry.PROPERTY_VALUE));
        }
        cursor.close();
        return results;
    }

    @Override
    public double QueryMaxPropertyValue(String characteristicId, String diseaseId, String scaleId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        double results = 0.0;
        String query = "SELECT MAX(PropertyValue) PropertyValue FROM QualitativeScaleValues WHERE characteristicId = ? AND diseaseId = ? AND scaleId = ?";
        String args [] = new String[] { characteristicId, diseaseId, scaleId };
        Cursor cursor = db.rawQuery(query, args);
        if(cursor.moveToFirst()) {
            results = cursor.getDouble(cursor.getColumnIndexOrThrow(QualitativeScaleValuesEntry.PROPERTY_VALUE));
        }
        cursor.close();
        return results;
    }

    @Override
    public List<String> QueryDiseases(String[] diseaseIds) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<String> results = new ArrayList<>();

        String sql = "SELECT Description FROM DiseaseModels WHERE DiseaseId IN (?, ?)";
        String args [] = new String[] { diseaseIds[0], diseaseIds[1] };
        Cursor cursor = db.rawQuery(sql, args);
        int i =1;
        while(cursor.moveToNext()) {
            results.add(String.valueOf(i++) + ". " + cursor.getString(cursor.getColumnIndexOrThrow(DiseaseModelEntry.DESCRIPTION)));
        }
        cursor.close();
        return results;
    }
}
