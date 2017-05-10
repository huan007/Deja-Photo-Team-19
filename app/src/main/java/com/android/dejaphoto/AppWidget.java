package com.android.dejaphoto;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;


import android.app.PendingIntent;
import android.content.Intent;


import java.io.File;
//import com.android.dejaphoto.SettingsActivity;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    public static String settingsAction = "openSettings";
    public static String nextAction = "nextPhoto";
    public static String previousAction = "previousPhoto";

    ImageController controller;
    PhotoQueue<Photo> queue;

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


            //update the intents
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d("App Widget", "onEnabled()");

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("App Widget", "onReceive()");
        super.onReceive(context, intent);
        //Call Enabled()
        //onEnabled(context);
        //Call onUpdate()
        //AppWidgetManager manager = AppWidgetManager.getInstance(context);
        //ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AppWidget.class.getName());
        //int[] appWidgetIds = manager.getAppWidgetIds(thisAppWidget);
        //onUpdate(context, manager, appWidgetIds);

        if (queue != null)
        {
            Log.d("App Widget", "Queue is not null");
        }
        else
            Log.d("App Widget", "Queue is NULL");

        if (intent.getAction().equals(nextAction)) {//Next Photo
            //TODO
        }

        if (intent.getAction().equals(previousAction)) {//Previous Photo
            //TODO
        }
        Log.d("App Widget", "End onReceive()");
    }





}

