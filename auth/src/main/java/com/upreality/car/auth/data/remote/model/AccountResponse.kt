package com.upreality.car.auth.data.remote.model

import java.util.*

data class AccountResponse(
    var id: String? = null,
    var title: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var role: String? = null,
    var created: Date? = null,
    var updated: String? = null,
    var isVerified: Boolean? = null,
    var jwtToken: String? = null,
    var refreshToken: String? = null,
)