package com.android.dejaphoto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * Creates a photo database comprised of three hashmaps that photos on phone are loaded into.
 * Has functionality for queering the hash maps and getting photos relevant to current user
 * location/time of day/day of week. Creates a list of relevant photos to display on homescreen
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

    //Put photos into the database
    public DatabaseManager(List<Photo> photoList)
    {
        initialize();
        update(photoList);
    }
    //get photos related to current day of the week
    public List<Photo> queryDayOfTheWeek(String dow)
    {
        if (dow != null) {
            List <Photo> photoList = dayMap.get(dow);
            return photoList;
        }
        else return null;
    }
    //get photos related to current hour
    public List<Photo> queryHour(String hour)
    {
        if (hour != null)
        {
            List<Photo> photoList = timeMap.get(hour);
            return photoList;
        }
        else return null;
    }
    //get photos related to current location
    public List<Photo> queryLocation(String latitude, String longitude)
    {
        if (latitude != null && longitude != null)
        {
            double latDouble = new Double(latitude);
            double longDouble = new Double(longitude);
            TreeSet<Photo> results = new TreeSet<>(new Comparator<Photo>() {
                @Override
                public int compare(Photo photo1, Photo photo2) {
                    double result = photo1.distanceFromOrigin - photo2.distanceFromOrigin;
                    return (int) result;
                }
            });

            for (Photo photo : locationMap.get("Normal"))
            {//Since we do have location, we will compare in the normal set

                //Use the distance formula to calculate the differences
                double latDifference = Math.pow(latDouble - new Double(photo.latitude), 2);
                double longDifference = Math.pow(longDouble - new Double(photo.longitude), 2);
                double temp = Math.sqrt(latDifference + longDifference);
                double distanceFromOrigin = Math.abs(temp);

                //Update the distance and add into the list
                photo.distanceFromOrigin = distanceFromOrigin;
                results.add(photo);
            }

            //Return the sorted collection as a list
            return new LinkedList<Photo>(results);
        }
        //If no info, then return the unknown set of photos with no information
        else return locationMap.get("Unknown");
    }

    //Method to initialize hash maps with photos from default camera album
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

        //Add Unknown container to locationMap
        locationMap.put("Unknown", new LinkedList<Photo>());
        locationMap.put("Normal", new LinkedList<Photo>());
    }

    //add any new photos taken with camera into hashmaps
    public void update(List<Photo> photoList)
    {
        for (Photo photo : photoList)
        {//Parse each photo and put it into appropriate container
            //Put in dayMap
            List<Photo> dayContainer;
            List<Photo> timeContainer;
            List<Photo> locationContainer;

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

            //Put photo in appropriate locationContainer
            String latitude = photo.getLatitude();
            String longitude = photo.getLongitude();
            if (latitude != null && longitude != null)
            {
                locationContainer = locationMap.get("Normal");
                locationContainer.add(photo);
            }
            else
            {//No information about the location
                locationContainer = locationMap.get("Unknown");
                locationContainer.add(photo);
            }

            //Update size
            size++;
        }
    }
    //getter method for size of photo database
    public int size()
    {
        return size;
    }


}
