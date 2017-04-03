/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;

import org.odk.collect.android.R;
import org.odk.collect.android.widgets.GeoPointWidget;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GeoPointActivity extends GeoActivity {

    private TextView descTextView;
    private TextView hintTextView;
    private TextView accuracyTextView;

    private ProgressBar progressBar;

    private Button acceptLocationButton;

    private double targetAccuracy;
    private NumberFormat formatter = new DecimalFormat("#0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle(getString(R.string.get_location));

        Intent intent = getIntent();
        targetAccuracy = GeoPointWidget.DEFAULT_LOCATION_ACCURACY;
        if (intent != null && intent.getExtras() != null) {
            if (intent.hasExtra(GeoPointWidget.ACCURACY_THRESHOLD)) {
                targetAccuracy = intent.getDoubleExtra(GeoPointWidget.ACCURACY_THRESHOLD,
                        GeoPointWidget.DEFAULT_LOCATION_ACCURACY);
            }
        }

        setContentView(R.layout.activity_geopoint);
        progressBar = (ProgressBar) findViewById(R.id.loading_location);

        hintTextView = (TextView) findViewById(R.id.loading_location_hint);
        descTextView = (TextView) findViewById(R.id.loading_location_desc);
        accuracyTextView = (TextView) findViewById(R.id.loading_location_accuracy);

        acceptLocationButton = (Button) findViewById(R.id.loading_location_accept);
        acceptLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnCurrentLocation();
            }
        });
    }

    @Override
    protected void onLocationUpdatesStarted() {
        progressBar.setVisibility(View.VISIBLE);
        descTextView.setVisibility(View.VISIBLE);
        hintTextView.setVisibility(View.VISIBLE);
        accuracyTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            updateLoadingHints();
            acceptLocationButton.setEnabled(true);
            if (mCurrentLocation.hasAccuracy() && mCurrentLocation.getAccuracy() < targetAccuracy) {
                returnCurrentLocation();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (mCurrentLocation != null) {
            updateLoadingHints();
            acceptLocationButton.setEnabled(true);
            if (mCurrentLocation.hasAccuracy() && mCurrentLocation.getAccuracy() < targetAccuracy) {
                returnCurrentLocation();
            }
        }
    }

    private void updateLoadingHints() {
        accuracyTextView.setText(getString(R.string.loading_location_accuracy,
                formatter.format(mCurrentLocation.getAccuracy()),
                formatter.format(targetAccuracy)));
    }

    private void returnCurrentLocation() {
        Intent intent = new Intent();
        String location = mCurrentLocation.getLatitude() + " " +
                mCurrentLocation.getLongitude() + " " +
                mCurrentLocation.getAltitude() + " " +
                mCurrentLocation.getAccuracy();
        intent.putExtra(FormEntryActivity.LOCATION_RESULT, location);
        setResult(RESULT_OK, intent);
        finish();
    }
}
