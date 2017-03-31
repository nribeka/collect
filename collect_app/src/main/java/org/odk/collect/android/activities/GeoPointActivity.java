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
import android.view.Window;

import com.google.android.gms.location.LocationServices;

import org.odk.collect.android.R;
import org.odk.collect.android.utilities.PlayServicesUtil;

public class GeoPointActivity extends GeoActivity {

    private static final String TAG = "GeoPointActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setTitle(getString(R.string.get_location));

        if (PlayServicesUtil.isGooglePlayServicesAvailable(GeoPointActivity.this)) {
            buildGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
            checkLocationSettings();
        } else {
            PlayServicesUtil.showGooglePlayServicesAvailabilityErrorDialog(GeoPointActivity.this);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        startLocationUpdates();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
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
    public void onLocationChanged(Location location) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            returnLocation();
        }
    }
}
