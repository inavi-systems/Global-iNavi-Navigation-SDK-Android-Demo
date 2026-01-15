package com.inavisys.navisdk.androiddemo.utils

enum class Function(val label: String, val description: String) {
    ZOOM_IN("Zoom In", "Zooms in one level from the current level"),
    ZOOM_OUT("Zoom Out", "Zooms out one level from the current level"),
    GEOCODING("Geocoding", "Calls the Geocoding API"),
    REVERSE_GEOCODING("Reverse Geocoding", "Calls the ReverseGeocoding API "),
    SEARCH("Search", "Calls the Search API"),
    RUN_GUIDANCE("Run Guidance", "Search the route and start guiding");

    companion object Companion {

        fun makeFeatureList(): List<Feature> {
            val featureList = mutableListOf<Feature>()

            featureList.add(
                Feature(
                    name = "",
                    label = "Map",
                    description = "",
                    category = ""
                )
            )

            Function.entries.forEach { naviFunction ->
                featureList.add(
                    Feature(
                        naviFunction.name,
                        naviFunction.label,
                        naviFunction.description,
                        "Map"
                    )
                )
            }

            return featureList
        }
    }
}