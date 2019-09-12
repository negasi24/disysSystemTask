package com.disys.systemtask;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.disys.systemtask.adapter.NewsAdapter;
import com.disys.systemtask.model.News;
import com.disys.systemtask.network.NewsDataLoader;
import com.disys.systemtask.utility.Utilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*

-> News activity is for display the latest news

 */

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<HashMap<String, String>> {


    private static final String TAG = NewsActivity.class.getName();
    List<News> newsArrayList = new ArrayList<>();
    RecyclerView rvNews;
    NewsAdapter mAdapter;
    Context mContext;
    ProgressBar progressBar;
    SYSApplication myApplication;
    SwipeRefreshLayout pullToRefresh;
    TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mContext = this;
        myApplication = (SYSApplication) getApplication();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
        callAPI();
        initSwipRefresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    //Intiallize the controls and events
    private void init() {

        progressBar = (ProgressBar) findViewById(R.id.progress);

        tvMsg = (TextView) findViewById(R.id.tvMsg);
        tvMsg.setText("Loading...");

        rvNews = (RecyclerView) findViewById(R.id.rvNews);
        rvNews.setHasFixedSize(true);
        rvNews.setItemViewCacheSize(10);
        rvNews.setDrawingCacheEnabled(true);
        rvNews.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvNews.setLayoutManager(mLayoutManager);
        rvNews.setNestedScrollingEnabled(false);
        rvNews.setItemAnimator(new DefaultItemAnimator());
        rvNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                pullToRefresh.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    //bind adapter with recycler view
    private void bindAdapter() {
        mAdapter = null;
        mAdapter = new NewsAdapter(newsArrayList, mContext);
        rvNews.setAdapter(mAdapter);
    }

    //implement the swip refresh
    private void initSwipRefresh() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                callAPI(); // your code

            }
        });

        pullToRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

//        pullToRefresh.setEnabled(false);
    }

    //call the api for get the news data
    private void callAPI() {
        if (myApplication.checkNetwork()) {
            newsArrayList.clear();
            progressBar.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(0, null, this).forceLoad();
        } else {

            Intent i = new Intent(this, OopsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    public Loader<HashMap<String, String>> onCreateLoader(int i, Bundle bundle) {
        //call the network loader for establish the connection between the mobile and server through API
        //call api the get the news data
        return new NewsDataLoader(this, null);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, String>> loader, HashMap<String, String> data) {

        //Hide the prgressbar, msgtextview, swipeRefresh after received response from api
        progressBar.setVisibility(View.GONE);
        tvMsg.setVisibility(View.GONE);
        pullToRefresh.setRefreshing(false);

        if (data != null && !data.isEmpty()) {
            try {
                setData(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utilities.showToast(getApplicationContext(), data.get(getResources().getString(R.string.status_msg)));
        }

    }
    //handle the success and failuer response code
    //manipuate the response data
    private void setData(HashMap<String, String> data) {

        if (data.get(getResources().getString(R.string.response_code)).equals("200")) {
            try {

                if(data.get("success").equals("true")) {
                    JSONArray jsonPayloadArray = new JSONArray(data.get("payload"));
                    Type type = new TypeToken<ArrayList<News>>() {
                    }.getType();
                    Gson gson = new Gson();
                    if (jsonPayloadArray.length() > 0) {

                        newsArrayList = gson.fromJson(jsonPayloadArray.toString(), type);
                        bindAdapter();

                    } else {
                        //Display the error response
                        Utilities.showToast(getApplicationContext(), data.get(getResources().getString(R.string.status_msg)));

                    }
                }
                else {
                    //Display the error response
                    Utilities.showToast(getApplicationContext(), data.get(getResources().getString(R.string.status_msg)));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (data.get(getResources().getString(R.string.response_code)).equals("500")) { //Internal server error
            Utilities.showToast(getApplicationContext(), "Internal Server Error. Please try after sometimes or contact admin");
        }
        else {
            Utilities.showToast(getApplicationContext(), data.get(getResources().getString(R.string.status_msg)));
        }

    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, String>> loader) {

    }
}
