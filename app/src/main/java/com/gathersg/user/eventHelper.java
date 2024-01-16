package com.gathersg.user;

import com.google.firebase.firestore.Blob;

import java.sql.Timestamp;
import java.util.Stack;

public class eventHelper {
    public static String eventName;
    public static String eventDescription;
    public static String eventDate;
    public static String eventLocName;
    public static String eventOrganiser;
    public static double eventLatitude,eventLongitude;
    public static long eventSignUp;
    public static int eventMaxParticipants;
    public static Blob eventImage;
    public static String eventStatus;
    public static final String KEY_EVENTS = "events";
    public static final String KEY_EVENTNAME = "eventName";
    public static final String KEY_EVENTDESC = " eventDesc";
    public static final String KEY_EVENTLOCNAME = "eventLocName";
    public static final String KEY_EVENTMAXPAX = "eventMaxPax";
    public static final String KEY_EVENTIMAGE = "eventImage";
    public static final String KEY_EVENTSIGNUP = "eventSignUp";

    public static final String KEY_EVENTORG = "eventOrg";
    public static final String KEY_LAT ="eventLat";
    public static final String KEY_LON = "eventLon";
    public static final String KEY_EVENTDATE = "eventDate";

    public static final String KEY_EVENTSTATUS = "eventStatus";
    public static final String KEY_EVENTATTENED = "Attended";
    public static final String KEY_EVENTUPCOMING = "Upcoming";
    public static final String KEY_EVENTCONCLUDED = "Concluded";




    public eventHelper(){

    }
    public String getEventOrganiser(){
        return eventOrganiser;
    }
    public void setEventOrganiser(){
        this.eventOrganiser = eventOrganiser;
    }

    public String getEventLocName(){
        return eventLocName;
    }
    public void setEventLocName(String eventLocName){
        this.eventLocName =eventLocName;
    }

    public String getEventDescription(){
        return eventDescription;
    }
    public void setEventDescription(String eventDescription){
        this.eventDescription = eventDescription;
    }

    public String getEventDate(){
        return eventDate;
    }

    public void setEventDate(String eventDate){
        this.eventDate = eventDate;
    }
    public double getEventLatitude( ){
        return eventLatitude;
    }
    public void setEventLatitude(double eventLatitude){
        this.eventLatitude = eventLatitude;
    }
    public double getEventLongitude( ){
        return eventLongitude;
    }
    public void setEventLongitude(double eventLongitude){
        this.eventLongitude = eventLongitude;
    }
    public int getEventMaxParticipants(){
        return eventMaxParticipants;
    }

    public void setEventMaxParticipants(int eventMaxParticipants){
        this.eventMaxParticipants = eventMaxParticipants;
    }
    public Blob getEventImage(){
        return eventImage;
    }
    public void setEventImage(Blob eventImage){
        this.eventImage =eventImage;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void uploadAllEvents(String name,String desc,String date,String organiser,String locName,Double lat,Double lon ,Blob image)
    {
        eventName = name;
        eventDescription =desc;
        eventDate = date;
        eventLocName = locName;
        eventOrganiser = organiser;
        eventLatitude = 0.0;
        eventLongitude = 0.0;
        eventImage = image;
    }
    public void uploadmyEvents(String name,String desc,String date,String organiser,String locName,Double lat,Double lon ,Blob image,String status)
    {
        eventName = name;
        eventDescription =desc;
        eventDate = date;
        eventLocName = locName;
        eventOrganiser = organiser;
        eventLatitude = 0.0;
        eventLongitude = 0.0;
        eventImage = image;
        eventStatus = status;
    }
    public void uploadmyOrgEvents(String name,String desc,String date,String organiser,String locName,Double lat,Double lon ,Blob image,String status,Long pax)
    {
        eventName = name;
        eventDescription =desc;
        eventDate = date;
        eventLocName = locName;
        eventOrganiser = organiser;
        eventLatitude = 0.0;
        eventLongitude = 0.0;
        eventImage = image;
        eventStatus = status;
        eventSignUp = pax;
    }

}
