package layout;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;


import android.app.PendingIntent;
import android.content.Intent;


import com.android.dejaphoto.GetAllPhotosFromGallery;
import com.android.dejaphoto.MainActivity;
import com.android.dejaphoto.Photo;
import com.android.dejaphoto.PhotoQueue;
import com.android.dejaphoto.PhotoChooser;
import com.android.dejaphoto.Chooser;
import com.android.dejaphoto.R;
import com.android.dejaphoto.ImageController;

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
            Log.e("App Widget", "onUpdate()");
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
        Log.e("App Widget", "onEnabled()");
        initialize(context);
        show(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        //Call Enabled()
        onEnabled(context);
        //Call onUpdate()
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), AppWidget.class.getName());
        int[] appWidgetIds = manager.getAppWidgetIds(thisAppWidget);

        onUpdate(context, manager, appWidgetIds);

        if (intent.getAction().equals(nextAction)) {//Next Photo
            Log.e("App Widget", "onReceive()");
            next(context);
        }

        if (intent.getAction().equals(previousAction)) {//Previous Photo
            previous(context);
        }
    }


    public void show(Context context) {
        Photo photo = new Photo();
        Bitmap newBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
        photo.setPhoto(newBitmap);
        controller.displayImage(photo);
    }

    public void next(Context context) {
        //get next photo
        Log.d("App Widget", "nextCalled");
        Photo nextPhoto = queue.next();

        // no photos in album
        if (nextPhoto == null) {
            Log.d("App Widget", "No Photo");
            Bitmap newBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
            System.out.println(newBitmap);
            nextPhoto = new Photo();
            nextPhoto.setPhoto(newBitmap);


        }
        controller.displayImage(nextPhoto);
    }

    public void previous(Context context) {
        //get previous photo
        Log.d("App Widget", "previousCalled");
        Photo previousPhoto = queue.previous();

        // no previous photo
        if (previousPhoto == null) {
            Log.d("App Widget", "No Photo");
            Bitmap newBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
            System.out.println(newBitmap);
            previousPhoto = new Photo();
            previousPhoto.setPhoto(newBitmap);
        }
        controller.displayImage(previousPhoto);
    }

    public void initialize(Context context) {
        //Create controller
        controller = new ImageController(context);

        //Get list of pictures
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
        GetAllPhotosFromGallery gallery = new GetAllPhotosFromGallery(file, context);

        PhotoChooser chooser = new PhotoChooser(gallery.getImages());
        queue = new PhotoQueue<>(chooser);
    }


}

