package com.pniew.notepad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {
    private OnNoteClickListener listener;
    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getContent().equals(newItem.getContent());
        }
    };

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //here we have to create and return the Holder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        //here we take care of getting the data from the single Note.class object into the View of our NoteHolder.
        //we want all the elements to get to their places - title to title place, category to the category place, etc
        Note currentNote = getItem(position);
        holder.textOfTitleOfTheNote.setText(currentNote.getTitle());
        holder.textOfContentOfTheNote.setText(currentNote.getContent());
    }

//    public void setNotes(List<Note> notes) {
//        this.adaptersListOfNotes = notes;
//        notifyDataSetChanged();
//    }

    public Note getNoteAt(int position){
        return getItem(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textOfTitleOfTheNote;
        private TextView textOfContentOfTheNote;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textOfContentOfTheNote = itemView.findViewById(R.id.text_view_content);
            textOfTitleOfTheNote = itemView.findViewById(R.id.text_view_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onNoteClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public void setOnNoteClickListener(OnNoteClickListener listener){
        this.listener = listener;
    }
}
