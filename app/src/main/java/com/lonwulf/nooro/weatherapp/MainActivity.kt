package com.lonwulf.nooro.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lonwulf.nooro.weatherapp.core.util.NetworkMonitor
import com.lonwulf.nooro.weatherapp.navigation.Destinations
import com.lonwulf.nooro.weatherapp.navigation.NavGraph
import com.lonwulf.nooro.weatherapp.navigation.TopLevelDestinations
import com.lonwulf.nooro.weatherapp.ui.screens.HistoryScreenComposable
import com.lonwulf.nooro.weatherapp.ui.screens.HomeScreenComposable
import com.lonwulf.nooro.weatherapp.ui.screens.SearchScreenComposable
import com.lonwulf.nooro.weatherapp.ui.screens.SettingsScreenComposable
import com.lonwulf.nooro.weatherapp.ui.theme.BottomBarBgGray
import com.lonwulf.nooro.weatherapp.ui.theme.BottomBarContentColor
import com.lonwulf.nooro.weatherapp.ui.theme.BottomBarSelectedColor
import com.lonwulf.nooro.weatherapp.ui.theme.ErrorRed
import com.lonwulf.nooro.weatherapp.ui.theme.WeatherAppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val networkMonitor: NetworkMonitor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()
                val navStackBackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navStackBackEntry?.destination
                val snackBarHostState = remember { SnackbarHostState() }
                val isConnected by networkMonitor.networkChangeEvent.collectAsState()

                val hideAppBarInScreens = listOf(
                    Destinations.SearchScreen.route
                )
                val showBottomBar = currentDestination?.route !in hideAppBarInScreens

                Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }, topBar = {
                    Column {
                        val title =
                            if (currentDestination?.route == TopLevelDestinations.HomeScreen.route) "Nooro Weather App" else currentDestination?.route
                                ?: ""
                        Toolbar(
                            title = title,
                            navHostController = navController,
                            currentDestination = currentDestination
                        )
                        if (isConnected.not()) {
                            ConnectivityStrip()
                        }
                    }

                }, bottomBar = {
                    if (showBottomBar) {
                        BottomNavigation(
                            navHostController = navController,
                            currentDestination = currentDestination
                        )
                    }
                }) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val composables = mapOf(
                            Destinations.SearchScreen.route to SearchScreenComposable(),
                            TopLevelDestinations.HomeScreen.route to HomeScreenComposable(),
                            TopLevelDestinations.HistoryScreen.route to HistoryScreenComposable(),
                            TopLevelDestinations.SettingsScreen.route to SettingsScreenComposable(),
                        )
                        NavGraph(
                            navHostController = navController,
                            composables = composables,
                            snackbarHostState = snackBarHostState
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        networkMonitor.unregisterNetworkCallback()
        super.onStop()
    }

    @Composable
    private fun ConnectivityStrip(modifier: Modifier = Modifier) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val (txt, box) = createRefs()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(colorResource(R.color.white))
                    .constrainAs(box) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    }
            )
            Text(
                text = stringResource(R.string.no_internet),
                color = ErrorRed,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.constrainAs(txt) {
                    top.linkTo(box.top)
                    bottom.linkTo(box.bottom)
                    start.linkTo(box.start)
                    end.linkTo(box.end)
                }
            )
        }
    }

    @Composable
    private fun BottomNavigation(
        navHostController: NavHostController,
        currentDestination: NavDestination?
    ) {
        val screens = listOf(
            TopLevelDestinations.HomeScreen,
            TopLevelDestinations.HistoryScreen,
            TopLevelDestinations.SettingsScreen
        )
        NavigationBar(
            containerColor = BottomBarBgGray,
            contentColor = BottomBarContentColor,
            tonalElevation = 5.dp
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navHostController = navHostController
                )
            }
        }
    }

    @Composable
    private fun RowScope.AddItem(
        screen: TopLevelDestinations,
        currentDestination: NavDestination?,
        navHostController: NavHostController
    ) {
        NavigationBarItem(
            label = { Text(text = screen.title) },
            colors = NavigationBarItemColors(
                selectedIconColor = Color.White,
                selectedTextColor = BottomBarContentColor,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                selectedIndicatorColor = BottomBarSelectedColor,
                disabledIconColor = Color.Gray,
                disabledTextColor = Color.Gray
            ),
            selected = currentDestination?.route == screen.route,
            onClick = {
                navHostController.navigate(screen.route) {
                    popUpTo(navHostController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            },
            icon = {
                BadgedBox(badge = {}) {
                    Icon(imageVector = screen.icon, contentDescription = "bottom bar icon")
                }
            })
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Toolbar(
        title: String,
        navHostController: NavHostController,
        currentDestination: NavDestination?
    ) {
        TopAppBar(
            navigationIcon = {
                if (currentDestination?.route == Destinations.SearchScreen.route) {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            },
            title = { Text(text = title) }, actions = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BottomBarSelectedColor,
                scrolledContainerColor = colorResource(
                    id = R.color.white
                ),
                navigationIconContentColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.background,
                actionIconContentColor = colorResource(
                    id = R.color.white
                )
            ),
        )
    }
}
