package com.example.geolocateapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.geolocateapp.Fragment.GeolocateFragment;
import com.example.geolocateapp.Fragment.GeolocateResultFragment;
import com.example.geolocateapp.Fragment.StartFragment;
import com.example.geolocateapp.Fragment.StartServerFragment;
import com.example.geolocateapp.Fragment.TabletFragment;
import com.example.geolocateapp.Fragment.TestOobeeSDKFragment;
import com.example.geolocateapp.OnDataPass;
import com.example.geolocateapp.PermissionHintDialog;
import com.example.geolocateapp.R;
import com.example.geolocateapp.databinding.ActivityMainBinding;
import com.geocomply.oobeelib.IOobeeCallback;
import com.geocomply.oobeelib.InvalidPortException;
import com.geocomply.oobeelib.OobeeClient;
import com.geocomply.oobeelib.sdk.GeoComplyClientException;
import com.geocomply.oobeelib.sdk.LoadingSdkException;
import com.geocomply.oobeelib.sdk.Sdk;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.ClientProtocolException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements OnDataPass {
    private ActivityMainBinding binding;
    public static String Port="";
    ArrayList<String> items = new ArrayList<String>();
    StartFragment startFragment=new StartFragment();
    StartServerFragment startServerFragment=new StartServerFragment();
    TestOobeeSDKFragment testOobeeSDKFragment=new TestOobeeSDKFragment();
    TabletFragment tabletFragment=new TabletFragment();
    GeolocateFragment geolocateFragment=new GeolocateFragment();
    public static String test="";
    public static String license="";
    private String TAG = "DEMO_OOBEE_SDK";

    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 0x300;
    //----------------------------------------------------------------------------------------------
    private String[] sdkVersions = {"2.3.0", "2.4.0", "2.5.0"};
    private final int PERMISSION_REQUEST_CODE = 0x1;
    //----------------------------------------------------------------------------------------------
    public static OobeeClient mOobeeClient;
    private ProgressDialog mProgressDialog;
    private boolean mIsPermissionsGranted = true;
    private String currentSdkVersion = sdkVersions[2];
    private boolean mIsServerStarted = false;
    private Bundle bundleData;
    private String finalData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mOobeeClient=new OobeeClient(this);
        if(isTablet(this))
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_view,tabletFragment)
                    .commit();
        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_view,startFragment)
                    .commit();
        }
    }

    @Override
    public void onDataPass(String data){
        if(data.equals("StartLocalServer"))
        {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view,startServerFragment)
                        .addToBackStack("StartServerFragment")
                        .commit();
        }
        else
        {
            if(data.equals("TestOobeeSDK"))
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view,testOobeeSDKFragment)
                        .addToBackStack("TestOobeeSDKfragment")
                        .commit();
            }
            else
            {
                if(data.contains("Generate license"))
                {
                    String[] result=data.split(":");
                    String env=result[1].trim().replace("-","");
                    String url=getStringResourceByName(env).replace("amp;","");
                    Toast.makeText(MainActivity.this,result[1]+" is selected!",Toast.LENGTH_SHORT).show();
                    test=url;

                    RequestQueue requestQueue=Volley.newRequestQueue(this);
                    StringRequest request=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            StringTokenizer tokenizer=new StringTokenizer(response,"<>");
                            ArrayList<String>resultString=new ArrayList<>();
                            while(tokenizer.hasMoreTokens())
                            {
                                resultString.add(tokenizer.nextToken());
                            }
                            license = resultString.get(3);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    requestQueue.add(request);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view,geolocateFragment)
                            .addToBackStack("geolocateFragment")
                            .commit();
                }
                else
                {
                    if(data.equals("Geolocate result"))
                    {
                        checkLocationPermissions();
                        if (mIsPermissionsGranted) {
                            String userId=bundleData.getString("UserId");
                            String phoneNumber=bundleData.getString("PhoneNumber");
                            String geolocationReason=bundleData.getString("GeolocationReason");
                            String sessionKey=bundleData.getString("SessionKey");
                            requestGeolocation(currentSdkVersion,license,userId,phoneNumber,geolocationReason,sessionKey);
                        } else {
                            Toast.makeText(MainActivity.this, "Location permissions are not granted", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        ClipboardManager clipboardManager= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData=ClipData.newPlainText("Data",finalData);
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(this,"Copied!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onBundlePass(Bundle bundle) {
        bundleData=bundle;
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }


    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getResources().getString(resId);
    }

    public InputStream getUrlData(String url) throws URISyntaxException, IOException {

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet method = new HttpGet(new URI(url));
        HttpResponse res = client.execute(method);
        return res.getEntity().getContent();
    }

    private void checkLocationPermissions() {
        mIsPermissionsGranted = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mIsPermissionsGranted = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                openDialogRequestPermissionInSettings();
            else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_CODE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mIsPermissionsGranted = false;
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void requestGeolocation(String sdkVersion, String license, String userId, String userPhoneNumber, String geolocationReason, String sessionKey) {
        try {
            Log.d(TAG,"Doing geolocation... SDK version: " + sdkVersion);
            triggerGeolocation(sdkVersion, license, userId, userPhoneNumber, geolocationReason, sessionKey);
            showProgressDialog("Doing geolocation...");
        } catch (GeoComplyClientException e) {
            cancelProgressDialog();
            Log.d(TAG,"Error: " + e.getCode() + ". Message: " + e.getMessage());
            showMessage("Error", "Error: " + e.getCode() + ". Message: " + e.getMessage());
        } catch (LoadingSdkException lse) {
            Log.d(TAG,"Error when loading SDK. Details: " + lse.getMessage());
            showMessage("Error", "Error when loading SDK. Details: " + lse.getMessage());
        }
    }

    private void openDialogRequestPermissionInSettings() {
        int message;
        String button = "";
        message = R.string.text_ask_permission_android_10;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            message = R.string.text_ask_permission_android_12;
        }
        button = getString(R.string.settings);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openSettingPermission();
                    }
                })
                .setNegativeButton(getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void openSettingPermission() {
        startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName())), SETTINGS_ACTIVITY_REQUEST_CODE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, PermissionHintDialog.class));
            }
        }, 500);
    }
    public void triggerGeolocation(String sdkVersion, String license, String userId, String userPhoneNumber, String geolocationReason, String sessionKey) throws GeoComplyClientException, LoadingSdkException {
        mOobeeClient.requestGeolocation(sdkVersion, license, userId, userPhoneNumber, geolocationReason, null, 0, new Sdk.Callbacks(new Sdk.GeoComplyClientCallback() {
            @Override
            public void onGeolocationFailed(int errorCode, String errorMessage) {
                cancelProgressDialog();
                showMessage("Error", "Error: " + errorCode + ". Message: " + errorMessage);
            }

            @Override
            public void onGeolocationAvailable(String data) {
                cancelProgressDialog();
                Log.d(TAG,"Geolocation finished successfully");
                Bundle bundle = new Bundle();
                bundle.putString("finalData", data);
                GeolocateResultFragment fragObj = new GeolocateResultFragment();
                fragObj.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view,fragObj)
                        .addToBackStack("geolocateResultFragment")
                        .commit();
                finalData=data;
            }
        }, new Sdk.GeoComplyClientLogCallback() {
            @Override
            public void onLogUpdated(String logLevel, String message) {
                Log.d("MainActivity", message);
            }
        }, new Sdk.GeoComplyClientDeviceConfigCallback() {
            @Override
            public boolean onLocationServicesDisabled(Set<Integer> set) {
                return false;
            }
        }));
    }


    //----------------------------------------------------------------------------------------------
    private void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    //----------------------------------------------------------------------------------------------
    private void cancelProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    //----------------------------------------------------------------------------------------------
    private void showMessage(String title, String message) {
        showAlert(title, message);
    }

    //----------------------------------------------------------------------------------------------


    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}