package com.inavisys.navisdk.androiddemo.view

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inavi.maps.orbis.InvMapFragment
import com.inavisys.navisdk.androiddemo.R
import com.inavisys.navisdk.androiddemo.adapter.FeatureAdapter
import com.inavisys.navisdk.androiddemo.base.BaseActivity
import com.inavisys.navisdk.androiddemo.databinding.ActivityMainBinding
import com.inavisys.navisdk.androiddemo.utils.Constant.DEFAULT_LAT
import com.inavisys.navisdk.androiddemo.utils.Constant.DEFAULT_LON
import com.inavisys.navisdk.androiddemo.utils.Constant.TAG
import com.inavisys.navisdk.androiddemo.utils.Feature
import com.inavisys.navisdk.androiddemo.utils.ItemClickSupport
import com.inavisys.navisdk.androiddemo.utils.Function
import com.inavisys.navisdk.core.NaviController
import com.inavisys.navisdk.core.controller.OnGeocodingListener
import com.inavisys.navisdk.core.controller.OnNaviInitializeListener
import com.inavisys.navisdk.core.controller.OnOrbisMapModeListener
import com.inavisys.navisdk.core.controller.OnOrbisRouteListener
import com.inavisys.navisdk.core.controller.OnReverseGeocodingListener
import com.inavisys.navisdk.core.controller.OnSearchListener
import com.inavisys.navisdk.model.location.LocationInfo
import com.inavisys.navisdk.model.network.search.ReqSearch
import com.inavisys.navisdk.model.search.InvGeocodeResponse
import com.inavisys.navisdk.model.search.InvReverseGeocodeResponse
import com.inavisys.navisdk.ui.NaviUIController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main), ItemClickSupport.OnItemClickListener {

    private val features = mutableListOf<Feature?>()

    override fun init(savedInstanceState: Bundle?) {
        initSDK()
        initUI()
        loadFeatures()
        ItemClickSupport.addTo(binding.mainActRc).setOnItemClickListener(this)
    }

    override fun onItemClicked(recyclerView: RecyclerView?, position: Int, view: View?) {
        val feature = features.getOrNull(position) ?: return

        if (feature.name?.isEmpty() == true)
            return

        val currentCategory = feature.category
        val navigationCategory = getString(R.string.inv_category_map)

        if (currentCategory == navigationCategory) {

            when (feature.name) {
                Function.ZOOM_IN.name -> {
                    NaviController.zoomIn()
                    binding.mainActRc.visibility = View.GONE
                }
                Function.ZOOM_OUT.name -> {
                    NaviController.zoomOut()
                    binding.mainActRc.visibility = View.GONE
                }
                Function.GEOCODING.name -> {
                    NaviController.requestGeocoding(
                        address = "Taipei City Hall",
                        onGeoCodingListener = object : OnGeocodingListener {
                            override fun onSuccess(geocodeResponse: InvGeocodeResponse) {
                                Log.i(TAG, "Geocoding API Success: $geocodeResponse")
                            }

                            override fun onFail(errorCode: Int, errorMsg: String) {
                                Log.i(TAG, "Geocoding API Fail, errorCode: $errorCode, Msg: $errorMsg")
                            }
                        }
                    )
                }
                Function.REVERSE_GEOCODING.name -> {
                    NaviController.requestReverseGeocoding(
                        lat = DEFAULT_LAT.toFloat(),
                        lon = DEFAULT_LON.toFloat(),
                        onReverseGeocodingListener = object: OnReverseGeocodingListener {
                            override fun onSuccess(reverseGeocodeResponse: InvReverseGeocodeResponse) {
                                Log.i(TAG, "Reverse Geocoding API Success: $reverseGeocodeResponse")
                            }

                            override fun onFail(errorCode: Int, errorMsg: String) {
                                Log.i(TAG, "Reverse Geocoding API Fail, errorCode: $errorCode, msg: $errorMsg")
                            }
                        }
                    )
                }
                Function.SEARCH.name -> {
                    val currentPosition = NaviController.getCurrentPosition().coordinate

                    NaviController.runSearch(
                        req = ReqSearch(
                            query = "Taipei City",
                            coordinate = currentPosition
                        ),
                        listener = object : OnSearchListener {
                            override fun onSuccess(response: List<LocationInfo.SearchItem>) {
                                response.forEach { item ->
                                    Log.i(TAG, "Search API Success, Address, Title: ${item.mainTitle} AddrRoad: ${item.addrRoad}")
                                }
                            }

                            override fun onFail() {
                                Log.i(TAG, "Search API Fail")
                            }
                        }
                    )
                }

                Function.RUN_GUIDANCE.name -> {
                    binding.loadingOverlay.visibility = View.VISIBLE

                    val startPosition = LocationInfo.fromNaviLocationToRoutePtItem(
                        NaviController.getCurrentPosition(), "Start"
                    )

                    val destination = LocationInfo.RoutePtItem(
                        name = "Destination",
                        address = "Destination Address",
                        wgsLat = 25.004832,
                        wgsLon = 121.206882,
                        centerLat = 25.004832,
                        centerLon = 121.206882
                    )

                    NaviController.runRoute(
                        start = startPosition,
                        end = destination,
                        via = null,
                        listener = object : OnOrbisRouteListener {
                            override fun onSuccess(listID: List<String?>) {
                                listID[0]?.let {
                                    val goalSearchItem = LocationInfo.fromNaviLocationToSearchItem(
                                        naviLocation = LocationInfo.NaviLocation(destination.coordinate.wgsLon, destination.coordinate.wgsLat),
                                        mainTitle = "",
                                        addrJibun = "",
                                        addrRoad = "",
                                        distance = 0
                                    )
                                    NaviController.runGuidance(goalSearchItem, it)
                                }
                                binding.mainActRc.visibility = View.GONE
                                binding.loadingOverlay.visibility = View.GONE
                            }

                            override fun onFail(errorCode: Int, errorMsg: String) {
                                Log.i(TAG, "Route API Fail, errorCode: $errorCode, msg: $errorMsg")
                                binding.loadingOverlay.visibility = View.GONE
                            }
                        }
                    )
                }
            }

            return
        }

        startFeature(feature)
    }

    private fun initSDK() {
        binding.loadingOverlay.visibility = View.VISIBLE

        NaviController.initalizeNavi(this@MainActivity, object : OnNaviInitializeListener {
            override fun onMapReady(invMapFragment: Any?) {
                val mapFragment = invMapFragment as? InvMapFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.map_container, mapFragment ?: return)
                    .commit()
            }

            override fun onInitSuccess() {
                NaviUIController.orbisInitLayout(this@MainActivity, binding.orbisMapLayer.id)
                binding.loadingOverlay.visibility = View.GONE
            }

            override fun onInitFail(errorCode: Int, errorMsg: String) {
                binding.loadingOverlay.visibility = View.GONE
            }
        })

        NaviController.setMapModeListener(object : OnOrbisMapModeListener {
            override fun onNormal() { binding.searchFragmentContainer.visibility = View.VISIBLE }
            override fun onGuide() { binding.searchFragmentContainer.visibility = View.GONE }
            override fun onMapMove() {}
            override fun onSimuleGuide() { binding.searchFragmentContainer.visibility = View.GONE }
            override fun onSimlMapMove() {}
        })
    }

    private fun initUI() {
        with(binding.mainActRc) {
            this.layoutManager = LinearLayoutManager(this@MainActivity)
            this.addOnItemTouchListener(RecyclerView.SimpleOnItemTouchListener())
            this.setHasFixedSize(true)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.mainActRc.isVisible = !binding.mainActRc.isVisible
            }
        })
    }

    private fun loadFeatures() = CoroutineScope(Dispatchers.Default).launch {
        features.clear()

        val app: PackageInfo = kotlin.runCatching {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES or PackageManager.GET_META_DATA)
        }.getOrElse {
            it.printStackTrace()
            return@launch
        }

        val packageName = applicationContext.packageName
        val metaDataKey = getString(R.string.inv_category)

        app.activities?.forEach { info ->
            if (info.labelRes != 0 && info.name.startsWith(packageName) && !info.name.equals(MainActivity::class.java.name)) {
                val label = getString(info.labelRes)
                val description = resolveString(info.descriptionRes)
                val category = resolveMetaData(info.metaData, metaDataKey)

                features.add(
                    Feature(
                        name = info.name,
                        label = label,
                        description = description,
                        category = category,
                    )
                )
            }
        }

        onFeaturesLoaded()
    }

    private fun onFeaturesLoaded() {
        if (features.isEmpty()) {
            return
        }

        val newFeatures = features.toMutableList()
        val categorySize = features.mapNotNull { it?.category }.distinct().size

        var currentCategory = ""
        var addSize = 0

        for (i in 0 until (newFeatures.size + categorySize)) {
            val feature = features[i - addSize]
            val category = feature?.category ?: continue

            if (currentCategory != category) {
                addSize += 1
                newFeatures.add(i, Feature("", category, "", ""))
                currentCategory = category
            }
        }

        features.clear()
        features.addAll(Function.makeFeatureList())
        features.addAll(newFeatures)

        binding.mainActRc.setAdapter(FeatureAdapter(features.filterNotNull().toList()))
    }

    private fun resolveString(@StringRes stringRes: Int): String = kotlin.runCatching {
        getString(stringRes)
    }.getOrDefault("-")

    private fun resolveMetaData(
        bundle: Bundle,
        key: String,
    ): String? {
        return bundle.getString(key)
    }

    private fun startFeature(feature: Feature) {
        val intent = Intent().apply {
            component = ComponentName(packageName, feature.name ?: "")
        }
        startActivity(intent)
    }
}
