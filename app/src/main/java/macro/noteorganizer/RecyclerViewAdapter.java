package macro.noteorganizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import macro.noteorganizer.models.nosql.NotesDO;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    public ArrayList<NotesDO> notes;

    public RecyclerViewAdapter(ArrayList<NotesDO> n) {
        notes = n;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View notesView = inflater.inflate(R.layout.item_note, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(notesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int i) {
        NotesDO note = notes.get(i);

        TextView textView = viewHolder.title;
        textView.setText(note.getTitle());
        TextView textView1 = viewHolder.content;
        textView1.setText(note.getContent());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public void onClick(View view) {

    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;

        public ViewHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.textView_title);
            content = (TextView) v.findViewById(R.id.textView_content);
        }
    }
}
