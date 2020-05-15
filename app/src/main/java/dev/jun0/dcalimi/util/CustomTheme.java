package dev.jun0.dcalimi.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import dev.jun0.dcalimi.R;

public class CustomTheme {
    private Context mContext;

    public CustomTheme(Context context){
        mContext = context;
    }

    public void setThemeByPreference(){
        setThemeByColor(getPreferenceColor());
    }

    private void setThemeByColor(int color){
        final int redPrimary = mContext.getColor(R.color.redPrimary);
        final int pinkPrimary = mContext.getColor(R.color.pinkPrimary);
        final int purplePrimary = mContext.getColor(R.color.purplePrimary);
        final int indigoPrimary = mContext.getColor(R.color.indigoPrimary);
        final int bluePrimary = mContext.getColor(R.color.bluePrimary);
        final int tealPrimary = mContext.getColor(R.color.tealPrimary);
        final int greenPrimary = mContext.getColor(R.color.greenPrimary);
        final int orangePrimary = mContext.getColor(R.color.orangePrimary);
        final int brownPrimary = mContext.getColor(R.color.brownPrimary);
        final int blueGreyPrimary = mContext.getColor(R.color.blueGreyPrimary);

        if (color == redPrimary) {
            mContext.setTheme(R.style.AppThemeRed);
        } else if (color == pinkPrimary) {
            mContext.setTheme(R.style.AppThemePink);
        } else if (color == purplePrimary) {
            mContext.setTheme(R.style.AppThemePurple);
        } else if (color == indigoPrimary) {
            mContext.setTheme(R.style.AppThemeIndigo);
        } else if (color == bluePrimary) {
            mContext.setTheme(R.style.AppThemeBlue);
        } else if (color == tealPrimary) {
            mContext.setTheme(R.style.AppThemeTeal);
        } else if (color == greenPrimary) {
            mContext.setTheme(R.style.AppThemeGreen);
        } else if (color == orangePrimary) {
            mContext.setTheme(R.style.AppThemeOrange);
        } else if (color == brownPrimary) {
            mContext.setTheme(R.style.AppThemeBrown);
        } else if (color == blueGreyPrimary) {
            mContext.setTheme(R.style.AppThemeBlueGrey);
        }
    }

    private int getPreferenceColor(){
        SharedPreferences preferenceSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferenceSharedPreferences.getInt("themeColor", mContext.getColor(R.color.greenPrimary));
    }
}
