package com.inavisys.navisdk.androiddemo.view.map

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.inavi.maps.orbis.MapFragment
import com.inavisys.navisdk.androiddemo.R
import com.inavisys.navisdk.androiddemo.base.InvBaseActivity
import com.inavisys.navisdk.androiddemo.utils.Constant.DEFAULT_LAT
import com.inavisys.navisdk.androiddemo.utils.Constant.DEFAULT_LON


open class InvMapFragmentActivity<T: ViewDataBinding> constructor(
    @LayoutRes private val layoutResId: Int,
) : InvBaseActivity<T>(layoutResId) {

    val mapFragment = MapFragment()

    constructor() : this(
        layoutResId = R.layout.activity_inv_map_fragment
    )

    override fun init(savedInstanceState: Bundle?) {
        initUI()
    }

    private fun initUI() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()

        mapFragment.setMapCenter(
            longitude = DEFAULT_LON,
            latitude = DEFAULT_LAT,
            zoom = 15.0,
            tilt = 0.0,
            bearing = 360.0,
            animated = false,
            durationMs = 0
        )
    }
}
