package com.fgtit.finger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.data.UserItem;
import com.fgtit.network.ApiHelper;
import com.fgtit.network.Response;
import com.fgtit.service.PostMaster;
import com.fgtit.utils.Threading;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.fgtit.app.ActivityList;
import com.fgtit.app.UpdateApp;
import com.fgtit.data.GlobalData;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.utils.ExtApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import android_serialport_api.SerialPort;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener, LocationListener {

    private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    private static final int RE_WORK0 = 0;
    private static final int RE_WORK1 = 1;
    private static final int RE_WORK2 = 2;

    private String btAddress = "";

    private Menu mainMenu;
    private TextView txtView;
    private Button btn01, btn02, btn03;
    private long exitTime = 0;
    private WakeLock wakeLock;

    private Timer startTimer;
    private TimerTask startTask;
    Handler startHandler;

    private ProgressDialog progressDialog;

    private SoundPool soundPool;
    private int soundIda, soundIdb;
    private boolean soundflag = false;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    // 1
    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateState;
    // 2
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;


    @SuppressLint({"NewApi", "SetJavaScriptEnabled", "InvalidWakeLockTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        startService(new Intent(this, PostMaster.class));

        setContentView(R.layout.activity_main);

        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActivityList.getInstance().IsUseNFC = ExtApi.IsSupportNFC(this);
        SerialPort sp = new SerialPort();
        if (sp.getmodel().equals("b82")) {
            ActivityList.getInstance().IsPad = true;
        } else {
            ActivityList.getInstance().IsPad = false;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        txtView = (TextView) findViewById(R.id.textView1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gmapView);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();


        btn01 = (Button) findViewById(R.id.button1);
        btn01.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignOnActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, RE_WORK1);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        btn02 = (Button) findViewById(R.id.button2);
        btn02.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignOffActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, RE_WORK2);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "sc");
        wakeLock.acquire();

        ActivityList.getInstance().setMainContext(this);
        ActivityList.getInstance().LoadConfig();

        GlobalData.getInstance().SetContext(this);
        GlobalData.getInstance().CreateDir();
        GlobalData.getInstance().LoadFileList();
        GlobalData.getInstance().LoadConfig();
        GlobalData.getInstance().LoadWorkList();
        GlobalData.getInstance().LoadLineList();
        //GlobalData.getInstance().LoadDeptList();
        getUserRecordFromDatabaseAndSyncThem();

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundIda = soundPool.load(this, R.raw.start, 1);
        soundIdb = soundPool.load(this, R.raw.stop, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundflag = true;
            }
        });


        //FPMatch.getInstance().InitMatch(1, "http://www.hfteco.com/ ");
        if (FPMatch.getInstance().InitMatch() == 0) {
            Toast.makeText(getApplicationContext(), "Init Matcher Fail!", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getApplicationContext(), "Init Matcher OK!", Toast.LENGTH_SHORT).show();
        }

        UpdateApp.getInstance().setAppContext(this);
