package com.agrinett.agridiagnose.data;

import android.content.Context;
import android.net.NetworkInfo;

import com.agrinett.agridiagnose.models.Characteristic;
import com.agrinett.agridiagnose.models.DiseaseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IRepository {
    NetworkInfo GetNetworkInfo();
    boolean QueryIsSynced();
    void NonQuerySyncData(HashMap<String, JSONObject> data) throws JSONException;
    List<Characteristic> QueryFilterQuestions();
    List<DiseaseModel> QueryDiseaseModels(String characteristicId);
    Map<String, Double> QueryUtilities();
    List<Characteristic> QueryQuestions(String diseaseId);
    String QueryScaleId(String characteristicId, String diseaseId);
    String QueryReason(String characteristicId, String diseaseId, String scaleId);
    List<String> QueryResponses(String characteristicId, String diseaseId);
    double QueryPropertyValue(String property, String characteristicId, String diseaseId);
    double QueryMinPropertyValue(String characteristicId, String diseaseId, String scaleId);
    double QueryMaxPropertyValue(String characteristicId, String diseaseId, String scaleId);
    List<String> QueryDiseases(String diseaseIds []);
}
