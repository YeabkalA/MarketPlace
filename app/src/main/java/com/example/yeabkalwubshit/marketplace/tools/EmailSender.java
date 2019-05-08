package com.example.yeabkalwubshit.marketplace.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Tool to connect a user to an email client app with some email details pre-filled.
 */
public class EmailSender {
    public static class EmailInfo {
        public String getSubject() {
            return subject;
        }

        public EmailInfo setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public String getReceiver() {
            return receiver;
        }

        public EmailInfo setReceiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public EmailInfo setMessage(String message) {
            this.message = message;
            return this;
        }

        private String subject, receiver, message;


    }
    public static void sendEmail(Context context, EmailInfo info) {
        Log.i("Send email", "");
        String[] TO = {info.getReceiver()};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, info.getSubject());
        emailIntent.putExtra(Intent.EXTRA_TEXT, info.getMessage());

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //activity.finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}

