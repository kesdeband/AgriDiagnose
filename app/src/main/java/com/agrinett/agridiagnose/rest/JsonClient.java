package com.agrinett.agridiagnose.rest;

import android.content.Context;

import com.agrinett.agridiagnose.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

import com.agrinett.agridiagnose.data.AgriDiagnoseContract.CharacteristicsEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.DiseaseModelEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.DiseaseModelDetailsEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.ScalesEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.QualitativeScaleEntry;
import com.agrinett.agridiagnose.data.AgriDiagnoseContract.QualitativeScaleValuesEntry;

public class JsonClient {
    public final static String REQUEST_TAG = "com.agrinett.agridiagnose.volley.JsonRequest";

    private Context _context;
    private HashMap<String, JSONObject> _responses;
    private int _requestCounter;

    public JsonClient(Context context) {
        _context = context;
        _responses = new HashMap<>();
        _requestCounter = 0;
    }

    public synchronized void loadJson() {
        loadCharacteristics();
        loadModels();
        loadScales();
        loadModelDetails();
        loadQualitativeScales();
        loadQualitativeScaleValues();
    }

    public synchronized boolean isJsonLoaded() {
        return _requestCounter == 0;
    }

    public synchronized HashMap<String, JSONObject> getJsonData() {
        return _responses;
    }

    private void loadCharacteristics() {
        final String REST_URL = _context.getString(R.string.characteristics_rest_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, REST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addRequestResponse(CharacteristicsEntry.TABLE_NAME, response);
                removeRequest();
            }
        }, onError) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                Log.d("Status_Code", String.valueOf(response.statusCode));
                if(response.statusCode == 200) {
                    return super.parseNetworkResponse(response);
                }
                return Response.error(new VolleyError(response));
            }
        };
        addRequest(request);
    }

    private void loadModels() {
        final String REST_URL = _context.getString(R.string.models_rest_url);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, REST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addRequestResponse(DiseaseModelEntry.TABLE_NAME, response);
                removeRequest();
            }
        }, onError) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if(response.statusCode == 200) {
                    return super.parseNetworkResponse(response);
                }
                return Response.error(new VolleyError(response));
            }
        };
        addRequest(request);
    }

    private void loadScales() {
        final String REST_URL = _context.getString(R.string.scales_rest_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, REST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addRequestResponse(ScalesEntry.TABLE_NAME, response);
                removeRequest();
            }
        }, onError) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if(response.statusCode == 200) {
                    return super.parseNetworkResponse(response);
                }
                return Response.error(new VolleyError(response));
            }
        };
        addRequest(request);
    }

    private void loadModelDetails() {
        final String REST_URL = _context.getString(R.string.model_details_rest_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, REST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addRequestResponse(DiseaseModelDetailsEntry.TABLE_NAME, response);
                removeRequest();
            }
        }, onError) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if(response.statusCode == 200) {
                    return super.parseNetworkResponse(response);
                }
                return Response.error(new VolleyError(response));
            }
        };
        addRequest(request);
    }

    private void loadQualitativeScales() {
        final String REST_URL = _context.getString(R.string.qualitative_scale_rest_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, REST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addRequestResponse(QualitativeScaleEntry.TABLE_NAME, response);
                removeRequest();
            }
        }, onError) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if(response.statusCode == 200) {
                    return super.parseNetworkResponse(response);
                }
                return Response.error(new VolleyError(response));
            }
        };
        addRequest(request);
    }

    private void loadQualitativeScaleValues() {
        final String REST_URL = _context.getString(R.string.qualitative_scale_values_rest_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, REST_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addRequestResponse(QualitativeScaleValuesEntry.TABLE_NAME, response);
                removeRequest();
            }
        }, onError) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if(response.statusCode == 200) {
                    return super.parseNetworkResponse(response);
                }
                return Response.error(new VolleyError(response));
            }
        };
        addRequest(request);
    }

    private synchronized void addRequestResponse(String table, JSONObject response) {
        _responses.put(table, response);
    }

    private synchronized void addRequest(Request request) {
//        Log.d("ADD_REQUEST", "Called Method");
        VolleyRequestQueue.getInstance(_context).addToRequestQueue(request, REQUEST_TAG);
        _requestCounter++;
    }

    private synchronized void removeRequest()  {
        _requestCounter--;
    }

    private final Response.ErrorListener onError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyRequestQueue.getInstance(_context).cancelPendingRequests(REQUEST_TAG);
            _requestCounter = 0;
            _responses.put("error", new JSONObject());
        }
    };
}
