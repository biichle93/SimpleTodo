package com.example.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    var listOfStrings = mutableListOf<String>()
    //late init means we will initialize later
    lateinit var adapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onLongClickListener = object : TaskItemAdapter.OnLongClickListener{
            override fun onItemLongClicked(position: Int) {
                listOfStrings.removeAt(position)
                // tell adapter that data has changed
                adapter.notifyDataSetChanged()

                saveItems()
            }
        }
        loadItems()

        // lookup recyclerView in layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        //create adapter and pass in user data
        adapter = TaskItemAdapter(listOfStrings, onLongClickListener)
        //attach adapter to recyclerview to populate
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        //set up button and input field so the user can enter a task
        val input = findViewById<EditText>(R.id.addTaskField)
        //get reference of button and then put a setOnClickListener
        findViewById<Button>(R.id.button).setOnClickListener{
            //1. Grab the text from the user
            val userInputtedTask = input.text.toString()
            //2. Add String to list of tasks
            listOfStrings.add(userInputtedTask)
            //notify the adapter that data has updated, this is needed to tell the adapter it needs to update view
            adapter.notifyItemInserted(listOfStrings.size-1)
            //3. Reset text field to clear after adding to the list
            input.setText("")
            saveItems()
        }
    }
    //Save data that was inputted
    //save by writing and reading to a file

    //get file we need
    fun getDataFile() : File {

        //every line is going to represent a task
        return File(filesDir, "data.txt")
    }

    //load the items by reading each line in the
    fun loadItems(){
        try {
            listOfStrings = FileUtils.readLines(getDataFile(), Charset.defaultCharset())
        }catch(ioException: IOException){
            ioException.printStackTrace()
        }
    }
    //Save items by writing them into our file
    fun saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), listOfStrings)
        } catch(ioException: IOException){
            ioException.printStackTrace()
        }
    }

}