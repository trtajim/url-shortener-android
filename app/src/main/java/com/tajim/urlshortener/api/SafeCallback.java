package com.tajim.urlshortener.api;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.tajim.urlshortener.utils.AppUtils;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public abstract class SafeCallback implements Callback {
    private final Context context;

    protected SafeCallback(Context context) {
        this.context = context.getApplicationContext();
    }

    public abstract void onSuccess(String bodyFromResponse);
    protected void onFailureHandled(Call call, IOException e) {
        showToastInUiThread(e.getMessage());
        AppUtils.endLoading();
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        onFailureHandled(call, e);
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        AppUtils.endLoading();

        if ("true".equals(response.header("X-Auth-Handled"))) {
            return;
        }

        String body = response.body().string();

        JSONObject jsonObject = AppUtils.getJsonObjFromString(body);

        if (jsonObject == null) {
            showToastInUiThread( "Invalid server response");
            return;
        }

        if (!response.isSuccessful()){
            String message = AppUtils.getStringFromJsonObject(jsonObject, "message", "Something went wrong");
            showToastInUiThread(message);
            return;
        }

        AppUtils.postUI(()->{
             onSuccess(body);
        });


    }
    private void showToastInUiThread(String message) {
        AppUtils.postUI(()->{
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }

}
