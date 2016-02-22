package com.ftunram.secsurf.core;

import org.opencv.BuildConfig;

public class myScore implements Comparable<myScore> {
    public String ID;
    public double sc;

    public myScore() {
        this.sc = 0.0d;
        this.ID = BuildConfig.FLAVOR;
    }

    public int compareTo(myScore two) {
        double result = this.sc - two.sc;
        if (result < 0.0d) {
            return -1;
        }
        if (result == 0.0d) {
            return 0;
        }
        return 1;
    }
}
