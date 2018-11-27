package io.dume.dume.student.grabingLocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import carbon.beta.AppBarLayout;
import carbon.widget.Button;
import carbon.widget.ImageView;
import carbon.widget.RelativeLayout;
import io.dume.dume.R;
import io.dume.dume.customView.HorizontalLoadView;
import io.dume.dume.student.grabingInfo.GrabingInfoActivity;
import io.dume.dume.student.pojo.CusStuAppComMapActivity;
import io.dume.dume.student.pojo.MyGpsLocationChangeListener;
import io.dume.dume.teacher.crudskill.CrudSkillActivity;
import io.dume.dume.util.DumeUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static io.dume.dume.util.DumeUtils.STUDENT;
import static io.dume.dume.util.DumeUtils.hideKeyboard;
import static io.dume.dume.util.DumeUtils.setMargins;
import static io.dume.dume.util.DumeUtils.showKeyboard;

public class GrabingLocationActivity extends CusStuAppComMapActivity implements OnMapReadyCallback,
        GrabingLocaitonContract.View, MyGpsLocationChangeListener {

    private GrabingLocaitonContract.Presenter mPresenter;
    private static final CharacterStyle STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
    private GoogleMap mMap;
    private static final String TAG = "GrabingLocationActivity";
    private static final int fromFlag = 2;
    private Context mContext;
    private LatLng mCenterLatLong;
    private SupportMapFragment mapFragment;
    private FloatingActionButton fab;
    private View map;
    private Toolbar toolbar;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout llBottomSheet;
    private FloatingActionButton bottomSheetFab;
    private boolean firstTime = true;
    private HorizontalLoadView loadView;
    private android.support.design.widget.AppBarLayout myAppbarlayout;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView autoCompleteRecyView;
    private RecyclerView menualCompleteRecyView;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    protected EditText inputSearch;
    private CompositeDisposable compositeDisposable;
    private PlaceAutoRecyAda recyclerAutoAdapter;
    private PlaceMenualRecyAda recyclerMenualAdapter;
    private ArrayList<AutocompletePrediction> initAdapterData;
    private ImageView discardImage;
    private Button locationDoneBtn;
    private RelativeLayout inputSearchContainer;
    private String retrivedAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stu3_activity_grabing_location);
        buildGoogleApiClient();
        setActivityContextMap(this, fromFlag);
        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        getLocationPermission(mapFragment);
        mPresenter = new GrabingLocationPresenter(this, new GrabingLocationModel());
        mPresenter.grabingLocationPageEnqueue();
        setDarkStatusBarIcon();
        setIsNight();
        compositeDisposable = new CompositeDisposable();

        //auto one
        initAdapterData = new ArrayList<AutocompletePrediction>();
        recyclerAutoAdapter = new PlaceAutoRecyAda(this, initAdapterData, mGoogleApiClient) {
            @Override
            void OnItemClicked(View v, AutocompletePrediction clickedPrediction) {
                inputSearch.setText(clickedPrediction.getPrimaryText(STYLE_NORMAL));
                Places.GeoDataApi.getPlaceById(mGoogleApiClient, clickedPrediction.getPlaceId())
                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                            //TODO not complete
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getStatus().isSuccess()) {
                                    final Place myPlace = places.get(0);
                                    LatLng queriedLocation = myPlace.getLatLng();
                                    Log.e(TAG, " " + queriedLocation.latitude);
                                    Log.e(TAG, " " + queriedLocation.longitude);
                                    moveCamera(new LatLng(queriedLocation.latitude, queriedLocation.longitude), DEFAULT_ZOOM
                                            , "typed location", mMap);
                                }
                                places.release();
                            }
                        });
                hideKeyboard(GrabingLocationActivity.this);
                inputSearch.clearFocus();
                inputSearchContainer.requestFocus();
                llBottomSheet.setVisibility(View.INVISIBLE);
                locationDoneBtn.setVisibility(View.VISIBLE);
             }
        };
        autoCompleteRecyView.setAdapter(recyclerAutoAdapter);
        autoCompleteRecyView.setLayoutManager(new LinearLayoutManager(this));
        //menual one
        recyclerMenualAdapter = new PlaceMenualRecyAda(this, getFinalData());
        menualCompleteRecyView.setAdapter(recyclerMenualAdapter);
        menualCompleteRecyView.setLayoutManager(new LinearLayoutManager(this));

        retrivedAction = getIntent().getAction();
        if(Objects.requireNonNull(retrivedAction).equals("fromPPA")){
            fromProfilePage();
        }


    }

    private void fromProfilePage() {
        Toast.makeText(mContext, "from Profile Page", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    public void findView() {
        fab = findViewById(R.id.fab);
        map = findViewById(R.id.map);
        //mLocationMarkerText = (TextView) findViewById(R.id.locationMarkertext);
        loadView = findViewById(R.id.loadView);
        toolbar = findViewById(R.id.accountToolbar);
        llBottomSheet = findViewById(R.id.searchBottomSheet);
        myAppbarlayout = findViewById(R.id.my_appbarLayout_cardView);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.my_main_container);
        autoCompleteRecyView = findViewById(R.id.recycler_view_autoPlaces);
        menualCompleteRecyView = findViewById(R.id.recycler_view_manual);
        inputSearch = findViewById(R.id.input_search);
        discardImage = findViewById(R.id.discard_image);
        locationDoneBtn = findViewById(R.id.location_done_btn);
        inputSearchContainer = findViewById(R.id.input_search_container);

    }

    @Override
    public void initGrabingLocationPage() {
        fab.setAlpha(0.90f);
        //initializing actionbar/toolbar
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_more_vert_black_24dp);
        toolbar.setOverflowIcon(drawable);

        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        llBottomSheet.animate().scaleX(1 + (1 * 0.058f)).setDuration(0).start();
        if (!loadView.isRunningAnimation()) {
            loadView.startLoading();
        }
        ViewTreeObserver vto = llBottomSheet.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                llBottomSheet.getLayoutParams().height = (llBottomSheet.getHeight() - myAppbarlayout.getHeight() - (int) (5 * (getResources().getDisplayMetrics().density)));
                llBottomSheet.requestLayout();
                bottomSheetBehavior.onLayoutChild(coordinatorLayout, llBottomSheet, ViewCompat.LAYOUT_DIRECTION_LTR);
                ViewTreeObserver obs = llBottomSheet.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }

        });


        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAutocomplete(s);
                if (discardImage.getVisibility() == View.INVISIBLE && !s.equals("")) {
                    discardImage.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    discardImage.setVisibility(View.INVISIBLE);
                }
            }
        });


        inputSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    llBottomSheet.setVisibility(View.VISIBLE);
                    locationDoneBtn.setVisibility(View.INVISIBLE);
                    //setMargins(fab,16, 16, 16, 10);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Log.e(TAG, "onFocusChange: has focus");

                } else {
                    Log.e(TAG, "onFocusChange: nonononono focus");

                }
            }
        });


    }

    @Override
    public void configGrabingLocationPage() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    if (fab.getVisibility() == View.INVISIBLE) {
                        fab.setVisibility(View.VISIBLE);
                    }
                    hideKeyboard(GrabingLocationActivity.this);
                    inputSearch.clearFocus();
                    inputSearchContainer.requestFocus();
                } else if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    showKeyboard(GrabingLocationActivity.this);
                } else if (BottomSheetBehavior.STATE_DRAGGING == newState) {

                } else if (BottomSheetBehavior.STATE_SETTLING == newState) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                llBottomSheet.animate().scaleX(1 + (slideOffset * 0.058f)).setDuration(0).start();
                fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
                if (COMPASSBTN != null) {
                    COMPASSBTN.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
                }

            }
        });


    }

    @Override
    public void makingCallbackInterfaces() {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        ISNIGHT = HOUR < 4 || HOUR > 24;
        if (ISNIGHT) {
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style_night_json);
            googleMap.setMapStyle(style);
        } else {
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style_default_json);
            googleMap.setMapStyle(style);
        }
        mMap = googleMap;
        onMapReadyListener(mMap);
        onMapReadyGeneralConfig();
        mMap.setPadding((int) (10 * (getResources().getDisplayMetrics().density)), 0, 0, (int) (72 * (getResources().getDisplayMetrics().density)));


        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                locationDoneBtn.setEnabled(false);
                locationDoneBtn.setBackgroundColor(getResources().getColor(R.color.disable_color));
                inputSearch.setText(R.string.LoadingText);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Cleaning all the markers.
                if (mMap != null) {
                    mMap.clear();
                }

                mCenterLatLong = mMap.getCameraPosition().target;
                //mZoom = mGoogleMap.getCameraPosition().zoom;
                //mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);
                String mainAddress = getAddress(mCenterLatLong.latitude, mCenterLatLong.longitude);
                inputSearch.setText(mainAddress);
                locationDoneBtn.setEnabled(true);
                locationDoneBtn.setBackgroundColor(getResources().getColor(R.color.colorBlack));

            }
        });

    }

    @Override
    public void onCenterCurrentLocation() {
        Drawable d = fab.getDrawable();
        if (d instanceof Animatable) {
            ((Animatable) d).start();
        }
        Log.d(TAG, "onClick: clicked gps icon");
        getDeviceLocation(mMap);

    }

    @Override
    public void onDiscardSearchClicked() {
        inputSearch.getText().clear();
        discardImage.setVisibility(View.INVISIBLE);
    }

    //not interested right now
    @Override
    public void onLocationDoneBtnClicked() {
        //startActivity(new Intent(this, GrabingInfoActivity.class).setAction(DumeUtils.STUDENT));
        startActivity(new Intent(this, CrudSkillActivity.class).setAction(DumeUtils.STUDENT));
    }

    public void onGrabingLocationViewClicked(View view) {
        mPresenter.onGrabingLocationViewIntracted(view);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

    }

    @Override
    public void onMyGpsLocationChanged(Location location) {

    }

    @SuppressLint("CheckResult")
    private void getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Log.i(TAG, "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            final PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    LAT_LNG_BOUNDS, null);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            Callable<AutocompletePredictionBuffer> callable = () -> {
                AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);
                final Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    Toast.makeText(GrabingLocationActivity.this, "Error contacting API: " + status.toString(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                    autocompletePredictions.release();
                    return null;
                }
                return autocompletePredictions;
            };

            Observable<AutocompletePredictionBuffer> observable = Observable.fromCallable(callable);

            Disposable disposable = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((AutocompletePredictionBuffer item) -> {
                                ArrayList<AutocompletePrediction> autocompletePredictions = DataBufferUtils.freezeAndClose(item);

                                Log.i(TAG, "Query completed. Received final " + autocompletePredictions);
                                publishTheAutoResults(autocompletePredictions);
                            },
                            error -> Log.e(TAG, "error ", new RuntimeException(error)),
                            () -> Log.e(TAG, "DONE "));
            compositeDisposable.add(disposable);

        } else {
            Log.e(TAG, "Google API client is not connected for autocomplete query.");
        }
    }

    public void publishTheAutoResults(ArrayList<AutocompletePrediction> results) {
        if (results == null) {
            Log.e(TAG, "publishTheAutoResults: null found");
            autoCompleteRecyView.invalidate();
        } else {
            Log.e(TAG, "publishTheAutoResults: " + results);
            recyclerAutoAdapter.update(results);
            //recyclerAutoAdapter.notifyDataSetChanged();
        }
    }

    public List<MenualRecyclerData> getFinalData() {
        List<MenualRecyclerData> data = new ArrayList<>();
        String[] primaryText = getResources().getStringArray(R.array.MenualPrimaryText);
        String[] secondaryText = getResources().getStringArray(R.array.MenualSecondaryText);
        int[] imageIcons = {
                R.drawable.ic_back_in_time,
                R.drawable.ic_current_location_icon,
                R.drawable.ic_home_place,
                R.drawable.ic_work_places,
                R.drawable.ic_star_border_black_24dp,
                R.drawable.ic_set_location_on_map
        };

        for (int i = 0; i < primaryText.length && i < secondaryText.length && i < imageIcons.length; i++) {
            MenualRecyclerData current = new MenualRecyclerData();
            current.primaryText = primaryText[i];
            current.secondaryText = secondaryText[i];
            current.imageSrc = imageIcons[i];
            data.add(current);
        }
        return data;
    }

}
