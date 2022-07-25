package com.example.geolocateapp;

import android.os.Bundle;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public interface OnDataPass {
        void onDataPass(String data);
        void onBundlePass(Bundle bundle);
}
