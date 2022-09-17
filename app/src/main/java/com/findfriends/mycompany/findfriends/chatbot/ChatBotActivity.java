package com.findfriends.mycompany.findfriends.chatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.findfriends.mycompany.findfriends.R;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;

import java.io.InputStream;
import java.util.UUID;


public class ChatBotActivity extends AppCompatActivity {

    private SessionsClient sessionsClient;
    private SessionName session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        initV2Chatbot();
        Button test = findViewById(R.id.testButton);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","test button clicked");
                sendMessage();
            }
        });
    }

    private void initV2Chatbot() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.test_agent_credentials);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials)credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, UUID.randomUUID().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(){
        QueryInput queryInput = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText("Hello").setLanguageCode("en-US"))
                .build();
        new BotAsyncTask(this,session,sessionsClient,queryInput).execute();
    }

    public void callbackV2(DetectIntentResponse response) {
        if (response != null) {
            // process aiResponse here
            String botReply = response.getQueryResult().getFulfillmentText();
            Log.d("TAG", "V2 Bot Reply: " + botReply);
        } else {
            Log.d("TAG", "Bot Reply: Null");
        }
    }













































   /* private void initChatBot(){
        final AIConfiguration config = new AIConfiguration("<client code>",
                AIConfiguration.SupportedLanguages.English,AIConfiguration.RecognitionEngine.System);

        AIService aiService = AIService.getService(this, config);
        aiService.setListener(this);

        Button test = findViewById(R.id.testButton);
        test.setOnClickListener(v -> {
            try {
                aiService.textRequest(new AIRequest("hello"));
            } catch (AIServiceException e) {
                e.printStackTrace();
            }
        });*/

    }

























































/*
    @Override
    public void onResult(AIResponse response) {
        if(response != null){
            Result r = response.getResult();
            String botReply = r.getFulfillment().getSpeech();
            Log.d("TAG","Bot reply: "+botReply);
        }
        else{
            Log.d("TAG","Bot reply is null");
        }
    }

    @Override
    public void onError(AIError error) {
        Log.d("TAG", "Listening error: " + error.getMessage());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }*/

