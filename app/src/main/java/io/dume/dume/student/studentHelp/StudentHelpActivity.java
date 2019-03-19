package io.dume.dume.student.studentHelp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.dume.dume.Google;
import io.dume.dume.R;
import io.dume.dume.common.appInfoActivity.AppInfoActivity;
import io.dume.dume.common.privacyPolicy.PrivacyPolicyActivity;
import io.dume.dume.model.DumeModel;
import io.dume.dume.student.common.SettingData;
import io.dume.dume.student.common.SettingsAdapter;
import io.dume.dume.student.pojo.CustomStuAppCompatActivity;
import io.dume.dume.student.pojo.SearchDataStore;
import io.dume.dume.student.studentSettings.SavedPlacesAdaData;
import io.dume.dume.student.studentSettings.SavedPlacesAdapter;
import io.dume.dume.student.studentSettings.StudentSettingsActivity;
import io.dume.dume.teacher.homepage.TeacherContract;
import io.dume.dume.teacher.homepage.TeacherDataStore;
import io.dume.dume.util.AlertMsgDialogue;
import io.dume.dume.util.DumeUtils;

import static io.dume.dume.util.DumeUtils.configAppbarTittle;
import static io.dume.dume.util.DumeUtils.configureAppbar;

public class StudentHelpActivity extends CustomStuAppCompatActivity implements StudentHelpContract.View {

