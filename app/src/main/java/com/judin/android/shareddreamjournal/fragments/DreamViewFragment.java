package com.judin.android.shareddreamjournal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.model.Dream;

public class DreamViewFragment extends Fragment {
    public static final String TAG = "DreamViewFragment";
    private static final String ARG_DREAM = "dream";

    private Dream dream;
    private TextView titleTextView;
    private TextView dreamTextView;
    private TextView authorTextView;
    private TextView dateTextView;

    public static DreamViewFragment newInstance(Dream dream) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_DREAM, dream);

        DreamViewFragment fragment = new DreamViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        dream = args.getParcelable(ARG_DREAM);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dream_view, container, false);

        titleTextView = v.findViewById(R.id.dream_title_text_view);
        dreamTextView = v.findViewById(R.id.dream_text_text_view);
        authorTextView = v.findViewById(R.id.author_text_view);
        dateTextView = v.findViewById(R.id.added_date_text_view);

        titleTextView.setText(dream.getTitle());
        dreamTextView.setText(dream.getText());
        authorTextView.setText(dream.getAuthor());
        dateTextView.setText(dream.getCreationTimestampString());

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.dream_view_menu, menu);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Your Title");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_edit_dream) {
            // TODO: navigate to edit dream fragment or change everything text views to text areas
            return true;
        } else if(item.getItemId() == R.id.item_delete_dream){
            // TODO: show confirm delete dream dialog
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
