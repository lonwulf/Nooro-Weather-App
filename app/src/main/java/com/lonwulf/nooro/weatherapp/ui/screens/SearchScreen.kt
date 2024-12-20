package com.lonwulf.nooro.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.lonwulf.nooro.weatherapp.core.util.GenericResultState
import com.lonwulf.nooro.weatherapp.domain.model.WeatherModel
import com.lonwulf.nooro.weatherapp.navigation.NavComposable
import com.lonwulf.nooro.weatherapp.navigation.TopLevelDestinations
import com.lonwulf.nooro.weatherapp.presentation.ui.LoadImageFromUrl
import com.lonwulf.nooro.weatherapp.presentation.ui.SearchBar
import com.lonwulf.nooro.weatherapp.ui.theme.BottomBarBgGray
import com.lonwulf.nooro.weatherapp.ui.theme.TextBlack
import com.lonwulf.nooro.weatherapp.ui.viewmodel.SharedViewModel
import org.koin.androidx.compose.navigation.koinNavViewModel


class SearchScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController, snackbarHostState: SnackbarHostState
    ) {
        SearchScreen(navHostController = navHostController)
    }
}

@Composable
fun SearchScreen(modifier: Modifier = Modifier, navHostController: NavHostController) {
    val parentEntry =
        remember { navHostController.getBackStackEntry(TopLevelDestinations.HomeScreen.route) }
    val vm = koinNavViewModel<SharedViewModel>(viewModelStoreOwner = parentEntry)
    var weatherObject by remember { mutableStateOf<WeatherModel?>(null) }
    val apiState by vm.weatherForeCastStateFlow.collectAsState()
    var isClicked by remember { mutableStateOf(false) }

    LaunchedEffect(apiState) {
        when (apiState) {
            is GenericResultState.Loading -> {
            }

            is GenericResultState.Empty -> {}
            is GenericResultState.Error -> {}
            is GenericResultState.Success -> {
                weatherObject =
                    (apiState as GenericResultState.Success<WeatherModel>).result!!
            }
        }
    }

    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (searchField, weatherTile,placeholderTxt) = createRefs()

        SearchBar(modifier = modifier.constrainAs(searchField) {
            top.linkTo(parent.top, 30.dp)
            start.linkTo(parent.start, 20.dp)
            end.linkTo(parent.end, 20.dp)
            width = Dimension.fillToConstraints
        }, onClick = {

        }, onSearch = {
            vm.fetchWeatherForeCast(it)
        })

        weatherObject.takeIf { it != null }?.let { model ->
            ElevatedCard(
                modifier = modifier
                    .wrapContentHeight()
                    .clickable {
                        isClicked = true
                        weatherObject?.let {
                            vm.addWeatherHistory(it)
                        }
                        navHostController.navigate(TopLevelDestinations.HomeScreen.route)
                    }
                    .constrainAs(weatherTile) {
                        top.linkTo(searchField.bottom, margin = 20.dp)
                        start.linkTo(parent.start, 30.dp)
                        end.linkTo(parent.end, 30.dp)
                        width = Dimension.fillToConstraints
                    },
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = BottomBarBgGray,
                    contentColor = TextBlack
                )
            ) {
                ConstraintLayout(
                    modifier = modifier
                        .background(color = BottomBarBgGray)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp)
                ) {
                    val (temp, img, name, checkIcn) = createRefs()

                    model.name?.let {
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = it,
                            modifier = modifier.constrainAs(name) {
                                top.linkTo(parent.top, margin = 5.dp)
                                start.linkTo(parent.start, margin = 5.dp)
                            })
                    }
                    model.tempC?.let {
                        Text(
                            style = MaterialTheme.typography.displayMedium,
                            text = "$it °",
                            modifier = modifier.constrainAs(temp) {
                                top.linkTo(name.bottom, margin = 10.dp)
                                start.linkTo(name.start)
                            })
                    }
                    model.iconUrl?.let {
                        LoadImageFromUrl(
                            url = "https:$it",
                            ctx = LocalContext.current,
                            modifier = modifier
                                .size(100.dp)
                                .constrainAs(img) {
                                    end.linkTo(checkIcn.start, 10.dp)
                                    top.linkTo(name.top)
                                    bottom.linkTo(temp.bottom)
                                    height = Dimension.fillToConstraints
                                })
                    }
                    if (isClicked) {
                        Image(
                            painter = painterResource(com.lonwulf.nooro.weatherapp.presentation.R.drawable.check_circle_52dp),
                            contentDescription = "",
                            modifier = modifier
                                .size(20.dp)
                                .constrainAs(checkIcn) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    end.linkTo(parent.end, 5.dp)
                                })
                    }
                    Spacer(modifier = modifier.height(10.dp))
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
