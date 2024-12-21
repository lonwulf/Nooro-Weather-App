package com.lonwulf.nooro.weatherapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import com.lonwulf.nooro.weatherapp.core.util.GenericResultState
import com.lonwulf.nooro.weatherapp.domain.model.WeatherHistoryPreferences
import com.lonwulf.nooro.weatherapp.navigation.Destinations
import com.lonwulf.nooro.weatherapp.ui.screens.HomeScreen
import com.lonwulf.nooro.weatherapp.ui.viewmodel.SharedViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: SharedViewModel
    private lateinit var navHostController: NavHostController

    @Before
    fun setup() {
        mockViewModel = Mockito.mock(SharedViewModel::class.java)
        navHostController = mock<NavHostController>()
//        navHostController = TestNavHostController(composeTestRule.activity)
    }

    @Test
    fun testHomeScreenDisplaysLoadingState() {
        val loadingState = MutableStateFlow(GenericResultState.Loading)
        whenever(mockViewModel.weatherPreferencesList).thenReturn(loadingState)

        composeTestRule.setContent {
            HomeScreen(
                navHostController = navHostController,
                sharedViewModel = mockViewModel
            )
        }

        // Verify that the loading indicator is displayed
        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
    }

    @Test
    fun testHomeScreenDisplaysData() {
        // Mock success state with a weather history list
        val mockHistory = listOf(
            WeatherHistoryPreferences(
                name = "Nairobi",
                humidity = 65,
                temp = 23.0,
                iconUrl = "url/to/icon",
                feelsLike = 24.5,
                condition = "Cloudy",
                uv = 4.4
            )
        )
        val successState = MutableStateFlow(GenericResultState.Success(mockHistory))
        whenever(mockViewModel.weatherPreferencesList).thenReturn(successState)

        // Launch the HomeScreen
        composeTestRule.setContent {
            HomeScreen(
                navHostController = navHostController,
                sharedViewModel = mockViewModel
            )
        }

        // Verify that data is displayed correctly
        composeTestRule.onNodeWithText("Nairobi").assertIsDisplayed()
        composeTestRule.onNodeWithText("23.0°").assertIsDisplayed()
    }

    @Test
    fun testHomeScreenDisplaysEmptyState() {
        val emptyState: StateFlow<GenericResultState<List<WeatherHistoryPreferences>>> =
            MutableStateFlow(GenericResultState.Empty)

        // When the view model's weatherPreferencesList is accessed, return the mocked StateFlow
        whenever(mockViewModel.weatherPreferencesList).thenReturn(emptyState)

        composeTestRule.setContent {
            HomeScreen(
                navHostController = navHostController,
                sharedViewModel = mockViewModel
            )
        }

        // Verify that the empty state message is displayed
        composeTestRule.onNodeWithText("No city selected").assertIsDisplayed()
        composeTestRule.onNodeWithText("Please search").assertIsDisplayed()
    }

    @Test
    fun `clicking search bar navigates to search screen`() {
        // Given
        val emptyStateFlow = MutableStateFlow<GenericResultState<List<WeatherHistoryPreferences>>>(
            GenericResultState.Empty
        )

        whenever(mockViewModel.weatherPreferencesList).thenReturn(emptyStateFlow)
        whenever(mockViewModel.fetchAllHistory()).then { Unit }  // or just mock the void method

        // When
        composeTestRule.setContent {
            HomeScreen(
                navHostController = navHostController,
                sharedViewModel = mockViewModel
            )
        }

        // Then
        composeTestRule.onNodeWithTag("search_bar").performClick()
        assertEquals(Destinations.SearchScreen.route, navHostController.currentDestination?.route)
    }

}