    private StudentHelpContract.Presenter mPresenter;
    private static final int fromFlag = 12;
    private String[] helpNameArr;
    private RecyclerView helpRecyclerView;
    private AppBarLayout appBarLayout;
    private View helpContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stunav_activity3_student_help);
        setActivityContext(this, fromFlag);
        findLoadView();
        mPresenter = new StudentHelpPresenter(this, new StudentHelpModel());
        mPresenter.studentHelpEnqueue();
        configureAppbar(this, "Help");

        //setting the recycler view
        SettingsAdapter helpAdapter = new SettingsAdapter(this, getFinalData()) {
            @Override
            protected void OnButtonClicked(View v, int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(StudentHelpActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        String url = "https://dume-2d063.firebaseapp.com/home";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;

                    case 2:
                        helpContent.setVisibility(View.GONE);
                        configAppbarTittle(StudentHelpActivity.this, helpNameArr[position]);
                        appBarLayout.setExpanded(false);
                        getFragmentManager().beginTransaction().replace(R.id.content, new HowToUseFragment()).commit();
                        break;
                    case 3:
                        helpContent.setVisibility(View.GONE);
                        configAppbarTittle(StudentHelpActivity.this, helpNameArr[position]);
                        appBarLayout.setExpanded(false);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new FAQFragment()).commit();
                        break;
                    case 4:
                        helpContent.setVisibility(View.GONE);
                        configAppbarTittle(StudentHelpActivity.this, helpNameArr[position]);
                        appBarLayout.setExpanded(false);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new ContactUsFragment()).commit();
                        break;
                    case 5:
                        updateAppCalled();
                        break;
                    case 6:
                        startActivity(new Intent(StudentHelpActivity.this, PrivacyPolicyActivity.class).setAction("fromHelp"));
                        break;
                    case 7:
                        startActivity(new Intent(StudentHelpActivity.this, AppInfoActivity.class).setAction("fromHelp"));
                        break;
                }

            }
        };
        helpRecyclerView.setAdapter(helpAdapter);
        helpRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private void updateAppCalled() {

        Bundle Uargs = new Bundle();
        Uargs.putString("msg", "Sorry! No update available.");
        AlertMsgDialogue updateAlertDialogue = new AlertMsgDialogue();
        updateAlertDialogue.setItemChoiceListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(StudentHelpActivity.this, "Ok", Toast.LENGTH_SHORT).show();
            }
        }, "Ok");
        updateAlertDialogue.setArguments(Uargs);
        updateAlertDialogue.show(getSupportFragmentManager(), "updateAlertDialogue");
    }

    @Override
    public void findView() {
        helpNameArr = getResources().getStringArray(R.array.helpHeader);
        helpRecyclerView = findViewById(R.id.help_recycler);
        appBarLayout = findViewById(R.id.app_bar);
        helpContent = findViewById(R.id.help_content);

    }

    @Override
    public void initStudentHelp() {

    }

    @Override
    public void configStudentHelp() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public List<SettingData> getFinalData() {
        List<SettingData> data = new ArrayList<>();
        int[] imageIcons = {
                R.drawable.ic_help_whats_new,
                R.drawable.ic_dume_web,
                R.drawable.ic_help_feature,
                R.drawable.ic_help_faq,
                R.drawable.ic_help_contact_us,
                R.drawable.ic_sync,
                R.drawable.ic_help_privacy_policy,
                R.drawable.ic_help_app_info
        };

        for (int i = 0; i < helpNameArr.length && i < imageIcons.length; i++) {
            SettingData current = new SettingData();
            current.settingName = helpNameArr[i];
            current.settingIcon = imageIcons[i];
            data.add(current);
        }
        return data;
    }



    //testing the contact up
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ContactUsFragment extends Fragment {

        private StudentHelpActivity myMainActivity;
        private AutoCompleteTextView queryTextView;
        private Context context;
        private DumeModel dumeModel;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            dumeModel = new DumeModel(context);
        }

        @Override
        public void onAttach(Context context) {
            this.context = context;
            super.onAttach(context);

        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            myMainActivity = (StudentHelpActivity) getActivity();
            View rootView = inflater.inflate(R.layout.custom_contact_up_fragment, container, false);
            queryTextView = rootView.findViewById(R.id.feedback_textview);
            Button submitBTN = rootView.findViewById(R.id.submit_btn);
            Button readFaqBTN = rootView.findViewById(R.id.skip_btn);
            TextView limit = rootView.findViewById(R.id.limitTV);
            queryTextView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    queryTextView.setHint("Please describe your problem");
                } else {
                    queryTextView.setHint("Please describe your problem");
                }
            });

            queryTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().length() >= 200) {
                        limit.setText(editable.toString().length() + "/200");
                        limit.setTextColor(Color.RED);
                    } else {
                        limit.setText(editable.toString().length() + "/200");
                        limit.setTextColor(Color.BLACK);
                    }
                }
            });
            submitBTN.setOnClickListener(view -> {
                submitBTN.setEnabled(false);

                myMainActivity.showProgress();
                if (!queryTextView.getText().toString().equals("")) {
                    dumeModel.reportIssue(Google.getInstance().getAccountMajor().equals(DumeUtils.STUDENT) ? SearchDataStore.getInstance().getUserMail() : TeacherDataStore.getInstance().gettUserMail(), queryTextView.getText().toString(), new TeacherContract.Model.Listener<Void>() {
                        @Override
                        public void onSuccess(Void list) {
                            submitBTN.setEnabled(true);

                            Toast.makeText(myMainActivity, "Your message is sent to Dume authority. You will be notified by your email : " + SearchDataStore.getInstance().getUserMail(), Toast.LENGTH_LONG).show();
                            myMainActivity.hideProgress();
                        }

                        @Override
                        public void onError(String msg) {
                            myMainActivity.hideProgress();
                            Toast.makeText(myMainActivity, msg, Toast.LENGTH_SHORT).show();
                            submitBTN.setEnabled(true);


                        }
                    });
                }
                queryTextView.getText().clear();
            });
            readFaqBTN.setOnClickListener(view -> {

            });
            return rootView;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), StudentHelpActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    //testing the faq here
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FAQFragment extends Fragment {

        private StudentHelpActivity myMainActivity;
        private WebView webView;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            myMainActivity = (StudentHelpActivity) getActivity();
            View rootView = inflater.inflate(R.layout.custom_faq_fragment, container, false);
            webView = rootView.findViewById(R.id.activity_main_webview);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("https://dume-2d063.firebaseapp.com/faq");
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            return rootView;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    startActivity(new Intent(getActivity(), StudentHelpActivity.class));
                }
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


    }

    public static class HowToUseFragment extends android.app.Fragment {
        private Context context;

        private StudentHelpActivity myMainActivity;
        private WebView webView;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            myMainActivity = (StudentHelpActivity) getActivity();
            View rootView = inflater.inflate(R.layout.custom_faq_fragment, container, false);
            webView = rootView.findViewById(R.id.activity_main_webview);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("https://dume-2d063.firebaseapp.com/hows");
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            return rootView;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    startActivity(new Intent(getActivity(), StudentHelpActivity.class));
                }
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onAttach(Context context) {
            this.context = context;
            super.onAttach(context);
        }
    }
}
