package io.dume.dume.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Keep;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import io.dume.dume.R;
import io.dume.dume.student.pojo.SearchDataStore;
import io.dume.dume.student.recordsPage.Record;
import io.dume.dume.teacher.homepage.TeacherContract;


@Keep
public class DumeUtils {
    public static final String TEACHER = "teacher";
    public static final String BOOTCAMP = "bootcamp";
    public static final String STUDENT = "student";
    public static final String RECORDTAB = "recordTab";
    public static final String SELECTED_ID = "s_id";
    private static final String TAG = "Bal";
    public static final String SETTING_PREFERENCE = "settingPre";


    public static int GALLARY_IMAGE = 1;
    public static int CAMERA_IMAGE = 2;
    private static final int WIDTH_INDEX = 0;
    private static final int HEIGHT_INDEX = 1;
    private static ArrayList<String> endOfNest;

    public static String getFormattedDate(Date current) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat formatter = new SimpleDateFormat("hh:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(current);
    }

    public static String getApplicationName() {
        return "Dume";
    }

    public static boolean isInteger(String string) {
        return TextUtils.isDigitsOnly(string);
    }

    public static boolean isValidEmailAddress(String email) {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = Objects.requireNonNull(lm).isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e(getApplicationName(), "isGpsEnabled method exception : ", ex);
        }
        return gps_enabled;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int id, int color, int textColor, int count, float x, float y) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(id);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context, x, y);
        }
        badge.setCircleColor(color);
        badge.setCircleTextColor(textColor);

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(id, badge);
    }

    public static void setTextOverDrawable(Context context, LayerDrawable icon, int id, int textColor, String data) {

        TextDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(id);
        if (reuse != null && reuse instanceof TextDrawable) {
            badge = (TextDrawable) reuse;
        } else {
            badge = new TextDrawable(context);
        }
        badge.setCircleTextColor(textColor);
        badge.setString(data);
        icon.mutate();
        icon.setDrawableByLayerId(id, badge);
    }

    public static void setTextOverDrawable(Context context, LayerDrawable icon, int id, int textColor, String data, int flag) {

        TextDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(id);
        if (reuse != null && reuse instanceof TextDrawable) {
            badge = (TextDrawable) reuse;
        } else {
            badge = new TextDrawable(context);
        }
        badge.setFlag(flag);
        badge.setCircleTextColor(textColor);

        badge.setString(data);
        icon.mutate();
        icon.setDrawableByLayerId(id, badge);
    }

    public static void setBadgeChar(Context context, LayerDrawable icon, int color, int textColor, char character, float x, float y) {

        BadgeDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context, x, y);
        }
        badge.setCircleColor(color);
        badge.setCircleTextColor(textColor);

        badge.setChar(character);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public static void configureAppbar(Context context, String title) {
        AppCompatActivity activity = (AppCompatActivity) context;
        Toolbar toolbar = activity.findViewById(R.id.accountToolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar supportActionBar = activity.getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        CollapsingToolbarLayout collapsingToolbarLayout = activity.findViewById(R.id.accountCollapsing);
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Cairo-Light.ttf"));
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Cairo-Light.ttf"));
        collapsingToolbarLayout.setTitle(title);

        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_more_vert_black_24dp);
        toolbar.setOverflowIcon(drawable);
    }

    public static void configureAppbar(Context context, String title, boolean isWhite) {
        AppCompatActivity activity = (AppCompatActivity) context;
        Toolbar toolbar = activity.findViewById(R.id.accountToolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar supportActionBar = activity.getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        CollapsingToolbarLayout collapsingToolbarLayout = activity.findViewById(R.id.accountCollapsing);
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Cairo-Light.ttf"));
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Cairo-Light.ttf"));
        collapsingToolbarLayout.setTitle(title);

        if (isWhite) {
            Drawable drawable = null; /*I've check validation of xml file there's no error :( */
            try {
                drawable = context.getResources().getDrawable(R.drawable.ic_more_vert_white_24dp);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            toolbar.setOverflowIcon(drawable);
        } else {
//            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_more_vert_black_24dp);
            Drawable drawable = null;
            try {
                drawable = context.getResources().getDrawable(R.drawable.ic_more_vert_black_24dp);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            toolbar.setOverflowIcon(drawable);
        }
    }

    //testing code for messages goes here
    public static void showToast(Context context, @StringRes int text, boolean isLong) {
        showToast(context, context.getString(text), isLong);
    }

    public static void showToast(Context context, String text, boolean isLong) {
        Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static String getDurationString(int seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat(seconds >= 3600 ? "HH:mm:ss" : "mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

    public static void configAppbarTittle(Context context, String title) {
        AppCompatActivity activity = (AppCompatActivity) context;
        CollapsingToolbarLayout collapsingToolbarLayout = activity.findViewById(R.id.accountCollapsing);
        collapsingToolbarLayout.setTitle(title);
    }

    public static void configToolbarTittle(Context context, String title) {
        AppCompatActivity activity = (AppCompatActivity) context;
        Toolbar toolbar = activity.findViewById(R.id.accountToolbar);
        toolbar.setTitle(title);
    }

    public static void configAppToolBarTitle(Context context, String title) {
        AppCompatActivity activity = (AppCompatActivity) context;
        CollapsingToolbarLayout collapsingToolbarLayout = activity.findViewById(R.id.accountCollapsing);
        Toolbar toolbar = activity.findViewById(R.id.accountToolbar);
        toolbar.setTitle(title);
        collapsingToolbarLayout.setTitle(title);
    }


    public static void configureAppbarWithoutColloapsing(Context context, String title) {
        AppCompatActivity activity = (AppCompatActivity) context;
        Toolbar toolbar = activity.findViewById(R.id.accountToolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar supportActionBar = activity.getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setTitleTextAppearance(context, R.style.MyTextApprncColOne);
        toolbar.setTitle(title);
        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_more_vert_black_24dp);
        toolbar.setOverflowIcon(drawable);
    }


    public static int[] getScreenSize(Context context) {
        int[] widthHeight = new int[2];
        widthHeight[WIDTH_INDEX] = 0;
        widthHeight[HEIGHT_INDEX] = 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        widthHeight[WIDTH_INDEX] = size.x;
        widthHeight[HEIGHT_INDEX] = size.y;

        if (isScreenSizeRetrieved(widthHeight)) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            widthHeight[0] = metrics.widthPixels;
            widthHeight[1] = metrics.heightPixels;
        }

        // Last defense. Use deprecated API that was introduced in lower than API 13
        if (isScreenSizeRetrieved(widthHeight)) {
            widthHeight[0] = display.getWidth(); // deprecated
            widthHeight[1] = display.getHeight(); // deprecated
        }

        return widthHeight;
    }

    private static boolean isScreenSizeRetrieved(int[] widthHeight) {
        return widthHeight[WIDTH_INDEX] == 0 || widthHeight[HEIGHT_INDEX] == 0;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void toggleKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        Objects.requireNonNull(imm).showSoftInput(view, 0);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String firstTwo(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }

    public static String firstThree(String str) {
        return str.length() < 3 ? str : str.substring(0, 3);
    }

    public static String firstFour(String str) {
        return str.length() < 4 ? str : str.substring(0, 4);
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins((int) (l * (v.getContext().getResources().getDisplayMetrics().density)),
                    (int) (t * (v.getContext().getResources().getDisplayMetrics().density)),
                    (int) (r * (v.getContext().getResources().getDisplayMetrics().density)),
                    (int) (b * (v.getContext().getResources().getDisplayMetrics().density)));
            v.requestLayout();
        }
    }


    public static void animateImage(ImageView imageView) {
        imageView.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                imageView.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(shrink);
    }

    public static void makeFullScreen(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


    @SuppressLint("HardwareIds")
    public static ArrayList<String> getImei(Context context) {
        ArrayList<String> imeiList = new ArrayList<>();
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);

        String imeiSIM1 = telephonyInfo.getImsiSIM1();
        String imeiSIM2 = telephonyInfo.getImsiSIM2();
        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
        boolean isDualSIM = telephonyInfo.isDualSIM();

        Log.e("bal", " IME1 : " + imeiSIM1 + "\n" +
                " IME2 : " + imeiSIM2 + "\n" +
                " IS DUAL SIM : " + isDualSIM + "\n" +
                " IS SIM1 READY : " + isSIM1Ready + "\n" +
                " IS SIM2 READY : " + isSIM2Ready + "\n");
        imeiList.add(imeiSIM1);
        if (isDualSIM) {
            imeiList.add(imeiSIM2);
        }
        return imeiList;
    }

    public static String getUserUID() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public static String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                Address obj = addresses.get(0);
                String mainAddress = obj.getAddressLine(0);
                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();
                Log.e("IGA", "Address" + add);
                return mainAddress;
            } else {
                Toast.makeText(context, "Address still not selected.", Toast.LENGTH_SHORT).show();
                return "";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void onPopupButtonClick(Context context, View button) {
        PopupMenu popup = new PopupMenu(context, button);
        popup.getMenuInflater().inflate(R.menu.menu_edit_remove, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> true);
        popup.show();
    }

    public static String generateQueryString(String packageName, List<String> queryList, List<String> queryListName) {
        StringBuilder mQuery = new StringBuilder();
        endOfNest = new ArrayList<>(Arrays.asList("Subject", "Field", "Software", "Language", "Flavour", "Type", "Course", " Language ", "Item"));
        mQuery.append(firstTwo(packageName));


        int ignoredElement = 2;
        if (packageName.equals(SearchDataStore.DUME_GANG)) {
            ignoredElement++;
        }
        for (int i = 0; i < queryList.size() - ignoredElement; i++) {
            if (!endOfNest.contains(queryListName.get(i))) {
                mQuery.append(firstTwo(queryList.get(i)));
            } else {
                String s = queryList.get(i);
                String trim = s.replaceAll("\\s", "");
                String[] split = trim.split(",");
                for (String aSplit : split) {
                    mQuery.append(firstTwo(aSplit));
                }
            }
        }
        return mQuery.toString();
    }

    public static String generateCommonQueryString(String packageName, List<String> queryList, List<String> queryListName) {
        StringBuilder mQuery = new StringBuilder();
        mQuery.append(firstTwo(packageName));

        int ignoredElement = 3;
        if (packageName.equals(SearchDataStore.DUME_GANG)) {
            ignoredElement++;
        }
        for (int i = 0; i < queryList.size() - ignoredElement; i++) {
            mQuery.append(firstTwo(queryList.get(i)));
        }
        return mQuery.toString();
    }


    public static boolean isCommonMatched(List<String> localQueryList, List<String> dbQueryList, int threshold) {
        for (int i = 0; i < localQueryList.size() - threshold; i++) {
            if (!localQueryList.get(i).equals(dbQueryList.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAMinimalMatch(List<String> localQueryList, List<String> dbQueryList, int threshold) {
        //getting the subject array string
        String localSB = localQueryList.get(localQueryList.size() - threshold);
        String dbSB = dbQueryList.get(dbQueryList.size() - threshold);
        Log.e(TAG, "isAMinimalMatch: " + localSB + " : " + dbSB);

        String localtrim = localSB.replaceAll("\\s", "");
        String dbtrim = dbSB.replaceAll("\\s", "");
        String[] localsplit = localtrim.split(",");
        //     String[] dbsplit = dbtrim.split(",");
        for (String aLocalsplit : localsplit) {
            if (!dbtrim.contains(aLocalsplit)) {
                Log.e(TAG, "isAMinimalMatch: " + false);
                return false;
            }
        }
        Log.e(TAG, "isAMinimalMatch: " + true);
        return true;
    }

    public static Map<String, Object> getQueryMap(String packageName, List<String> queryList, List<String> queryListName) {
        StringBuilder mQuery = new StringBuilder();
        Map<String, Object> queryMap = new HashMap<>();
        endOfNest = new ArrayList<>(Arrays.asList("Subject", "Field", "Software", "Language", "Flavour", "Type", "Course", " Language ", "Item"));
        mQuery.append(firstTwo(packageName));
        ArrayList<String> subjectList = new ArrayList<>();

        StringBuilder commonQuery = new StringBuilder();

        int ignoredElement = 2;
        if (packageName.equals(SearchDataStore.DUME_GANG)) {
            ignoredElement++;
        }
        for (int i = 0; i < queryList.size() - ignoredElement; i++) {
            if (endOfNest.contains(queryListName.get(i))) {
                String s = queryList.get(i);
                String trim = s.replaceAll("\\s", "");
                String[] split = trim.split(",");
                for (String aSplit : split) {
                    mQuery.append(firstTwo(aSplit));
                    subjectList.add(firstTwo(aSplit));
                }
            } else {
                mQuery.append(firstTwo(queryList.get(i)));
                try {
                    commonQuery.append(firstTwo(queryList.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("bar", "Common Query : " + commonQuery.toString());
        queryMap.put("common_query", commonQuery.toString());
        queryMap.put("query_string", mQuery.toString());
        queryMap.put("subject_list", subjectList);
        return queryMap;
    }

    public static List<DocumentSnapshot> filterList(List<DocumentSnapshot> list, String identifier) {
        List<DocumentSnapshot> returnList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DocumentSnapshot record = list.get(i);
            if (identifier.equals(record.get("record_status"))) {
                returnList.add(record);
            }
        }
        return returnList;
    }

    public static List<Record> filterRecord(List<Record> list, String identifier) {
        List<Record> returnList = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Record record = list.get(i);
                if (identifier.equals(record.getStatus())) {
                    returnList.add(record);
                }
            }
        }
        return returnList;
    }

    public static String getLast(Map<String, Object> jizz) {
        endOfNest = new ArrayList<>(Arrays.asList("Subject", "Field", "Software", "Language", "Flavour", "Type", "Course", " Language ", "Item"));
        for (int j = 0; j < endOfNest.size(); j++) {
            if (jizz.containsKey(endOfNest.get(j))) {
                return (String) jizz.get(endOfNest.get(j));
            }
        }
        return "undefined";
    }

    public static List<String> getEndOFNest() {
        endOfNest = new ArrayList<>(Arrays.asList("Subject", "Field", "Software", "Language", "Flavour", "Type", "Course", " Language ", "Item"));
        return endOfNest;
    }


    public static int giveIconOnName(String TabName) {
        switch (TabName) {
            case "Level":
                return 0;
            case "Medium":
                return 1;
            case "Class":
                return 2;
            case "Subject":
                return 3;
            case "Field":
                return 4;
            case "Division":
                return 5;
            case "Language":
                return 6;
            case " Language ":
                return 7;
            case "Software":
                return 8;
            case "Music":
                return 9;
            case "Item":
                return 10;
            case "Flavour":
                return 11;
            case "Degree":
                return 12;
            case "Branch":
                return 5;
            case "Type":
                return 11;
            case "Gender":
                return 14;
            case "Salary":
                return 15;
            case "Cross Check":
                return 13;
            case "Capacity":
                return 16;
            default:
                return 5;
        }
    }


    public static int giveIconOnCategoryName(String TabName) {
        switch (TabName) {
            case "Education":
                return 0;
            case "Software":
                return 1;
            case "Programming":
                return 2;
            case "Language":
                return 3;
            case "Dance":
                return 4;
            case "Art":
                return 5;
            case "Cooking":
                return 6;
            case "Music":
                return 7;
            case "Others":
                return 8;
            default:
                return 0;
        }
    }

    private static Button requestBTN;
    private static BottomSheetDialog mMakeRequestBSD;
    private static View cancelsheetRootView;
    private static BottomSheetDialog mBackBSD;
    private static View backsheetRootView;
    private static TextView confirmMainText;
    private static TextView confirmSubText;
    private static Button comfirmYesBtn;
    private static Button confirmNoBtn;


    public static void notifyDialog(Context context, boolean hideBtn, boolean cancelable, String title, String body, String positiveString, TeacherContract.Model.Listener<Boolean> listener) {
        mMakeRequestBSD = new BottomSheetDialog(context);
        cancelsheetRootView = LayoutInflater.from(context).inflate(R.layout.custom_bottom_sheet_dialogue_cancel, null);
        mMakeRequestBSD.setContentView(cancelsheetRootView);
        confirmMainText = mMakeRequestBSD.findViewById(R.id.main_text);
        confirmSubText = mMakeRequestBSD.findViewById(R.id.sub_text);
        comfirmYesBtn = mMakeRequestBSD.findViewById(R.id.cancel_yes_btn);
        confirmNoBtn = mMakeRequestBSD.findViewById(R.id.cancel_no_btn);
        if (confirmMainText != null && confirmSubText != null && comfirmYesBtn != null && confirmNoBtn != null) {
            confirmMainText.setText(title);
            confirmSubText.setText(body);
            confirmNoBtn.setText("No");
            comfirmYesBtn.setText(positiveString);
            if (positiveString.equals("Add Skill")) {
                confirmNoBtn.setText("Not Now");
            }

            comfirmYesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSuccess(true);
                    mMakeRequestBSD.dismiss();
                }
            });
            confirmNoBtn.setEnabled(cancelable);
            if (!cancelable) {
                confirmNoBtn.setTextColor(context.getResources().getColor(R.color.disable_color));
            }
            confirmNoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSuccess(false);
                    mMakeRequestBSD.dismiss();
                }
            });

            setMargins(confirmNoBtn, 0, 0, (int) (10 * context.getResources().getDisplayMetrics().density), (int) (4 * context.getResources().getDisplayMetrics().density));
            if (hideBtn) {
                comfirmYesBtn.setVisibility(View.GONE);
                confirmNoBtn.setTextColor(context.getResources().getColor(R.color.percent_off_active_color));
                setMargins(confirmNoBtn, 0, 0, 0, (int) (4 * context.getResources().getDisplayMetrics().density));
                confirmNoBtn.setText("Retry");
                confirmNoBtn.setEnabled(true);
            }
            try {
                mMakeRequestBSD.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMakeRequestBSD.setCancelable(cancelable);
            mMakeRequestBSD.setCanceledOnTouchOutside(false);
        }
    }

    public static Number getCalculatedSalary(Number mentorAskedSalary, String dbPackageName, List<String> dbQueryList) {
        //adding if not found in previous records
        //TODO calculated salary
        //getting the subject array string
        SearchDataStore searchDataStore = SearchDataStore.getInstance();
        int threshold = dbPackageName.equals(SearchDataStore.DUME_GANG) ? 4 : 3;
        List<String> localQueryList = searchDataStore.getQueryList();

        String localSB = localQueryList.get(localQueryList.size() - threshold);
        String dbSB = dbQueryList.get(dbQueryList.size() - threshold);

        String localtrim = localSB.replaceAll("\\s", "");
        String dbtrim = dbSB.replaceAll("\\s", "");

        String[] localsplit = localtrim.split(",");
        String[] dbsplit = dbtrim.split(",");

        Number calculatedSalary = mentorAskedSalary.floatValue() * 0.25 + ((mentorAskedSalary.floatValue() - (mentorAskedSalary.floatValue() * 0.25)) /
                (dbsplit.length)) * localsplit.length;
        return calculatedSalary;
    }


    public static String getFacebookPageURL(Context context, String fbUrl, String pageID ) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + fbUrl;
            } else {
                return "fb://page/" + pageID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return fbUrl;
        }
    }

    static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) account = accounts[0];
        else account = null;

        if (account == null)return null;
        else return account.name;
    }

}

