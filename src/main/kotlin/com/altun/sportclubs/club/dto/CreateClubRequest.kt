package com.altun.sportclubs.club.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateClubRequest(
    @field:NotBlank(message = "Club name is required")
    @field:Size(min = 2, max = 100, message = "Club name must be between 2 and 100 characters")
    val name: String,

    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,

    @field:Size(max = 500, message = "Logo URL cannot exceed 500 characters")
    val logoUrl: String? = null
)