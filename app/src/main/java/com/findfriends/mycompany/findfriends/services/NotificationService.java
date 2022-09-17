package com.findfriends.mycompany.findfriends.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.findfriends.mycompany.findfriends.activities.LoginActivity;
import com.findfriends.mycompany.findfriends.activities.MessagesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();

            String userId = remoteMessage.getData().get(AppConstants.USER_ID);
            String userName = remoteMessage.getData().get(AppConstants.USER_NAME);
            String userImage = remoteMessage.getData().get(AppConstants.USER_IMAGE);
            String currentUserId = remoteMessage.getData().get(AppConstants.CURRENT_USER_ID);
            String currentUserName = remoteMessage.getData().get(AppConstants.CURRENT_USER_NAME);
            String currentUserImage = remoteMessage.getData().get(AppConstants.CURRENT_USER_IMAGE);

            if(currentUserImage != null){
                Intent intent = new Intent(getApplicationContext(),MessagesActivity.class);
                intent.putExtra(AppConstants.USER_ID,userId);
                intent.putExtra(AppConstants.USER_NAME,userName);
                intent.putExtra(AppConstants.USER_IMAGE,userImage);
                intent.putExtra(AppConstants.CURRENT_USER_ID,currentUserId);
                intent.putExtra(AppConstants.CURRENT_USER_NAME,currentUserName);
                intent.putExtra(AppConstants.CURRENT_USER_IMAGE,currentUserImage);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(title);
                inboxStyle.addLine(message);

                String channelId = "notification_channel";

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setContentTitle(title)
                                .setAutoCancel(true)
                                .setContentText(message)
                                .setSmallIcon(R.drawable.myicon)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setStyle(inboxStyle)
                                .setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence channelName = "Message provenant de Firebase";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                    notificationManager.createNotificationChannel(mChannel);
                }

                notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
            }
            else{
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.putExtra(AppConstants.ACTIVITY_TAG,1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(title);
                inboxStyle.addLine(message);

                String channelId = "notification_channel";

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setContentTitle(title)
                                .setAutoCancel(true)
                                .setContentText(message)
                                .setSmallIcon(R.drawable.myicon)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setStyle(inboxStyle)
                                .setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence channelName = "Message provenant de Firebase";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                    notificationManager.createNotificationChannel(mChannel);
                }

                notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());

            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            UserApi.updateRegistrationToken(FirebaseAuth.getInstance().getUid(),token);
    }
}
