package com.example.core.config

object ConfigMapper {
    // Mapping from Entities to Domain Models
    fun AppConfigEntity.toDomain() = AppConfiguration(
        appVersion = appVersion,
        isMaintenanceMode = isMaintenanceMode,
        isForceUpdate = isForceUpdate,
        supportContact = supportContact,
        whatsappBookingNumber = whatsappBookingNumber
    )

    fun PricingConfigEntity.toDomain() = PricingConfig(
        sedanPricePerKm = sedanPricePerKm,
        suvPricePerKm = suvPricePerKm,
        minimumBookingDistanceKm = minimumBookingDistanceKm,
        nightSurcharge = nightSurcharge,
        holidaySurcharge = holidaySurcharge,
        waitingCharge = waitingCharge,
        tollPolicy = tollPolicy
    )

    fun BookingPoliciesEntity.toDomain() = BookingPolicies(
        cancellationFee = cancellationFee,
        cancellationPolicyText = cancellationPolicyText,
        bookingNoticeTitle = bookingNoticeTitle,
        bookingNoticeBody = bookingNoticeBody,
        additionalRiderPolicies = if (additionalRiderPoliciesJoined.isEmpty()) emptyList() else additionalRiderPoliciesJoined.split("||")
    )

    fun PopularRouteEntity.toDomain() = PopularRoute(
        routeId = routeId,
        routeName = routeName,
        pickupAddress = pickupAddress,
        pickupLat = pickupLat,
        pickupLng = pickupLng,
        dropoffAddress = dropoffAddress,
        dropoffLat = dropoffLat,
        dropoffLng = dropoffLng,
        estimatedDistance = estimatedDistance,
        estimatedDuration = estimatedDuration,
        fixedPromotionalFare = fixedPromotionalFare,
        supportedVehicleTypes = if (supportedVehicleTypesJoined.isEmpty()) emptyList() else supportedVehicleTypesJoined.split(","),
        displayPriority = displayPriority,
        isActive = isActive
    )

    // Mapping from Domain Models to Entities
    fun AppConfiguration.toEntity() = AppConfigEntity(
        appVersion = appVersion,
        isMaintenanceMode = isMaintenanceMode,
        isForceUpdate = isForceUpdate,
        supportContact = supportContact,
        whatsappBookingNumber = whatsappBookingNumber
    )

    fun PricingConfig.toEntity() = PricingConfigEntity(
        sedanPricePerKm = sedanPricePerKm,
        suvPricePerKm = suvPricePerKm,
        minimumBookingDistanceKm = minimumBookingDistanceKm,
        nightSurcharge = nightSurcharge,
        holidaySurcharge = holidaySurcharge,
        waitingCharge = waitingCharge,
        tollPolicy = tollPolicy
    )

    fun BookingPolicies.toEntity() = BookingPoliciesEntity(
        cancellationFee = cancellationFee,
        cancellationPolicyText = cancellationPolicyText,
        bookingNoticeTitle = bookingNoticeTitle,
        bookingNoticeBody = bookingNoticeBody,
        additionalRiderPoliciesJoined = additionalRiderPolicies.joinToString("||")
    )

    fun PopularRoute.toEntity() = PopularRouteEntity(
        routeId = routeId,
        routeName = routeName,
        pickupAddress = pickupAddress,
        pickupLat = pickupLat,
        pickupLng = pickupLng,
        dropoffAddress = dropoffAddress,
        dropoffLat = dropoffLat,
        dropoffLng = dropoffLng,
        estimatedDistance = estimatedDistance,
        estimatedDuration = estimatedDuration,
        fixedPromotionalFare = fixedPromotionalFare,
        supportedVehicleTypesJoined = supportedVehicleTypes.joinToString(","),
        displayPriority = displayPriority,
        isActive = isActive
    )

    // Safe mapping from Firestore document map to Domain Model
    fun mapToAppConfig(map: Map<String, Any>?): AppConfiguration {
        if (map == null) return AppConfiguration()
        return AppConfiguration(
            appVersion = map["appVersion"] as? String ?: "1.0.0",
            isMaintenanceMode = map["isMaintenanceMode"] as? Boolean ?: false,
            isForceUpdate = map["isForceUpdate"] as? Boolean ?: false,
            supportContact = map["supportContact"] as? String ?: "+1-800-CABKARO",
            whatsappBookingNumber = map["whatsappBookingNumber"] as? String ?: "+1-800-CAB-WAPP"
        )
    }

    fun mapToPricingConfig(map: Map<String, Any>?): PricingConfig {
        if (map == null) return PricingConfig()
        return PricingConfig(
            sedanPricePerKm = (map["sedanPricePerKm"] as? Number)?.toDouble() ?: 15.0,
            suvPricePerKm = (map["suvPricePerKm"] as? Number)?.toDouble() ?: 22.0,
            minimumBookingDistanceKm = (map["minimumBookingDistanceKm"] as? Number)?.toDouble() ?: 100.0,
            nightSurcharge = (map["nightSurcharge"] as? Number)?.toDouble() ?: 0.0,
            holidaySurcharge = (map["holidaySurcharge"] as? Number)?.toDouble() ?: 0.0,
            waitingCharge = (map["waitingCharge"] as? Number)?.toDouble() ?: 0.0,
            tollPolicy = map["tollPolicy"] as? String ?: "Tolls are extra where applicable"
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun mapToBookingPolicies(map: Map<String, Any>?): BookingPolicies {
        if (map == null) return BookingPolicies()
        return BookingPolicies(
            cancellationFee = (map["cancellationFee"] as? Number)?.toDouble() ?: 50.0,
            cancellationPolicyText = map["cancellationPolicyText"] as? String ?: "Free cancellation within 5 minutes of booking.",
            bookingNoticeTitle = map["bookingNoticeTitle"] as? String ?: "Important Ride Information",
            bookingNoticeBody = map["bookingNoticeBody"] as? String ?: "Please verify your rider PIN with the driver before starting the trip.",
            additionalRiderPolicies = map["additionalRiderPolicies"] as? List<String> ?: emptyList()
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun mapToPopularRoute(id: String, map: Map<String, Any>): PopularRoute {
        return PopularRoute(
            routeId = id,
            routeName = map["routeName"] as? String ?: "Unknown Route",
            pickupAddress = map["pickupAddress"] as? String ?: "",
            pickupLat = (map["pickupLat"] as? Number)?.toDouble() ?: 0.0,
            pickupLng = (map["pickupLng"] as? Number)?.toDouble() ?: 0.0,
            dropoffAddress = map["dropoffAddress"] as? String ?: "",
            dropoffLat = (map["dropoffLat"] as? Number)?.toDouble() ?: 0.0,
            dropoffLng = (map["dropoffLng"] as? Number)?.toDouble() ?: 0.0,
            estimatedDistance = (map["estimatedDistance"] as? Number)?.toDouble() ?: 0.0,
            estimatedDuration = map["estimatedDuration"] as? String ?: "N/A",
            fixedPromotionalFare = (map["fixedPromotionalFare"] as? Number)?.toDouble() ?: 0.0,
            supportedVehicleTypes = map["supportedVehicleTypes"] as? List<String> ?: listOf("SEDAN", "SUV"),
            displayPriority = (map["displayPriority"] as? Number)?.toInt() ?: 0,
            isActive = map["isActive"] as? Boolean ?: true
        )
    }
}
