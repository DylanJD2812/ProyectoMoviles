package DTO

import java.time.LocalDateTime

data class HistoryDTO(
    val id: String = "",
    val title: String = "",
    val comment: String = "",
    val location: String = "",
    val photoUrl: String? = null,
    val personId: String = "",
    val createdAt: String = ""
)
