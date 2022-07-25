package com.example.geolocateapp.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.geolocateapp.OnDataPass;
import com.example.geolocateapp.R;
import com.example.geolocateapp.databinding.FragmentTestOobeeSdkBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestOobeeSDKFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestOobeeSDKFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private FragmentTestOobeeSdkBinding binding;
    OnDataPass dataPasser;
    String selectedEnv="QAT-580";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TestOobeeSDKFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestOobeeSDKFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestOobeeSDKFragment newInstance(String param1, String param2) {
        TestOobeeSDKFragment fragment = new TestOobeeSDKFragment();
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
        binding = FragmentTestOobeeSdkBinding.inflate(inflater,container, false);
        binding.GenerateButton.setOnClickListener(this);
        ArrayAdapter<CharSequence>typeEnvList=ArrayAdapter.createFromResource(getContext(), R.array.TypeEnv,android.R.layout.simple_spinner_item);
        typeEnvList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.Spinner.setAdapter(typeEnvList);
        binding.Spinner.setOnItemSelectedListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        dataPasser.onDataPass("Generate license:"+selectedEnv);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected=parent.getItemAtPosition(position).toString();
        ArrayAdapter<CharSequence> envList;
        if(selected.equals("QAT"))
        {
            envList=ArrayAdapter.createFromResource(getContext(),R.array.QAT, android.R.layout.simple_spinner_item);
        }
        else
        {
            if(selected.equals("STAGING"))
            {
                envList = ArrayAdapter.createFromResource(getContext(), R.array.STAGING, android.R.layout.simple_spinner_item);
            }
            else
            {
                envList = ArrayAdapter.createFromResource(getContext(), R.array.PRODUCT, android.R.layout.simple_spinner_item);
            }
        }
        envList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.SpinnerTwo.setAdapter(envList);
        binding.SpinnerTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEnv=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPasser= (OnDataPass) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}