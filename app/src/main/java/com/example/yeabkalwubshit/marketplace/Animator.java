package com.example.yeabkalwubshit.marketplace;

import android.graphics.Color;

import java.util.Arrays;

public class Animator {
    int tr[];
    int tg[];
    int tb[];
    int br[];
    int bg[];
    int bb[];

    int textR;
    int textB;
    int textG;
    int backR;
    int backB;
    int backG;


    final static int STEPS = 256 - 131;

    int getTextColor() {
        return Color.rgb(textR, textG, textB);
    }
    int getBackgroundColor() {
        return Color.rgb(backR, backG, backB);
    }

    public Animator() {
        tr = new int[STEPS];
        tg = new int[STEPS];
        tb = new int[STEPS];
        br = new int[STEPS];
        bg = new int[STEPS];
        bb = new int[STEPS];

        double whiteMult = 255.0/131;

        for(int i=131; i<=255; i++) {
            int brw = (int) (whiteMult * (i-131));
            tr[i-131] = brw;
            tg[i-131] = i;
            tb[i-131] = i;
            br[i-131] = 255-brw;
            bg[i-131] = 255 - (i-131);
            bb[i-131] = 255 - (i-131);
        }

        System.out.println("the color array looks like " + Arrays.toString(bg));
        System.out.println("the color array for red looks like " + Arrays.toString(br));
        Thread t = new Thread(new Runnable() {
            public void run() {
                int ind = 0;
                while(true) {
                    textR = tr[ind];
                    textB = tb[ind];
                    textG = tg[ind];
                    backR = br[ind];
                    backB = bb[ind];
                    backG = bg[ind];
                    ind = (ind + 1) % 125;
                }
            }
        });
        t.start();
    }



}
