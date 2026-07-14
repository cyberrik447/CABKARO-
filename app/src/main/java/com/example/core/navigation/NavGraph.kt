package com.example.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.ui.screens.*

@Composable
fun CabkaroNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
        modifier = modifier
    ) {
        composable<Screen.Splash> {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Authentication) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Screen.Authentication> {
            AuthenticationScreen(
                onNavigateToOtp = { mobile ->
                    navController.navigate(Screen.OtpVerification(mobile))
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Screen.OtpVerification> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.OtpVerification>()
            OtpVerificationScreen(
                mobileNumber = route.mobileNumber,
                onNavigateToHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Authentication) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Screen.Home> {
            HomeScreen(
                onNavigateToConfirmation = { pickup, plat, plng, drop, dlat, dlng, dist ->
                    navController.navigate(
                        Screen.BookingConfirmation(
                            pickupAddress = pickup,
                            pickupLat = plat,
                            pickupLng = plng,
                            dropoffAddress = drop,
                            dropoffLat = dlat,
                            dropoffLng = dlng,
                            distanceKm = dist
                        )
                    )
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Screen.BookingConfirmation> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.BookingConfirmation>()
            BookingConfirmationScreen(
                pickupAddress = route.pickupAddress,
                dropoffAddress = route.dropoffAddress,
                distanceKm = route.distanceKm,
                vehicleType = "SEDAN", // default or passed
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToActiveBooking = { bookingId ->
                    navController.navigate(Screen.ActiveBooking(bookingId)) {
                        popUpTo(Screen.Home) { inclusive = false }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Screen.ActiveBooking> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ActiveBooking>()
            ActiveBookingScreen(
                bookingId = route.bookingId,
                onNavigateHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Screen.History> {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToActiveBooking = { bookingId ->
                    navController.navigate(Screen.ActiveBooking(bookingId))
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable<Screen.Settings> {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
