/*
 * This file is part of WhereYouGo.
 *
 * WhereYouGo is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * WhereYouGo is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with WhereYouGo. If not,
 * see <http://www.gnu.org/licenses/>.
 *
 * Description:
 * NotificationService class for the status bar which can be run as a foreground
 * service and allows the application on android 8.0 forward to receive GPS data.
 * If the application is in the background or the screen is off it would only get a few GPS updates
 * an hour.
 *
 * Implemented start actions:
 *   START_NOTIFICATION_SERVICE              This will start the service in the background or
 *                                           move a already running service from foreground to
 *                                           background.
 *
 *   START_NOTIFICATION_SERVICE_FOREGROUND   This will start the service in the foreground or
 *                                           move a already running service from background to
 *                                           foreground.
 *
 *   STOP_NOTIFICATION_SERVICE               This will stop the notification service.
 *
 * Author: kurly1  27.05.2018
 *
 * Changes:
 * Date         Who                    Detail
 * 30.05.2018   Kurly1                 Code cleanup
 * 02.06.2018   Kurly1                 Removed dependencies to A class.
 * 05.06.2018   Kurly1                 fixed some naming conventions
 */

package menion.android.whereyougo.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;

import menion.android.whereyougo.R;


public class NotificationService extends Service {
    private static final int notification_id = 10;
    private boolean foreground = false;
    private boolean runing = false;
    private NotificationManager mNM;
    private String contentTitel;

    public static final String TITEL = "ContentTitel";
    public static final String START_NOTIFICATION_SERVICE = "START_NOTIFICATION_SERVICE";
    public static final String START_NOTIFICATION_SERVICE_FOREGROUND = "START_NOTIFICATION_SERVICE_FOREGROUND";
    public static final String STOP_NOTIFICATION_SERVICE = "STOP_NOTIFICATION_SERVICE";

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
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
                    contentTitel = intent.getStringExtra(TITEL);
                    startNotificationService(true);
                    break;
                case START_NOTIFICATION_SERVICE_FOREGROUND:
                    contentTitel = intent.getStringExtra(TITEL);
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
        Logger.v(this.getClass().getName(), "Start notification service.");

        Intent intent = new Intent(this, menion.android.whereyougo.gui.activity.MainActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(contentTitel);
        builder.setSmallIcon(R.drawable.ic_title_logo);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.build();

        Notification notification = builder.build();

        if (runing) {
            if (background == foreground) {
                mNM.cancel(notification_id);
                stopForeground(true);
            }
        }
        if (!background) {
            startForeground(notification_id, notification);
            foreground = true;
        } else {
            mNM.notify(notification_id, notification);
            foreground = false;
        }
        runing = true;
    }

    private void stopNotificationService() {
        Logger.v(this.getClass().getName(), "Stop notification service.");
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
