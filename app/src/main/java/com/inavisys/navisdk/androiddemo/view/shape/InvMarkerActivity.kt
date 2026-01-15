package com.inavisys.navisdk.androiddemo.view.shape


import android.os.Bundle
import android.widget.Checkable
import com.inavisys.navisdk.androiddemo.R
import com.inavisys.navisdk.androiddemo.databinding.ActivityInvShapeMarkerBinding
import com.inavisys.navisdk.androiddemo.view.map.InvMapFragmentActivity
import com.inavisys.navisdk.model.engine.map.ImageSource
import com.inavisys.navisdk.model.engine.map.MapMarkerOptions
import com.inavisys.navisdk.model.location.LocationInfo

class InvMarkerActivity: InvMapFragmentActivity<ActivityInvShapeMarkerBinding>(R.layout.activity_inv_shape_marker){

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

        val inventoryPoints = listOf(
            LocationInfo.Point(121.465444, 25.0118520),
            LocationInfo.Point(121.466444, 25.0139520),
            LocationInfo.Point(121.467444, 25.0117520),
            LocationInfo.Point(121.463444, 25.0126520)
        ).map { point ->
            MapMarkerOptions(
                point = point,
                imageSource = ImageSource.ResourceId(R.drawable.inv_map_pin)
            )
        }

        inventoryPoints.forEach {
            mapFragment.addMarker(it)
        }

        binding.rmShape.setOnClickListener { v ->
            if (v is Checkable) {
                val checked = v.isChecked
                v.isChecked = !checked

                inventoryPoints.forEach { marker ->
                    when (checked) {
                        true -> mapFragment.addMarker(marker)
                        else -> mapFragment.removeMarker(marker.identifier)
                    }
                }
            }
        }
    }
}