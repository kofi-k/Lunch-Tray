package com.example.lunchtray.model

import androidx.annotation.StringRes
import com.example.lunchtray.R

enum class TrayScreens(@StringRes title: Int) {
    Start(title = R.string.app_name),
    EntreeMenu(title = R.string.choose_entree),
    SideDishMenu(title = R.string.choose_side_dish),
    AccompanimentMenu(title = R.string.choose_accompaniment),
    Checkout(title = R.string.order_checkout)
}