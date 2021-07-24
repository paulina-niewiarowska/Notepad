package com.pniew.notepad;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FOR_ADD_A_NOTE = 1;
    public static final int REQUEST_CODE_FOR_EDIT_A_NOTE = 2;

    private NoteViewModel noteViewModel;
    private int requestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //register activity for result here:
        ActivityResultLauncher<Intent> addEditActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        if (resultCode == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            if (requestCode == REQUEST_CODE_FOR_ADD_A_NOTE) {
                                String title = data.getStringExtra(AddEditNewNoteActivity.INTENT_EXTRA_TITLE);
                                String content = data.getStringExtra(AddEditNewNoteActivity.INTENT_EXTRA_CONTENT);

                                Note note = new Note(title, content);
                                noteViewModel.insertNote(note);

                                Toast.makeText(MainActivity.this, "Jest zbawiony", Toast.LENGTH_SHORT).show();
                            } else if (requestCode == REQUEST_CODE_FOR_EDIT_A_NOTE) {
                                int id = data.getIntExtra(AddEditNewNoteActivity.INTENT_EXTRA_ID, -1);
                                if (id == -1) {
                                    Toast.makeText(MainActivity.this, "Sorry, item cannot be updated", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                String title = data.getStringExtra(AddEditNewNoteActivity.INTENT_EXTRA_TITLE);
                                String content = data.getStringExtra(AddEditNewNoteActivity.INTENT_EXTRA_CONTENT);

                                Note note = new Note(title, content);
                                note.setId(id);
                                noteViewModel.updateNote(note);

                                Toast.makeText(MainActivity.this, "Note updated!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                                Toast.makeText(MainActivity.this, "Anulowano", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton actionButton = findViewById(R.id.add_new_button);
        actionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNewNoteActivity.class);
                requestCode = REQUEST_CODE_FOR_ADD_A_NOTE;
                addEditActivityResultLauncher.launch(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        NoteAdapter noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);

        noteViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.setNotes(notes);
            }
        });

            noteAdapter.setOnNoteClickListener(new NoteAdapter.OnNoteClickListener() {
                @Override
                public void onNoteClick(Note note) {
                    Intent intent = new Intent(MainActivity.this, AddEditNewNoteActivity.class);
                    intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_ID, note.getId());
                    intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_TITLE, note.getTitle());
                    intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_CONTENT, note.getContent());

                    requestCode = REQUEST_CODE_FOR_EDIT_A_NOTE;
                    addEditActivityResultLauncher.launch(intent);
                }
            });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                switch(direction){
                    case ItemTouchHelper.LEFT:
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch(i){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        deleteTheNote();
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        noteAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                        return;
                                }
                            }

                            private void deleteTheNote() {
                                noteViewModel.deleteNote(noteAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                            }
                        };

                        alertBuilder.setMessage("Do you want to delete the item?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

                        break;
                    case ItemTouchHelper.RIGHT:
                        noteAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        break;
                }

            }


        }).attachToRecyclerView(recyclerView);

    }



}