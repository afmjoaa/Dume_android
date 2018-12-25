package io.dume.dume.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.dume.dume.afterSplashTrp.AfterSplashActivity;
import io.dume.dume.auth.AuthModel;
import io.dume.dume.student.homePage.HomePageActivity;
import io.dume.dume.teacher.homepage.TeacherActivtiy;
import io.dume.dume.util.DumeUtils;

import static io.dume.dume.util.DumeUtils.makeFullScreen;

public class SplashActivity extends AppCompatActivity implements SplashContract.View {
    SplashContract.Presenter presenter;
    private static final String TAG = "SplashActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SplashPresenter(this, new AuthModel(this, this));
        presenter.enqueue();
        makeFullScreen(this);
        presenter.init(this);


    }

    @Override
    public void gotoLoginActivity() {
        startActivity(new Intent(this, AfterSplashActivity.class));
        finish();
    }

    @Override
    public void gotoTeacherActivity() {
        //  startActivity(new Intent(this, TeacherActivityMock.class));
        startActivity(new Intent(this, TeacherActivtiy.class));
        finish();
    }

    @Override
    public void gotoStudentActivity() {
        startActivity(new Intent(this, HomePageActivity.class));
        finish();
    }
}
