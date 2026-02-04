package net.research.kt.coroutine.exception

class ParticipantNotFoundException(message: String) : RuntimeException(message)

class SimulationTimeoutException(message: String) : RuntimeException(message)

class InvalidSimulationRequestException(message: String) : RuntimeException(message)
