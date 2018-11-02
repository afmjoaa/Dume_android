package io.dume.dume.student.mentorAddvertise;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.dume.dume.R;
import io.dume.dume.student.pojo.CustomStuAppCompatActivity;

import static io.dume.dume.util.DumeUtils.configureAppbar;

public class MentorAddvertiseActivity extends CustomStuAppCompatActivity implements MentorAddvertiseContact.View {

    private MentorAddvertiseContact.Presenter mPresenter;
    private static final String TAG = "MentorAddvertiseActivit";
    private static final int fromFlag = 16;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stu15_activity_mentor_addvertise);
        setActivityContext(this, fromFlag);
        mPresenter = new MentorAddvertisePresenter(this, new MentorAddvertiseModel());
        mPresenter.mentorAddvertiseEnqueue();
        configureAppbar(this, "Start mentoring");

    }

    @Override
    public void findView() {

    }

    @Override
    public void initMentorAddvertise() {

    }

    @Override
    public void configMentorAddvertise() {

    }
}
