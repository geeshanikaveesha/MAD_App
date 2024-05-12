package com.example.todo

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.todo.databinding.ActivityMainBinding // Import ViewBinding class generated for your layout file
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // Declare ViewBinding variable
    private lateinit var database: myDatabase
    private lateinit var adapter: Adapter // Declare adapter property
    private var dataList = mutableListOf<CardInfo>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Room.databaseBuilder(applicationContext, myDatabase::class.java, "To_Do").build()

        // Set up search functionality
        setUpSearchView() // Call function to set up search view

        binding.add.setOnClickListener {
            val intent = Intent(this, CreateCard::class.java)
            startActivity(intent)
        }

        binding.deleteAll.setOnClickListener {
            showConfirmationDialog()
        }
        setupSortingButtons()
        setRecycler()
        loadData()

    }
    private fun setupSortingButtons() {
        binding.sortbtn.setOnClickListener {
            sortByPriority()
        }
    }
    private fun sortByPriority() {
        val highPriorityList = dataList.filter { it.priority.equals("high", ignoreCase = true) }
        val mediumPriorityList = dataList.filter { it.priority.equals("medium", ignoreCase = true) }
        val lowPriorityList = dataList.filter { it.priority.equals("low", ignoreCase = true) }.reversed()

        val sortedList = mutableListOf<CardInfo>()
        sortedList.addAll(highPriorityList)
        sortedList.addAll(mediumPriorityList)
        sortedList.addAll(lowPriorityList)

        adapter.setData(sortedList)
        }
    private fun setRecycler() {
        dataList = DataObject.getAllData().toMutableList()
        adapter = Adapter(dataList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
    private fun loadData() {
        dataList = DataObject.getAllData().toMutableList()
        adapter?.setData(dataList) // Use safe call operator to avoid NullPointerException
    }

    private fun setUpSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission (optional)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle query text changes
                adapter.filter(newText.orEmpty())
                return true
            }
        })
    }




    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.custom_dialog, null)
        builder.setView(view)

        val dialogTitle = view.findViewById<TextView>(R.id.dialog_title)
        dialogTitle.text = "Delete All"

        val dialogMessage = view.findViewById<TextView>(R.id.dialog_message)
        dialogMessage.text = "Are you sure you want to delete all todos?"


        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            deleteAllTodos()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)


        dialog.show()
    }


    private fun deleteAllTodos() {
        DataObject.deleteAll()
        GlobalScope.launch {
            database.dao().deleteAll()
        }
        setRecycler()
    }
}