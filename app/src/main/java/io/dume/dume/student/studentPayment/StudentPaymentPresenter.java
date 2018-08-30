package io.dume.dume.student.studentPayment;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class StudentPaymentPresenter implements StudentPaymentContract.Presenter {

    private StudentPaymentContract.View mView;
    private StudentPaymentContract.Model mModel;
    private Context context;
    private Activity activity;

    public StudentPaymentPresenter(Context context, StudentPaymentContract.Model mModel) {
        this.context = context;
        this.activity = (Activity) context;
        this.mView = (StudentPaymentContract.View) context;
        this.mModel = mModel;
    }

    @Override
    public void studentPaymentEnqueue() {

    }

    @Override
    public void onStudentPaymentIntracted(View view) {

    }
}
