package com.datasite.poc.garden

enum class SimulatorAction(
    val weight: Int
) {
    SensorReadings(20),
    UserAccess(10),
    MoveSensor(5),
    RenameGarden(2),
    RenameSensor(2),
}
