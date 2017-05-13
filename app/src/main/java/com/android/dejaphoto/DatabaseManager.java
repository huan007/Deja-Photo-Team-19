package com.android.dejaphoto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Cyrax on 5/11/2017.
 */

public class DatabaseManager {
    HashMap<String, List<Photo>> dayMap;
    HashMap<String, List<Photo>> timeMap;
    HashMap<String, List<Photo>> locationMap;
    int size;

    public DatabaseManager()
    {
        initialize();
    }

    public DatabaseManager(List<Photo> photoList)
    {
        //Put photos into the database
        initialize();
        update(photoList);
    }

    public List<Photo> queryDayOfTheWeek(String dow)
    {
        if (dow != null) {
            List <Photo> photoList = dayMap.get(dow);
            return photoList;
        }

        else return null;
    }

    public List<Photo> queryHour(String hour)
    {
        if (hour != null)
        {
            List<Photo> photoList = timeMap.get(hour);
            return photoList;
        }
        else return null;
    }

    public void initialize()
    {
        dayMap = new HashMap<String, List<Photo>>(5);
        timeMap = new HashMap<String, List<Photo>>(5);
        locationMap = new HashMap<String, List<Photo>>(5);
        size = 0;

        //Add containers to dayMap
        dayMap.put("Monday", new LinkedList<Photo>());
        dayMap.put("Tuesday", new LinkedList<Photo>());
        dayMap.put("Wednesday", new LinkedList<Photo>());
        dayMap.put("Thursday", new LinkedList<Photo>());
        dayMap.put("Friday", new LinkedList<Photo>());
        dayMap.put("Saturday", new LinkedList<Photo>());
        dayMap.put("Sunday", new LinkedList<Photo>());
        dayMap.put("Unknown", new LinkedList<Photo>());

        //Add containers to timeMap
        for(int i = 1; i < 25; i++)
        {
            SimpleDateFormat hourFormat = new SimpleDateFormat("kk");
            try {
                Date myDate = hourFormat.parse(Integer.toString(i));
                timeMap.put(hourFormat.format(myDate), new LinkedList<Photo>());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        timeMap.put("Unknown", new LinkedList<Photo>());
    }

    public void update(List<Photo> photoList)
    {
        for (Photo photo : photoList)
        {//Parse each photo and put it into appropriate container
            //Put in dayMap
            List<Photo> dayContainer;
            List<Photo> timeContainer;

            //Put photo in appropriate dayContainer
            String dow = photo.getDayOfTheWeek();
            if (dow != null)
            {
                dayContainer = dayMap.get(dow);
                dayContainer.add(photo);
            }
            else
            {//No information about DOW
                dayContainer = dayMap.get("Unknown");
                dayContainer.add(photo);
            }

            //Put photo in appropriate timeContainer
            String hour = photo.getHour();
            if (hour != null){
                timeContainer = timeMap.get(hour);
                timeContainer.add(photo);
            }
            else
            {//No information about the time
                timeContainer = timeMap.get("Unknown");
                timeContainer.add(photo);
            }

            //Update size
            size++;
        }
    }

    public int size()
    {
        return size;
    }


}
