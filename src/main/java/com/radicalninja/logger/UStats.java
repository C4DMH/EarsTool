package com.radicalninja.logger;

/**
 * Created by gwicks on 28/03/2017.
 */

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 3/2/15.
 */
public class UStats {

    //private static final String TAG = "UStats";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = UStats.class.getSimpleName();


    private DateFormat mDateFormat = new SimpleDateFormat();

    static String directoryName = "/videoDIARY/";
    static String time;

    //Context mContext = MainActivity.getIntance();



//    @SuppressWarnings("ResourceType")
//    public static void getStats(Context context){
//        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
//        int interval = UsageStatsManager.INTERVAL_YEARLY;
//        Calendar calendar = Calendar.getInstance();
//        long endTime = calendar.getTimeInMillis();
//        calendar.add(Calendar.DAY_OF_YEAR, -1);
//        long startTime = calendar.getTimeInMillis();
//
//        Log.d(TAG, " getStats Range start:" + dateFormat.format(startTime) );
//        Log.d(TAG, "get Stats Range end:" + dateFormat.format(endTime));
//
//        UsageEvents uEvents = usm.queryEvents(startTime,endTime);
//        while (uEvents.hasNextEvent()){
//            UsageEvents.Event e = new UsageEvents.Event();
//            uEvents.getNextEvent(e);
//
//            if (e != null){
//                Log.d(TAG, "Event: " + e.getPackageName() + "\t" +  e.getTimeStamp());
//            }
//        }
//    }

    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();

        //
//        calendar.add(Calendar.DAY_OF_YEAR, -1);
//        long endtime = calendar.get(Calendar.DAY_OF_YEAR);
//        calendar.add(Calendar.DAY_OF_YEAR, -1);
//        long starttime = calendar.get(Calendar.DAY_OF_YEAR);
//
//        Log.d(TAG, "getUsageStatsList: NEW DAYS: endtime: " + endtime + " startime: " + starttime);

        //


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Log.d(TAG, "getUsageStatsList: SDF =  " + sdf);

        //calendar.setTime(sdf.p);
        // long endTime = calendar.setTime();





        //

        long endTime = calendar.getTimeInMillis();
        //calendar.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = calendar.getTimeInMillis() - 24*60*60*1000;
        Log.d(TAG, "getUsageStatsList: endtime: " + endTime + "starttime: " + startTime);

        Date one = new Date(startTime);
        Date two = new Date(endTime);

        Log.d(TAG, "getUsageStatsList: data start time: " + one);
        Log.d(TAG, "getUsageStatsList: date endtime:  " + two);
        time = two.toString();



        // long endTime1 = calendar.

        //  Log.d(TAG, "Range start1 :" + dateFormat.format(calendar.getTimeInMillis()));
        // Log.d(TAG, "Range end1 :" + dateFormat.format(System.currentTimeMillis()));

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  startTime,endTime);     // calendar.getTimeInMillis(), System.currentTimeMillis()); //(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
        //List<UsageStats> usageStatsList = getUsageStatistics(statsUsageInterval.mInterval);

        Map<String, UsageStats> stats = usm.queryAndAggregateUsageStats(startTime, endTime);

        for(String key : stats.keySet()){
            Log.d(TAG, "getUsageStatsList: KEYS: " + stats.get(key));
        }

        for(Map.Entry<String, UsageStats> entry : stats.entrySet()){
            Log.d(TAG, "getUsageStatsList: " + entry.getKey() + " " + entry.getValue().getTotalTimeInForeground());
        }



        Collections.sort(usageStatsList, new TotalTimeUsed());
        return usageStatsList;
    }

    //public static void printUsageStats(List<UsageStats> usageStatsList){
    public static String printUsageStats(List<UsageStats> usageStatsList, Context context){

        Log.d(TAG, "printUsageStats: in print");

        String uri = (context.getExternalFilesDir(null) + directoryName + "AppUsage_" + time + ".txt");
        //StatsJobService.setFielName(uri);
        //StatsJobService.fileName = uri;

        Log.d(TAG, "printUsageStats: The string URI for file is: " + uri);


        File file = new File(uri);
        for (UsageStats u : usageStatsList){

            if(u.getTotalTimeInForeground() > 0){

                int minutes = (int)u.getTotalTimeInForeground()/60000;
                int seconds = (int)(u.getTotalTimeInForeground() % 60000) / 1000;
                Log.d(TAG, "printUsageStats: minutes: " + minutes + " seconds: " + seconds);
                //writeToFile(file, "UsageStats: minutes: " + minutes + " seconds: " + seconds +"\n");
                Log.d(TAG, "Pkg: " + u.getPackageName()  + "\n\tForegroundTime: "
                        + u.getTotalTimeInForeground()/1000 + " seconds " );// mDateFormat.format(u.getLastTimeUsed()) + " time last used") ;
                writeToFile(file, "Pkg: " + u.getPackageName() +   "\nForegroundTime: "
                        + u.getTotalTimeInForeground()/1000 + " seconds \n" );
                Date data = new Date(u.getLastTimeUsed());

                Log.d(TAG, "printUsageStats: DATE = " + data);
                //writeToFile(file, "printUsageStats: DATE = " + data + "\n");


                Log.d(TAG, "printUsageStats: Time last used: " +data);
                writeToFile(file, "Time last used: " +data +"\n");
                Log.d(TAG, "printUsageStats: ______________________________________________________");
                writeToFile(file, " ______________________________________________________\n\n");



            }

//            int minutes = (int)u.getTotalTimeInForeground()/60000;
//            int seconds = (int)(u.getTotalTimeInForeground() % 60000) / 1000;
//            Log.d(TAG, "printUsageStats: minutes: " + minutes + " seconds: " + seconds);
//            Log.d(TAG, "Pkg: " + u.getPackageName() +  "\t" + "ForegroundTime: "
//                    + u.getTotalTimeInForeground()/1000 + " seconds " );// mDateFormat.format(u.getLastTimeUsed()) + " time last used") ;
        }

        return uri;




    }

    public static String printCurrentUsageStatus(Context context){
        return printUsageStats(getUsageStatsList(context), context);
    }
    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

    private static class TotalTimeUsed implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getTotalTimeInForeground(), left.getTotalTimeInForeground());
        }
    }

    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        System.out.println("The state of the media is: " + Environment.getExternalStorageState());

        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            Log.e("History", "In try");
            Log.d(TAG, "writeToFile: ");
            stream = new FileOutputStream(file, true);
            Log.d(TAG, "writeToFile: 2");
            stream.write(data.getBytes());
            Log.d(TAG, "writeToFile: 3");
        } catch (FileNotFoundException e) {
            Log.e("History", "In catch");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }


        try {

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
