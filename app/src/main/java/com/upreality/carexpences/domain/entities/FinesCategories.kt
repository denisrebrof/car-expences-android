package com.upreality.carexpences.domain.entities

enum class FinesCategories(val id: Int) {
    SpeedLimit(0),
    Parking(1),
    RoadMarking(2),
    Other(4);

    companion object {
        fun findByValue(value: Int) = MaintanceType.values().firstOrNull { it.id == value }
    }
}