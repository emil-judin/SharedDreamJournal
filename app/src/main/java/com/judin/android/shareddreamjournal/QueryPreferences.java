package com.judin.android.shareddreamjournal;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_REG_DIRTY = "regStatus";

    public static boolean isRegDirty(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_REG_DIRTY, false);
    }

    public static void setRegDirty(Context context, boolean isDirty){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_REG_DIRTY, isDirty)
                .apply();
    }
}
