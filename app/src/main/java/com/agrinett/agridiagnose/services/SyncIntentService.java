package com.agrinett.agridiagnose.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.agrinett.agridiagnose.data.Repository;
import com.agrinett.agridiagnose.rest.JsonClient;

import org.json.JSONObject;

import java.util.HashMap;

public class SyncIntentService extends IntentService {

    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean result = true;
        HashMap<String, JSONObject> responses;
        JsonClient jsonClient = new JsonClient(SyncIntentService.this);
        Repository repository = new Repository(SyncIntentService.this);
        ResultReceiver resultReceiver = intent.getParcelableExtra("result");

        try {
            jsonClient.loadJson();
            while (true) {
                if (jsonClient.isJsonLoaded() && !jsonClient.getJsonData().isEmpty()) {
                    responses = jsonClient.getJsonData();
                    break;
                }
            }

            if (responses.containsKey("error")) {
                result = false;
            } else {
                repository.NonQuerySyncData(responses);
            }
        }
        catch (Exception ex) {
            Log.e("SYNC_ERROR", ex.getMessage());
            result = false;
        }

        Bundle bundle = new Bundle();
        bundle.putBoolean("result", result);
        resultReceiver.send(18, bundle);
    }
}
