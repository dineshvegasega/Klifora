package com.klifora.shop.ui.main.nearestFranchise

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.klifora.shop.R
import com.klifora.shop.databinding.MapInfoWindowBinding
import com.klifora.shop.databinding.NearestFranchiseBinding
import com.klifora.shop.models.ItemFranchise
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.utils.PermissionUtils
import com.klifora.shop.utils.callPermissionDialog
import com.klifora.shop.utils.callPermissionDialogGPS
import com.klifora.shop.utils.getLocationFromAddress
import com.klifora.shop.utils.isLocationEnabled
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NearestFranchise : Fragment(), OnMapReadyCallback {
    private val viewModel: NearestFranchiseVM by viewModels()
    private var _binding: NearestFranchiseBinding? = null
    private val binding get() = _binding!!
    protected var map: GoogleMap? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    lateinit var fusedLocation: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NearestFranchiseBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()!!.callBack(0)
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.apply {
            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }
            topBarBack.ivCartLayout.visibility = View.GONE
            topBarBack.ivCart.singleClick {
                findNavController().navigate(R.id.action_nearestFranchise_to_cart)
            }
        }
    }



    var array = ArrayList<ItemFranchise>()

    private fun setUpMapIfNeeded() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        val callB = this
        mapFragment?.getMapAsync(this)

        viewModel.franchiseList(){
            val fList = this
//            array = fList
            fList.forEach {
                val addr = (it.d_address+","+it.d_city+","+it.d_state+","+it.d_pincode).getLocationFromAddress()
                it.apply {
                    this.latLng = addr
                }
                array.add(it)
            }

            mainThread {
                mapFragment?.getMapAsync(callB)
            }
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    override fun onMapReady(p0: GoogleMap) {
        map = p0
        Log.e("onMapReady", "onMapReady")

        map?.mapType = GoogleMap.MAP_TYPE_NORMAL
        map?.uiSettings?.isZoomControlsEnabled = true

        array.forEach {
            val markerOptions = MarkerOptions().position(it.latLng)
                .title(it.name)
                .snippet(Gson().toJson(it))
                .icon(
                    BitmapDescriptorFactory
                        .fromResource(R.drawable.marker)
                )

            map?.addMarker(markerOptions)?.hideInfoWindow()
        }

        map?.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker -> // on marker click we are getting the title of our marker
            binding.apply {
                val data = Gson().fromJson(marker.snippet, ItemFranchise::class.java)
                textTitle.text = data.name
                textAddr.text = data.register_address
                textPincode.text = "Pincode - " + data.d_pincode
                textContact.text = "Contact - " + data.mobile_number
                if(mapinfo.isVisible == true){
                    mapinfo.visibility = View.GONE
                } else{
                    mapinfo.visibility = View.VISIBLE
                }
            }

            marker.hideInfoWindow();
            true
        })

    }


    private fun callMediaPermissionsWithLocation() {
        try {
            if (isLocationEnabled(requireActivity()) == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    Log.e("TAG", "AAAAAAAAAAA")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Log.e("TAG", "BBBBBBBBB")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    Log.e("TAG", "CCCCCCCCC")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            } else {
                requireActivity().callPermissionDialogGPS {
                    someActivityResultLauncherWithLocationGPS.launch(this)
                }
            }
        } catch (e : Exception){

        }

    }


    @SuppressLint("MissingPermission")
    private val activityResultLauncherWithLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            try {
                if (!permissions.entries.toString().contains("false")) {
                    setUpMapIfNeeded()
                    setUpLocationListener()
                } else {
                    requireActivity().callPermissionDialog {
                        someActivityResultLauncherWithLocation.launch(this)
                    }
                }
            } catch (e : Exception){

            }
        }


    var someActivityResultLauncherWithLocation = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            callMediaPermissionsWithLocation()
        } catch (e : Exception){

        }
    }

    var someActivityResultLauncherWithLocationGPS = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            Log.e("TAG", "result.resultCode "+result.resultCode)
            callMediaPermissionsWithLocation()
        } catch (e : Exception){

        }
    }


    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
            if (location == null){
//                val latLong = LatLng(location!!.latitude, location.longitude)
                setUpLocationListener()
                return@addOnSuccessListener
            }

            map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
            map?.animateCamera(CameraUpdateFactory.zoomTo(5f))
            map?.isMyLocationEnabled = true
        }

//        ActivityCompat.requestPermissions(
//            requireActivity(), arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ), 100
//        )
//        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
//        fusedLocation?.let {
//            if (ActivityCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return
//            }
//
//            it.lastLocation.addOnSuccessListener { location: Location? ->
//                Log.e("TAG", "onViewCreated: " + location.toString())
//
//                if (location == null){
//                    setUpLocationListener()
//                    return@addOnSuccessListener
//                }
//
//                map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
//                map?.animateCamera(CameraUpdateFactory.zoomTo(5f))
//                map?.isMyLocationEnabled = true
//            }
//
//        }



    }

    override fun onStart() {
        super.onStart()

        callMediaPermissionsWithLocation()


//        when {
//            PermissionUtils.isAccessFineLocationGranted(requireContext()) -> {
//                when {
//                    PermissionUtils.isLocationEnabled(requireContext()) -> {
//                        setUpMapIfNeeded()
//                        setUpLocationListener()
//                    }
//
//                    else -> {
//                        PermissionUtils.showGPSNotEnabledDialog(requireContext())
//                    }
//                }
//            }
//
//            else -> {
//                PermissionUtils.requestAccessFineLocationPermission(
//                    MainActivity.mainActivity.get()!!,
//                    LOCATION_PERMISSION_REQUEST_CODE
//                )
//            }
//        }
    }

}