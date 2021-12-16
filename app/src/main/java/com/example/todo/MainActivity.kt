package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.annotation.Nullable
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    val KEY_ITEM_TEXT = "item_text"
    val KEY_ITEM_POS = "item_position"
    val EDIT_TEXT_CODE = 20 // arbitrary value since we only have one activity
    val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        data -> OnActivityResult(EDIT_TEXT_CODE, data)
    }
    var listOfStrings = mutableListOf<String>()
    //late init means we will initialize later
    lateinit var adapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onLongClickListener = object : TaskItemAdapter.OnLongClickListener{
            override fun onItemLongClicked(position: Int) {
                val itemToDelete = listOfStrings.get(position)
                Toast.makeText(applicationContext, "$itemToDelete successfully deleted!", Toast.LENGTH_SHORT).show()
                listOfStrings.removeAt(position)
                // tell adapter that data has changed
                adapter.notifyItemRemoved(position)

                saveItems()
            }
        }
        val onClickListener = object : TaskItemAdapter.OnClickListener{
            override fun onItemClicked(position: Int) {
                //create new activity
                val intent = Intent(this@MainActivity, EditActivity::class.java)
                //pass the data back to original activity
                intent.putExtra(KEY_ITEM_TEXT, listOfStrings.get(position))
                intent.putExtra(KEY_ITEM_POS, position)
                //display the activity
                getContent.launch(intent)
            }

        }
        loadItems()

        // lookup recyclerView in layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        //create adapter and pass in user data
        adapter = TaskItemAdapter(listOfStrings, onLongClickListener, onClickListener)
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
    //handle the result of edit activity

    fun OnActivityResult(requestCode: Int, data: ActivityResult){
        if(data.resultCode == RESULT_OK){
            val dataRetrieved = data.data?.extras
            when(requestCode){
                EDIT_TEXT_CODE ->{
                    if(dataRetrieved != null){
                        val position = dataRetrieved.getInt(KEY_ITEM_POS)
                        val item = dataRetrieved.getString(KEY_ITEM_TEXT).toString()

                        listOfStrings.set(position,item)
                        adapter.notifyItemChanged(position)
                        saveItems()
                        Toast.makeText(applicationContext, "Item successfully updated!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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