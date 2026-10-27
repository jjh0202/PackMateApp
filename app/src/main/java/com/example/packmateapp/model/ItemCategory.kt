package com.example.packmateapp.model

enum class ItemCategory(val label: String) {
    CLOTHES("의류"),
    TOILETRY("세면도구"),
    ELECTRONICS("전자기기"),
    DOCUMENT("서류"),
    MEDICINE("의약품"),
    ETC("기타");

    companion object {
        fun fromName(name: String): ItemCategory = entries.find { it.name == name } ?: ETC
    }
}
