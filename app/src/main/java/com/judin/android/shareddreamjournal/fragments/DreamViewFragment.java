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

import com.google.firebase.firestore.FirebaseFirestore;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.model.Dream;

public class DreamViewFragment extends Fragment {
    public static final String TAG = "DreamViewFragment";
    private static final String ARG_DREAM = "dream";

    private FirebaseFirestore mFirestore;
    private Dream mDream;
    private TextView mTitleText;
    private TextView mDreamText;
    private TextView mAuthorText;
    private TextView mDateText;

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
        mFirestore = FirebaseFirestore.getInstance();

        Bundle args = getArguments();
        mDream = args.getParcelable(ARG_DREAM);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dream_view, container, false);

        mTitleText = v.findViewById(R.id.dream_title_text_view);
        mDreamText = v.findViewById(R.id.dream_text_text_view);
        mAuthorText = v.findViewById(R.id.author_text_view);
        mDateText = v.findViewById(R.id.added_date_text_view);

        mTitleText.setText(mDream.getTitle());
        mDreamText.setText(mDream.getText());
        mAuthorText.setText(mDream.getAuthor());
        mDateText.setText(mDream.getAddedDateString());

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
