package com.gathersg.user;

import com.google.firebase.firestore.Blob;

public class eventHelper {
    public static final String KEY_EVENTS = "events";
    public static final String KEY_EVENTNAME = "eventName";
    public static final String KEY_EVENTDESC = " eventDesc";
    public static final String KEY_EVENTLOCNAME = "eventLocName";
    public static final String KEY_EVENTMAXPAX = "eventMaxPax";
    public static final String KEY_EVENTIMAGE = "eventImage";
    public static final String KEY_EVENTSIGNUP = "eventSignUp";
    public static final String KEY_EVENTORG = "eventOrg";
    public static final String KEY_LAT = "eventLat";
    public static final String KEY_LON = "eventLon";
    public static final String KEY_EVENTDATE = "eventDate";
    public static final String KEY_EVENTSTATUS = "eventStatus";
    public static final String KEY_EVENTATTENED = "Attended";
     static String KEY_EVENTUPCOMING = "Upcoming";
    static String KEY_EVENTCONCLUDED = "Concluded";
    static String KEY_OPEN = "Open";
    static String KEY_CLOSE= "Close";
    static String KEY_CANCELLED = "Cancelled";

    public static final String KEY_SIGNUPSTATUS = "signUpStatus";
    public static String eventName;
    public static String eventDescription;
    public static String eventDate;
    public static String eventLocName;
    public static String eventOrganiser;
    public static double eventLatitude, eventLongitude;
    public static long eventSignUp;
    public static int eventMaxParticipants;
    public static Blob eventImage;
    public static String eventStatus;


    public eventHelper() {

    }

    public String getEventOrganiser() {
        return eventOrganiser;
    }

    public void setEventOrganiser() {
        eventOrganiser = eventOrganiser;
    }

    public String getEventLocName() {
        return eventLocName;
    }

    public void setEventLocName(String eventLocName) {
        eventHelper.eventLocName = eventLocName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        eventHelper.eventDescription = eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        eventHelper.eventDate = eventDate;
    }

    public double getEventLatitude() {
        return eventLatitude;
    }

    public void setEventLatitude(double eventLatitude) {
        eventHelper.eventLatitude = eventLatitude;
    }

    public double getEventLongitude() {
        return eventLongitude;
    }

    public void setEventLongitude(double eventLongitude) {
        eventHelper.eventLongitude = eventLongitude;
    }

    public int getEventMaxParticipants() {
        return eventMaxParticipants;
    }

    public void setEventMaxParticipants(int eventMaxParticipants) {
        eventHelper.eventMaxParticipants = eventMaxParticipants;
    }

    public Blob getEventImage() {
        return eventImage;
    }

    public void setEventImage(Blob eventImage) {
        eventHelper.eventImage = eventImage;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        eventHelper.eventName = eventName;
    }

    public void uploadAllEvents(String name, String desc, String date, String organiser, String locName, Double lat, Double lon, Blob image) {
        eventName = name;
        eventDescription = desc;
        eventDate = date;
        eventLocName = locName;
        eventOrganiser = organiser;
        eventLatitude = 0.0;
        eventLongitude = 0.0;
        eventImage = image;
    }

    public void uploadmyEvents(String name, String desc, String date, String organiser, String locName, Double lat, Double lon, Blob image, String status) {
        eventName = name;
        eventDescription = desc;
        eventDate = date;
        eventLocName = locName;
        eventOrganiser = organiser;
        eventLatitude = 0.0;
        eventLongitude = 0.0;
        eventImage = image;
        eventStatus = status;
    }

    public void uploadmyOrgEvents(String name, String desc, String date, String organiser, String locName, Double lat, Double lon, Blob image, String status, Long pax) {
        eventName = name;
        eventDescription = desc;
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
