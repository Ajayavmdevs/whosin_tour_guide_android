package com.whosin.app.comman;

import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;
import static android.net.NetworkCapabilities.TRANSPORT_WIFI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.whosin.app.R;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.manager.TranslationManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrencyModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PaymentTabbyModel;
import com.whosin.app.service.models.SpecialOfferModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Utils {

    private static final String TAG = Utils.class.getSimpleName();



    public static final String IMAGES_FOLDER_ROOT_NAME = "desk_space";

    // --------------------------------------
    // region Common
    // --------------------------------------

    public enum setType{
        SETINVITESTATUS,NONE
    }

    public static setType selectedType = setType.NONE;

    public static void setSelectedStatus(setType status) {
        selectedType = status;
    }

    public static String addResolutionSuffix(String imageName, String resolutionSuffix) {
        String[] parts = imageName.split("\\.");
        if (parts.length > 1) {
            parts[parts.length - 2] += resolutionSuffix;
            return String.join(".", parts);
        }
        return imageName + resolutionSuffix;
    }

    public static void setupOfferButtons(OffersModel model, TextView one, TextView two, TextView three) {

        one.setVisibility(View.GONE);
        two.setVisibility(View.GONE);
        three.setVisibility(View.GONE);
        if (AppDelegate.activity == null) { return; }
        if (AppDelegate.activity.isFinishing() || AppDelegate.activity.isDestroyed()) { return; }
        if (model.isExpired()){
            AppDelegate.activity.runOnUiThread(() -> {
                setButtonTitle(one, getLangValue("expired"));
                ViewCompat.setBackgroundTintList(one, ContextCompat.getColorStateList(Graphics.context, R.color.clear));
                one.setTextColor(Color.RED);
                one.setEnabled(false);
            });
            return;
        }
        boolean isAvailableForBuy = model.isAvailableToBuy();
        boolean hasSpecialOffer = model.isSpecialOffer();
        if (AppDelegate.activity == null || AppDelegate.activity.isDestroyed() || AppDelegate.activity.isFinishing()) { return; }
        AppDelegate.activity.runOnUiThread(() -> {
            if (isAvailableForBuy && hasSpecialOffer) {
                setButtonTitle(one, getLangValue("claim_discount"));
                setButtonTitle(two, getLangValue("buy_now"));
//                setButtonTitle(three, "Invite your friends");
            } else if (isAvailableForBuy) {
                setButtonTitle(two, getLangValue("buy_now"));
//                setButtonTitle(one, "Invite your friends");
            } else if (hasSpecialOffer) {
                setButtonTitle(two, getLangValue("claim_discount"));
//                setButtonTitle(one, "Invite your friends");
            } else {
//                setButtonTitle(one, "Invite your friends");
            }
        });
    }

    public static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }
    public static void setButtonTitle(TextView button, String title) {
        button.setVisibility(View.VISIBLE);
        button.setText(title);
        if (title.equals(getLangValue("claim_discount"))) {
            ViewCompat.setBackgroundTintList(button, ContextCompat.getColorStateList(Graphics.context, R.color.claim_button));
//            button.setBgColor(Graphics.context.getColor(R.color.claim_button));
        } else if (title.equals(getLangValue("buy_now"))) {
            ViewCompat.setBackgroundTintList(button, ContextCompat.getColorStateList(Graphics.context, R.color.buy_button));
//            button.setBgColor(Graphics.context.getColor(R.color.buy_button));
        } else {
            ViewCompat.setBackgroundTintList(button, ContextCompat.getColorStateList(Graphics.context, R.color.button_pink));
//            button.setBgColor(Graphics.context.getColor(R.color.button_pink));
        }
    }

    public static void openProfile(Activity activity, String userId) {
        if (SessionManager.shared.getUser().getId().equals(userId)) {

//            activity.startActivity(new Intent(activity, ProfileFragment.class));
        } else {
            activity.startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", userId));
        }
    }


