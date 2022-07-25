package com.example.geolocateapp.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geolocateapp.Activity.MainActivity;
import com.example.geolocateapp.OnDataPass;
import com.example.geolocateapp.R;
import com.example.geolocateapp.databinding.FragmentStartServerBinding;
import com.geocomply.oobeelib.*;
import com.geocomply.oobeelib.sdk.GeoComplyClientException;
import com.geocomply.oobeelib.sdk.LoadingSdkException;
import com.geocomply.oobeelib.sdk.Sdk;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartServerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartServerFragment extends Fragment implements View.OnClickListener,IOobeeCallback{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "DEMO_OOBEE_SDK";

    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 0x300;
    //----------------------------------------------------------------------------------------------
    private String[] sdkVersions = {"2.3.0", "2.4.0", "2.5.0"};
    private final int PERMISSION_REQUEST_CODE = 0x1;
    //----------------------------------------------------------------------------------------------
    private ProgressDialog mProgressDialog;
    private boolean mIsPermissionsGranted = true;
    private String currentSdkVersion = sdkVersions[2];
    private boolean mIsServerStarted = false;
    FragmentStartServerBinding binding;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartServerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartServerFragment newInstance(String param1, String param2) {
        StartServerFragment fragment = new StartServerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentStartServerBinding.inflate(inflater, container, false);
        binding.StartButton.setOnClickListener(this);
        if(!MainActivity.Port.equals(""))
        {
            binding.edtPort.setText(MainActivity.Port);
            binding.StartButton.setText("Stop");
            binding.StartButton.setBackgroundResource(R.drawable.stop_border);
            mIsServerStarted=true;
        }

        //registerOobeeCallback for start OobeeServer. You need start server to listen request from OobeeJS
        MainActivity.mOobeeClient.registerOobeeCallback(new IOobeeCallback() {
            @Override
            public void onInitOobeeClient(int error, String message) {
                String port = binding.edtPort.getText().toString();
                Log.d(TAG, "onInitOobeeClient [port=" + port + "]: " + error + ", " + message);
                if (error == OobeeError.SUCCESS) {
                    mIsServerStarted = true;
                    binding.StartButton.setText("Stop");
                    binding.StartButton.setBackgroundResource(R.drawable.stop_border);
                    MainActivity.Port=port;
                } else {
                    showAlert("Init Oobee Error", error + " -> " + message);
                    Log.d(TAG,"Init Oobee Error: " + error + " -> " + message);
                }
                cancelProgressDialog();
            }

            @Override
            public void onStopOobeeClient(int error, String message) {
                mIsServerStarted = false;
                binding.StartButton.setText("Start");
                binding.StartButton.setBackgroundResource(R.drawable.border);
                MainActivity.Port="";
                Log.d(TAG,"onStopOobeeClient: " + error + ", " + message);
                cancelProgressDialog();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if(binding.edtPort.getText().toString().equals(""))
        {
            Toast.makeText(getContext(),"Please fill the port!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (mIsServerStarted) {
                MainActivity.mOobeeClient.stop();
            } else {
                initOobeeClient();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    @Override
    public void onInitOobeeClient(int i, String s) {

    }

    @Override
    public void onStopOobeeClient(int i, String s) {

    }

    //----------------------------------------------------------------------------------------------
    private void initOobeeClient() {
        if (MainActivity.mOobeeClient != null) {
            try {
                int port = Integer.parseInt(binding.edtPort.getText().toString());
                MainActivity.mOobeeClient.setHttpServerPorts(new int[]{port});
                MainActivity.mOobeeClient.init();
                Log.d(TAG,"Initializing Oobee...");
                showProgressDialog("Initializing Oobee...");
            } catch (InvalidPortException e) {
                e.printStackTrace();
                Log.d(TAG,"Error when init Oobee -> " + e.getMessage());
            }
        } else {
            Log.d(TAG,"Null Oobee client");
        }
    }


    private void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
        }
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void cancelProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
}