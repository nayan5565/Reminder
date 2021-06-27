package com.shadhinlab.reminder.activities.calender;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.tools.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.shadhinlab.reminder.activities.calender.MyCalendarView.Language.Arabic;


public class MyCalendarView extends Dialog implements View.OnClickListener {

    public MyCalendarView(Context ctx) {
        super(ctx);
    }

    public MyCalendarView(Context ctx, int themeResId) {
        super(ctx, themeResId);
        build();
    }

    //
    protected MyCalendarView(Context ctx, boolean cancelable, OnCancelListener cancelListener) {
        super(ctx, cancelable, cancelListener);
    }

    public enum Mode {
        Hijri(1),
        Gregorian(2);
        private int mode;

        Mode(int mode) {
            this.mode = mode;
        }

        public int getModeValue() {
            return mode;
        }
    }

    public enum Language {
        Arabic(1),
        English(2),
        Default(3);
        private int language;

        Language(int language) {
            this.language = language;
        }

        public int getLanguageValue() {
            return language;
        }
    }

    public static String title;
    public static int language;
    public static OnDateSetListener onDateSetListener;
    public static Mode mode = Mode.Gregorian;
    public static int adjustment;
    public static int hijri_min;
    public static int hijri_max;
    public static int gregorian_min;
    public static int gregorian_max;
    public static boolean setDefaultDate = false;
    public static int defaultDay;
    public static int defaultMonth;
    public static int defaultYear;
    public static boolean scrolling;

    private Context context;
    private String[] days;
    private TextView monthTextView, yearTextView, lastSelectedDay;
    private TableLayout tableLayout, tableLayoutDay;
    private CalendarInstance calendarInstance;
    private List<TextView> textViewList;
    private TableRow daysHeader;
    ImageView nextButton, previousButton;
    RelativeLayout relative_next, relative_previous;
    Drawable drawableSelect;

    TextView tv_done;
    TextView tv_cancel;

    LinearLayout linear_calendar_header;
    LinearLayout linear_datePicker;
    public TextView hedertextview;

    LinearLayout linear_calendar;
    RelativeLayout linear_datepicker_header;
    LinearLayout linear_bottom_button;
    ScrollView scrollView;

    /**
     * listener for getting selected date in implemented class
     */
    public interface OnDateSetListener {
        void onDateSet(int year, int month, int day);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_hijri_calendar_with_time);
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            context = getContext();

            linear_calendar = findViewById(R.id.linear_calendar);
            scrollView = findViewById(R.id.scrollView);
            configureLanguage();
            setupView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void configureLanguage() {
        if (language == Language.Arabic.getLanguageValue()) {
            days = context.getResources().getStringArray(R.array.hijri_date_picker_days_arabic);
        } else {
            days = context.getResources().getStringArray(R.array.hijri_date_picker_days);
        }
    }

    private void setupView() {
        setToolBar();
        initViews();
        initHeaderOfCalender();
        initDays();
        initListener();
    }

    @Override
    public void onClick(View view) {

        TextView temp = (TextView) view;

        if (!temp.getText().toString().trim().isEmpty()) {

            if (lastSelectedDay != null) {
                lastSelectedDay.setTextColor(nextMonthDaysTextColor);
                lastSelectedDay.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                lastSelectedDay.setBackground(null);
            }

            temp.setBackground(drawableSelect);

            temp.setTextColor(context.getResources().getColor(android.R.color.white));
            lastSelectedDay = temp;
            int selectDay = Integer.parseInt(temp.getText().toString());
            calendarInstance.setDay(selectDay);
            calendarInstance.setSelectedDayMonthYear(selectDay, calendarInstance.getCurrentMonth() + 1, calendarInstance.getCurrentYear());


//            if (MyCalendarView.language == Arabic.getLanguageValue()) {
            Toast.makeText(context, calendarInstance.getDayOfMonth() + "/" + (calendarInstance.getCurrentMonth() + 1) + "/" + calendarInstance.getCurrentYear(), Toast.LENGTH_SHORT).show();
//            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initViews() {

        linear_bottom_button = findViewById(R.id.linear_bottom_button);
        tv_done = findViewById(R.id.tv_done);
        tv_cancel = findViewById(R.id.tv_cancel);

        relative_next = findViewById(R.id.relative_next);
        relative_previous = findViewById(R.id.relative_previous);

        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);

        tableLayoutDay = findViewById(R.id.calendarTableLayoutDay);
        tableLayout = findViewById(R.id.calendarTableLayout);
        monthTextView = findViewById(R.id.monthTextView);
        yearTextView = findViewById(R.id.yearTextView);

        textViewList = new ArrayList<>();

        drawableSelect = context.getDrawable(R.drawable.hijri_date_picker_card_selected);
        drawableSelect.setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

        calendarInstance = new CalendarInstance(context, mode.getModeValue());
        if (setDefaultDate) {
            calendarInstance.setDay(defaultDay);
            calendarInstance.setMonth(defaultMonth);
            calendarInstance.setYear(defaultYear);
        }

        headerDaysColor = context.getResources().getColor(R.color.colorPrimary);
        preMonthDaysTextColor = context.getResources().getColor(R.color.light_gray);
        nextMonthDaysTextColor = context.getResources().getColor(R.color.black);
    }


