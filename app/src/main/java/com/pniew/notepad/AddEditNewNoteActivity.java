package com.pniew.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
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

    //member variables

    private NoteViewModel noteViewModel;

    public static final String INTENT_EXTRA_ID = "com.pniew.notepad.INTENT_EXTRA_ID";
    public static final String INTENT_EXTRA_TITLE = "com.pniew.notepad.INTENT_EXTRA_TITLE";
    public static final String INTENT_EXTRA_CONTENT = "com.pniew.notepad.INTENT_EXTRA_CONTENT";
    public static final String INTENT_EXTRA_I_WANT_TO = "com.pniew.notepad.INTENT_EXTRA_I_WANT_TO";
    public static final String INTENT_EXTRA_DONE = "com.pniew.notepad.INTENT_EXTRA_DONE";
    public static final int ADDED = 1;
    public static final int EDITED = 2;

    private boolean onPause;
    private boolean done;
    private EditText editTextTitle;
    private EditText editTextContent;
//    private EditText editTextCategory;
//    private NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        noteViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(NoteViewModel.class);
        onPause = false;
        done = false;

        editTextTitle = findViewById(R.id.add_title_field);
        editTextContent = findViewById(R.id.add_content_field);

        // guzik "wstecz", który działa jak strzałka wstecz
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_button);

        //sprawdzamy, czy Main Activity wysyła nam jakiś pakiecik z danymi. Jeśli tak, to znaczy,
        //że edytujemy notatkę, więc należy ustawić pod nią pola i nadać odpowiedni tytuł
        Intent intent = getIntent();
        if(intent.hasExtra(INTENT_EXTRA_ID)){
            setTitle("Edit note");
            editTextTitle.setText(intent.getStringExtra(INTENT_EXTRA_TITLE));
            editTextContent.setText(intent.getStringExtra(INTENT_EXTRA_CONTENT));
        } else {
            setTitle("Add new note");
        }

    }

    // definicja metody saveNote. Metoda zostanie wywołana w momencie kliknięcia guzika z menu.
    private void saveNote(){
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();

        if (content.isEmpty()) {
            if (!onPause) {
                Toast.makeText(this, "Please insert content of your note", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        //nowy intent, który przekaże dane do maina:
        Intent data = new Intent();

        //dodajemy nową notatkę:
        Note note = new Note(title, content);
        //jeśli edytujemy, to kładziemy też id notatki, którą edytujemy
        int id = getIntent().getIntExtra(INTENT_EXTRA_ID, -1);
        int iWantTo = getIntent().getIntExtra(INTENT_EXTRA_I_WANT_TO, -1);

        if(iWantTo == MainActivity.ADD_A_NOTE && !done) {

            noteViewModel.insertNote(note);
            done = true;
            Toast.makeText(this, "Is saved", Toast.LENGTH_SHORT).show();
            data.putExtra(INTENT_EXTRA_DONE, ADDED);

        } else if (iWantTo == MainActivity.EDIT_A_NOTE && !done) {

            if(id != -1){
                note.setId(id);
                noteViewModel.updateNote(note);
                done = true;
                Toast.makeText(this, "Note edited", Toast.LENGTH_SHORT).show();
                data.putExtra(INTENT_EXTRA_DONE, EDITED);
            } else {
                Toast.makeText(this, "Sorry, item cannot be updated", Toast.LENGTH_LONG).show();
                return;
            }
        }
        setResult(RESULT_OK, data);
        finish();
    }

    //tworzenie menu na podstawie pliku resource:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu_resource_file, menu);
        return true;
    }


    //w menu resource file znajduje się lista przycisków, mają one swoje id.
    //gdy user klika przycisk z menu, odpala się ta metoda. W zależności od id
    //klikniętego guziczka, możemy zrobić różne rzeczy (switch statement):
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_button:
                saveNote();
                return true;
            case R.id.menu_delete_button:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddEditNewNoteActivity.this);
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteNote();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                alertBuilder.setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", onClickListener)
                        .setNegativeButton("No", onClickListener)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteNote() {
        Intent intent = getIntent();
        int id = intent.getIntExtra(INTENT_EXTRA_ID, -1);

        if (id != -1) {
            String title = intent.getStringExtra(INTENT_EXTRA_TITLE);
            String content = intent.getStringExtra(INTENT_EXTRA_CONTENT);
            Note note = new Note(title, content);
            note.setId(id);
            noteViewModel.deleteNote(note);
            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onPause() {
        onPause = true;
        if (!done) {
        saveNote();}
        super.onPause();
    }
}