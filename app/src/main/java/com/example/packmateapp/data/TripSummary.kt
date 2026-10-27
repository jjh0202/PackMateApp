package com.example.packmateapp.data

import com.example.packmateapp.data.entity.TripEntity

// 여행 카드에 표시할 여행 정보 + 짐 진행률
data class TripSummary(
    val trip: TripEntity,
    val totalCount: Int,
    val packedCount: Int
)
