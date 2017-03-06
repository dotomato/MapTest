package com.chen.maptest.MyView;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.amap.api.maps.model.Text;
import com.chen.maptest.R;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen on 17-3-6.
 * Copyright *
 */

public class MyTimeShow extends ConstraintLayout {

    private TextView showyear;
    private TextView showmonth;
    private TextView showday;
    private TextView showweek;

    private Context mContext;
    public MyTimeShow(Context context) {
        super(context);
        init(context);
    }

    public MyTimeShow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyTimeShow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context var){
        mContext = var;
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        showyear = (TextView) findViewById(R.id.show_year);
        showmonth = (TextView) findViewById(R.id.show_month);
        showday = (TextView) findViewById(R.id.show_day);
        showweek = (TextView) findViewById(R.id.show_week);
    }

    public void setTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String tw="";
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:tw="一";break;
            case Calendar.TUESDAY:tw="二";break;
            case Calendar.WEDNESDAY:tw="三";break;
            case Calendar.THURSDAY:tw="四";break;
            case Calendar.FRIDAY:tw="五";break;
            case Calendar.SATURDAY:tw="六";break;
            case Calendar.SUNDAY:tw="日";break;
        }

        showyear.setText(String.valueOf(year));
        showmonth.setText(String.valueOf(month));
        showday.setText(String.valueOf(day));
        showyear.setText("星期"+tw);
    }
}
