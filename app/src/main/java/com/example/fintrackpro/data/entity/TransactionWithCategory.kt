package com.example.fintrackpro.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithCategory(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val category: CategoryEntity,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "accountId"
    )
    val account: AccountEntity
)
