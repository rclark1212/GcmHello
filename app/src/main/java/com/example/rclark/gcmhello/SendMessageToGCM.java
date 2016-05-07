package com.example.rclark.gcmhello;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.rclark.myapplication.backend.messaging.Messaging;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by rclark on 5/6/16.
 */
public class SendMessageToGCM extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private static int msgId = 10;

    private static Messaging myApiService = null;

    public SendMessageToGCM() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String msg = intent.getStringExtra("param");

        Log.d(TAG, "Enter SendMessage with " + msg);

        if (myApiService == null) {
            sendRegistrationToServer();
            Log.d(TAG, "Registered server");
        }

        try {
            Log.d(TAG, "try mybean");
            String response = myApiService.sayHi(msg).execute().getData();
            Log.d(TAG, "got " + response);

            myApiService.broadcastMessage(msg).execute();
            Log.d(TAG, "Sent message");
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
        }
    }

    private void sendRegistrationToServer() {
        Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                // otherwise they can be skipped
                .setRootUrl("https://default-demo-app-2c3f1.appspot.com/_ah/api/");
        /*
                .setRootUrl("http://10.0.2.2:8080/_ah/api/").
                        setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
            @Override
            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                abstractGoogleClientRequest.setDisableGZipContent(true);
            }
        }); */

        myApiService = builder.build();
    }
}
