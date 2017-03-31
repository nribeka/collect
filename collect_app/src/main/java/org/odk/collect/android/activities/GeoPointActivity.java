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
import android.widget.ProgressBar;

import com.google.android.gms.location.LocationServices;

import org.odk.collect.android.R;
import org.odk.collect.android.widgets.GeoPointWidget;

public class GeoPointActivity extends GeoActivity {

    private ProgressBar progressBar;

    private double mLocationAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle(getString(R.string.get_location));

        Intent intent = getIntent();
        mLocationAccuracy = GeoPointWidget.DEFAULT_LOCATION_ACCURACY;
        if (intent != null && intent.getExtras() != null) {
            if (intent.hasExtra(GeoPointWidget.ACCURACY_THRESHOLD)) {
                mLocationAccuracy = intent.getDoubleExtra(GeoPointWidget.ACCURACY_THRESHOLD,
                        GeoPointWidget.DEFAULT_LOCATION_ACCURACY);
            }
        }

        setContentView(R.layout.activity_geopoint);
        progressBar = (ProgressBar) findViewById(R.id.loading_location);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null
                && mCurrentLocation.hasAccuracy()
                && mCurrentLocation.getAccuracy() < mLocationAccuracy) {
            returnLocation();
        }
    }

    private void returnLocation() {
        Intent intent = new Intent();
        String builder = mCurrentLocation.getLatitude() + " " +
                mCurrentLocation.getLongitude() + " " +
                mCurrentLocation.getAltitude() + " " +
                mCurrentLocation.getAccuracy();
        intent.putExtra(FormEntryActivity.LOCATION_RESULT, builder);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onLocationUpdates() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (mCurrentLocation != null
                && mCurrentLocation.hasAccuracy()
                && mCurrentLocation.getAccuracy() < mLocationAccuracy) {
            returnLocation();
        }
    }
}
