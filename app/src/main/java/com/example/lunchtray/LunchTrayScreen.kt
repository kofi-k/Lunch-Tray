/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.model.OrderUiState
import com.example.lunchtray.ui.*


enum class TrayScreens {
    Start, EntreeMenu, SideDishMenu, AccompanimentMenu,
    Checkout
}


@Composable
fun LunchTrayApp(modifier: Modifier = Modifier) {

    val viewModel: OrderViewModel = viewModel()
    val navController: NavHostController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val title = when (TrayScreens.valueOf(backStackEntry?.destination?.route ?: TrayScreens.Start.name)) {
        TrayScreens.EntreeMenu -> R.string.choose_entree
        TrayScreens.SideDishMenu -> R.string.choose_accompaniment
        TrayScreens.Checkout -> R.string.order_checkout
        else -> R.string.app_name
    }

    Scaffold(topBar = {
        Appbar(title = title,
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() })
    }) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = TrayScreens.Start.name,
            modifier = modifier.padding(innerPadding),
        ) {
            composable(route = TrayScreens.Start.name) {
                StartOrderScreen(modifier = Modifier, onStartOrderButtonClicked = {
                    navController.navigate(TrayScreens.EntreeMenu.name)
                })
            }
            composable(route = TrayScreens.EntreeMenu.name) {
                EntreeMenuScreen(options = DataSource.entreeMenuItems, onCancelButtonClicked = {
                    cancelOrderAndNavigateToStart(viewModel = viewModel,
                        navController = navController)
                }, onSelectionChanged = {
                    viewModel.updateEntree(it)
                }, onNextButtonClicked = {
                    navController.navigate(TrayScreens.SideDishMenu.name)
                })
            }
            composable(route = TrayScreens.SideDishMenu.name) {
                SideDishMenuScreen(options = DataSource.sideDishMenuItems, onNextButtonClicked = {
                    navController.navigate(TrayScreens.AccompanimentMenu.name)
                }, onSelectionChanged = {
                    viewModel.updateSideDish(it)
                }, onCancelButtonClicked = {
                    cancelOrderAndNavigateToPreviousScreen(navController = navController,
                        destination = TrayScreens.EntreeMenu)
                })
            }
            composable(route = TrayScreens.AccompanimentMenu.name) {
                AccompanimentMenuScreen(options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToPreviousScreen(navController = navController,
                            destination = TrayScreens.SideDishMenu)
                    },
                    onSelectionChanged = {
                        viewModel.updateAccompaniment(it)
                    },
                    onNextButtonClicked = {
                        navController.navigate(TrayScreens.Checkout.name)
                    })
            }
            composable(route = TrayScreens.Checkout.name) {
                CheckoutScreen(orderUiState = OrderUiState(), onNextButtonClicked = {
                    navController.navigate(TrayScreens.Start.name)
                }, onCancelButtonClicked = {
                    cancelOrderAndNavigateToPreviousScreen(navController = navController,
                        destination = TrayScreens.AccompanimentMenu)
                })
            }
        }
    }
}

private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController,
) {
    viewModel.resetOrder()
    navController.popBackStack(TrayScreens.Start.name, inclusive = false)
}

private fun cancelOrderAndNavigateToPreviousScreen(
    navController: NavHostController,
    destination: TrayScreens,
) = navController.popBackStack(destination.name, inclusive = false)

@Composable
fun Appbar(
    @StringRes title: Int,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(title = {
        Text(text = stringResource(id = title))
    }, modifier = modifier, navigationIcon = {
        if (canNavigateBack) {
            IconButton(onClick = navigateUp) {
                Icon(imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button))
            }
        }
    })
}