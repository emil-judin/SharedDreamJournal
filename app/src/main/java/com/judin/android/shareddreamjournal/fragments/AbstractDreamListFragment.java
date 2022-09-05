package com.judin.android.shareddreamjournal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.model.Dream;
import com.judin.android.shareddreamjournal.model.Loadable;
import com.judin.android.shareddreamjournal.model.Paginatable;
import com.judin.android.shareddreamjournal.model.Initializable;

import java.util.List;

public abstract class AbstractDreamListFragment extends Fragment implements Initializable, Loadable {
    private static final String TAG = "AbstractDreamList";

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    protected static final int QUERY_LIMIT = 25;
    protected static final int LOAD_OFFSET = 15;

    protected RecyclerView mRecyclerView;
    protected ProgressBar mProgressBar;
    protected DreamAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dream_list, container, false);
        mProgressBar = v.findViewById(R.id.progress_bar);
        mRecyclerView = v.findViewById(R.id.dream_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // RecyclerView is destroyed when navigating to detail fragment
        // If user is navigating back reuse old adapter, else initialize new dream list
        if(mAdapter != null){
            mRecyclerView.setAdapter(mAdapter);
            stopLoading();
        } else {
            // Handle loading in initialize
            initialize();
        }

        return v;
    }

    protected class DreamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Paginatable {
        private List<Dream> mDreams;

        public DreamAdapter(List<Dream> dreams) {
            mDreams = dreams;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if(viewType == VIEW_TYPE_ITEM){
                return new DreamHolder(inflater, viewGroup);
            } else if(viewType == VIEW_TYPE_FOOTER) {
                //ProgressHolder
                return new ProgressHolder(inflater, viewGroup);
            } else {
                return null;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {
            if(viewHolder instanceof DreamHolder){
                ((DreamHolder) viewHolder).bind(mDreams.get(pos));
            } else if(viewHolder instanceof ProgressHolder) {
                ((ProgressHolder) viewHolder).bind();
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mDreams.get(position) == null ? VIEW_TYPE_FOOTER : VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return mDreams.size();
        }

        @Override
        public void append(QuerySnapshot result) {
            List<Dream> dreams = result.toObjects(Dream.class);
            mDreams.addAll(dreams);
            notifyItemRangeInserted(mDreams.size() - 1, dreams.size());
        }

        @Override
        public void addProgressBar(){
            mDreams.add(null);
            notifyItemInserted(mDreams.size() - 1);
        }

        @Override
        public void removeProgressBar(){
            //important whether this runs before or after paginates dreams are added!
            mDreams.remove(mDreams.size() - 1);
            notifyItemRemoved(mDreams.size());
        }
    }

    protected class DreamHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Dream mDream;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private TextView mDateTextView;

        public DreamHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_dream, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.dream_title_text_view);
            mAuthorTextView = itemView.findViewById(R.id.author_text_view);
            mDateTextView = itemView.findViewById(R.id.date_text_view);
        }

        public void bind(Dream dream) {
            mDream = dream;
            mTitleTextView.setText(dream.getTitle());
            mAuthorTextView.setText(dream.getAuthor());
            mDateTextView.setText(dream.getAddedDateString());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, DreamViewFragment.newInstance(mDream), "DreamViewFragment")
                .addToBackStack(null)
                .commit();
        }
    }

    protected class ProgressHolder extends RecyclerView.ViewHolder{
        private ProgressBar mRecyclerProgressBar;

        public ProgressHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_progress_bar, parent, false));
            mRecyclerProgressBar = itemView.findViewById(R.id.recycler_view_progress_bar);
        }

        public void bind(){
            //blank
        }
    }

    protected void initializeAdapter(List<Dream> dreams) {
        mAdapter = new DreamAdapter(dreams);
        mRecyclerView.setAdapter(mAdapter);
    }

    protected void scrollToTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (firstVisibleItemPosition == 0) {
                layoutManager.scrollToPosition(0);
            }
        }
    }

    @Override
    public void startLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void stopLoading(){
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
