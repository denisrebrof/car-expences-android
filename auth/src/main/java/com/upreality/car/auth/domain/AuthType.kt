package com.upreality.car.auth.domain

enum class AuthType(val id: Int) {
    UNDEFINED(0),
    PHONE(1),
    EMAIL(2),
    GOOGLE(3)
}