
package com.rakshit.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true)

    val id:Int = 0,

    val amount:Int,

    val isIncome:Boolean,

    val category:String,

    val date:String

)