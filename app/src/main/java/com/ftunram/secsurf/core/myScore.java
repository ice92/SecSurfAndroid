package com.ftunram.secsurf.core;

/**
 * Created by user on 1/12/2016.
 */
public class myScore implements Comparable<myScore>{
    public double sc;
    public String ID;

    public myScore() {
        sc=0.0;
        ID="";
    }

    public int compareTo(myScore two ) {
        // I migth compare them using the int first
        // and if they're the same, use the string...
        double result = this.sc - two.sc;
        if (result < 0)
        {
            return -1;
        }
        else if (result == 0)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
}