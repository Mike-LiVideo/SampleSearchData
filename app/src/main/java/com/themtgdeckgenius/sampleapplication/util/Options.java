package com.themtgdeckgenius.sampleapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import java.io.File;
import java.util.Properties;

/**
 * Application wide options defined in device-dependant property file.
 *
 * Properties file is downloaded and then set to shared preferences
 * 
 * 
 */
public class Options{

    public static final String CLIENT_NAME = "ClientName";
    public static final String UNIT = "Unit";
    public static final String HOST = "Host";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    public static final String STATE = "State";
    /**getOptions default = 320*/
    public static final String PAPER_WIDTH = "PaperWidth";
    /**getOptions default = -1*/
    public static final String HEADER_HEIGHT = "HeaderHeight";
    /**getOptions default = false*/
    public static final String SINGLE_PROCESSING = "SingleProcessing";
    /**getOptions default = true*/
    public static final String USES_VIDEO = "ClientUsesVideo";
    /**getOptions default = false*/
    public static final String PARK_MOBILE = "UsesParkMobile";
    /**getOptions default = false*/
    public static final String DPT = "UsesDPT";
    /**getOptions default = true*/
    public static final String USES_RECORDING = "ClientUsesRecording";
    /**getOptions default = true*/
    public static final String USES_REPEAT_OFFENDERS = "UsesRepeatOffenders";
    /**getOptions default = false*/
    public static final String USES_PERMIT_LOOKUP = "UsesPermitLookup";
    /**getOptions default = false*/
    public static final String USES_PROGRESIVE_BAIL = "UsesProgressiveBail";
    /**getOptions default = true*/
    public static final String SCOFFLAW_ON = "ScoffLawOn";
    /**getOptions default = true*/
    public static final String CAN_VOID = "CanVoid";
    /**getOptions default = true*/
    public static final String CAN_REISSUE = "CanReissue";
    /**getOptions default = true*/
    public static final String USES_PLATE_INFO = "UsesPlateInfoSearch";
    /**getOptions default = 5*/
    public static final String VIDEO_MAX_ATTACH = "VideoMaxNumber";
    /**getOptions default = 30*/
    public static final String VIDEO_MAX_TIME = "VideoMaxTime";
    /**getOptions default = 5*/
    public static final String RECORD_MAX_ATTACH = "RecordMaxNumber";
    /**getOptions default = 30*/
    public static final String RECORD_MAX_TIME = "RecordMaxTime";
    /**getOptions default = 3*/
    public static final String MAX_VIOLATIONS = "MaxNumberOfViolations";

    public static boolean hasOldVersion(final Context aContext){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + FileListInterface.OPTIONS_FILE,
                getOptionsFilename());
        return file.exists();
    }

    /**
     * Gets String variable stored from the properties file
     *
     * @param property The property value you want.
     * @param context The application's context.
     * @return The string variable requested.
     */
    public static String getOptions(final String property, final Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(property, "");
    }

    /**
     * Gets String variable stored from the properties file
     *
     * @param property The property value you want.
     * @param defaultInt What to return if property is not found
     * @param context The application's context.
     * @return The string variable requested.
     */
    public static int getOptions(final String property, int defaultInt, final Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(property, defaultInt);
    }

    /**
     * Gets String variable stored from the properties file
     *
     * @param property The property value you want.
     * @param defaultBoolean The value to return if property is found.
     * @param context The application's context.
     * @return The string variable requested.
     */
    public static boolean getOptions(final String property, boolean defaultBoolean, final Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(property, defaultBoolean);
    }

    public static void set(final Properties aProperties, final Context aContext){
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(aContext).edit();
        for(String name : aProperties.stringPropertyNames()){

            try{
                if(aProperties.getProperty(name).trim().equalsIgnoreCase("true") || aProperties.getProperty(name).trim().equalsIgnoreCase("false")){
                    editor.putBoolean(name, Boolean.parseBoolean(aProperties.getProperty(name).trim()));
                }
                else if(isNumeric(aProperties.getProperty(name).trim())){
                    editor.putInt(name, Integer.parseInt(aProperties.getProperty(name).trim()));
                }
                else{
                    editor.putString(name, aProperties.getProperty(name).trim());
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        editor.apply();
    }

	public static boolean isNumeric(String str){
		return str.matches("-?\\d+(\\.\\d+)?");  
	}

    public static String getOptionsFilename(){
        return FileListInterface.OPTIONS_FILE;
    }

}
