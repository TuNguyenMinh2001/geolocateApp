package com.example.geolocateapp.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.geolocateapp.Activity.MainActivity;
import com.example.geolocateapp.OnDataPass;
import com.example.geolocateapp.R;
import com.example.geolocateapp.databinding.FragmentGeolocateBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeolocateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeolocateFragment extends Fragment implements View.OnClickListener {
    FragmentGeolocateBinding binding;
    OnDataPass dataPasser;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GeolocateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeolocateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeolocateFragment newInstance(String param1, String param2) {
        GeolocateFragment fragment = new GeolocateFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentGeolocateBinding.inflate(inflater, container, false);
        binding.geolocateButton.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPasser= (OnDataPass) context;
    }

    @Override
    public void onClick(View v) {
        if(binding.UserId.getText().toString().equals("")||binding.PhoneNumber.getText().toString().equals("")||
        binding.GeolocationReason.getText().toString().equals("")||binding.SessionKey.getText().toString().equals(""))
        {
            Toast.makeText(getContext(),"Please fill all the blanks!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Bundle bundle=new Bundle();
            bundle.putString("UserId",binding.UserId.getText().toString());
            bundle.putString("PhoneNumber",binding.PhoneNumber.getText().toString());
            bundle.putString("GeolocationReason",binding.GeolocationReason.getText().toString());
            bundle.putString("SessionKey",binding.SessionKey.getText().toString());
            dataPasser.onBundlePass(bundle);
            dataPasser.onDataPass("Geolocate result");
        }
    }
}