//        LoadUserListThread();

        setFpIoState(true);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
        switch (densityDpi) {
            case 210: {    //7 inch
                LinearLayout mapLayout = (LinearLayout) findViewById(R.id.mapLayout);
                mapLayout.getLayoutParams().height = 920;
            }
            break;
            case 240: {    //5 inch-
            }
            break;
        }
    }

    private void getUserRecordFromDatabaseAndSyncThem() {
        loadUsersAsync();

    }

    private void syncUsers() {
        List<UserItem> userItems = GlobalData.getInstance().userList;
        int i = 0;
        if (userItems != null && userItems.size() != 0) {
            for (final UserItem userItem : userItems) {
                userMap.put(userItem, userItems.get(i));
                i++;
                if (userItem.isSyncWithBackend == false)
                    Threading.async(ApiHelper.getEnrollObservable(userItem).map(new Function<Response, Response>() {
                        @Override
                        public Response apply(Response response) throws Exception {
                            userMap.get(userItem).isSyncWithBackend = true;
                            GlobalData.getInstance().SaveUsersList();
                            Log.d("", "");
                            return response;
                        }
                    }), new Consumer<Response>() {
                        @Override
                        public void accept(Response response) throws Exception {
                            Log.d("", "");
                        }
                    });
            }
        }
    }

    Map<UserItem, UserItem> userMap = new HashMap<UserItem, UserItem>();

    private void loadUsersAsync() {
        Threading.async(new Callable<List<UserItem>>() {
            @Override
            public List<UserItem> call() throws Exception {
                GlobalData.getInstance().LoadUsersList();
                if (GlobalData.getInstance().userList == null)
                    return new ArrayList<UserItem>();
                else
                    return GlobalData.getInstance().userList;
            }
        }, new Consumer<List<UserItem>>() {
            @Override
            public void accept(List<UserItem> userItems) throws Exception {
                syncUsers();
            }
        });
    }

    private void setFpIoState(boolean isOn) {
        int state = 0;
        if (isOn) {
            state = 1;
        } else {
            state = 0;
        }
        Intent i = new Intent("ismart.intent.action.fingerPrint_control");
        i.putExtra("state", state);
        sendBroadcast(i);
    }

    @SuppressLint("HandlerLeak")
    public Handler mLoadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        //progressDialog.setTitle("Please Wait");
                        progressDialog.setMessage("Please Wait,Load Count : " + String.valueOf(msg.arg1) + " ...");
                        //progressDialog.setIcon(android.R.drawable.btn_star);
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "User Count:" + String.valueOf(GlobalData.getInstance().userList.size()), Toast.LENGTH_SHORT).show();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Please Wait,Load Count : " + String.valueOf(msg.arg1) + " ...", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    void LoadUserListThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadUsers();
            }
        });
        thread.start();
    }

    private void loadUsers() {
        int count = GlobalData.getInstance().GetUsersCount();
        if (count > 20000) {
            Message message = new Message();
            message.what = 1;
            message.arg1 = count;
            mLoadHandler.sendMessage(message);

            GlobalData.getInstance().LoadUsersList();

            message.what = 2;
            mLoadHandler.sendMessage(message);
        } else {
            Message message = new Message();
            message.what = 3;
            message.arg1 = count;
            mLoadHandler.sendMessage(message);
            GlobalData.getInstance().LoadUsersList();
            Log.d("","");
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        if (mLocationUpdateState) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        TimerStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 3
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeStop();

        wakeLock.release();
        soundPool.release();
        soundPool = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
        if (mGoogleApiClient.isConnected() && !mLocationUpdateState) {
            startLocationUpdates();
        }
    }


    private String getAddress(LatLng latLng) {
        // 1
        Geocoder geocoder = new Geocoder(this);
        String addressText = "";
        List<Address> addresses = null;
        Address address = null;
        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressText += (i == 0) ? address.getAddressLine(i) : ("\n" + address.getAddressLine(i));
                }
            }
        } catch (IOException e) {
        }
        return addressText;
    }


    protected void placeMarkerOnMap(LatLng location) {
        MarkerOptions markerOptions = new MarkerOptions().position(location);

        String titleStr = getAddress(location);  // add these two lines
        markerOptions.title(titleStr);

        mMap.addMarker(markerOptions);
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());
                //add pin at user's location
                placeMarkerOnMap(currentLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));

                StringBuffer sb = new StringBuffer(256);

                sb.append("Latitude: ");
                sb.append(mLastLocation.getLatitude());
                sb.append("  Longitude: ");
                sb.append(mLastLocation.getLongitude());

                txtView.setText(sb.toString());

                GlobalData.getInstance().glat = mLastLocation.getLatitude();
                GlobalData.getInstance().glng = mLastLocation.getLongitude();
                ActivityList.getInstance().MapLat = mLastLocation.getLatitude();
                ActivityList.getInstance().MapLng = mLastLocation.getLongitude();
                GetLocationInfo(mLastLocation);
            }
        }
    }

    public void TimerStart() {
        startTimer = new Timer();
        startHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                //mLocationClient.requestLocation();
                super.handleMessage(msg);
            }
        };
        startTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                startHandler.sendMessage(message);
            }
        };
        startTimer.schedule(startTask, 15000, 15000);
    }

    public void TimeStop() {
        if (startTimer != null) {
            startTimer.cancel();
            startTimer = null;
            startTask.cancel();
            startTask = null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            exitApplication();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exitApplication() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), getString(R.string.txt_exitinfo), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {

            ActivityList.getInstance().SetConfigByVal("MapLat", String.valueOf(ActivityList.getInstance().MapLat));
            ActivityList.getInstance().SetConfigByVal("MapLng", String.valueOf(ActivityList.getInstance().MapLng));

            finish();
            System.exit(0);
            //AppList.getInstance().exit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mainMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                exitApplication();
                return true;
            case R.id.action_refresh:
                return true;
            case R.id.action_manage: {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                //btReader.SetMessageHandler(btHandler);
                //Intent serverIntent = new Intent(this, DeviceListActivity.class);
                //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                mLocationUpdateState = true;
                startLocationUpdates();
            }
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
    }


    private void GetLocationInfo(Location location) {
        if (location != null) {
            GlobalData.getInstance().glocal = true;
            GlobalData.getInstance().glat = location.getLatitude();
            GlobalData.getInstance().glng = location.getLongitude();
            //SetMapCenter(location.getLatitude(),location.getLongitude());
            //SetMapMaker(location.getLatitude(),location.getLongitude());
            txtView.setText("Net Location :  " + String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
        } else {
            GlobalData.getInstance().glocal = false;
            txtView.setText("No location found");
        }

    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // 2
        mLocationRequest.setInterval(10000);
        // 3
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    // 4
                    case LocationSettingsStatusCodes.SUCCESS:
                        mLocationUpdateState = true;
                        startLocationUpdates();
                        break;
                    // 5
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    // 6
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


}
