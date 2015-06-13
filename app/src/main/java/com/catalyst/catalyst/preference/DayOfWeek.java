package com.catalyst.catalyst.preference;

import android.content.Context;

import com.catalyst.catalyst.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds the WeekDay Enumeration for notifying users of new inspirations.
 *
 * Created by Nick Piscopio on 5/31/15.
 */
public class DayOfWeek
{
    private static Context context;

    private WeekDay weekDay;

    /**
     * Enumeration of the days of the week.
     */
    private enum WeekDay
    {
        SUNDAY(context.getString(R.string.setting_inspire_sunday)),
        MONDAY(context.getString(R.string.setting_inspire_monday)),
        TUESDAY(context.getString(R.string.setting_inspire_tuesday)),
        WEDNESDAY(context.getString(R.string.setting_inspire_wednesday)),
        THURSDAY(context.getString(R.string.setting_inspire_thursday)),
        FRIDAY(context.getString(R.string.setting_inspire_friday)),
        SATURDAY(context.getString(R.string.setting_inspire_saturday));

        private int value;

        private String day;

        WeekDay(String day)
        {
            this.day = day;
            this.value = ordinal();
        }

        /**
         * Get the week day from its corresponding string value.
         *
         * @param text  The string to get the WeekDay.
         *
         * @return  The WeekDay.
         */
        public static WeekDay fromString(String text)
        {
            if (text != null)
            {
                for (WeekDay weekDay : WeekDay.values())
                {
                    if (text.equalsIgnoreCase(weekDay.day))
                    {
                        return weekDay;
                    }
                }
            }

            return null;
        }

        /**
         * Map of enumerations with corresponding integer values.
         */
        private static final Map<Integer, WeekDay> intToTypeMap = new HashMap<>();
        static
        {
            for (WeekDay type : WeekDay.values())
            {
                intToTypeMap.put(type.value, type);
            }
        }

        /**
         * Get the week day from its corresponding int value.
         *
         * @param i The int to get the WeekDay.
         *
         * @return  The WeekDay.
         */
        public static WeekDay fromInt(int i)
        {
            WeekDay type = intToTypeMap.get(Integer.valueOf(i));

            if (type == null)
            {
                return null;
            }

            return type;
        }

        /**
         * Gets the day of the week's value.
         */
        public int getValue()
        {
            return value;
        }

        public String getDay()
        {
            return day;
        }
    }

    public DayOfWeek(Context context, String weekDay)
    {
        this.context = context;
        this.weekDay = WeekDay.fromString(weekDay);
    }

    public DayOfWeek(Context context, int weekDay)
    {
        this.context = context;
        this.weekDay = WeekDay.fromInt(weekDay);
    }

    /**
     * Gets the week day value.
     *
     * @return  The corresponding integer value.
     */
    public int getWeekDayValue()
    {
        return weekDay.getValue();
    }

    /**
     * Gets the week day string.
     *
     * @return  The corresponding string value.
     */
    public String getWeekDayString()
    {
        return weekDay.getDay();
    }
}