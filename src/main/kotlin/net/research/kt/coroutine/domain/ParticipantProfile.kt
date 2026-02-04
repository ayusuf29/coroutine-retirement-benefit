package net.research.kt.coroutine.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ParticipantProfile(
    @JsonProperty("participantId")
    val participantId: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("birthDate")
    val birthDate: LocalDate,

    @JsonProperty("registrationDate")
    val registrationDate: LocalDate,

    @JsonProperty("employerName")
    val employerName: String,

    @JsonProperty("currentSalary")
    val currentSalary: Double
) {
    fun getAge(): Int {
        val today = LocalDate.now()
        var age = today.year - birthDate.year
        if (today.monthValue < birthDate.monthValue ||
            (today.monthValue == birthDate.monthValue && today.dayOfMonth < birthDate.dayOfMonth)
        ) {
            age--
        }
        return age
    }

    fun getYearsOfParticipation(): Int {
        val today = LocalDate.now()
        var years = today.year - registrationDate.year
        if (today.monthValue < registrationDate.monthValue ||
            (today.monthValue == registrationDate.monthValue && today.dayOfMonth < registrationDate.dayOfMonth)
        ) {
            years--
        }
        return years
    }
}
