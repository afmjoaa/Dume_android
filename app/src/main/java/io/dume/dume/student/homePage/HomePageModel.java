package io.dume.dume.student.homePage;

import android.app.Activity;
import android.content.Context;
import io.dume.dume.student.pojo.StuBaseModel;

public class HomePageModel extends StuBaseModel implements HomePageContract.Model {
    private Context context;
    private Activity activity;
    private static final String TAG = "HomePageModel";

    public HomePageModel(Activity activity, Context context) {
        super(activity, context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void hawwa() {

    }
}
