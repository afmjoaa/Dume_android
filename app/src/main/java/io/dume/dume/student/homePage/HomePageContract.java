package io.dume.dume.student.homePage;

import android.view.MenuItem;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;
import java.util.Map;

import io.dume.dume.student.homePage.adapter.HomePageRatingData;
import io.dume.dume.student.homePage.adapter.HomePageRecyclerData;
import io.dume.dume.student.recordsPage.Record;
import io.dume.dume.teacher.homepage.TeacherContract;

public interface HomePageContract {


    interface View {

        void init();

        void findView();

        void gotoGrabingLocationPage();

        //getters

        //setters

        void flush(String msg);

        void searchFilterClicked();
    }

    interface Presenter {

        void homePageEnqueue();

        void onViewIntracted(android.view.View view);

        void onMenuItemInteracted(MenuItem item);

        void checkNetworkAndGps();

        void defaultOptionMenuCreated();

        void getDataFromDB();
    }

    interface Model {

        void applyPromo(HomePageRecyclerData promoData, String promo_code, String accountType, TeacherContract.Model.Listener<String> listener);

        void updatePromo(HomePageRecyclerData promoData, TeacherContract.Model.Listener<String> listener);

        void removeAppliedPromo(HomePageRecyclerData promoData, TeacherContract.Model.Listener<Boolean> listener);

        void getPromo(String promoCode, TeacherContract.Model.Listener<HomePageRecyclerData> listener);

        void submitRating(String record_id, String skill_id, String opponent_uid, String myAccountType, Map<String, Boolean> inputRating, Float inputStar, String feedbackString, TeacherContract.Model.Listener<Void> listener);

        void getSingleRecords(String recordId, TeacherContract.Model.Listener<Record> listener);

        void addShapShotListener(EventListener<DocumentSnapshot> updateViewListener);

        void removeCompletedRating(String identify, TeacherContract.Model.Listener<Void> listener);
    }
}
