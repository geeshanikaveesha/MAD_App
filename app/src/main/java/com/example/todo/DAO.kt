package com.example.todo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DAO {
    @Insert
    fun insertTask(entity: Entity?)

    @Update
    fun updateTask(entity: Entity?)

    @Delete
    fun deleteTask(entity: Entity?)

    @Query("DELETE FROM to_do")
    fun deleteAll(): Int // Change the return type to int

    @get:Query("SELECT * FROM to_do")
    val tasks: List<CardInfo?>?
}
