package com.example.yeabkalwubshit.marketplace.tools;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Graphics class to create an animation for the intro splash screen.
 */
public class Animator {
    Thread t;
    private LinearLayout layout;
    final private TextView text;
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

    public Animator(LinearLayout layout, final TextView text) {
        this.layout = layout;
        this.text = text;
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

        final LinearLayout layoutCopy = layout;

        t = new Thread(new Runnable() {
            public void run() {
                int ind = 0;
                while(true) {
                    textR = tr[ind]; textB = tb[ind]; textG = tg[ind];
                    backR = br[ind]; backB = bb[ind]; backG = bg[ind];
                    ind = (ind + 1) % 125;
                    layoutCopy.setBackgroundDrawable(new ColorDrawable(getBackgroundColor()));
                    text.setBackgroundDrawable(new ColorDrawable(getBackgroundColor()));
                    text.setTextColor(getTextColor());
                    try {
                        Thread.sleep(20);
                    } catch(Exception e) {

                    }
                }
            }
        });

    }

    void start() {
        t.start();
    }



}
