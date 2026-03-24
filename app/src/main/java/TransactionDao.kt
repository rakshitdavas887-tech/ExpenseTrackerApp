package com.rakshit.expensetracker

import androidx.room.*

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM TransactionEntity")

    suspend fun getAll(): List<TransactionEntity>

}