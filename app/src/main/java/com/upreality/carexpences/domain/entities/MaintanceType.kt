package com.upreality.carexpences.domain.entities

enum class MaintanceType(val id : Int) {
    Maintenance(0),
    RepairService(1),
    Other(2);

    companion object {
        fun findByValue(value: Int) = values().firstOrNull { it.id == value }
    }
}