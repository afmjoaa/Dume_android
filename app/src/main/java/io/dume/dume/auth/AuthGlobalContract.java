package io.dume.dume.auth;

import com.google.firebase.auth.FirebaseUser;

public interface AuthGlobalContract {

    interface Model {
        boolean isUserLoggedIn();

        FirebaseUser getUser();

        void onAccountTypeFound(FirebaseUser user, AccountTypeFoundListener listener);

    }

    interface View {

        void gotoTeacherActivity();

        void gotoStudentActivity();
    }

    interface AccountTypeFoundListener {
        void onStart();

        void onTeacherFound();

        void onStudentFound();

        void onFail(String exeption);
    }
}

