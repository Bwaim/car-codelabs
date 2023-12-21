package com.example.places.carappservice.screen

import android.graphics.Color
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.places.carappservice.R
import com.example.places.data.PlacesRepository
import com.example.places.data.model.toIntent

class DetailScreen(carContext: CarContext, private val placeId: Int): Screen(carContext) {
    private var isFavorite = false
    override fun onGetTemplate(): Template {

        val place = PlacesRepository().getPlace(placeId)
            ?: return MessageTemplate.Builder("Place not found")
                .setHeaderAction(Action.BACK)
                .build()

        val navigateAction = Action.Builder()
            .setTitle("Navigate")
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext,
                        R.drawable.baseline_navigation_24
                    )
                ).build()
            )
        // Only certain intent actions are supported by `startCarApp`. Check its documentation
        // for all of the details. To open another app that can handle navigating to a location
        // you must use the CarContext.ACTION_NAVIGATE action and not Intent.ACTION_VIEW like
        // you might on a phone.
            .setOnClickListener { carContext.startCarApp(place.toIntent(CarContext.ACTION_NAVIGATE)) }
            .build()

        val actionStrip = ActionStrip.Builder()
            .addAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.baseline_favorite_24
                            )
                        ).setTint(
                            if (isFavorite) CarColor.RED else CarColor.createCustom(
                                Color.LTGRAY,
                                Color.DKGRAY
                            )
                        ).build()
                    )
                    .setOnClickListener {
                        isFavorite = !isFavorite
                        // Request that `onGetTemplate` be called again so that updates to the
                        // screen's state can be picked up
                        invalidate()
                    }.build()
            ).build()

        return PaneTemplate.Builder(
            Pane.Builder()
                .addAction(navigateAction)
                .addRow(
                    Row.Builder()
                        .setTitle("Coordinates")
                        .addText("${place.latitude}, ${place.longitude}")
                        .build()
                )
                .addRow(
                    Row.Builder()
                        .setTitle("Desciption")
                        .addText(place.description)
                        .build()
                ).build()
        )
            .setTitle(place.name)
            .setHeaderAction(Action.BACK)
            .setActionStrip(actionStrip)
            .build()
    }
}
