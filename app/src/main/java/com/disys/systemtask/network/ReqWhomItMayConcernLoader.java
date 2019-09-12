package com.disys.systemtask.network;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;

import com.disys.systemtask.R;
import com.disys.systemtask.utility.APIURLs;
import com.disys.systemtask.utility.ApiClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ReqWhomItMayConcernLoader extends AsyncTaskLoader<HashMap<String, String>> {
    private final static String TAG = ReqWhomItMayConcernLoader.class.getName();

    HashMap<String, String> response = new HashMap<>();
    HashMap<String, String> input = new HashMap<>();
    private int responsecode = 0;
    private Context context;
    private String AccessToken;

    public ReqWhomItMayConcernLoader(Context context, HashMap<String, String> input, String AccessToken) {
        super(context);
        this.input = input;
        this.AccessToken = AccessToken;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  Log.e(TAG, jsonResponse);
        return extractResponseData(jsonResponse);
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */

    /*
     Here we included the consumer key and consumer secret key directly. For security purpose, we may get these keys from
     firebase remote configuration
    */
    private String makeHttpRequest() throws  Exception {
        String jsonResponse = "";
        OkHttpClient client = ApiClient.getClient();
        RequestBody formBody = new FormBody.Builder()
                .add("eid", input.get("eid"))
                .add("name", input.get("name"))
                .add("idbarahno", input.get("idbarahno"))
                .add("emailaddress", input.get("emailaddress"))
                .add("unifiednumber", input.get("unifiednumber"))
                .add("mobileno", input.get("mobileno"))
                .build();
        Request request = new Request.Builder()
                .url(APIURLs.SignUp)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("consumer-key", "mobile_dev")
                .header("consumer-secret", "20891a1b4504ddc33d42501f9c8d2215fbe85008")
                .post(formBody)
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

                    response.put("success", jsonObject.getString("success"));
                    response.put("message", jsonObject.getString("message"));

                    if (jsonObject.getBoolean("success") == true) {
                        response.put("payload", jsonObject.getJSONObject("payload").toString());
                        response.put("referenceNo", jsonObject.getJSONObject("payload").getString("referenceNo"));

                    }

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
