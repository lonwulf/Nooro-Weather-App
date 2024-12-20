package com.lonwulf.nooro.weatherapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.lonwulf.nooro.weatherapp.R
import com.lonwulf.nooro.weatherapp.navigation.NavComposable
import com.lonwulf.nooro.weatherapp.navigation.TopLevelDestinations
import com.lonwulf.nooro.weatherapp.ui.theme.BottomBarBgGray
import com.lonwulf.nooro.weatherapp.ui.theme.BottomBarSelectedColor
import com.lonwulf.nooro.weatherapp.ui.theme.ErrorRed
import com.lonwulf.nooro.weatherapp.ui.theme.TextBlack
import com.lonwulf.nooro.weatherapp.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.navigation.koinNavViewModel

class SettingsScreenComposable : NavComposable {
    @Composable
    override fun Composable(
        navHostController: NavHostController,
        snackbarHostState: SnackbarHostState
    ) {
        SettingsScreen(navHostController = navHostController, snackbarHostState = snackbarHostState)
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, navHostController: NavHostController, snackbarHostState: SnackbarHostState) {
    val parentEntry =
        remember { navHostController.getBackStackEntry(TopLevelDestinations.HomeScreen.route) }
    val vm = koinNavViewModel<SharedViewModel>(viewModelStoreOwner = parentEntry)
    val scope = rememberCoroutineScope()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = MaterialTheme.colorScheme.onSecondary)
    ) {
        val section = createRef()
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = BottomBarBgGray,
                contentColor = TextBlack
            ), shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .padding(horizontal = 5.dp, vertical = 10.dp)
                .border(
                    border = BorderStroke(
                        1.dp,
                        BottomBarSelectedColor
                    ), shape = RoundedCornerShape(20.dp)
                )
                .constrainAs(section) {
                    end.linkTo(parent.end, 20.dp)
                    start.linkTo(parent.start, 20.dp)
                    top.linkTo(parent.top, margin = 30.dp)
                    width = Dimension.fillToConstraints
                }) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp)
                    .clickable {
                        vm.clearAllData()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Cleared cache", duration = SnackbarDuration.Short
                            )
                        }
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "clear cache",
                    tint = ErrorRed
                )
                Spacer(modifier = modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.clear_cache),
                    color = TextBlack
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "arrow icon", tint = TextBlack
                )
            }
        }
    }
}
