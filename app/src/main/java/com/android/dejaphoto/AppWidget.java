package com.android.dejaphoto;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;


import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;



/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    // Strings of actions
    public static String settingsAction = "openSettings";
    public static String nextAction = "nextPhoto";
    public static String previousAction = "previousPhoto";
    public static String karmaAction = "karma";
    public static String releaseAction = "release";


    PhotoQueue<Photo> queue;    // queue storing photos

    DejaService mService;
    boolean mBound = false;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.d("App Widget", "onUpdate()");
            updateAppWidget(context, appWidgetManager, appWidgetId);

            //Get views
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            //Register openSettings action
            Intent settingsIntent = new Intent(context, MainActivity.class);
            settingsIntent.setAction(settingsAction);
            PendingIntent pendingSettingsIntent = PendingIntent.getActivity(context, 0, settingsIntent, 0);
            views.setOnClickPendingIntent(R.id.settingsButton, pendingSettingsIntent);

            //Register nextButton
            Intent nextPhotoIntent = new Intent(context, AppWidget.class);
            nextPhotoIntent.setAction(nextAction);
            PendingIntent pendingNextPhotoIntent = PendingIntent.getBroadcast(context, 0, nextPhotoIntent, 0);
            views.setOnClickPendingIntent(R.id.nextButton, pendingNextPhotoIntent);

            //Register previousButton
            Intent previousPhotoIntent = new Intent(context, AppWidget.class);
            previousPhotoIntent.setAction(previousAction);
            PendingIntent pendingPreviousPhotoIntent = PendingIntent.getBroadcast(context, 0, previousPhotoIntent, 0);
            views.setOnClickPendingIntent(R.id.previousButton, pendingPreviousPhotoIntent);

            //Register karma button
            Intent karmaIntent = new Intent(context, AppWidget.class);
            karmaIntent.setAction(karmaAction);
            PendingIntent pendingKarmaIntent = PendingIntent.getBroadcast(context, 0, karmaIntent, 0);
            views.setOnClickPendingIntent(R.id.karmaButton, pendingKarmaIntent);

            //Register release button
            Intent releaseIntent = new Intent(context, AppWidget.class);
            releaseIntent.setAction(releaseAction);
            PendingIntent pendingReleaseIntent = PendingIntent.getBroadcast(context, 0, releaseIntent, 0);
            views.setOnClickPendingIntent(R.id.releaseButton, pendingReleaseIntent);

            //update the intents
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Might add relevant functionality for when widget is first created in next iteration
        Log.d("App Widget", "onEnabled()");
    }

    @Override
    public void onDisabled(Context context) {
        // Might add relevant functionality for when widget is disabled in next iteration

        //unbind if necessary
        if (mBound) {
            context.unbindService(mConnection);
            mBound = false;
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("App Widget", "onReceive()");
        super.onReceive(context, intent);
        //Starting next/prev button functionality
        if (queue != null) {
            Log.d("App Widget", "Queue is not null");
        } else
            Log.d("App Widget", "Queue is NULL");
        //Next photo intent
        if (intent.getAction().equals(nextAction)) {
            //TODO
            Log.d("App Widget", "nextAction is called");

            //Start the service
            Intent serviceIntent = new Intent(context, DejaService.class);
            serviceIntent.putExtra(DejaService.actionFlag, DejaService.nextAction);
            Log.d("App Widget", "Extra string:" + serviceIntent.getStringExtra(DejaService.actionFlag));
            context.startService(serviceIntent);
            //mService.runNext();
        }
        //Previous button intent
        if (intent.getAction().equals(previousAction)) {
            //TODO
            Log.d("App Widget", "previousAction is called");
            //Start the service
            Intent serviceIntent = new Intent(context, DejaService.class);
            serviceIntent.putExtra(DejaService.actionFlag, DejaService.previousAction);
            Log.d("App Widget", "Extra string:" + serviceIntent.getStringExtra(DejaService.actionFlag));
            context.startService(serviceIntent);
            //mService.runPrevious();
        }
        //Karma button intent
        if (intent.getAction().equals(karmaAction)) {
            Log.d("AppWidget", "karmaAction is called");
            Intent serviceIntent = new Intent(context, DejaService.class);
            serviceIntent.putExtra(DejaService.actionFlag, DejaService.karmaAction);
            context.startService(serviceIntent);
        }
        //Release button intent
        if (intent.getAction().equals(releaseAction)) {
            Log.d("AppWidget", "releaseAction is called");
            Intent serviceIntent = new Intent(context, DejaService.class);
            serviceIntent.putExtra(DejaService.actionFlag, DejaService.releaseAction);
            context.startService(serviceIntent);
        }
        Log.d("App Widget", "End onReceive()");
    }

    // implementing background service
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DejaService.MyBinder binder = (DejaService.MyBinder) iBinder;
            mService = binder.getService();
            mBound = true;
            Log.d("App Widget", "Binded to service");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            Log.d("App Widget", "Unbinded to service");
        }
    };
}

