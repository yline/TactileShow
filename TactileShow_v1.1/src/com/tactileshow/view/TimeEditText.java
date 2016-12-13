package com.tactileshow.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class TimeEditText extends EditText
{
    private Context context;
    
    private Time time;
    
    private TimeEditText thisCla;
    
    public TimeEditText(Context context)
    {
        this(context, null);
    }
    
    public TimeEditText(Context ccontext, AttributeSet attrs)
    {
        super(ccontext, attrs);
        context = ccontext;
        thisCla = this;
        this.setFocusable(false);
        time = new Time();
        time.setToNow();
        setTime();
        
        this.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                ab.setTitle("时间设定");
                final TimePicker timePicker = new TimePicker(context);
                ab.setView(timePicker);
                ab.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        time.hour = timePicker.getCurrentHour();
                        time.minute = timePicker.getCurrentMinute();
                        setTime();
                    }
                });
                ab.create().show();
            }
        });
    }
    
    private void setTime()
    {
        String str = "";
        if (time.hour < 10)
            str += "0" + time.hour;
        else
            str += time.hour;
        str += " : ";
        if (time.minute < 10)
            str += "0" + time.minute;
        else
            str += time.minute;
        thisCla.setText(str);
    }
}
