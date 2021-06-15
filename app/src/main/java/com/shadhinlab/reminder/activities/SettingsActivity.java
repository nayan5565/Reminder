package com.shadhinlab.reminder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shadhinlab.reminder.R;
import com.shadhinlab.reminder.tools.Global;
import com.shadhinlab.reminder.tools.Utils;

public class SettingsActivity extends AppCompatActivity {
    private RadioButton rb1, rb2, rb3, rb4, rb5, rb6, rb7, rb8, rb9, rb10, rb11, rb12, rb13, rb14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        display();
    }

    private void init() {
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        rb5 = findViewById(R.id.rb5);
        rb6 = findViewById(R.id.rb6);
        rb7 = findViewById(R.id.rb7);
        rb8 = findViewById(R.id.rb8);
        rb9 = findViewById(R.id.rb9);
        rb10 = findViewById(R.id.rb10);
        rb11 = findViewById(R.id.rb11);
        rb12 = findViewById(R.id.rb12);
        rb13 = findViewById(R.id.rb13);
        rb14 = findViewById(R.id.rb14);
    }

    private void display() {
        switch (Utils.getPref(Global.PRAYER_METHOD, 2)) {
            case 0:
                rb1.setChecked(true);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 1:
                rb1.setChecked(false);
                rb2.setChecked(true);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 2:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(true);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 3:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(true);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 4:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(true);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 5:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(true);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 7:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(true);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 8:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(true);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 9:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(true);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 10:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(true);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 11:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(true);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 12:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(true);
                rb13.setChecked(false);
                rb14.setChecked(false);
                break;
            case 13:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(true);
                rb14.setChecked(false);
                break;
            case 14:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                rb5.setChecked(false);
                rb6.setChecked(false);
                rb7.setChecked(false);
                rb8.setChecked(false);
                rb9.setChecked(false);
                rb10.setChecked(false);
                rb11.setChecked(false);
                rb12.setChecked(false);
                rb13.setChecked(false);
                rb14.setChecked(true);
                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rb1:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 0);
                break;
            case R.id.rb2:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 1);
                break;
            case R.id.rb3:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 2);
                break;
            case R.id.rb4:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 3);
                break;
            case R.id.rb5:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 4);
                break;
            case R.id.rb6:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 5);
                break;
            case R.id.rb7:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 7);
                break;
            case R.id.rb8:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 8);
                break;
            case R.id.rb9:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 9);
                break;
            case R.id.rb10:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 10);
                break;
            case R.id.rb11:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 11);
                break;
            case R.id.rb12:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 12);
                break;
            case R.id.rb13:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 13);
                break;
            case R.id.rb14:
                if (checked)
                    Utils.savePref(Global.PRAYER_METHOD, 14);
                break;
        }
    }
}