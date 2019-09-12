package com.disys.systemtask.network;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.disys.systemtask.R;
import com.disys.systemtask.utility.APIURLs;
import com.disys.systemtask.utility.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
-> Extend the asyncTaskLoader with hasmap data
 */

public class NewsDataLoader extends AsyncTaskLoader<HashMap<String, String>> {
    private final static String TAG = NewsDataLoader.class.getName();

    HashMap<String, String> response = new HashMap<>();
    HashMap<String, String> input = new HashMap<>();
    private int responsecode = 0;

    Context context;

    public NewsDataLoader(Context context, HashMap<String, String> input) {
        super(context);

        this.input = input;
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        if (!response.isEmpty()) {
            deliverResult(response);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();
    }

    @Override
    public HashMap<String, String> loadInBackground() {
        try {
            response = getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected boolean onCancelLoad() {
        return super.onCancelLoad();
    }

    @Override
    public void deliverResult(HashMap<String, String> data) {
        response = data;
        super.deliverResult(data);
    }

    /**
     * api call
     *
     * @return result of RequestModel
     */

    private HashMap<String, String> getData() throws JSONException {
        // Create URL object

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest();
        } catch (IOException e) {
        }
        //  Log.e(TAG, jsonResponse);
        return extractResponseData(jsonResponse);
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */

    private String makeHttpRequest() throws IOException {
        //  final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonResponse = "";
        OkHttpClient client = ApiClient.getClient();

        /*
        Here we included the consumer key and consumer secret key directly. For security purpose, we may get these keys from
        firebase remote configuration
         */

        Request request = new Request.Builder()
                .url(APIURLs.GetNews)
                .header("Content-Type", "application/json")
                .header("consumer-key", "mobile_dev")
                .header("consumer-secret", "20891a1b4504ddc33d42501f9c8d2215fbe85008")
                .get()
                .build();
        Response responses = null;

        responses = client.newCall(request).execute();
        if (responses != null) {
            responsecode = responses.code();
            jsonResponse = responses.body().string();

        }
        return jsonResponse;
    }

    private HashMap<String, String> extractResponseData(String jsonResponse) {

        response.put(getContext().getString(R.string.response_code), String.valueOf(responsecode));

        if (responsecode == 200) {
            if (!TextUtils.isEmpty(jsonResponse)) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    if (jsonObject.getBoolean("success"))
                        response.put("payload", jsonObject.getJSONArray("payload").toString());

                    response.put("success", jsonObject.getString("success"));
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            } else {
                response.put(getContext().getString(R.string.no_data_found), jsonResponse);
            }
        } else {
            response.put(getContext().getString(R.string.status_msg), jsonResponse);

        }
        return response;
    }
}
