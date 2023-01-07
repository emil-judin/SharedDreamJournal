package com.judin.android.shareddreamjournal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.listener.PaginationScrollListener;
import com.judin.android.shareddreamjournal.model.Dream;
import com.judin.android.shareddreamjournal.model.DreamDiffCallback;
import com.judin.android.shareddreamjournal.model.FirebaseDataViewModel;
import com.judin.android.shareddreamjournal.model.InitializeViewModel;

import java.util.List;

public abstract class AbstractDreamListFragment extends Fragment implements InitializeViewModel {
    private static final String TAG = "AbstractDreamList";
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private FirebaseDataViewModel<Dream> dreamListViewModel;
    protected RecyclerView recyclerView;
    protected ProgressBar progressBar;
    protected DreamAdapter adapter;

    private boolean isUpdating = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get concrete dream view model
        dreamListViewModel = initializeViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dream_list, container, false);
        progressBar = v.findViewById(R.id.progress_bar);
        recyclerView = v.findViewById(R.id.dream_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // RecyclerView is destroyed when navigating to detail fragment
        // If user is navigating back reuse old adapter, else initialize new dream list
        if(adapter != null){
            recyclerView.setAdapter(adapter);
            stopFullPageLoad();
        } else {
            // bind observers
            dreamListViewModel.getIsInitializing().observe(getViewLifecycleOwner(), aBoolean -> {
                if(aBoolean){
                    startFullPageLoad();
                    recyclerView.clearOnScrollListeners();
                } else {
                    stopFullPageLoad();

                    // Add a scroll listener for pagination
                    recyclerView.addOnScrollListener(new PaginationScrollListener(dreamListViewModel));
                }
            });

            dreamListViewModel.getIsUpdating().observe(getViewLifecycleOwner(), aBoolean -> {
                if(aBoolean){
                    isUpdating = true;
                    adapter.addProgressBar();
                } else {
                    isUpdating = false;
                    adapter.removeProgressBar();
                }
            });

            dreamListViewModel.getData().observe(getViewLifecycleOwner(), dreams -> {
                adapter.updateDreamList(dreams);
            });

            // start first data fetch
            dreamListViewModel.fetchData();
            List<Dream> dreams = dreamListViewModel.getData().getValue();
            adapter = new DreamAdapter(dreams);
            recyclerView.setAdapter(adapter);
        }

        return v;
    }

    protected class DreamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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

        public void updateDreamList(List<Dream> newDreams){
            if(isUpdating){
                adapter.removeProgressBar();
                isUpdating = false;
            }
            final DreamDiffCallback dreamDiffCallback = new DreamDiffCallback(mDreams, newDreams);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(dreamDiffCallback);

            mDreams.clear();
            mDreams.addAll(newDreams);
            diffResult.dispatchUpdatesTo(this);
        }

        public void addProgressBar(){
            mDreams.add(null);
            notifyItemInserted(mDreams.size() - 1);
        }

        public void removeProgressBar(){
            // important whether this runs before or after paginates dreams are added!
            mDreams.remove(mDreams.size() - 1);
            notifyItemRemoved(mDreams.size());
        }
    }

    protected class DreamHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Dream dream;
        private TextView titleTextView;
        private TextView authorTextView;
        private TextView dateTextView;

        public DreamHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_dream, parent, false));
            itemView.setOnClickListener(this);

            titleTextView = itemView.findViewById(R.id.dream_title_text_view);
            authorTextView = itemView.findViewById(R.id.author_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
        }

        public void bind(Dream dream) {
            this.dream = dream;
            titleTextView.setText(dream.getTitle());
            authorTextView.setText(dream.getAuthor());
            dateTextView.setText(dream.getCreationTimestampString());
        }

        @Override
        public void onClick(View v) {
            // Load dream view
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, DreamViewFragment.newInstance(dream), "DreamViewFragment")
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

    protected void scrollToTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (firstVisibleItemPosition == 0) {
                layoutManager.scrollToPosition(0);
            }
        }
    }

    public void startFullPageLoad() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void stopFullPageLoad(){
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
