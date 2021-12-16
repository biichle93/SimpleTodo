package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val etItem = findViewById<EditText>(R.id.etItem)
        val btnSave = findViewById<Button>(R.id.btnSave)
        // set action bar to new title to let user know where theyre at
        supportActionBar?.title = "Edit your entry"
        //grab values from bundle passed from MainActivity
        etItem.setText(intent.getStringExtra("item_text"))
        val position = intent.getIntExtra("item_position", -1)
        //make a click listener on button, when user saves their edited entry
        btnSave.setOnClickListener{
            //create intent which contains results
            val intent = Intent()
            //pass the data
            intent.putExtra("item_text", etItem.text.toString())
            intent.putExtra("item_position", position)
            //set the result of intent
            setResult(RESULT_OK, intent)
            //finish the activity and go back to main activity
            finish()
        }
        


    }
}