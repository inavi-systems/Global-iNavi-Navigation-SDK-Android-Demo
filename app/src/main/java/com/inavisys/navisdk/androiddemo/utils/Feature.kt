package com.inavisys.navisdk.androiddemo.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Feature(
    val name: String?,
    val label: String?,
    val description: String?,
    val category: String?
) : Parcelable {

    val isHeaderItem: Boolean
        get() = name.isNullOrEmpty()

    val simpleName: String?
        get() = name?.substringAfterLast(".")

    fun getFeatureLabel(): String? {
        return label ?: this.simpleName
    }

    fun getFeatureDescription(): String {
        return description ?: "-"
    }
}