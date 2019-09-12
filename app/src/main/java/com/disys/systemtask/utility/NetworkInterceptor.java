package com.disys.systemtask.utility;


import com.disys.systemtask.R;
import com.disys.systemtask.SYSApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class NetworkInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response;
        response = chain.proceed(chain.request());
        if (!new NetworkUtils().isConnected()) {
            throw (new IOException(SYSApplication.getpInstance().getApplicationContext().getResources().getString(R.string.no_internet)));
        }
        return response;
    }
}
