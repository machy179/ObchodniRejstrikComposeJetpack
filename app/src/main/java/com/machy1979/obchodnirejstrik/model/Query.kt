package com.machy1979.obchodnirejstrik.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "queries_tbl")
data class Query(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "query_ico" )
    val ico: String,

    @ColumnInfo(name = "query_name")
    val name: String,

    @ColumnInfo(name = "query_address")
    val address: String,
)