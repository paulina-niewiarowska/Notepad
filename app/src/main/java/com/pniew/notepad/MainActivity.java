package com.pniew.notepad;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_A_NOTE = 1;
    public static final int EDIT_A_NOTE = 2;

    private NoteViewModel noteViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //register activity for result here, if you have any //

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton actionButton = findViewById(R.id.add_new_button);
        actionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNewNoteActivity.class);
                intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_I_WANT_TO, ADD_A_NOTE);
                MainActivity.this.startActivity(intent);
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
                noteAdapter.submitList(notes);
            }
        });

        noteAdapter.setOnNoteClickListener(new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNewNoteActivity.class);
                intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_ID, note.getId());
                intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_CONTENT, note.getContent());
                intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_I_WANT_TO, EDIT_A_NOTE);
                MainActivity.this.startActivity(intent);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(MainActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.swipe_to_delete))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.swipe_to_edit))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_item)
                        .addSwipeRightActionIcon(R.drawable.ic_edit)
                        .addSwipeLeftLabel("Delete")
                        .addSwipeRightLabel("Edit")
                        .create().decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        noteAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                        break;
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
                        Note note = noteAdapter.getNoteAt(viewHolder.getAdapterPosition());
                        Intent intent = new Intent(MainActivity.this, AddEditNewNoteActivity.class);
                        intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_ID, note.getId());
                        intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_TITLE, note.getTitle());
                        intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_CONTENT, note.getContent());
                        intent.putExtra(AddEditNewNoteActivity.INTENT_EXTRA_I_WANT_TO, EDIT_A_NOTE);
                        noteAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        MainActivity.this.startActivity(intent);
                        break;
                }

            }


        }).attachToRecyclerView(recyclerView);

    }
}