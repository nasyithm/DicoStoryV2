package com.nasyithm.dicostoryv2.view.story.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.nasyithm.dicostoryv2.R
import com.nasyithm.dicostoryv2.data.Result
import com.nasyithm.dicostoryv2.data.remote.response.ListStoryItem
import com.nasyithm.dicostoryv2.databinding.ActivityMapsBinding
import com.nasyithm.dicostoryv2.view.ViewModelFactory

class StoriesMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val storiesMapsViewModel: StoriesMapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val boundsBuilder = LatLngBounds.Builder()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getStoriesWithLocationData()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
    }

    private fun getStoriesWithLocationData() {
        storiesMapsViewModel.dataLoaded.observe(this) { isDataLoaded ->
            if (!isDataLoaded) {
                getStoriesWithLocation()
            } else {
                storiesMapsViewModel.storiesWithLocationData.observe(this) {
                    showLocationMarker(it)
                }
            }
        }
    }

    private fun getStoriesWithLocation() {
        storiesMapsViewModel.getStoriesWithLocation().observe(this) { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    showLocationMarker(result.data.listStory)
                    storiesMapsViewModel.setStoriesWithLocationData(result.data.listStory)
                    storiesMapsViewModel.setDataLoaded(true)
                }
                is Result.Error -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.get_data_failed))
                        setMessage(result.error)
                        setPositiveButton(getString(R.string.ok)) { _, _ -> }
                        create()
                        show()
                    }
                    storiesMapsViewModel.setDataLoaded(true)
                }
            }
        }
    }

    private fun showLocationMarker(storyList: List<ListStoryItem>) {
        storyList.forEach {
            val latLng = LatLng(it.lat!!, it.lon!!)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(it.name)
                    .snippet(it.description)
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "StoriesMapsActivity"
    }
}