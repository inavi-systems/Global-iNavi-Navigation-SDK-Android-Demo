package com.inavisys.navisdk.androiddemo.view.camera

import android.os.Bundle
import android.widget.Checkable
import com.inavisys.navisdk.androiddemo.R
import com.inavisys.navisdk.androiddemo.databinding.ActivityInvCameraMoveBinding
import com.inavisys.navisdk.androiddemo.view.map.InvMapFragmentActivity

class InvCameraMoveActivity: InvMapFragmentActivity<ActivityInvCameraMoveBinding>(R.layout.activity_inv_camera_move) {

    companion object {
        private val POSITION1 = Pair(25.0118520, 121.465444)
        private val POSITION2 = Pair(25.0292831, 121.431329)
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

        binding.rmShape.setOnClickListener {v ->
            if (v is Checkable) {
                val checked = v.isChecked
                v.isChecked = !checked

                mapFragment.setMapCenter(
                    latitude = if(checked) POSITION1.first else POSITION2.first,
                    longitude = if(checked) POSITION1.second else POSITION2.second,
                    zoom = 15.0,
                    tilt = 0.0,
                    bearing = 0.0,
                    animated = true,
                    durationMs = 2000
                )
            }
        }
    }
}