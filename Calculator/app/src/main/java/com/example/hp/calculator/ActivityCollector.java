package com.example.hp.calculator;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public abstract class ActivityCollector {
    private static List<Activity> collector = new ArrayList<>();

    public static void addActivity(Activity activity){
        collector.add(activity);
    }

    public static void remove(Activity activity){
        collector.remove(activity);
    }

    public static void exit(){
        for (Activity activity : collector){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
