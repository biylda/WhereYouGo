package menion.android.whereyougo.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;

import menion.android.whereyougo.R;


public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static final int notification_id = 10;
    private boolean foreground = false;
    private boolean runing = false;
    private NotificationManager mNM;

    public static final String START_NOTIFICATION_SERVICE = "START_NOTIFICATION_SERVICE";
    public static final String START_NOTIFICATION_SERVICE_FOREGROUND = "START_NOTIFICATION_SERVICE_FOREGROUND";
    public static final String STOP_NOTIFICATION_SERVICE = "STOP_NOTIFICATION_SERVICE";

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.v(this.getClass().getName(), "onCreate()");
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case START_NOTIFICATION_SERVICE:
                    startNotificationService(true);
                    break;
                case START_NOTIFICATION_SERVICE_FOREGROUND:
                    startNotificationService(false);
                    break;
                case STOP_NOTIFICATION_SERVICE:
                    stopNotificationService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startNotificationService(boolean background) {
        Logger.v(TAG, "Start notification service.");

        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(A.getAppName());
        builder.setSmallIcon(R.drawable.ic_title_logo);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.build();

        Notification notification = builder.build();

        if (runing) {
            if (background = foreground) {
                mNM.cancel(notification_id);
                stopForeground(true);
            }
        }
        if (foreground) {
            startForeground(1, notification);
        } else {
            mNM.notify(notification_id, notification);
        }
        runing = true;
    }

    private void stopNotificationService() {
        Logger.v(TAG, "Stop notification service.");
        if (foreground) {
            stopForeground(true);
            foreground = false;
        } else {
            mNM.cancel(notification_id);
        }

        runing = false;
        stopSelf();
    }
}