//    public static void openInviteButtonSheet(OffersModel model, VenueObjectModel venueObjectModel , FragmentManager fragmentManager) {
//        InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
//        inviteFriendDialog.venueObjectModel = venueObjectModel;
//        inviteFriendDialog.offersModel = model;
//        inviteFriendDialog.setShareListener( data -> {
//
//        } );
//        inviteFriendDialog.show( fragmentManager, "1" );
//    }

    public static String followButtonTitle(String status) {
        switch (status) {
            case "approved":
                return getLangValue("following");
            case "pending":
                return getLangValue("requested");
            default:
                return getLangValue("follow");
        }
    }

    public static boolean isValidPhoneNumber(String countryCode, String phoneNumber, String region) {
        try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse( "+" + countryCode + phoneNumber, region );
            PhoneNumberUtil.PhoneNumberType numberType = phoneNumberUtil.getNumberType( number );

            // Treat FIXED_LINE_OR_MOBILE as a valid number
            if (numberType == PhoneNumberUtil.PhoneNumberType.MOBILE ||
                    numberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE) {
                return phoneNumberUtil.isValidNumberForRegion( number, region );
            } else {
                return false;
            }
        } catch (NumberParseException e) {
            return false;
        }
    }

    public static boolean isValidPhoneNumber(String isoCountryCode, String phoneNumber) {
        if (TextUtils.isEmpty(isoCountryCode) || TextUtils.isEmpty(phoneNumber)) {
            return false;
        }

        try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

            // Get numeric country code from ISO code
            int countryCode = phoneNumberUtil.getCountryCodeForRegion(isoCountryCode.toUpperCase());

            // Combine +countryCode with phone number
            String fullNumber = "+" + countryCode + phoneNumber;

            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(fullNumber, isoCountryCode.toUpperCase());
            PhoneNumberUtil.PhoneNumberType numberType = phoneNumberUtil.getNumberType(number);

            // Consider MOBILE and FIXED_LINE_OR_MOBILE as valid
            return (numberType == PhoneNumberUtil.PhoneNumberType.MOBILE
                    || numberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE) && phoneNumberUtil.isValidNumberForRegion(number, isoCountryCode.toUpperCase());

        } catch (NumberParseException e) {
            return false;
        }
    }


    public static boolean isValidActivity(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return false;
        }
        return true;
    }


    public static String convertToMinutesFormat(String dateString) {
        long milliseconds = Long.parseLong( dateString );

        // Convert to minutes
        long minutes = (milliseconds / (1000 * 60)) % 60;

        // Format the time in "Xm" format
        return String.format( "%dm", minutes );
    }

    public static void applyFormatting(Activity activity, TextView textView, String authorName, String message) {
        String fullText = authorName + " " + message;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder( fullText );
        int startIndex = 0;
        int endIndex = authorName.length();
        spannableStringBuilder.setSpan( new StyleSpan( Typeface.BOLD ), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        startIndex = endIndex + 1;
        endIndex = fullText.length();
        int colorWhite85 = ContextCompat.getColor( activity, R.color.white_85 );
        spannableStringBuilder.setSpan( new ForegroundColorSpan( colorWhite85 ), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        textView.setText( spannableStringBuilder );
    }
    public static String getAlphaCountryCodeFromNumeric(String numericCountryCode) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        // Convert numeric country code to ISO 3166-1 alpha-2 country code
        String alphaCountryCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(numericCountryCode));
        return alphaCountryCode;
    }

    public static String convertDateFormat(String inputDate) {
        try {
            // Define the input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH );
            SimpleDateFormat outputFormat = new SimpleDateFormat( "d MMM, yyyy 'at' h:mm a", Locale.ENGLISH);

            // Parse the input date
            Date date = inputFormat.parse( inputDate );

            // Format the date in the desired output format
            String formattedDate = outputFormat.format( date );

            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Handle the exception as needed
        }
    }

    public static SpannableStringBuilder makeWordBold(String originalString, String wordToBold) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(originalString);
        int startIndex = originalString.indexOf(wordToBold);
        if (startIndex != -1) {
            int endIndex = startIndex + wordToBold.length();
            spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }


    public static String convertTimestamp(String timestamp, String targetTimeFormat) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH );
        TimeZone utcTimeZone = TimeZone.getTimeZone( "UTC" );
        sourceFormat.setTimeZone( utcTimeZone );

        try {
            Date parsedDate = sourceFormat.parse( timestamp );

            SimpleDateFormat targetFormat = new SimpleDateFormat( targetTimeFormat, Locale.ENGLISH );
            targetFormat.setTimeZone( TimeZone.getDefault() );

            return targetFormat.format( parsedDate );
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Handle the error as needed
        }
    }

    public static void setTimer(String eventTime, final TextView countTimer) {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
        Date date = null;
        try {
            date = dateFormat.parse( eventTime );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long currentTime = System.currentTimeMillis();
        long durationMillis = date.getTime() - currentTime;

        new CountDownTimer( durationMillis, 1000 ) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                long days = secondsRemaining / (3600 * 24);
                long hours = (secondsRemaining % (3600 * 24)) / 3600;
                long minutes = (secondsRemaining % 3600) / 60;
                long seconds = secondsRemaining % 60;
                countTimer.setText( String.format( "%02d:%02d:%02d:%02d", days, hours, minutes, seconds ) );
            }

            @Override
            public void onFinish() {
                countTimer.setText( "00:00:00:00" );
            }
        }.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setTimerForDubaiEvent(Activity activity, String date, String startTime, final TextView countTimer, int colorResId, TextView tvForStatus, String inviteStatus) {
        // Cancel any existing timer
        if (countTimer.getTag() != null) {
            CountDownTimer existingTimer = (CountDownTimer) countTimer.getTag();
            existingTimer.cancel();
        }

        // Set the text color for the timer
        countTimer.setTextColor(ContextCompat.getColor(activity, colorResId));

        // Get the current time in Dubai
        ZonedDateTime dubaiCurrentTime = ZonedDateTime.now(ZoneId.of("Asia/Dubai"));

        // Combine date and time strings into a LocalDateTime object
        LocalDate eventDate = LocalDate.parse(date);
        LocalTime eventTime = LocalTime.parse(startTime);
        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

        // Convert the event date and time to ZonedDateTime in Dubai timezone
        ZonedDateTime eventDateTimeInDubai = eventDateTime.atZone(ZoneId.of("Asia/Dubai"));

        // Check if the event date is in the past
        if (eventDateTimeInDubai.toInstant().toEpochMilli() <= dubaiCurrentTime.toInstant().toEpochMilli()) {
            tvForStatus.setVisibility(View.VISIBLE);
            countTimer.setVisibility(View.GONE);
            tvForStatus.setText("Event has started");
            tvForStatus.setTextColor(ContextCompat.getColor(activity, "in".equals(inviteStatus) ? R.color.green_medium : R.color.delete_red));
            return;
        }

        // Calculate the duration until the event
        long durationMillis = eventDateTimeInDubai.toInstant().toEpochMilli() - dubaiCurrentTime.toInstant().toEpochMilli();

        // Create a new CountDownTimer
        CountDownTimer countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                long days = secondsRemaining / (3600 * 24);
                long hours = (secondsRemaining % (3600 * 24)) / 3600;
                long minutes = (secondsRemaining % 3600) / 60;
                long seconds = secondsRemaining % 60;
                countTimer.setText(String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                countTimer.setText("00:00:00:00");
            }
        };

        // Start the timer and set it as a tag to the TextView
        countDownTimer.start();
        countTimer.setTag(countDownTimer);
    }

    public static void setTimerForDubaiEvent(Activity activity, String date, String startTime, final TextView countTimer) {
        CountDownTimer countDownTimer = null;

        // Get the current time in Dubai
        ZonedDateTime dubaiCurrentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dubaiCurrentTime = ZonedDateTime.now(ZoneId.of("Asia/Dubai"));
        }

        // Combine date and time strings into a LocalDateTime object
        LocalDate eventDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            eventDate = LocalDate.parse(date);
        }
        LocalTime eventTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            eventTime = LocalTime.parse(startTime);
        }
        LocalDateTime eventDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            eventDateTime = LocalDateTime.of(eventDate, eventTime);
        }

        // Convert the event date and time to ZonedDateTime in Dubai timezone
        ZonedDateTime eventDateTimeInDubai = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            eventDateTimeInDubai = eventDateTime.atZone(ZoneId.of("Asia/Dubai"));
        }


        // Calculate the duration until the event
        long durationMillis = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            durationMillis = eventDateTimeInDubai.toInstant().toEpochMilli() - dubaiCurrentTime.toInstant().toEpochMilli();
        }

        // Create a new CountDownTimer
        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                long days = secondsRemaining / (3600 * 24);
                long hours = (secondsRemaining % (3600 * 24)) / 3600;
                long minutes = (secondsRemaining % 3600) / 60;
                long seconds = secondsRemaining % 60;
                countTimer.setText(String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                countTimer.setText("00:00:00:00");
            }
        };

        // Start the timer
        countDownTimer.start();
    }

    public static boolean checkUpdateTimeAndShowAlert(String updatedAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);
        Date updateTime;
        try {
            updateTime = sdf.parse(updatedAt);
        } catch (ParseException e) {
            updateTime = new Date();
        }

        long differenceInSeconds = (new Date().getTime() - updateTime.getTime()) / 1000;

        return differenceInSeconds < 60;
    }

    public static String convertToIsoFormat(String date, String startTime) {
        try {
            // Combine date and time into one string
            String dateTime = date + " " + startTime;

            // Create a SimpleDateFormat object for the input format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date parsedDate = inputFormat.parse(dateTime);

            // Create a SimpleDateFormat object for the output format
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Format the parsed date into the desired format
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String date = "2024-07-25";
        String startTime = "16:23";

        String result = convertToIsoFormat(date, startTime);
        System.out.println(result); // Should print 2024-07-25T16:23:00.000Z
    }

    public static String capitalizeFirstLetter(String inputString) {
        if (inputString == null || inputString.isEmpty()) {
            return "";
        }
        return inputString.substring( 0, 1 ).toUpperCase() + inputString.substring( 1 );
    }


    // endregion
    // --------------------------------------
    // region Date
    // --------------------------------------

    public static boolean isFutureDate(Date dateToCheck) {
        Date currentDate = new Date();
        return dateToCheck.after( currentDate );
    }

    public static boolean isFutureDate(String dateToCheck, String format) {
        try {
            Date date = stringToDate( dateToCheck, format );
            if (date != null) {
                Date currentDate = new Date();
                return date.after( currentDate );
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    public static DatePickerDialog getDatePickerDialog(Activity activity, CommanCallback<Date> callback) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Calculate the cutoff date (18 years ago)
        Calendar cutoffDate = Calendar.getInstance();
        cutoffDate.add(Calendar.YEAR, -18); // 18 years ago

        // Create the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (DatePicker datePicker, int years, int monthOfYear, int dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, years);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    callback.onReceive(calendar.getTime());
                }, year, month, day);

        // Set maximum selectable date to "18 years ago"
        datePickerDialog.getDatePicker().setMaxDate(cutoffDate.getTimeInMillis() - 1);

        // Set minimum selectable date to a very early date (e.g., 1900)
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.YEAR, 1900); // Arbitrary old date
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        // Customize the button colors programmatically
        applyButtonColors(datePickerDialog, activity);

        return datePickerDialog;
    }


    public static void applyButtonColors(DatePickerDialog datePickerDialog, Activity activity) {
        int colorCancel = ContextCompat.getColor( activity, R.color.white ); // Set your color resource
        int colorOk = ContextCompat.getColor( activity, R.color.white ); // Set your color resource

        datePickerDialog.setOnShowListener( dialogInterface -> {
            Button negativeButton = ((AlertDialog) datePickerDialog).getButton( DialogInterface.BUTTON_NEGATIVE );
            Button positiveButton = ((AlertDialog) datePickerDialog).getButton( DialogInterface.BUTTON_POSITIVE );

            // Create ColorStateList for the button text color
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_pressed},
                            new int[]{}
                    },
                    new int[]{colorCancel, colorOk}
            );

            // Apply the ColorStateList to the button text color
            negativeButton.setTextColor( colorStateList );
            positiveButton.setTextColor( colorStateList );
        } );
    }

    public static String changeDateFormat(@NonNull String date, String from, String to) {
        if (isNullOrEmpty( date )) {
            return "";
        }
        Date date1 = new Date();
        try {
            date1 = stringToDate( date, from );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatDate( date1, to );
    }

    @Nullable
    public static Date stringToDate(@NonNull String date, String dateFormat) throws Exception {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat( dateFormat, Locale.ENGLISH);
        return format.parse( date );
    }

    public static String convertToDayName(String dayAbbreviation) {
        List<String> days = Arrays.asList( dayAbbreviation.split( "," ) );
        if (days.size() >= 7) {
            return "All days";
        } else if (days.size() == 2 && (days.contains( "sat" ) && days.contains( "sun" ))) {
            return "Weekend";
        } else if (days.size() == 5 && (!days.contains( "sat" ) && !days.contains( "sun" ))) {
            return "Week days";
        }
        List<String> capitalized = days.stream().map( Utils::capitalizeFirstLetter ).collect( Collectors.toList() );
        return TextUtils.join( ", ", capitalized );
    }

    @NonNull
    public static String formatDate(@NonNull Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat( dateFormat,Locale.ENGLISH );
        return format.format( date );
    }

    public static Date stringToDateWithUTC(@NonNull String date, String dateFormat) throws Exception {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat( dateFormat, Locale.ENGLISH );
        format.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        return format.parse( date );
    }

    public static Date stringToDateWithUTCForEvent(@NonNull String date, String dateFormat) throws Exception {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("UTC")); // Parse date as UTC
        Date utcDate = format.parse(date);
        if (utcDate != null) {
            // Convert UTC date to local time
            format.setTimeZone(TimeZone.getDefault());
            String localDateStr = format.format(utcDate);
            return format.parse(localDateStr);
        }
        return null;
    }

    public static String getTimeAgo(String date, Context context) {
        if (context == null) {
            context = SessionManager.shared.getContext();
        }
        long time = 0;
        try {
            time = stringToDateWithUTC( date, AppConstants.DATEFORMAT_LONG_TIME ).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final int DAY_MILLIS = 24 * HOUR_MILLIS;
        final int MONTH_MILLIS = 30 * DAY_MILLIS;

        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return getLangValue("time_just_now");
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return getLangValue("time_just_now");
        } else if (diff < 2 * MINUTE_MILLIS) {
            return getLangValue("a_minute_ago");
        } else if (diff < 50 * MINUTE_MILLIS) {
//            return diff / MINUTE_MILLIS + context.getString( R.string.minutes_ago );
            return setLangValue("time_minutes",String.valueOf(diff / MINUTE_MILLIS));
        } else if (diff < 90 * MINUTE_MILLIS) {
            return getLangValue("an_hour_ago");
        } else if (diff < 24 * HOUR_MILLIS) {
//            return diff / HOUR_MILLIS + " " + context.getString( R.string.hours_ago );
            return setLangValue("time_hours",String.valueOf(diff / HOUR_MILLIS));
        } else if (diff < 48 * HOUR_MILLIS) {
            return getLangValue("yesterday");
        } else if (diff < 30 * DAY_MILLIS) {
            int monthsAgo = (int) (diff / MONTH_MILLIS);
            return monthsAgo <= 1
                    ? getLangValue("m_ago")
                    : setLangValue("time_months",String.valueOf(monthsAgo));
        } else {
            return setLangValue("d_ago",String.valueOf(diff/DAY_MILLIS));
        }
    }


    public static String getTimeAgoForEvent(String date, Context context) {
        if (context == null) {
            context = SessionManager.shared.getContext();
        }
        long time = 0;
        try {
            time = stringToDateWithUTCForEvent( date, AppConstants.DATEFORMAT_LONG_TIME ).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final int DAY_MILLIS = 24 * HOUR_MILLIS;
        final int MONTH_MILLIS = 30 * DAY_MILLIS;

        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return getLangValue("time_just_now");
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return getLangValue("time_just_now");
        } else if (diff < 2 * MINUTE_MILLIS) {
            return getLangValue("a_minute_ago");
        } else if (diff < 50 * MINUTE_MILLIS) {
//            return diff / MINUTE_MILLIS + context.getString( R.string.minutes_ago );
            return setLangValue("time_minutes",String.valueOf(diff / MINUTE_MILLIS));
        } else if (diff < 90 * MINUTE_MILLIS) {
            return getLangValue("an_hour_ago");
        } else if (diff < 24 * HOUR_MILLIS) {
//            return diff / HOUR_MILLIS + " " + context.getString( R.string.hours_ago );
            return setLangValue("time_hours",String.valueOf(diff / HOUR_MILLIS));
        } else if (diff < 48 * HOUR_MILLIS) {
            return getLangValue("yesterday");
        } else if (diff < 30 * DAY_MILLIS) {
            int monthsAgo = (int) (diff / MONTH_MILLIS);
            return monthsAgo <= 1
                    ? getLangValue("m_ago")
                    : setLangValue("time_months",String.valueOf(monthsAgo));
        } else {
            return setLangValue("d_ago",String.valueOf(diff/DAY_MILLIS));
        }
    }



    public static Date incrementDateByNHour(Date date, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime( date );
        c.add( Calendar.HOUR, amount );
        return c.getTime();
    }

    public static String getTime(Calendar calendar, Context context) {
        int hour = calendar.get( Calendar.HOUR_OF_DAY );
        int minute = calendar.get( Calendar.MINUTE );
        String hourString;
        if (hour < 10) {
            hourString = String.format( Locale.ENGLISH, context.getString( R.string.zero_label ), hour );
        } else {
            hourString = String.format( Locale.ENGLISH, "%d", hour );
        }
        String minuteString;
        if (minute < 10) {
            minuteString = String.format( Locale.ENGLISH, context.getString( R.string.zero_label ), minute );
        } else {
            minuteString = String.format( Locale.ENGLISH, "%d", minute );
        }
        return hourString + ":" + minuteString;
    }

    public static void saveLastSyncDate() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat( AppConstants.DATEFORMAT_LONG_TIME );
        dateFormatGmt.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        String date = dateFormatGmt.format( new Date() );
        Preferences.shared.setString( "lastSyncedDate", date );
    }

    public static void saveLastOneMonthSyncDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(AppConstants.DATEFORMAT_LONG_TIME);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = dateFormatGmt.format(calendar.getTime());
        Preferences.shared.setString( "lastSyncedDate", date );
    }

    public static String detectDateFormat(String inputDate) {
        String[] possibleFormats = {
                "yyyy-MM-dd",
                "dd-MM-yyyy",
                "yyyy/MM/dd",
                // Add more date formats as needed
        };

        for (String format : possibleFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat( format, Locale.US );
                sdf.setLenient( false );  // Make the parsing strict
                sdf.parse( inputDate );
                return format;
            } catch (ParseException ignored) {
                // Continue checking other formats
            }
        }

        return null; // No matching format found
    }

    public static boolean checkDateGoOrNot(String originalDateString) {
        try {
            // Create a SimpleDateFormat object for the input format without 'Z' (UTC)
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

            // If the input format has 'Z', set the timezone to UTC
            originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date originalDate = originalFormat.parse(originalDateString);

            // Create another SimpleDateFormat object to convert the date to the local time zone
            SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            localFormat.setTimeZone(TimeZone.getDefault());

            // Format the original date to the local time zone
            String localDateString = localFormat.format(originalDate);
            Date localDate = localFormat.parse(localDateString);

            // Get the current date and time in the local time zone
            Date currentDate = new Date();

            // Compare the dates and times to determine if it's in the past
            return localDate.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception if needed, maybe log it or return a default value
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isWithinDubaiTimeTwoHours(String startTimeString) {
        try {
            // Parse the start time string into a LocalTime object
            LocalTime startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm"));

            // Get the current time in Dubai
            ZonedDateTime dubaiCurrentTime = ZonedDateTime.now(ZoneId.of("Asia/Dubai"));
            LocalTime currentTimeInDubai = dubaiCurrentTime.toLocalTime();

            // Calculate two hours before and after the start time
            LocalTime twoHoursBefore = startTime.minusHours(2);
            LocalTime twoHoursAfter = startTime.plusHours(2);

            // Check if the current time in Dubai is within two hours before or after the start time
            return (currentTimeInDubai.isAfter(twoHoursBefore) && currentTimeInDubai.isBefore(startTime)) ||
                    (currentTimeInDubai.isAfter(startTime) && currentTimeInDubai.isBefore(twoHoursAfter));

        } catch (DateTimeParseException e) {
            return false; // Return false if the time string cannot be parsed
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isDateToday(String dateString) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd")).isEqual(LocalDate.now());
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isDateBeforeToday(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd")).isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    public static boolean isCurrentTimeEqualForDubaiTime(String timeToCompare) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define the time format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            // Get the current time in Dubai
            ZonedDateTime dubaiCurrentTime = ZonedDateTime.now(ZoneId.of("Asia/Dubai"));
            LocalTime currentTimeInDubai = dubaiCurrentTime.toLocalTime();

            // Parse the time to compare
            LocalTime timeToCompareParsed = LocalTime.parse(timeToCompare, formatter);

            // Return true if the current time in Dubai is after the time to compare
            return currentTimeInDubai.isAfter(timeToCompareParsed);
        }

        return false;
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static String convertTo24HourFormat(String time12Hour) {
//        try {
//            // Define the formatter for 12-hour format with AM/PM
//            DateTimeFormatter formatter12Hour = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                formatter12Hour = DateTimeFormatter.ofPattern("hh:mm a");
//            }
//
//            // Parse the input time string to a LocalTime object
//            LocalTime localTime = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                localTime = LocalTime.parse(time12Hour, formatter12Hour);
//            }
//
//            // Define the formatter for 24-hour format
//            DateTimeFormatter formatter24Hour = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                formatter24Hour = DateTimeFormatter.ofPattern("HH:mm");
//            }
//
//            // Format the LocalTime object to 24-hour format string
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                return localTime.format(formatter24Hour);
//            }
//        } catch (DateTimeParseException e) {
//            // Handle parsing exception, if any
//            System.err.println("Invalid time format: " + e.getMessage());
//            return null;
//        }
//        return time12Hour;
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String convertTo24HourFormat(String time12Hour) {
        try {
            DateTimeFormatter formatter12Hour = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
            LocalTime localTime = LocalTime.parse(time12Hour.trim(), formatter12Hour);
            DateTimeFormatter formatter24Hour = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
            return localTime.format(formatter24Hour);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid time format: " + e.getMessage());
            return null;
        }
    }


    // endregion
    // --------------------------------------
    // region Strings
    // --------------------------------------

    @NonNull
    public static String getFullName(@NonNull String firstName, String lastName) {
        if (TextUtils.isEmpty( lastName )) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    public static boolean isValidUrl(@Nullable String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher( url.toLowerCase() );
        return m.matches();
    }

    public static boolean validateUrl(String urlString) {
        if (urlString == null || urlString.isEmpty()) {
            return false;
        }

        try {
            URL url = new URL(urlString);

            String scheme = url.getProtocol();
            String host = url.getHost();

            if (scheme != null && host != null &&
                    (scheme.equals("http") || scheme.equals("https") || scheme.equals("ftp") || scheme.equals("file"))) {
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    public static String notNullString(@Nullable String string) {
        if (TextUtils.isEmpty( string )) {
            return "";
        } else {
            return string;
        }
    }

    public static <T> List<T> notEmptyList(@Nullable List<T> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        } else {
            // Return the original list
            return list;
        }
    }

    public static int stringToInt(String strValue) {
        if (TextUtils.isEmpty(strValue)) { return 0; }
        int intValue = 0;
        try {
            intValue = Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            try {
                double doubleValue = Double.parseDouble(strValue);
                intValue = (int) doubleValue;
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        return intValue;
    }

    public static String formatAmount(String amountString) {
        try {
            double amount = Double.parseDouble( amountString );
            NumberFormat numberFormat = NumberFormat.getInstance( Locale.ENGLISH );
            return numberFormat.format( amount );
        } catch (NumberFormatException e) {
            return "";
        }
    }

    public static int getMarginBottom(Context context, float value) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        float scaleFactor = value;

        return (int) (screenHeight * scaleFactor);
    }

    public static int getMarginTop(Context context, float value) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;

        float scaleFactor = value;

        return (int) (screenHeight * scaleFactor);
    }

    public static int getMarginRight(Context context,float scale) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.widthPixels;

        float scaleFactor = scale;

        return (int) (screenHeight * scaleFactor);
    }

    public static String convertMainDateFormat(String originalDateString) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US );
            Date originalDate = originalFormat.parse( originalDateString );
            SimpleDateFormat outputFormat = new SimpleDateFormat( "E, dd MMM yyyy", Locale.US );
            return outputFormat.format( originalDate );
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertMainDateFormatReview(String originalDateString) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US );
            Date originalDate = originalFormat.parse( originalDateString );
            SimpleDateFormat outputFormat = new SimpleDateFormat( "dd MMM yyyy", Locale.US );
            return outputFormat.format( originalDate );
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertMainTimeFormat(String originalDateString) {
        try {
            // Step 1: Parse the original date string into a Date object
            SimpleDateFormat originalFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US );
            Date originalDate = originalFormat.parse( originalDateString );

            // Step 2: Format the Date object into the desired output time format
            SimpleDateFormat outputFormat = new SimpleDateFormat( "HH:mm", Locale.US );
            return outputFormat.format( originalDate );
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception appropriately based on your use case
            return null;
        }
    }

    public static String convertDateFormat(String inputDate, String format) {
        // Define input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat( format, Locale.ENGLISH );
        SimpleDateFormat outputFormat = new SimpleDateFormat( "E, dd MMM yyyy", Locale.ENGLISH );

        try {
            // Parse the input date
            Date date = inputFormat.parse( inputDate );

            // Format the date in the desired output format
            return outputFormat.format( date );

        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception as needed, e.g., return an error message
            return "Error converting date";
        }
    }

    public static String formatDateString(String dateString) {
        String[] possibleFormats = {
                "dd-MM-yyyy",
                "yyyy/MM/dd",
                "yyyy-MM-dd",
                "E, dd MMM yyyy",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        };

        Date inputDate = null;
        for (String format : possibleFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat( format, Locale.ENGLISH );
                inputDate = sdf.parse( dateString );
                if (inputDate != null) {
                    break;
                }
            } catch (ParseException e) {
            }
        }
        if (inputDate != null) {
            SimpleDateFormat outputDateFormat = new SimpleDateFormat( "E, dd MMM yyyy", Locale.ENGLISH );
            return outputDateFormat.format( inputDate );
        } else {
            return "Date not Available";
        }

    }

    public static String convertTimeFormat(String inputTime) {
        try {
            SimpleDateFormat inputTimeFormat = new SimpleDateFormat( "HH:mm", Locale.ENGLISH );
            SimpleDateFormat outputTimeFormat = new SimpleDateFormat( "HH:mm",Locale.ENGLISH);

            Date time = inputTimeFormat.parse( inputTime );
            return outputTimeFormat.format( time );
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String convert24HourTimeFormat(String inputTime) {
        try {
            SimpleDateFormat inputTimeFormat = new SimpleDateFormat( "HH:mm", Locale.ENGLISH );
            SimpleDateFormat outputTimeFormat = new SimpleDateFormat( "HH:mm", Locale.ENGLISH );

            Date time = inputTimeFormat.parse( inputTime );
            return outputTimeFormat.format( time );
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get( Calendar.HOUR_OF_DAY );

        String greeting = "";

        if (hour < 6) {
            greeting = TranslationManager.shared.get("good_night"); // 00:00 – 05:59
        } else if (hour < 12) {
            greeting = TranslationManager.shared.get("good_morning"); // 06:00 – 11:59
        } else if (hour < 17) {
            greeting = TranslationManager.shared.get("good_afternoon"); // 12:00 – 16:59
        } else {
            greeting = TranslationManager.shared.get("good_evening"); // 17:00 – 24:59
        }

        return greeting;
    }


    public static String convertDateFormat(String originalDate, String originalFormat, String targetFormat) {
        SimpleDateFormat originalFormatter = new SimpleDateFormat( originalFormat, Locale.US );
        SimpleDateFormat targetFormatter = new SimpleDateFormat( targetFormat, Locale.US );

        try {
            Date date = originalFormatter.parse( originalDate );
            return targetFormatter.format( date );
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parse exception as needed
            return "";
        }
    }

    public static String convertToCustomFormat(String isoDate) {
        // Define the input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH);

        try {
            // Parse the input date
            Date date = inputFormat.parse(isoDate);

            // Format the date to the desired output
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }


    // endregion
    // --------------------------------------
    // region System
    // --------------------------------------

    @Nullable
    public static String serialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName( "android.os.SystemProperties" );
            Method get = c.getMethod( "get", String.class );
            serialNumber = (String) get.invoke( c, "gsm.sn1" );

            if (serialNumber.equals( "" ))
                serialNumber = (String) get.invoke( c, "ril.serialnumber" );

            if (serialNumber.equals( "" )) serialNumber = (String) get.invoke( c, "ro.serialno" );

            if (serialNumber.equals( "" ))
                serialNumber = (String) get.invoke( c, "sys.serialnumber" );

            if (serialNumber.equals( "" )) serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals( Build.UNKNOWN )) {
                serialNumber = null;
            }

        } catch (Exception e) {
            Log.e( TAG, e.getLocalizedMessage() );
            serialNumber = null;
        }

        return serialNumber;
    }

//    public static String getSerialNumber() {
//        String serialNumber = AppConstants.EMPTY_STRING;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            serialNumber = serialNumber();
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            serialNumber = Build.getSerial();
//        } else {
//            serialNumber = Build.SERIAL;
//        }
//        return serialNumber;
//    }

    public static String getDeviceUniqueId(Context context) {
        String androidId = Settings.Secure.getString( context.getContentResolver(), Settings.Secure.ANDROID_ID );
        if (androidId == null) {
            androidId = UUID.randomUUID().toString();
        }
        String deviceUUID = Utils.getSharedPreferences( context ).getString( AppConstants.PREF_DEVICEUUID, androidId );
        Utils.getSharedPreferences( context ).edit().putString( AppConstants.PREF_DEVICEUUID, deviceUUID ).apply();
        return deviceUUID;
    }

    public static void hideKeyboard(@NonNull View view, @NonNull Context context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService( Activity.INPUT_METHOD_SERVICE );
            if (imm != null) {
                imm.hideSoftInputFromWindow( view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to check if the keyboard is visible
    public static boolean isKeyboardVisible(View rootView, Activity activity) {
        final int THRESHOLD_DP = 100; // Threshold to determine keyboard visibility in dp
        int threshold = (int) (THRESHOLD_DP * activity.getResources().getDisplayMetrics().density);
        int rootViewHeight = rootView.getHeight();
        int screenHeight = rootView.getRootView().getHeight();
        int heightDiff = screenHeight - rootViewHeight;
        return heightDiff > threshold;
    }


    public static void hideKeyboard(@NonNull Activity context) {
        View view = context.findViewById( android.R.id.content ).getRootView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE );
            if (imm != null) {
                imm.hideSoftInputFromWindow( view.getWindowToken(), 0 );
            }
        }
    }

    public static void showSoftKeyboard(@NonNull Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE );
        view.requestFocus();
        inputMethodManager.showSoftInput( view, 0 );
    }

    public static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return context.getSharedPreferences( AppConstants.PREFERENCE, Context.MODE_PRIVATE );
    }

    public static void allowDisplayActivityOverLockedState(@NonNull Activity activity) {
        // These flags ensure that the activity can be launched when the screen is locked.
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            activity.setShowWhenLocked( true );
            activity.setTurnScreenOn( true );
        } else {
            window.addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );
        }
    }

    public static int getActiveNetwork(Context context) {
        ConnectivityManager mConnectivityMgr = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetwork = mConnectivityMgr.getActiveNetworkInfo(); // Deprecated in API 29
        if (activeNetwork != null) {
            NetworkCapabilities capabilities = mConnectivityMgr.getNetworkCapabilities( mConnectivityMgr.getActiveNetwork() );
            if (capabilities != null) if (capabilities.hasTransport( TRANSPORT_CELLULAR )) {
                // connected to mobile data
                return TRANSPORT_CELLULAR;

            } else if (capabilities.hasTransport( TRANSPORT_WIFI )) {
                // connected to wifi
                return TRANSPORT_WIFI;
            }

        }
        return -1;
    }

    public static @ColorInt
    int getColorFromAttr(@NonNull Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute( attr, typedValue, true );
        @ColorInt int color = typedValue.data;
        return color;
    }

    public static Bitmap getScreenShot(@NonNull View screenView) {
        screenView.setDrawingCacheEnabled( true );
        Bitmap bitmap = Bitmap.createBitmap( screenView.getDrawingCache() );
        screenView.setDrawingCacheEnabled( false );
        return bitmap;
    }

    public static File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) {
        File file = null;
        try {
            file = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM ).toString() + File.separator + fileNameToSave );
            file.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress( Bitmap.CompressFormat.JPEG, 100, bos ); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream( file );
            fos.write( bitmapdata );
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return file; // it will return null
        }
    }


    public static boolean hasPermissions(@Nullable Context context, @Nullable String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission( context, permission ) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String loadGeoJsonFromAsset(Context context, String filename) {
        try {
            // Load GeoJSON file from local asset folder
            InputStream is = context.getAssets().open( filename );
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read( buffer );
            is.close();
            return new String( buffer, StandardCharsets.UTF_8 );
        } catch (Exception exception) {
            throw new RuntimeException( exception );
        }
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty( email ) && Patterns.EMAIL_ADDRESS.matcher( email ).matches();
    }

    public static boolean isValidPassword(final String password) {
        int MATCHES = 0;
        Pattern pattern;
        Matcher matcher;
        String PASSWORD_PATTERN = "^(?=.*[0-9]).{4,}$";
        pattern = Pattern.compile( PASSWORD_PATTERN );
        matcher = pattern.matcher( password );
        if (matcher.matches()) {
            MATCHES++;
        }
        PASSWORD_PATTERN = "^(?=.*[A-Z]).{4,}$";
        pattern = Pattern.compile( PASSWORD_PATTERN );
        matcher = pattern.matcher( password );
        if (matcher.matches()) {
            MATCHES++;
        }
        PASSWORD_PATTERN = "^(?=.*[-+_!@#$%^&.,?]).{4,}$";
        pattern = Pattern.compile( PASSWORD_PATTERN );
        matcher = pattern.matcher( password );
        if (matcher.matches()) {
            MATCHES++;
        }
        PASSWORD_PATTERN = "^(?=.*[a-z]).{4,}$";
        pattern = Pattern.compile( PASSWORD_PATTERN );
        matcher = pattern.matcher( password );
        if (matcher.matches()) {
            MATCHES++;
        }
        return MATCHES > 1;
    }

    public static boolean isValidPasswordWithPattern(final String password) {
        Pattern pattern;
        Matcher matcher;
        String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[-+_!@#$%^&.,?]).{8,}$";
        pattern = Pattern.compile( PASSWORD_PATTERN );
        matcher = pattern.matcher( password );
        return matcher.matches();
    }

    public static void parseStringToHSV(String state, float[] hsv) {
        DecimalFormat df = new DecimalFormat();

        if (TextUtils.isEmpty( state )) {
            hsv[0] = 0.0f;
            hsv[1] = 0.0f;
            hsv[2] = 0.0f;
            return;
        }
        String[] arr = state.split( "," );
        if (arr.length == 3) {
            hsv[0] = Float.parseFloat( arr[0] );
            hsv[1] = Float.parseFloat( arr[1] ) / 100;
            hsv[2] = Float.parseFloat( arr[2] ) / 100;
            if (hsv[2] > 1.0f) {
                hsv[2] = 1.0f;
            }
        } else {
            hsv[0] = 0.0f;
            hsv[1] = 0.0f;
            hsv[2] = 0.0f;
        }
    }

    public static void openShareDialog(Context context) {
        String shareMsg = "WHOS'IN\nCheck out WHOS'IN APP. I use it to browse through UAE's venues. " +
                "Get it for free at\n\nhttps://onelink.to/826pb7";
        Intent intent = new Intent( Intent.ACTION_SEND );
        intent.setType( "text/plain" );
        intent.putExtra( Intent.EXTRA_TEXT, shareMsg );
        context.startActivity( Intent.createChooser( intent, "Share" ) );
    }


    public static void sendSms(Context context, ArrayList<String> numbers) {
        if (numbers == null && numbers.isEmpty()) {
            Toast.makeText( context, "No phone numbers available", Toast.LENGTH_SHORT ).show();
            return;
        }

        String message = "Whosin\nCheck out WHOSIN APP. I use it to browse through UAE's venues. Get it for free at\nhttps://onelink.to/826pb7";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String number : numbers) {
                smsManager.sendTextMessage( number, null, message, null, null );
            }
            Toast.makeText( context, "SMS sent successfully", Toast.LENGTH_SHORT ).show();
        } catch (Exception e) {
            Toast.makeText( context, "Failed to send SMS", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        }
    }

    public static void openSmsApp(Activity activity, ArrayList<String> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            Toast.makeText( activity, "No phone numbers available", Toast.LENGTH_SHORT ).show();
            return;
        }
        String message = "Whosin\nCheck out WHOSIN APP. I use it to browse through UAE's venues. Get it for free at\nhttps://onelink.to/826pb7";
        Uri smsUri = Uri.parse( "smsto:" + String.join( ",", numbers ) );
        Intent intent = new Intent( Intent.ACTION_VIEW, smsUri );
        intent.putExtra( "sms_body", message );

        try {
            activity.startActivity( intent );
        } catch (Exception e) {
            Toast.makeText( activity, "Failed to open SMS application", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        }

//        try {
//            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//            smsIntent.setData(Uri.parse("smsto:"));
//            smsIntent.setType("vnd.android-dir/mms-sms");
//            smsIntent.putExtra("address", TextUtils.join(",", numbers));
//            smsIntent.putExtra("sms_body", message);
//            context.startActivity(smsIntent);
//        } catch (Exception e) {
//            Toast.makeText(context, "Failed to open SMS application", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
    }


    public static String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        try {
            if (contentURI.getScheme() != null) {
                @SuppressLint("Recycle") Cursor cursor = activity.getContentResolver().query( contentURI, null, null, null, null );
                if (cursor == null) {
                    return contentURI.getPath();
                } else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    return cursor.getString( idx );
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMimeType(Uri uri, Activity activity) {
        String mimeType = null;

        // Use ContentResolver to get the MIME type from the URI
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver contentResolver = activity.getContentResolver();
            mimeType = contentResolver.getType(uri);
        } else {
            // Fallback for file-based URIs (e.g., "file://")
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }

        // Default to "image/jpeg" if MIME type could not be determined
        if (mimeType == null) {
            mimeType = "image/jpeg";
        }

        return mimeType;
    }


    public static String getVideoRealPathFromURIPath(Uri contentURI, Activity activity) {
        String filePath = null;
        if ("content".equalsIgnoreCase( contentURI.getScheme() )) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try (Cursor cursor = activity.getContentResolver().query( contentURI, null, null, null, null )) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex( MediaStore.MediaColumns.DISPLAY_NAME );
                        if (columnIndex != -1) {
                            String fileName = cursor.getString( columnIndex );
                            DocumentFile documentFile = DocumentFile.fromSingleUri( activity, contentURI );
                            if (documentFile != null) {
                                filePath = activity.getExternalFilesDir( null ) + "/" + fileName;
                                try (InputStream inputStream = activity.getContentResolver().openInputStream( contentURI );
                                     OutputStream outputStream = new FileOutputStream( filePath )) {
                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                    int read;

                                    while ((read = inputStream.read( buffer )) != -1) {
                                        outputStream.write( buffer, 0, read );
                                    }

                                    outputStream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
        return filePath;
    }

    public static String getRealPathFromURIPathForListImages(Uri contentURI, Activity activity) {
        String filePath = "";

        try {
            if (contentURI.getScheme() != null) {
                // Use ContentResolver to retrieve information
                if (ContentResolver.SCHEME_CONTENT.equals(contentURI.getScheme())) {
                    Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                        // Handle deprecated DATA column issue
                        if (idx != -1) {
                            filePath = cursor.getString(idx);
                        } else {
                            // Fallback to get file from InputStream if the DATA column is not available
                            filePath = getFileFromInputStream(contentURI, activity);
                        }
                        cursor.close();
                    }
                } else if (ContentResolver.SCHEME_FILE.equals(contentURI.getScheme())) {
                    filePath = contentURI.getPath();  // Direct file scheme URI
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    // Helper function to save content from InputStream as a temporary file
    private static String getFileFromInputStream(Uri uri, Activity activity) throws IOException {
        InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        if (inputStream == null) return "";

        Random random = new Random();
        int randomNum = 10000 + random.nextInt(90000);

        // Save the InputStream to a file in cache directory
        File tempFile = File.createTempFile("image_" + randomNum, ".jpg", activity.getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        return tempFile.getAbsolutePath();  // Return the path to the temporary file
    }

    public static File createFileFromUri(Uri uri, String type, Activity activity) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String extension = type.equalsIgnoreCase("video") ? ".mp4" : ".jpg";
        String fileName = timestamp + "_" + System.currentTimeMillis() + extension;

        // Create the file in the cache directory
        File tempFile = new File(activity.getCacheDir(), fileName);

        // Write data from the URI to the file
        InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        return tempFile;
    }




    public static String getTodayName() {
        SimpleDateFormat sdf = new SimpleDateFormat( "EEE" );
        Date d = new Date();
        return sdf.format( d );
    }

    public static void openUrl(Activity activity, String urlString) {
        if (activity == null || TextUtils.isEmpty( urlString )) {
            return;
        }
        try {
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( urlString ) );
            activity.startActivity( intent );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String timestampToString(long timestamp, String format) {
        Calendar cal = Calendar.getInstance( Locale.ENGLISH );
        cal.setTimeInMillis( timestamp );
        return DateFormat.format( format, cal ).toString();
    }

    public static <T> List<T> getListOrEmpty(List<T> list) {
        return (list == null) ? new ArrayList<>() : list;
    }

    public static void setBottomMargin(View view, int margin) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.bottomMargin = margin;
            view.setLayoutParams( layoutParams );
        }
    }

    public static void setTopMargin(View view, int margin) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.topMargin = margin;
            view.setLayoutParams( layoutParams );
        }
    }

    public static void setRightMargin(View view, int margin) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = margin;
            view.setLayoutParams( layoutParams );
        }
    }

    public static void setLeftMargin(View view, int marginStart) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMarginStart(marginStart);
            view.setLayoutParams(layoutParams);
        }
    }


    // Change the status bar color
    public static void changeStatusBarColor(Window window, int color) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor( color );
    }

    public static String addPercentage(String value) {
        if (value == null) {
            return "";
        }

        if (!value.contains( "%" )) {
            return value + "%";
        }
        return value;
    }

    public static long getAudioDuration(File audioFile) throws IOException {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {
            metadataRetriever.setDataSource( audioFile.getAbsolutePath() );
            String durationStr = metadataRetriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_DURATION );
            return Long.parseLong( durationStr );
        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // Error case, handle it accordingly
        } finally {
            metadataRetriever.release();
        }
    }

    public static void smoothScrollToPosition(RecyclerView recyclerView, int targetPosition) {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller( recyclerView.getContext() ) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START; // or SNAP_TO_END
            }

            @Override
            protected int getHorizontalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START; // or SNAP_TO_END
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 0.03f; // Adjust this value as needed
            }
        };

        smoothScroller.setTargetPosition( targetPosition );

        recyclerView.getLayoutManager().startSmoothScroll( smoothScroller );
    }

    public static int calculateDiscountValueInt(int originalPrice, int discountPercentage) {
        int discount = (originalPrice * discountPercentage) / 100;
        int discountValue = (int) discount;
        int price = originalPrice - discountValue;
        return price;
    }

    // --------------------------------------
    // Data Service
    // --------------------------------------

    public static void requestLinkCreate(Activity activity, VenueObjectModel venueObjectModel) {
        if (venueObjectModel == null) { return; }
        if (activity == null) { return; }
        Graphics.showProgress( activity );
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty( "title", venueObjectModel.getName() );
        if (!venueObjectModel.getAbout().isEmpty()) {
            jsonObject.addProperty( "description", venueObjectModel.getAbout() );
        } else {
            jsonObject.addProperty( "description", venueObjectModel.getAddress() );
        }
        jsonObject.addProperty( "image", venueObjectModel.getCover() );
        jsonObject.addProperty( "itemId", venueObjectModel.getId() );
        jsonObject.addProperty( "itemType", "venue" );
        Log.d( "TAG", "requestLinkCreate: " + jsonObject );
        DataService.shared( activity ).requestLinkCreate( jsonObject, new RestCallback<ContainerModel<String>>() {

            @Override
            public void result(ContainerModel<String> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                if (!TextUtils.isEmpty( model.getData() )) {
                    String shareMsg = String.format( "%s\n\n%s\n\n%s", jsonObject.get( "title" ).getAsString(), jsonObject.get( "description" ).getAsString(), model.getData() );
                    Intent intent = new Intent( Intent.ACTION_SEND );
                    intent.setType( "text/plain" );
                    intent.putExtra( Intent.EXTRA_TEXT, shareMsg );
                    activity.startActivity( Intent.createChooser( intent, "Share" ) );
                }
                Graphics.hideProgress( activity );
            }
        } );
    }



    public static boolean isUser(String userId) {
        return SessionManager.shared.getUser().getId().equals( userId );
    }

    // endregion
    // --------------------------------------
    // region TextView
    // --------------------------------------

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final int viewLessLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag( tv.getText() );
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (tv.getLayout() == null) {
                    return;
                }
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener( this );
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd( 0 );
                    String text = tv.getText().subSequence( 0, lineEndIndex - expandText.length() + 1 ) + " " + expandText;
                    tv.setText( text );
                    tv.setMovementMethod( LinkMovementMethod.getInstance() );
                    tv.setText(
                            addClickablePartTextViewResizable( Html.fromHtml( tv.getText().toString() ), tv, viewLessLine, expandText,
                                    viewMore ), TextView.BufferType.SPANNABLE );

                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    if (tv.getLayout() == null) {
                        return;
                    }
                    int lineEndIndex = tv.getLayout().getLineEnd( maxLine - 1 );
                    String text = tv.getText().subSequence( 0, lineEndIndex - expandText.length() + 1 ) + " " + expandText;
                    tv.setText( text );
                    tv.setMovementMethod( LinkMovementMethod.getInstance() );
                    tv.setText(
                            addClickablePartTextViewResizable( Html.fromHtml( tv.getText().toString() ), tv, viewLessLine, expandText,
                                    viewMore ), TextView.BufferType.SPANNABLE );
                } else {
                    if (tv.getLayout() == null) {
                        return;
                    }
                    int lineEndIndex = tv.getLayout().getLineEnd( tv.getLayout().getLineCount() - 1 );
                    String text = tv.getText().subSequence( 0, lineEndIndex ) + " " + expandText;
                    tv.setText( text );
                    tv.setMovementMethod( LinkMovementMethod.getInstance() );
                    tv.setText(
                            addClickablePartTextViewResizable( Html.fromHtml( tv.getText().toString() ), tv, viewLessLine, expandText,
                                    viewMore ), TextView.BufferType.SPANNABLE );
                }
            }
        } );

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder( strSpanned );

        if (str.contains( spanableText )) {


            ssb.setSpan( new MySpannable( false ) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams( tv.getLayoutParams() );
                        tv.setText( tv.getTag().toString(), TextView.BufferType.SPANNABLE );
                        tv.invalidate();
                        makeTextViewResizable( tv, -1, maxLine, getLangValue("see_less"), false );
                    } else {
                        tv.setLayoutParams( tv.getLayoutParams() );
                        tv.setText( tv.getTag().toString(), TextView.BufferType.SPANNABLE );
                        tv.invalidate();
                        makeTextViewResizable( tv, 3, maxLine, "..." + getLangValue("see_more"), true );
                    }
                }
            }, str.indexOf( spanableText ), str.indexOf( spanableText ) + spanableText.length(), 0 );

        }
        return ssb;

    }

    public static class MySpannable extends ClickableSpan {

        private boolean isUnderline = true;

        /**
         * Constructor
         */
        public MySpannable(boolean isUnderline) {
            this.isUnderline = isUnderline;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText( isUnderline );
            ds.setColor( Color.parseColor( "#E6007E" ) );
        }

        @Override
        public void onClick(View widget) {


        }
    }

    public static void makeTextViewResizablehome(final TextView tv, final int maxLine, final int viewLessLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag( tv.getText() );
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (tv.getLayout() == null) {
                    return;
                }
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener( this );
                if (tv.getLayout() == null) {
                    return;
                }
                int lineEndIndex = tv.getLayout().getLineEnd( tv.getLayout().getLineCount() - 1 );
                String text = tv.getText().subSequence( 0, lineEndIndex ) + " " + expandText;
                tv.setText( text );
                tv.setMovementMethod( LinkMovementMethod.getInstance() );
                tv.setText(
                        addClickablePartTextViewResizablehome( Html.fromHtml( tv.getText().toString() ), tv, viewLessLine, expandText,
                                viewMore ), TextView.BufferType.SPANNABLE );
            }
        } );

    }

    private static SpannableStringBuilder addClickablePartTextViewResizablehome(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder( strSpanned );

        if (str.contains( spanableText )) {


            ssb.setSpan( new MySpannable( false ) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams( tv.getLayoutParams() );
                        tv.setText( tv.getTag().toString(), TextView.BufferType.SPANNABLE );
                        tv.invalidate();
                        makeTextViewResizablehome( tv, -1, maxLine, ".. See More", false );
                    }
                }
            }, str.indexOf( spanableText ), str.indexOf( spanableText ) + spanableText.length(), 0 );

        }
        return ssb;

    }

    public static void preventDoubleClick(final View view) {
        if (!view.isEnabled()) {
            return; // Return if view is already disabled
        }
        view.setEnabled( false ); // Disable the view
        new Handler().postDelayed( () -> view.setEnabled( true ), 1000 );
    }

    public static String getFollowButtonText(UserDetailModel model) {
        if (model.getFollow().isEmpty()) { return "Follow"; }
        if (model.getFollow().equals("approved")) {
            return "Following";
        } else if (model.getFollow().equals("na") || model.getFollow().equals("none")) {
            return "Follow";
        } else {
            return "Requested";
        }
    }

    public static void setInvitationStatus(ImageView imageView, String status) {
        if (status.equals( "pending" )) {
            imageView.setImageResource( R.drawable.icon_pending );
        } else if (status.equals( "in" )) {
            imageView.setImageResource( R.drawable.icon_complete );
        } else if (status.equals( "out" )) {
            imageView.setImageResource( R.drawable.icon_deleted);
        } else {
            if (status == null && status.isEmpty()) {
                imageView.setVisibility( View.GONE );
            }
        }
    }

    public static void setInvitationStatus(Activity activity ,ImageView imageView, String status, TextView textView) {
        if (status.equals( "pending" )) {
            imageView.setImageResource( R.drawable.icon_pending );
            textView.setTextColor(ContextCompat.getColor(activity, R.color.yellow));
        } else if (status.equals( "in" )) {
            imageView.setImageResource( R.drawable.icon_complete );
            textView.setTextColor(ContextCompat.getColor(activity, R.color.green));
        } else if (status.equals( "out" )) {
            imageView.setImageResource( R.drawable.icon_deleted);
            textView.setTextColor(ContextCompat.getColor(activity, R.color.red));
        } else {
            if (status == null && status.isEmpty()) {
                imageView.setVisibility( View.GONE );
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getPlatformIcon(String type) {
        switch (type) {
            case "instagram":
                return Graphics.context.getResources().getDrawable(R.drawable.instagram_icon);
            case "tiktok":
                return Graphics.context.getResources().getDrawable(R.drawable.tiktok_iconn);
            case "facebook":
                return Graphics.context.getResources().getDrawable(R.drawable.facebook_icon);
            case "google":
                return Graphics.context.getResources().getDrawable(R.drawable.social_google_icon);
            case "youtube":
                return Graphics.context.getResources().getDrawable(R.drawable.youtube_icon);
            case "snapchat":
                return Graphics.context.getResources().getDrawable(R.drawable.icon_snapchat);
            case "website":
                return Graphics.context.getResources().getDrawable(R.drawable.icon_website);
            case "whatsapp":
                return Graphics.context.getResources().getDrawable(R.drawable.icon_whatsapp);
            case "email":
                return Graphics.context.getResources().getDrawable(R.drawable.icon_email);
            case "whosin":
                return Graphics.context.getResources().getDrawable(R.drawable.social_app_icon);
            default:
                return null;
        }
    }

    public static void openSoicalSheet(Activity activity,String platForm, String socialAccount){
        ArrayList<String> data = new ArrayList<>();
        data.add("Open");
        data.add("Copy");
        Graphics.showActionSheet(activity, "WHOS’IN", data, (data1, position1) -> {
            switch (position1) {
                case 0:
                    Graphics.openSoicalAccount(activity, platForm, socialAccount);
                    break;
                case 1:
                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", socialAccount);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(activity, "The link has been copied.", Toast.LENGTH_SHORT).show();
                    break;
            }

        });
    }


    public static boolean isWithinSevenHours(String createdAt) {
        // Define the formatter for parsing the timestamp
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        }

        // Parse the createdAt time as a LocalDateTime
        LocalDateTime createdDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createdDateTime = LocalDateTime.parse(createdAt, formatter);
        }

        // Get the current time
        LocalDateTime currentTime = null; // or use the required time zone
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalDateTime.now(ZoneId.of("UTC"));
        }

        // Check if the created date is today
        boolean isToday = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            isToday = createdDateTime.toLocalDate().isEqual(currentTime.toLocalDate());
        }

        // Calculate the time difference between created time and now
        long hoursDifference = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            hoursDifference = Duration.between(createdDateTime, currentTime).toHours();
        }

        // Return true if the date is today and the time difference is less than or equal to 7 hours
        return isToday && hoursDifference <= 7;
    }

    public static boolean isNewEvent(String createdAt, String cloneId) {
        if (!TextUtils.isEmpty(cloneId)) return false;
        Date eventDate = stringToDate(createdAt);
        if (eventDate == null) {
            return false;
        }

        Date currentDate = new Date();
        Date sevenHoursAgo = new Date(currentDate.getTime() - 7 * 60 * 60 * 1000);

        return eventDate.after(sevenHoursAgo) && eventDate.before(currentDate);
    }

    private static Date stringToDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static int calculateRemainingDays(String givenDateString) {
        // Parse the given date
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        }
        ZonedDateTime givenDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            givenDate = ZonedDateTime.parse(givenDateString, formatter);
        }

        // Calculate 15 days from the given date
        ZonedDateTime futureDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            futureDate = givenDate.plusDays(15);
        }

        // Calculate the remaining days from the current date to the future date
        ZonedDateTime currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = ZonedDateTime.now();
        }
        long remainingDays = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            remainingDays = ChronoUnit.DAYS.between(currentDate.toLocalDate(), futureDate.toLocalDate());
        }

        // Return the remaining days, or 0 if the 15th day has been reached or passed
        return remainingDays > 0 ? (int) remainingDays : 0;
    }

    public static String convertDateYMd(String originalDateString) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat targetFormat = new SimpleDateFormat("EEE dd, MMMM yyyy", Locale.ENGLISH);

        Date date;
        String formattedDate = null;

        try {
            date = originalFormat.parse( originalDateString );
            formattedDate = targetFormat.format( date );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static String formatDateTime(String date, String startTime, String endTime) {
        // Parse the date string
        LocalDate parsedDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        // Parse the start and end times
        LocalTime parsedStartTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            parsedStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        }
        LocalTime parsedEndTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            parsedEndTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
        }

        // Combine date and time into LocalDateTime for start time
        LocalDateTime startDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startDateTime = LocalDateTime.of(parsedDate, parsedStartTime);
        }

        // Format the date to "Saturday 02 - Nov"
        DateTimeFormatter dateFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateFormatter = DateTimeFormatter.ofPattern("EEEE dd - MMM", Locale.ENGLISH);
        }
        String formattedDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate = startDateTime.format(dateFormatter);
        }

        // Format the start and end times as "22:39 - 23:44"
        String formattedTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedTime = parsedStartTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    + " - "
                    + parsedEndTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        // Combine both formatted parts
        return formattedDate + ", " + formattedTime;
    }

    public static boolean isPastEvent(String date, String time) {
        try {
            boolean is24Hour = time.contains(":") && !time.toLowerCase().contains("am") && !time.toLowerCase().contains("pm");

            // Combine date and time into one string
            String dateTime = date + " " + time;

            // Define the date format based on the time format
            SimpleDateFormat dateFormat = is24Hour
                    ? new SimpleDateFormat("yyyy-MM-dd HH:mm")  // Adjust this if your date format differs
                    : new SimpleDateFormat("yyyy-MM-dd hh:mm a"); // For 12-hour format

            // Set the time zone to Asia/Dubai for parsing
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Dubai"));

            // Parse the event date and time
            Date eventDateTime = dateFormat.parse(dateTime);

            // Get the current time in Asia/Dubai time zone
            Calendar dubaiCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dubai"));
            Date currentTimeInDubai = dubaiCalendar.getTime();

            // Check if the event date is today in Dubai time zone
            SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateOnlyFormat.setTimeZone(TimeZone.getTimeZone("Asia/Dubai"));
            String todayDate = dateOnlyFormat.format(currentTimeInDubai);
            String eventDate = dateOnlyFormat.format(eventDateTime);

            if (todayDate.equals(eventDate)) {
                // If today in Dubai, check if the event time is in the past
                return eventDateTime.before(currentTimeInDubai);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false; // Default to false if the parsing fails or conditions don't match
    }

    @SuppressLint("NewApi")
    public static boolean isSpotOpen(String spotCloseAt) {
        // Parse the given spot close time
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime closeTime = LocalTime.parse(spotCloseAt, timeFormatter);

        // Get the current time in the Asia/Dubai time zone
        ZonedDateTime currentDubaiTime = ZonedDateTime.now(ZoneId.of("Asia/Dubai"));
        LocalTime currentTimeDubai = currentDubaiTime.toLocalTime();

        // Check if the current time is before the spot close time
        return currentTimeDubai.isBefore(closeTime);
    }

    public static void generateDynamicLinks(Activity activity, UserDetailModel user) {

        if (activity == null) return;


        if (user == null) return;

        String shareMessage = user.getFullName() + "\n\n" + user.getBio() + "\n\n" + "https://explore.whosin.me/u/" + user.getUserId();

        Intent shareIntent = new Intent( Intent.ACTION_SEND );
        shareIntent.setType( "text/plain" );
        shareIntent.putExtra( Intent.EXTRA_TEXT, shareMessage );
        shareIntent.putExtra( Intent.EXTRA_SUBJECT, activity.getString( R.string.app_name ) );

        Intent chooser = Intent.createChooser( shareIntent, "Share via" );
        List<Intent> targetedShareIntents = new ArrayList<>();

        for (ResolveInfo resolveInfo : activity.getPackageManager().queryIntentActivities( shareIntent, 0 )) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (!packageName.equals( "com.twitter.android" ) && !packageName.equals( "com.apple.android.aShare" )) {
                Intent targetedShareIntent = new Intent( Intent.ACTION_SEND );
                targetedShareIntent.setType( "text/plain" );
                targetedShareIntent.putExtra( Intent.EXTRA_TEXT, shareMessage );
                targetedShareIntent.putExtra( Intent.EXTRA_SUBJECT, activity.getString( R.string.app_name ) );
                targetedShareIntent.setPackage( packageName );
                targetedShareIntents.add( targetedShareIntent );
            }
        }

        chooser.putExtra( Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray( new Parcelable[0] ) );
        activity.startActivity( chooser );
    }

    public static boolean isProfileComplete(UserDetailModel model) {
        return !(TextUtils.isEmpty(model.getImage()) ||
                TextUtils.isEmpty(model.getNationality()) ||
                TextUtils.isEmpty(model.getGender()) ||
                TextUtils.isEmpty(model.getDateOfBirth()) ||
                TextUtils.isEmpty(model.getFirstName()) ||
                TextUtils.isEmpty(model.getLastName()) ||
                TextUtils.isEmpty(model.getInstagram()));
    }

    public static void hideViews(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    public static void showViews(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void inVisibleViews(View... views) {
        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static Integer calculateAge(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date birthDate = dateFormat.parse(dateString);
            if (birthDate == null) {
                return null;
            }

            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDate);

            Calendar currentCalendar = Calendar.getInstance();

            int age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

            // Check if the birth date hasn't occurred yet this year
            if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }



//    public static void addSeeMore(final TextView textView, final Spanned fullText,int maxlines,String truncateText) {
//        textView.post(() -> {
//            int maxLines = maxlines; // Maximum lines before truncation
//            Layout layout = textView.getLayout();
//
//            if (layout != null && layout.getLineCount() > maxLines) {
//                int endIndex = layout.getLineEnd(maxLines - 1); // Get the last visible character index
//                String visibleText = fullText.subSequence(0, endIndex - 10) + " "; // Truncate text
//
//                SpannableStringBuilder spannable = new SpannableStringBuilder(visibleText);
////                spannable.append("... See More");
//                spannable.append(truncateText);
//
//                // Apply pink color to "..." and "See More"
//                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF1493")),
//                        spannable.length() - 11, spannable.length(),
//                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                textView.setText(spannable);
//                textView.setTextColor(Color.WHITE); // Keep main text in black
//            }
//        });
//    }


    public static void addSeeMore(final TextView textView, final Spanned fullText, int maxLines, String truncateText, final View.OnClickListener onTruncateClickListener) {
        // Try to get the layout immediately if available
        Layout layout = textView.getLayout();
        textView.setMaxLines(maxLines); // Enforce maxLines

        if (layout != null && layout.getLineCount() > maxLines) {
            // Layout is available, compute truncation immediately
            applyTruncation(textView, fullText, maxLines, truncateText, layout, onTruncateClickListener);
        } else {
            // Layout not available, set full text and queue truncation after layout
            textView.setText(fullText);
            textView.setTextColor(Color.WHITE);
            textView.post(() -> {
                Layout updatedLayout = textView.getLayout();
                if (updatedLayout != null && updatedLayout.getLineCount() > maxLines) {
                    applyTruncation(textView, fullText, maxLines, truncateText, updatedLayout, onTruncateClickListener);
                }
            });
        }
    }

    private static void applyTruncation(TextView textView, Spanned fullText, int maxLines, String truncateText, Layout layout, View.OnClickListener onTruncateClickListener) {
        int endIndex = layout.getLineEnd(maxLines - 1); // Last visible character index
        // Reserve space for truncateText
        int truncateLength = truncateText.length();
        String visibleText = fullText.subSequence(0, Math.max(0, endIndex - truncateLength)).toString();

        SpannableStringBuilder spannable = new SpannableStringBuilder(visibleText);
        spannable.append(truncateText); // Append truncateText (e.g., "See More")

        // Apply pink color to truncateText
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF1493")),
                spannable.length() - truncateText.length(), spannable.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply ClickableSpan to truncateText only
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (onTruncateClickListener != null) {
                    onTruncateClickListener.onClick(widget); // Trigger the provided listener
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // Remove underline for cleaner look
            }
        }, spannable.length() - truncateText.length(), spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the truncated text and enable clicking
        textView.setText(spannable);
        textView.setTextColor(Color.WHITE); // Keep main text white
        textView.setMovementMethod(LinkMovementMethod.getInstance()); // Enable clicking
    }


    public static boolean isWazeInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo("com.waze", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String roundFloatValue(Float value){
        if (value == null) return "0.0";
//        DecimalFormat df = new DecimalFormat("0.00");
//        DecimalFormat df = new DecimalFormat("0.##");
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
        return df.format(value);

    }

    public static String removePoint(String value) {
        if (value == null || value.isEmpty()) return "0";
        if (!value.contains(".")) return value;

        return value.substring(0, value.indexOf("."));
    }

    public static float roundFloatToFloat(Float value) {
        if (value == null) return 0f;

        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(2, RoundingMode.DOWN);
        return bd.floatValue();
    }

    public static double roundDoubleValueToDouble(Double value) {
        if (value == null) {
            return 0.0;
        }

        BigDecimal bd = new BigDecimal(String.valueOf(value))
                .setScale(2, RoundingMode.DOWN);

        // Force English format (dot decimal, English digits)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("0.00", symbols);

        return Double.parseDouble(df.format(bd));
    }



    public static boolean isVideo(String url) {
        return url.endsWith(".mp4") || url.endsWith(".avi") || url.endsWith(".mov") || url.endsWith(".mkv");
    }

    public static boolean isAvailableTabby(PaymentTabbyModel model) {
        return model != null && !TextUtils.isEmpty(model.getWeb_url());
    }


    public static String getTransferName(int id) {
        if (id == 41865) {
            return TranslationManager.shared.get("without_transfer");
        } else if (id == 41843) {
            return TranslationManager.shared.get("sharing_transfer");
        } else if (id == 41844) {
            return TranslationManager.shared.get("private_transfer");
        } else if (id == 43129) {
            return TranslationManager.shared.get("private_boat_without_transfers");
        } else if (id == 43110) {
            return TranslationManager.shared.get("pvt_yach_without_transfer");
        }
        return "";
    }


    public static void setStyledText(Context context, TextView textView, String originalText) {
        String currency = SessionManager.shared.getUser().getCurrency();
        SpannableString spannable;

        CurrencyModel matchedCurrency = null;
        if (AppSettingManager.shared.getAppSettingData() != null) {
            matchedCurrency = AppSettingManager.shared.getAppSettingData().getCurrencies().stream()
                    .filter(p -> p.getCurrency().equals(currency))
                    .findFirst()
                    .orElse(null);
        }

        if (matchedCurrency != null && !matchedCurrency.getCurrency().equals("AED")) {
            spannable = new SpannableString(matchedCurrency.getSymbol() + originalText);
        } else {
            spannable = new SpannableString("D" + originalText);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.aed_regular);
            if (typeface != null) {
                spannable.setSpan(new CustomTypefaceSpan(typeface), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        textView.setText(spannable);
    }


    public static SpannableString getStyledText(Context context, String originalText) {
        SpannableString spannable;
        String currency = SessionManager.shared.getUser().getCurrency();

        CurrencyModel matchedCurrency = null;
        if (AppSettingManager.shared.getAppSettingData() != null) {
            matchedCurrency = AppSettingManager.shared.getAppSettingData().getCurrencies().stream()
                    .filter(p -> p.getCurrency().equals(currency))
                    .findFirst()
                    .orElse(null);
        }

        if (matchedCurrency != null && !matchedCurrency.getCurrency().equals("AED")) {
            spannable = new SpannableString(matchedCurrency.getSymbol() + originalText);
        } else {
            spannable = new SpannableString("D" + originalText);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.aed_regular);
            if (typeface != null) {
                spannable.setSpan(new CustomTypefaceSpan(typeface), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannable;
    }


    public static String getCurrency(){
//        if (!TextUtils.isEmpty(SessionManager.shared.getUser().getCurrency())){
//            return SessionManager.shared.getUser().getCurrency().toLowerCase();
//        }
        return "aed";
    }

    public static float changeCurrencyToAED(Float price){
        if (TextUtils.isEmpty(SessionManager.shared.getUser().getCurrency())) return Utils.roundFloatToFloat(price);
        if (SessionManager.shared.getUser().getCurrency().equals("AED")) return Utils.roundFloatToFloat(price);
        Optional<CurrencyModel> aedModel = AppSettingManager.shared.getAppSettingData().getCurrencies().stream().filter(p -> p.getCurrency().equals(SessionManager.shared.getUser().getCurrency())).findFirst();
        return aedModel.map(currencyModel -> convertValueToFloat(price / currencyModel.getRate())).orElse(0f);
    }

    public static float convertIntoCurrenctCurrency(Float price){
        if (TextUtils.isEmpty(SessionManager.shared.getUser().getCurrency())) return Utils.roundFloatToFloat(price);
        if (SessionManager.shared.getUser().getCurrency().equals("AED")) return Utils.roundFloatToFloat(price);
        Optional<CurrencyModel> aedModel = AppSettingManager.shared.getAppSettingData().getCurrencies().stream().filter(p -> p.getCurrency().equals(SessionManager.shared.getUser().getCurrency())).findFirst();
        return aedModel.map(currencyModel -> convertValueToFloat(price * currencyModel.getRate())).orElse(0f);
    }

    public static String convertIntoCurrentCurrency(String price) {
        if (TextUtils.isEmpty(price)) return "0";
        if (TextUtils.isEmpty(SessionManager.shared.getUser().getCurrency())) return price;
        if (SessionManager.shared.getUser().getCurrency().equals("AED")) return price;

        // String → Float
        float priceValue;
        try {
            priceValue = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            return "0"; // fallback agar string invalid hai
        }

        Optional<CurrencyModel> aedModel = AppSettingManager.shared
                .getAppSettingData()
                .getCurrencies()
                .stream()
                .filter(p -> p.getCurrency().equals(SessionManager.shared.getUser().getCurrency()))
                .findFirst();

        // Convert and return as String
        return aedModel
                .map(currencyModel -> {
                    float converted = convertValueToFloat(priceValue * currencyModel.getRate());
                    return (converted % 1 == 0) ? String.valueOf((int) converted) : String.valueOf(converted);
                })
                .orElse("0");
    }



    private static float convertValueToFloat(Float value) {
        if (value == null) return 0f;

        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(0, RoundingMode.HALF_UP);
        return bd.floatValue();
    }


    public static boolean isGuestLogin(){
        return Preferences.shared.getInt("isGuestLogin") == 1;
    }

    public static void updateNoteText(int min, int max, TextView noteText, String note) {
        String finalText = "";
        List<String> notes = new ArrayList<>();

        if (!TextUtils.isEmpty(note)){
            finalText = note;
        }
//        else {
//            if (min > 0) {
////                notes.add("Minimum " + min + " pax allowed");
//                notes.add(setLangValue("note__min_text",String.valueOf(min)));
//            }
//
//            if (max > 0 && max < 1000) {
////                notes.add("Maximum " + max + " pax allowed");
//                notes.add(setLangValue("note__max_text",String.valueOf(max)));
//            }
//
//            finalText = String.join(getLangValue("and"), notes);
//        }



        if (finalText.isEmpty()) {
            noteText.setVisibility(View.GONE);
        } else {
            noteText.setVisibility(View.VISIBLE);
//            noteText.setText("Note : " + finalText);
            noteText.setText(setLangValue("note_text",finalText));
        }
    }


    public static int getNumericValue(TextView textView) {
        if (textView == null) return 0;

        String text = textView.getText().toString();

        // Keep only digits
        String numberOnly = text.replaceAll("[^0-9]", "");

        if (numberOnly.isEmpty()) {
            return 0; // default if no number found
        }

        return Integer.parseInt(numberOnly);
    }


    public static String getLangValue(String key){
        return TranslationManager.shared.get(key);
    }

    public static String setLangValue(String key,String value){
        String template = TranslationManager.shared.get(key);
        if (template == null) return "";
        return template.replaceAll("\\{.*?\\}", value);
    }

    public static String setLangValue(String key, String... values) {
        String template = TranslationManager.shared.get(key);
        if (template == null) return "";

        for (String val : values) {
            template = template.replaceFirst("\\{.*?\\}", val);
        }

        return template;
    }

    public static String getLang(){
        if (SessionManager.shared.getUser() == null) return "en";
        return SessionManager.shared.getUser().getLang();
    }

    public static void setTextOrHide(TextView textView, String value) {
        if (!Utils.isNullOrEmpty(value)) {
            textView.setText(value);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }


}
