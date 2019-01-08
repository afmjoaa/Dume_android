package io.dume.dume.teacher.mentor_settings;

import android.view.View;

import java.util.ArrayList;
import java.util.Map;

import io.dume.dume.R;
import io.dume.dume.inter_face.UserQueryListener;
import io.dume.dume.teacher.pojo.Education;
import io.dume.dume.teacher.pojo.GlobalListener;

public class AccountSettingsPresenter implements AccountSettingsContract.Presenter {

    AccountSettingsContract.MentorView view;
    AccountSettingsContract.MentorModel mentorModel;
    private static final String TAG = "AccountSettingsPresente";

    public AccountSettingsPresenter(AccountSettingsContract.MentorView view, AccountSettingsContract.MentorModel mentorModel) {
        this.view = view;
        this.mentorModel = mentorModel;
    }

    @Override
    public void enqueue() {
        view.setViewConfig();
        view.initJoaaRV();


    }

    @Override
    public void onViewClicked(View activityView) {
        switch (activityView.getId()) {

            case R.id.profileSection:
                if (view.isBasicInfoShowing()) {
                    view.hideBasicInfo();
                } else view.showBasicInfo();
                break;

            case R.id.accountEditButton:
                view.editAccount();
                break;
            case R.id.addLocationBTN:
                view.addLocation();
                break;
            case R.id.updateBTN:
                view.updateLocation();
                break;

            default:


        }
    }

    @Override
    public void loadData() {
        mentorModel.queryUserData(new UserQueryListener() {

            @Override
            public void onSuccess(Map<String, Object> objs) {
                view.hideLoading();
                view.updateUserInfo(objs);
            }

            @Override
            public void onFailure(String error) {
                view.hideLoading();
                view.toast(error);
            }

            @Override
            public void onStart() {
                view.showLoading();
            }
        });

    }
}
