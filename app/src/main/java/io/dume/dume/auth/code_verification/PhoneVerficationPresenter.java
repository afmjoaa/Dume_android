package io.dume.dume.auth.code_verification;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.dume.dume.auth.AuthGlobalContract;
import io.dume.dume.auth.DataStore;
import io.dume.dume.auth.auth.AuthContract;
import io.dume.dume.util.DumeUtils;

import static io.dume.dume.student.pojo.StuBaseModel.setStuProfile;

public class PhoneVerficationPresenter implements PhoneVerificationContract.Presenter {

    PhoneVerificationContract.View view;
    PhoneVerificationContract.Model model;
    private CountDownTimer countDownTimer;
    private static final String TAG = "PhoneVerficationPresent";
    private final FirebaseFirestore fireStore;
    private Context context;
    private final ArrayList<String> imeiList;

    public PhoneVerficationPresenter(Context context, PhoneVerificationContract.View view, PhoneVerificationContract.Model authModel) {
        this.context = context;
        this.view = view;
        this.model = authModel;
        fireStore = FirebaseFirestore.getInstance();
        imeiList = DumeUtils.getImei(context);

    }

    @Override
    public void enqueue() {
        view.findView();
        view.initActionBar();
        view.init();
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                view.updateTimer(l);
            }

            @Override
            public void onFinish() {
                view.onTimerCompleted();
            }
        };
        startTimer();

    }

    @Override
    public void onPinConfirm(String pin) {
        if (pin.isEmpty() || !DumeUtils.isInteger(pin)) {
            view.showToast("Enter a valid pin");
            return;
        }
        model.verifyCode(pin, new PhoneVerificationContract.Model.CodeVerificationCallBack() {
            @Override
            public void onStart() {
                view.showProgress();
                //"Authenticating..."
            }

            @Override
            public void onSuccess() {
                view.hideProgress();
                if (DataStore.STATION == 2) {
                    saveUserToDb(model.getData());
                } else {
                    nextActivity();
                }
            }

            @Override
            public void onFail(String error) {
                view.hideProgress();
                view.onVerificationFailed(error);
            }
        });
    }

    @Override
    public void onResendCode() {

        model.onResendCode(new AuthContract.Model.Callback() {
            @Override
            public void onStart() {
                view.showProgress();
                Log.w(TAG, "onStart: ");
                //"Resending Code"
            }

            @Override
            public void onFail(String error) {
                view.hideProgress();
                view.showToast(error);
            }

            @Override
            public void onSuccess(String id, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                view.hideProgress();
                view.showToast("Code sent. Please check your mobile phone");
                DataStore.resendingToken = forceResendingToken;
                startTimer();
            }

            @Override
            public void onAutoSuccess(AuthResult authResult) {
                view.hideProgress();
                if (DataStore.STATION == 2) {
                    saveUserToDb(model.getData());
                } else {
                    nextActivity();
                }

            }
        });
    }

    private void nextActivity() {
        model.onAccountTypeFound(FirebaseAuth.getInstance().getCurrentUser(), new AuthGlobalContract.AccountTypeFoundListener() {
            @Override
            public void onStart() {
                view.showProgress();
                //"Authenticating..."
            }

            @Override
            public void onTeacherFound() {
                view.hideProgress();
                view.gotoTeacherActivity();
            }

            @Override
            public void onStudentFound() {
                view.hideProgress();
                view.gotoStudentActivity();
            }

            @Override
            public void onBootcamp() {

            }

            @Override
            public void onFail(String exeption) {
                view.hideProgress();
                view.showToast(exeption);
            }
        });
    }

    @Override
    public void startTimer() {
        view.onTimerStarted();
        countDownTimer.start();
    }


    private void saveUserToDb(DataStore dataStore) {
        if (dataStore != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
            Map<String, Object> user = new HashMap<>();
            user.put("first_name", dataStore.getFirstName());
            user.put("last_name", dataStore.getLastName());
            user.put("account_major", dataStore.getAccountManjor());
            user.put("phone_number", dataStore.getPhoneNumber());
            user.put("avatar", dataStore.getPhotoUri());
            if (dataStore.getEmail() != null) {
                user.put("email", dataStore.getEmail());
            }
            user.put("gender", "");
            user.put("religion", "");
            user.put("birth_date", "");
            user.put("obligation", false);
            user.put("referred", false);
            user.put("referer_id", "");
            user.put("user_ref_link", "");
            user.put("account_active", true);
            user.put("imei", imeiList);
            view.showProgress();
            //"Saving User..."
            DocumentReference userStudentProInfo = fireStore.collection("/users/students/stu_pro_info").document(model.getUser().getUid());
            fireStore.collection("mini_users").document(model.getUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    setStuProfile((Activity) context,userStudentProInfo,generateStuProInfo(dataStore));
                    view.hideProgress();
                    nextActivity();
                    Log.w(TAG, "onComplete: User Added");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "onFailure: User Not Added  " + e.getLocalizedMessage());
                }
            });
        } else {
            Log.w(TAG, "saveUserToDb: " + "Datastore null or user not logged in");
        }
    }

    private Map<String, Object> generateStuProInfo(DataStore dataStore){
        Map<String, Object> stuProInfo = new HashMap<>();
        stuProInfo.put("first_name", dataStore.getFirstName());
        stuProInfo.put("last_name", dataStore.getLastName());
        stuProInfo.put("phone_number", dataStore.getPhoneNumber());
        stuProInfo.put("avatar", dataStore.getPhotoUri());
        stuProInfo.put("email", dataStore.getEmail());
        stuProInfo.put("gender", "");
        stuProInfo.put("current_status", "");
        stuProInfo.put("previous_result", "");
        stuProInfo.put("pro_com_%", "60");

        GeoPoint current_address = new GeoPoint(84.9, -180);
        stuProInfo.put("current_address", current_address);

        ArrayList<String> appliedPromoList = null;
        stuProInfo.put("applied_promo", appliedPromoList);
        ArrayList<String> availablePromoList = null;
        stuProInfo.put("available_promo", availablePromoList);

        Map<String, Object> selfRating = new HashMap<>();
        selfRating.put("star_rating", 5.00);
        selfRating.put("communication", "100%");
        selfRating.put("behaviour", "100%");
        stuProInfo.put("self_rating", selfRating);

        return stuProInfo;
    }


}
