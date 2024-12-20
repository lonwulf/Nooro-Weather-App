package com.lonwulf.nooro.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.lonwulf.nooro.weatherapp.R
import com.lonwulf.nooro.weatherapp.core.util.GenericResultState
import com.lonwulf.nooro.weatherapp.domain.model.WeatherHistoryPreferences
import com.lonwulf.nooro.weatherapp.domain.model.WeatherModel
import com.lonwulf.nooro.weatherapp.navigation.Destinations
import com.lonwulf.nooro.weatherapp.navigation.NavComposable
import com.lonwulf.nooro.weatherapp.presentation.ui.LoadImageFromUrl
import com.lonwulf.nooro.weatherapp.presentation.ui.SearchBar
import com.lonwulf.nooro.weatherapp.ui.theme.TextBlack
import com.lonwulf.nooro.weatherapp.ui.viewmodel.SharedViewModel
import org.koin.androidx.compose.navigation.koinNavViewModel

class HomeScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController, snackbarHostState: SnackbarHostState
    ) {
        HomeScreen(navHostController = navHostController)
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel = koinNavViewModel(),
) {
    var weatherObject by remember { mutableStateOf<WeatherModel?>(null) }
    val apiState by sharedViewModel.weatherForeCastStateFlow.collectAsState()
    var preferenceList by remember { mutableStateOf<List<WeatherHistoryPreferences>>(emptyList()) }
    val historyFetchState by sharedViewModel.weatherPreferencesList.collectAsState()

    LaunchedEffect(Unit) {
        sharedViewModel.fetchAllHistory()
    }

    LaunchedEffect(apiState, historyFetchState) {
        when (apiState) {
            is GenericResultState.Loading -> {}
            is GenericResultState.Empty -> {}
            is GenericResultState.Error -> {}
            is GenericResultState.Success -> {
                weatherObject = (apiState as GenericResultState.Success<WeatherModel>).result!!
            }
        }

        when (historyFetchState) {
            is GenericResultState.Loading -> {}
            is GenericResultState.Empty -> {}
            is GenericResultState.Error -> {}
            is GenericResultState.Success -> {
                preferenceList =
                    (historyFetchState as GenericResultState.Success<List<WeatherHistoryPreferences>>).result!!
            }
        }
    }


    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (searchField, degreeValue, placeholderTxt, img, locationText, arrowIcn, weatherTile) = createRefs()
        val horizontalGuide = createGuidelineFromBottom(0.4f)

        SearchBar(modifier = modifier.constrainAs(searchField) {
            top.linkTo(parent.top, 30.dp)
            start.linkTo(parent.start, 20.dp)
            end.linkTo(parent.end, 20.dp)
            width = Dimension.fillToConstraints
        }, onClick = {
            navHostController.navigate(Destinations.SearchScreen.route)
        }, onSearch = {})

        val dataToDisplay = when {
            preferenceList.isNotEmpty() -> {
                preferenceList.last().let { pref ->
                    WeatherModel(
                        name = pref.name,
                        tempC = pref.temp,
                        humidity = pref.humidity,
                        condition = pref.condition,
                        feelsLike = pref.feelsLike,
                        iconUrl = pref.iconUrl
                    )
                }
            }

            else -> weatherObject
        }

        dataToDisplay.takeIf { it != null }?.let {
            LoadImageFromUrl(
                url = "https:${it.iconUrl}",
                ctx = LocalContext.current,
                modifier = modifier
                    .size(200.dp)
                    .constrainAs(img) {
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        bottom.linkTo(locationText.top, 20.dp)
                    })

            Text(
                text = it.name.toString(),
                style = MaterialTheme.typography.headlineLarge,
                modifier = modifier.constrainAs(locationText) {
                    bottom.linkTo(degreeValue.top, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
            Image(
                painter = painterResource(com.lonwulf.nooro.weatherapp.presentation.R.drawable.fancy_arrow),
                contentDescription = "fancy arrow",
                modifier = modifier
                    .size(20.dp)
                    .constrainAs(arrowIcn) {
                        start.linkTo(locationText.end, margin = 10.dp)
                        top.linkTo(locationText.top)
                        bottom.linkTo(locationText.bottom)
                    })
            Text(
                text = it.tempC.toString().plus("°"),
                style = MaterialTheme.typography.displayLarge,
                modifier = modifier.constrainAs(degreeValue) {
                    bottom.linkTo(horizontalGuide)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

            Box(
                modifier = modifier
                    .constrainAs(weatherTile) {
                        start.linkTo(parent.start, 20.dp)
                        end.linkTo(parent.end, 20.dp)
                        top.linkTo(horizontalGuide)
                        width = Dimension.fillToConstraints
                    }
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WeatherItem(
                        label = stringResource(R.string.humidity), value = it.humidity.toString().plus("%")
                    )
                    WeatherItem(
                        label = stringResource(R.string.uv), value = it.uv.toString()
                    )
                    WeatherItem(
                        label = stringResource(R.string.feels_like), value = it.feelsLike.toString().plus("°")
                    )
                }
            }
        } ?: ShowEmptyData(modifier.constrainAs(placeholderTxt) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        })
    }
}

@Composable
fun ShowEmptyData(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_city_selected),
            style = MaterialTheme.typography.headlineLarge.copy(
                color = TextBlack
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.please_search),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextBlack
            )
        )
    }
}

@Composable
fun WeatherItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label, style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value, style = MaterialTheme.typography.bodyLarge.copy(
                color = TextBlack
            )
        )
    }
}
