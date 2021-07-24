package com.pniew.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class AddEditNewNoteActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_ID = "com.pniew.notepad.INTENT_EXTRA_ID";
    public static final String INTENT_EXTRA_TITLE = "com.pniew.notepad.INTENT_EXTRA_TITLE";
    public static final String INTENT_EXTRA_CONTENT = "com.pniew.notepad.INTENT_EXTRA_CONTENT";

    private EditText editTextTitle;
    private EditText editTextContent;
//    private EditText editTextCategory;
//    private NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        editTextTitle = findViewById(R.id.add_title_field);
        editTextContent = findViewById(R.id.add_content_field);
//        editTextCategory = findViewById(R.id.add_category_field);
//        numberPicker = findViewById(R.id.the_number_picker);

//        numberPicker.setMaxValue(10);
//        numberPicker.setMinValue(1);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_button);

        Intent intent = getIntent();
        if(intent.hasExtra(INTENT_EXTRA_ID)){
            setTitle("Edit note");
            editTextTitle.setText(intent.getStringExtra(INTENT_EXTRA_TITLE));
            editTextContent.setText(intent.getStringExtra(INTENT_EXTRA_CONTENT));
        } else {
            setTitle("Add new note");
        }

    }

    private void saveNote(){
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();
//        String category = editTextCategory.getText().toString();

        if (content.trim().isEmpty()) {
            Toast.makeText(this, "Please insert content of your note", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(INTENT_EXTRA_TITLE, title);
        data.putExtra(INTENT_EXTRA_CONTENT, content);
        int id = getIntent().getIntExtra(INTENT_EXTRA_ID, -1);
        if(id != -1){
            data.putExtra(INTENT_EXTRA_ID, id);
        }
//        data.putExtra(INTENT_EXTRA_CATEGORY, category);

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu_resource_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_button:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}