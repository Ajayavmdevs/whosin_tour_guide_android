package com.whosin.app.comman;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.whosin.app.R;
import com.whosin.app.ui.controller.IconButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimerUtils {

    private static CountDownTimer countDownTimer;

    private static boolean isTimerRunning = false;

//    public static void startCountdown(String isoTime, TextView timerTextView, Runnable onFinish) {
//        long eventMillis = parseISOTimeToMillis(isoTime);
//        if (eventMillis == -1) {
//            onFinish.run(); // If parsing fails, close activity
//            return;
//        }
//
//        // Convert to UTC Calendar
//        Calendar eventCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        eventCalendar.setTimeInMillis(eventMillis);
//
//        // Get current time in UTC
//        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        long currentMillis = currentCalendar.getTimeInMillis();
//
//        // Calculate end time (10 minutes after event start)
//        long endMillis = eventMillis + (10 * 60 * 1000); // Add 10 minutes
//
//        // If time has already passed, close the activity
//        if (currentMillis >= endMillis) {
//            onFinish.run();
//            return;
//        }
//
//        // Start countdown if within 10 minutes
//        long remainingMillis = endMillis - currentMillis;
//        isTimerRunning = true;
//        startCountdownTimer(remainingMillis, timerTextView, () -> {
//            isTimerRunning = false;
//            onFinish.run();
//        });
//    }

    public static void startCountdown(Activity activity,String isoTime, TextView timerTextView, View fillProgressView, Runnable onFinish) {
        long eventMillis = parseISOTimeToMillis(isoTime);
        if (eventMillis == -1) {
            onFinish.run(); // fallback
            return;
        }

        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long currentMillis = currentCalendar.getTimeInMillis();

        long endMillis = eventMillis + (15 * 60 * 1000);
        if (currentMillis >= endMillis) {
            onFinish.run();
            return;
        }

        long remainingMillis = endMillis - currentMillis;
        long totalMillis = 15 * 60 * 1000;

        isTimerRunning = true;
        startCountdownTimer(activity,remainingMillis, totalMillis, timerTextView, fillProgressView, () -> {
            isTimerRunning = false;
            onFinish.run();
        });
    }


//    private static void startCountdownTimer(long durationMillis, TextView timerTextView, Runnable onFinish) {
//        countDownTimer = new CountDownTimer(durationMillis, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                int minutes = (int) (millisUntilFinished / 60000);
//                int seconds = (int) (millisUntilFinished % 60000 / 1000);
//                timerTextView.setText(String.format(Locale.getDefault(), "Ticket will be updated in %02d:%02d", minutes, seconds));
//            }
//
//            @Override
//            public void onFinish() {
//                onFinish.run();
//            }
//        }.start();
//    }

    private static void startCountdownTimer(Activity activity,long durationMillis, long totalMillis, TextView timerTextView, View fillProgressView, Runnable onFinish) {
        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 60000);
                int seconds = (int) (millisUntilFinished % 60000 / 1000);
//                timerTextView.setText(String.format(Locale.getDefault(), "Ticket will be updated in %02d:%02d", minutes, seconds));

                timerTextView.setText(String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds));
                timerTextView.setTextColor(ContextCompat.getColor(activity, R.color.white));


                // Fill bar progress
                float progress = (totalMillis - millisUntilFinished) / (float) totalMillis;
                fillProgressView.setPivotX(0f);
                fillProgressView.setScaleX(progress);
            }

            @Override
            public void onFinish() {
                fillProgressView.setScaleX(1f);
                onFinish.run();
            }
        };
        fillProgressView.setPivotX(0f);
        fillProgressView.setScaleX(0f);
        countDownTimer.start();
    }

    private static long parseISOTimeToMillis(String isoTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Ensure UTC time parsing
            return sdf.parse(isoTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 if parsing fails
        }
    }

    public static void cancelCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isTimerRunning = false;
    }

    public static boolean isTimerRunning() {
        return isTimerRunning;
    }
}
