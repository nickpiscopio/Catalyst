package com.catalyst.catalyst.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.util.Constant;
import com.catalyst.catalyst.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Time preference node for the preference screen.
 *
 * Created by Nick Piscopio on 5/28/15.
 */
public class TimePreference extends DialogPreference
{
    private Calendar calendar;
    private TimePicker picker = null;

    private DateUtil dateUtil;

    Context context;

    public TimePreference(Context ctxt)
    {
        this(ctxt, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs)
    {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle)
    {
        super(ctxt, attrs, defStyle);

        this.context = ctxt;

        setDialogTitle("");
        setPositiveButtonText(R.string.positive_set);

        calendar = new GregorianCalendar();

        dateUtil = new DateUtil(context);
    }

    @Override
    protected View onCreateDialogView()
    {
        picker = new TimePicker(getContext());
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        picker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        picker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        if (positiveResult)
        {
            calendar.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
            calendar.set(Calendar.MINUTE, picker.getCurrentMinute());

            setSummary(getSummary());

            long millis = calendar.getTimeInMillis();

            if (callChangeListener(millis))
            {
                long settingsTime = dateUtil.convertTime(millis, TimeZone.getTimeZone(
                        Constant.TIMEZONE_UTC),
                                                         TimeZone.getDefault());
                persistLong(settingsTime);

                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
    {
        long persistedLong = getPersistedLong(Long.valueOf(context.getResources().getString(R.string.default_interval_time)));
        long convertedDate = dateUtil.convertTime(persistedLong, TimeZone.getDefault(),
                TimeZone.getTimeZone(Constant.TIMEZONE_UTC));

        calendar.setTimeInMillis(convertedDate);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary()
    {
        if (calendar == null)
        {
            return null;
        }

        return DateFormat.getTimeFormat(context).format(new Date(calendar.getTimeInMillis()));
    }
}