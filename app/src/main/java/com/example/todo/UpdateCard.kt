package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.example.todo.databinding.ActivityUpdateCardBinding // Import ViewBinding class generated for your layout file
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateCard : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateCardBinding // Declare ViewBinding variable
    private lateinit var database: myDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCardBinding.inflate(layoutInflater) // Inflate layout using ViewBinding
        setContentView(binding.root)
        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "To_Do"
        ).build()
        val pos = intent.getIntExtra("id", -1)
        if (pos != -1) {
            val title = DataObject.getData(pos).title
            val priority = DataObject.getData(pos).priority
            binding.createTitle.setText(title) // Access views through ViewBinding variable
            binding.createPriority.setText(priority) // Access views through ViewBinding variable

            binding.deleteButton.setOnClickListener { // Access views through ViewBinding variable
                DataObject.deleteData(pos)
                GlobalScope.launch {
                    database.dao().deleteTask(
                        Entity(
                            pos + 1,
                            binding.createTitle.text.toString(), // Access views through ViewBinding variable
                            binding.createPriority.text.toString() // Access views through ViewBinding variable
                        )
                    )
                }
                myIntent()
            }

            binding.updateButton.setOnClickListener { // Access views through ViewBinding variable
                DataObject.updateData(
                    pos,
                    binding.createTitle.text.toString(), // Access views through ViewBinding variable
                    binding.createPriority.text.toString() // Access views through ViewBinding variable
                )
                GlobalScope.launch {
                    database.dao().updateTask(
                        Entity(
                            pos + 1, binding.createTitle.text.toString(), // Access views through ViewBinding variable
                            binding.createPriority.text.toString() // Access views through ViewBinding variable
                        )
                    )
                }
                myIntent()
            }
        }
    }

    private fun myIntent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
