package com.catalyst.catalyst.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.preference.DayOfWeek;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

/**
 * Utility class for dates.
 *
 * Created by Nick Piscopio on 6/13/15.
 */
public class DateUtil
{
    //Needs to be 53 because the first week of the year is 1 and might not be a full week.
    private static final int WEEKS_IN_YEAR = 53;
    private static final int FIRST_WEEK_MIN = 1;
    private static final String DELIMITER = ", ";

    private Context context;

    public DateUtil(Context context)
    {
       this.context = context;
    }

    /**
     * Sorts a set of dates.
     *
     * @param dates     The dates to sort.
     *
     * @return  A string of dates.
     */
    public String sortDate(Set<String> dates)
    {
        String valueString = "";

        int[] weekDayValues = new int[dates.size()];

        String[] values = dates.toArray(new String[dates.size()]);

        int prefLength = values.length;

        for (int i = 0; i < prefLength; i++)
        {
            weekDayValues[i] = new DayOfWeek(context, values[i]).getWeekDayValue();
        }

        Arrays.sort(weekDayValues);

        int weekDayLength = weekDayValues.length;

        for (int i = 0; i < weekDayLength; i++)
        {
            String day = new DayOfWeek(context, weekDayValues[i]).getWeekDayString();

            valueString += (i != weekDayLength - 1) ? day + DELIMITER : day;
        }

        return valueString;
    }

    /**
     * Gets the next alarm time.
     *
     * @param unsortedDays      A list of string dates to sort.
     * @param alarmTime         The time the alarm is set.
     *
     * @return  The next alarm time.
     */
    public long getNextAlarm(Set<String> unsortedDays, long alarmTime)
    {
        Set<String> unsortedTemp = new HashSet<>();
        unsortedTemp.addAll(unsortedDays);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String today = new DayOfWeek(context, day - 1).getWeekDayString();

        boolean incrementing = false;

        String nextAlarm = today;

        //Today found
        if (unsortedTemp.contains(today))
        {
            long now = calendar.getTimeInMillis();
            long alarmMillis = getMidnight() + convertTime(alarmTime, TimeZone.getTimeZone("UTC"), TimeZone.getDefault());
            if (now > alarmMillis)
            {
                incrementing = true;
            }
        }
        else
        {
            unsortedTemp.add(today);

            incrementing = true;
        }

        if (incrementing)
        {
            List<String> days = Arrays.asList(sortDate(unsortedTemp).split(DELIMITER));

            int todayIndex = days.indexOf(today);

            boolean incrementToNextWeek = todayIndex == days.size() - 1;

            int nextIndex = (incrementToNextWeek) ? 0 : todayIndex + 1;

            nextAlarm = days.get(nextIndex);

            incrementing = incrementToNextWeek;
        }

        Calendar settingsCal = Calendar.getInstance();
        settingsCal.setTimeInMillis(alarmTime);

        calendar.setMinimalDaysInFirstWeek(FIRST_WEEK_MIN);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_WEEK, new DayOfWeek(context, nextAlarm).getWeekDayValue() + 1);

        if (incrementing)
        {
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

            if (weekOfYear == 1)
            {
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                calendar.set(Calendar.MONTH, 0);
            }

            if (weekOfYear < WEEKS_IN_YEAR)
            {
                calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear + 1);
            }
            else
            {
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                calendar.set(Calendar.WEEK_OF_YEAR, 1);
            }
        }

        calendar.set(Calendar.HOUR_OF_DAY, settingsCal.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, settingsCal.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * Gets this morning at midnight.
     *
     * @return Midnight today.
     */
    private long getMidnight()
    {
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        return midnight.getTimeInMillis();
    }

    /**
     * Converts long time from one timezone to another.
     *
     * @param time  The time in long format to convert.
     * @param from  The starting timezone.
     * @param to    The ending timezone.
     *
     * @return  The converted time.
     */
    public long convertTime(long time, TimeZone from, TimeZone to)
    {
        return time + getTimeZoneOffset(time, from, to);
    }

    public long convertDefaultTime()
    {
        long defaultTime = Long.valueOf(context.getResources().getString(
                R.string.default_interval_time));
        long convertedDefaultTime = convertTime(defaultTime, TimeZone.getDefault(),
                                                TimeZone.getTimeZone("UTC"));

        return convertedDefaultTime;
    }

    public void storeDefaultTime(long defaultTime)
    {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putLong(context.getResources().getString(R.string.preference_time), defaultTime).apply();
    }

    /**
     * Gets the timezone offset to convert a time.
     *
     * @param time  The time in long format to convert.
     * @param from  The starting timezone.
     * @param to    The ending timezone.
     *
     * @return  The timezone offset.
     */
    private long getTimeZoneOffset(long time, TimeZone from, TimeZone to)
    {
        int fromOffset = from.getOffset(time);
        int toOffset = to.getOffset(time);
        int diff = 0;

        if (fromOffset >= 0)
        {
            if (toOffset > 0)
            {
                toOffset = -1*toOffset;
            }
            else
            {
                toOffset = Math.abs(toOffset);
            }

            diff = (fromOffset+toOffset)*-1;
        }
        else
        {
            if (toOffset <= 0)
            {
                toOffset = -1*Math.abs(toOffset);
            }

            diff = (Math.abs(fromOffset)+toOffset);
        }

        return diff;
    }
}