    private void setToolBar() {
        linear_calendar_header = findViewById(R.id.linear_header);
        linear_datePicker = findViewById(R.id.linear_datePicker);

        linear_datepicker_header = findViewById(R.id.linear_datepicker_header);

        hedertextview = findViewById(R.id.tv_title);
        hedertextview.setText(context.getResources().getString(R.string.header_calendar));

    }

    @SuppressLint("RestrictedApi")
    public void setButtonTint(Button button) {
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(context.getResources().getColor(R.color.hijri_date_picker_accent_color)));
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    int headerDaysColor;
    Typeface fontTypeface;
    int preMonthDaysTextColor;
    int nextMonthDaysTextColor;

    private void initHeaderOfCalender() {

        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams paramsLayout = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(0, 8, 0, 8);
        daysHeader = new TableRow(context);
        daysHeader.setBackgroundColor(context.getResources().getColor(R.color.white));
        daysHeader.setGravity(Gravity.CENTER);

        for (int i = 0; i < 7; i++) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(paramsLayout);

            TextView textView = new TextView(context);
            textView.setLayoutParams(params);
            textView.setTextSize(11);
            textView.setTextColor(headerDaysColor);
            if (fontTypefaceMonth != null) {
                textView.setTypeface(fontTypefaceMonth);
            }
            textView.setGravity(Gravity.CENTER);
            textView.setText(days[i]);
            linearLayout.addView(textView);
            daysHeader.addView(linearLayout);

        }
    }

    Typeface fontTypefaceMonth;

    private void initDays() {

        tableLayout.removeAllViews();

        tableLayoutDay.removeAllViews();
        tableLayoutDay.addView(daysHeader);

        updateCalenderInformation();

        int count = 1;
        boolean firstTime = true;
        int weekstart = calendarInstance.getWeekStartFrom();
        if (weekstart == Calendar.SATURDAY || (weekstart == Calendar.FRIDAY && calendarInstance.getLastDayOfMonth() == 31)) {
            weekstart = 6;
        } else {
            weekstart = 5;
        }

        TableRow.LayoutParams paramsLayout = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams params = new TableRow.LayoutParams(dpToPx(30), dpToPx(30));
        params.setMargins(0, 2, 0, 2);

        for (int i = 0; i < weekstart; i++) {
            TableRow row = new TableRow(context);
            row.setGravity(Gravity.CENTER);
            for (int j = 1; j <= 7; j++) {

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(paramsLayout);
                linearLayout.setGravity(Gravity.CENTER);

                TextView textView = new TextView(context);
                textView.setLayoutParams(params);
                textView.setGravity(Gravity.CENTER);

                if (fontTypefaceMonth != null) {
                    textView.setTypeface(fontTypefaceMonth);
                }

                boolean isDatePassed = false;
                if (count - 1 < calendarInstance.lengthOfMonthAccordingToyear(calendarInstance.getCurrentYear(), calendarInstance.getCurrentMonth())) {

                    if (mode == Mode.Hijri) {
                        int calDay = calendarInstance.getCalendarDay();
                        int calMonth = calendarInstance.getCalendarCurrentMonth();
                        int calYear = calendarInstance.getCalendarCurrentYear();

                        int currDay = calendarInstance.getDayOfMonth();
                        int currMonth = calendarInstance.getCurrentMonth();
                        int currYear = calendarInstance.getCurrentYear();

                        if (currMonth < calMonth
                                && currYear == calYear) {

                            textView.setTextColor(preMonthDaysTextColor);

                        } else if (calendarInstance.isCurrentMonth()
                                && currYear == calYear
                                && count < calDay) {

                            textView.setTextColor(preMonthDaysTextColor);

                        } else if (currYear < calYear) {

                            textView.setTextColor(preMonthDaysTextColor);

                        } else {

                            textView.setTextColor(nextMonthDaysTextColor);
                            textView.setOnClickListener(this);

                        }

                    } else {

                        if (calendarInstance.getCurrentMonth() < calendarInstance.getCalendarCurrentMonth()
                                && calendarInstance.getCurrentYear() == calendarInstance.getCalendarCurrentYear()) {

                            textView.setTextColor(preMonthDaysTextColor);

                        } else if (calendarInstance.isCurrentMonth()
                                && calendarInstance.getCurrentYear() == calendarInstance.getCalendarCurrentYear()
                                && count < calendarInstance.getDayOfMonth()) {

                            textView.setTextColor(preMonthDaysTextColor);

                        } else if (calendarInstance.getCurrentYear() < calendarInstance.getCalendarCurrentYear()) {

                            textView.setTextColor(preMonthDaysTextColor);

                        } else {
                            textView.setTextColor(nextMonthDaysTextColor);
                            textView.setOnClickListener(this);
                        }
                    }

                    String pretex = "";
                    if (count < 10) {
                        pretex = "0";
                    } else {
                        pretex = "";
                    }

                    if (firstTime && j == calendarInstance.getWeekStartFrom()) {
                        String text = language == Arabic.getLanguageValue() ? CalendarInstance.toArabicNumbers(pretex + count + "") : pretex + count + "";
                        textView.setText(text);
                        firstTime = false;
                        count++;
                    } else if (!firstTime) {
                        textView.setText(language == Arabic.getLanguageValue() ? CalendarInstance.toArabicNumbers(pretex + count + "") : pretex + count + "");
                        count++;
                        isDatePassed = true;
                    } else {
                        textView.setText(" ");

                    }

                } else {
                    textView.setText(" ");
                }

                if (mode == Mode.Hijri) {

                    if ((calendarInstance.isCurrentMonth() || (calendarInstance.getCurrentMonth() == defaultMonth && calendarInstance.getCurrentYear() == defaultYear))
                            && count - 1 == calendarInstance.getCalendarDay() && !textView.getText().toString().contains(" ")) {
                        textView.setBackground(drawableSelect);
                        calendarInstance.setSelectedDayMonthYear(calendarInstance.getCalendarDay(), calendarInstance.getCurrentMonth() + 1, calendarInstance.getCurrentYear());
                        textView.setTextColor(context.getResources().getColor(android.R.color.white));
                        lastSelectedDay = textView;

                    } else if (!calendarInstance.isCurrentMonth()
                            && count - 1 == 1 && !textView.getText().toString().contains(" ")
                            && ((calendarInstance.getCurrentMonth() > calendarInstance.getCalendarCurrentMonth()
                            && calendarInstance.getCurrentYear() == calendarInstance.getCalendarCurrentYear())
                            || calendarInstance.getCurrentYear() > calendarInstance.getCalendarCurrentYear())) {

                        textView.setBackground(drawableSelect);
                        calendarInstance.setSelectedDayMonthYear(1, calendarInstance.getCurrentMonth() + 1, calendarInstance.getCurrentYear());
                        textView.setTextColor(context.getResources().getColor(android.R.color.white));
                        lastSelectedDay = textView;
                    }

                } else {
                    if ((calendarInstance.isCurrentMonth() || (calendarInstance.getCurrentMonth() == defaultMonth && calendarInstance.getCurrentYear() == defaultYear))
                            && count - 1 == calendarInstance.getDayOfMonth() && !textView.getText().toString().contains(" ")) {
                        textView.setBackground(drawableSelect);

                        calendarInstance.setSelectedDayMonthYear(calendarInstance.getDayOfMonth(), calendarInstance.getCurrentMonth() + 1, calendarInstance.getCurrentYear());

                        textView.setTextColor(context.getResources().getColor(android.R.color.white));
                        lastSelectedDay = textView;

                    } else if (!calendarInstance.isCurrentMonth()
                            && count - 1 == 1 && !textView.getText().toString().contains(" ")
                            && ((calendarInstance.getCurrentMonth() > calendarInstance.getCalendarCurrentMonth()
                            && calendarInstance.getCurrentYear() == calendarInstance.getCalendarCurrentYear())
                            || calendarInstance.getCurrentYear() > calendarInstance.getCalendarCurrentYear())) {

                        textView.setBackground(drawableSelect);
                        calendarInstance.setSelectedDayMonthYear(1, calendarInstance.getCurrentMonth() + 1, calendarInstance.getCurrentYear());
                        textView.setTextColor(context.getResources().getColor(android.R.color.white));
                        lastSelectedDay = textView;
                    }
                }


                textViewList.add(textView);
                linearLayout.addView(textView);
                row.addView(linearLayout);
            }
            tableLayout.addView(row);
        }
    }

    private void updateCalenderInformation() {
        monthTextView.setText(calendarInstance.getMonthName());
        yearTextView.setText("" + calendarInstance.getCurrentYear());
        monthTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        yearTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }

    private void pickDateFromCalender() {
        int dmy[] = calendarInstance.getSelectedDayMonthYear();
        int year = dmy[2];
        int month = dmy[1];
        int day = dmy[0];
        onDateSetListener.onDateSet(year,
                month,
                day
        );
    }

    private void initListener() {

        relative_previous.setOnClickListener(view -> {

            previous();
            int dmy[] = calendarInstance.getSelectedDayMonthYear();
        });

        relative_next.setOnClickListener(view -> {
            next();
            int dmy[] = calendarInstance.getSelectedDayMonthYear();
        });


        tv_cancel.setOnClickListener(view -> MyCalendarView.this.dismiss());

        tv_done.setOnClickListener(view -> {

           pickDateFromCalender();
            MyCalendarView.this.dismiss();

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.log("Dialog back pressed");
        pickDateFromCalender();

    }

    public void previous() {
        calendarInstance.minusMonth();
        initDays();
    }

    public void next() {
        calendarInstance.plusMonth();
        initDays();
    }

    static MyCalendarView myDialog;

    public static MyCalendarView getInstance(Context ctx, boolean cancelable) {
        MyCalendarView.title = "";
        MyCalendarView.hijri_max = 1450;
        MyCalendarView.hijri_min = 1437;
        MyCalendarView.adjustment = 0;
        MyCalendarView.gregorian_max = 2050;
        MyCalendarView.gregorian_min = 2013;
        MyCalendarView.language = Language.Default.getLanguageValue();
        MyCalendarView.scrolling = true;
        myDialog = null;
        myDialog = new MyCalendarView(ctx, R.style.Theme_CustomDialog);

//            myDialog.show();
//            myDialog.hide();

        return myDialog;
    }

    public void build() {
        onCreate(null);
    }

    public void setMaxHijriYear(int maxYear) {
        MyCalendarView.hijri_max = maxYear;
    }

    public void setMinHijriYear(int minYear) {
        MyCalendarView.hijri_min = minYear;
    }

    public void setAdjustment(int adjustment) {
        MyCalendarView.adjustment = adjustment;
    }

    public void setMinMaxHijriYear(int min, int max) {
        MyCalendarView.hijri_max = max;
        MyCalendarView.hijri_min = min;
    }

    public void setEnableScrolling(boolean scrolling) {
        MyCalendarView.scrolling = scrolling;

    }

    public void setMaxGregorianYear(int maxYear) {
        MyCalendarView.gregorian_max = maxYear;

    }

    public void setMinGregorianYear(int minYear) {
        MyCalendarView.gregorian_min = minYear;

    }

    public void setMinMaxGregorianYear(int min, int max) {
        MyCalendarView.gregorian_max = max;
        MyCalendarView.gregorian_min = min;

    }

    public void setUILanguage(Language language) {
        MyCalendarView.language = language.getLanguageValue();

    }

    public void setOnDateSetListener(OnDateSetListener onDateSetListener) {
        MyCalendarView.onDateSetListener = onDateSetListener;
    }

    public void showDialog() {
        if (myDialog != null) {
            myDialog.show();
        }
    }

    public void hideDialog() {
        if (myDialog != null) {
            myDialog.dismiss();
        }
    }

    public void setMode(Mode mode) {
        MyCalendarView.mode = mode;
    }

    public void setDefaultHijriDate(int day, int month, int year) {
        if (month > 11 || month < 0)
            throw new RuntimeException("Month must be between 0-11");
        MyCalendarView.setDefaultDate = true;
        MyCalendarView.defaultDay = day;
        MyCalendarView.defaultMonth = month > 11 ? 0 : month;
        MyCalendarView.defaultYear = year;
    }

    public void setCalendarTitleVisibility(boolean isVisible) {
        if (isVisible) {
            linear_calendar_header.setVisibility(View.VISIBLE);
        } else {
            linear_calendar_header.setVisibility(View.GONE);
        }
    }


    public void setDatePickerVisibility(boolean isvisible) {
        if (isvisible) {
            linear_datePicker.setVisibility(View.VISIBLE);
        } else {
            linear_datePicker.setVisibility(View.GONE);
        }
    }

    public void setDatePickerMonthYearTitlebackground(int color) {
        linear_datepicker_header.setBackgroundColor(color);
    }

    public void setCalendarTitlebackground(int color) {
        linear_calendar_header.setBackgroundColor(color);
    }


    public void setCalendarbackground(int color) {
        linear_calendar.setBackgroundColor(color);
    }

    public void setDatePickerWeeklyDaysBackground(int color) {
        tableLayoutDay.setBackgroundColor(color);
    }

    public void setDatePickerMonthlyDaysBckground(int color) {
        tableLayout.setBackgroundColor(color);
    }


    public void setBottomButtonColor(int color) {
        linear_bottom_button.setBackgroundColor(color);
    }

    public void setDoneButtonColorAndText(int color, String text) {
        tv_done.setBackgroundColor(color);
        tv_done.setText(text);
    }

    public void setDoneButtonVisibility(boolean isvisible) {
        if (isvisible) {
            tv_done.setVisibility(View.VISIBLE);
        } else {
            tv_done.setVisibility(View.GONE);
        }
    }

    public void setCancelButtonColorAndText(int color, String text) {
        tv_cancel.setBackgroundColor(color);
        tv_cancel.setText(text);
    }

    public void setCancelButtonVisibility(boolean isvisible) {
        if (isvisible) {
            tv_cancel.setVisibility(View.VISIBLE);
        } else {
            tv_cancel.setVisibility(View.GONE);
        }
    }

    public void setLeftArrowDrawableBackground(Drawable drawable) {
        relative_previous.setBackground(drawable);
    }

    public void setLeftArrowImageResource(int resourceid) {
        previousButton.setImageResource(resourceid);
    }

    public void seRightArrowDrawableBackground(Drawable drawable) {
        relative_next.setBackground(drawable);
    }

    public void setRightArrowImageResource(int resourceid) {
        nextButton.setImageResource(resourceid);
    }


    public void setCalendarHeaderText(String text) {
        hedertextview.setText(text);
    }


    public void setDateSelectDrawable(Drawable drawable) {
        this.drawableSelect = drawable;
    }

    public void setDateSelectColor(int color) {
        drawableSelect.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public void setDaysOfWeekTextColor(int color) {
        headerDaysColor = color;
        if (daysHeader != null) {
            daysHeader.removeAllViews();
            initHeaderOfCalender();
            tableLayoutDay.removeAllViews();
            tableLayoutDay.addView(daysHeader);
        }
    }

    public void setDaysOfWeekFontTypeface(Typeface fontTypeface) {
        this.fontTypefaceMonth = fontTypeface;
        if (daysHeader != null) {
            daysHeader.removeAllViews();
            initHeaderOfCalender();
            tableLayoutDay.removeAllViews();
            tableLayoutDay.addView(daysHeader);
        }
    }

    public void setMonthNameFontTypeface(Typeface fontTypeface) {
        monthTextView.setTypeface(fontTypeface);
    }

    public void seYearNameFontTypeface(Typeface fontTypeface) {
        yearTextView.setTypeface(fontTypeface);
    }

    public void setDaysOfMonthFontTypeface(Typeface fontTypeface) {
        this.fontTypefaceMonth = fontTypeface;
        initDays();
    }


    public void setDaysOfWeekBackground(int color) {
        daysHeader.setBackgroundColor(color);
    }

    public void setMonthNameTextColor(int color) {
        monthTextView.setTextColor(color);
    }

    public void setPreviousDaysOfMonthTextColor(int color) {
        preMonthDaysTextColor = color;
        initDays();
    }

    public void setNextDaysOfMonthTextColor(int color) {
        nextMonthDaysTextColor = color;
        initDays();
    }

    public void setYearNameTextColor(int color) {
        yearTextView.setTextColor(color);
    }
}

