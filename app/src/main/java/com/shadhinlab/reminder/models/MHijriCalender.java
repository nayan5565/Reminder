package com.shadhinlab.reminder.models;

public class MHijriCalender {
    private MHijri hijri;
    private MGregorian gregorian;

    public MHijri getHijri() {
        return hijri;
    }

    public void setHijri(MHijri hijri) {
        this.hijri = hijri;
    }

    public MGregorian getGregorian() {
        return gregorian;
    }

    public void setGregorian(MGregorian gregorian) {
        this.gregorian = gregorian;
    }
}
