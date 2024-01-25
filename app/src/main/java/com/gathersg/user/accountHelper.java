package com.gathersg.user;

public class accountHelper {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_UEN = "UEN";
    public static final String KEY_VOLUNTEER = "volunteer";
    public static final String KEY_ORGANISERS = "organisers";
    public static final String KEY_MYEVENTS = "myEvents";
    public static final String KEY_VIA = "via";
    public static String accountType;
    public static String accountUID;


    public accountHelper() {

    }


    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        accountHelper.accountType = accountType;
    }

}
