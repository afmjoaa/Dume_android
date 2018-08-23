package io.dume.dume.teacher.mentor_settings;

import android.view.View;

import java.util.ArrayList;

import io.dume.dume.R;

public class AccountSettingsPresenter implements AccountSettingsContract.Presenter {

    AccountSettingsContract.MentorView view;
    AccountSettingsContract.MentorModel mentorModel;

    public AccountSettingsPresenter(AccountSettingsContract.MentorView view, AccountSettingsContract.MentorModel mentorModel) {
        this.view = view;
        this.mentorModel = mentorModel;
    }

    @Override
    public void enqueue() {
        view.setViewConfig();
        view.setUpBadge();


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
            case R.id.addAcademicBTN:
                view.addAcademic();
                break;
            default:


        }
    }

    @Override
    public void loadData() {
        view.showLoading();
        mentorModel.getDataArray(new AccountSettingsContract.MentorModel.DataListener() {
            @Override
            public void onSuccess(ArrayList<String> arrayList) {
                view.gatherDataInListView(arrayList);
                view.hideLoading();
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
            }
        });
    }
}
