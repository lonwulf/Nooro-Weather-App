package com.lonwulf.nooro.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.lonwulf.nooro.weatherapp.R
import com.lonwulf.nooro.weatherapp.core.util.GenericResultState
import com.lonwulf.nooro.weatherapp.domain.model.WeatherHistoryPreferences
import com.lonwulf.nooro.weatherapp.navigation.NavComposable
import com.lonwulf.nooro.weatherapp.navigation.TopLevelDestinations
import com.lonwulf.nooro.weatherapp.presentation.ui.LoadImageFromUrl
import com.lonwulf.nooro.weatherapp.ui.theme.BottomBarBgGray
import com.lonwulf.nooro.weatherapp.ui.theme.BottomBarSelectedColor
import com.lonwulf.nooro.weatherapp.ui.theme.TextBlack
import com.lonwulf.nooro.weatherapp.ui.viewmodel.SharedViewModel
import org.koin.androidx.compose.navigation.koinNavViewModel

class HistoryScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        HistoryScreen(navHostController = navHostController)
    }

}

@Composable
fun HistoryScreen(modifier: Modifier = Modifier, navHostController: NavHostController) {
    val parentEntry =
        remember { navHostController.getBackStackEntry(TopLevelDestinations.HomeScreen.route) }
    val vm = koinNavViewModel<SharedViewModel>(viewModelStoreOwner = parentEntry)
    var preferenceList by remember { mutableStateOf<List<WeatherHistoryPreferences>>(emptyList()) }
    val historyFetchState by vm.weatherPreferencesList.collectAsState()


    LaunchedEffect(Unit) {
        vm.fetchAllHistory()
    }
    LaunchedEffect(historyFetchState) {
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
    preferenceList.takeIf { it.isNotEmpty() }?.let {
        LazyColumn(
            modifier = modifier
                .background(color = BottomBarBgGray)
                .fillMaxSize()
                .padding(10.dp)
        ) {
            items(items = preferenceList) { prefs ->
                ElevatedCard(
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp),
                    elevation = CardDefaults.elevatedCardElevation(8.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = BottomBarBgGray,
                        contentColor = BottomBarSelectedColor
                    )
                ) {
                    ConstraintLayout(
                        modifier = modifier
                            .background(color = BottomBarBgGray)
                            .padding(10.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        val (name, temp, feelsLike, img) = createRefs()
                        Text(text = prefs.name, modifier = modifier.constrainAs(name) {
                            top.linkTo(parent.top, margin = 10.dp)
                            start.linkTo(parent.start, margin = 20.dp)
                        })
                        Text(text = "Temperature: ${prefs.temp}°", modifier = modifier.constrainAs(temp) {
                            top.linkTo(name.bottom, margin = 10.dp)
                            start.linkTo(parent.start, margin = 20.dp)
                        })
                        Text(
                            text = "Feels Like: ${prefs.feelsLike}°",
                            modifier = modifier.constrainAs(feelsLike) {
                                top.linkTo(temp.bottom, margin = 10.dp)
                                start.linkTo(parent.start, margin = 20.dp)
                            })
                        LoadImageFromUrl(
                            url = prefs.iconUrl ?: "",
                            ctx = LocalContext.current,
                            modifier = modifier
                                .width(100.dp)
                                .constrainAs(img) {
                                    end.linkTo(parent.end, margin = 5.dp)
                                    top.linkTo(name.top)
                                    bottom.linkTo(feelsLike.bottom)
                                    height = Dimension.fillToConstraints
                                })
                    }
                }
            }
        }
    } ?: ShowEmptyData()
}

@Composable
fun ShowEmptyData() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.no_data),
            style = MaterialTheme.typography.headlineLarge.copy(
                color = TextBlack
            )
        )
    }

}
