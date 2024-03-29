package com.judin.android.shareddreamjournal.listener;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.judin.android.shareddreamjournal.model.FirebaseDataViewModel;

public class PaginationScrollListener extends RecyclerView.OnScrollListener{
    private static final String TAG = "PaginationScroll";
    protected static final int LOAD_OFFSET = 15;
    private final FirebaseDataViewModel<?> firebaseDataViewModel;

    public PaginationScrollListener(FirebaseDataViewModel<?> firebaseDataViewModel){
        this.firebaseDataViewModel = firebaseDataViewModel;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = linearLayoutManager.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();

        if ((firstVisibleItemPosition + visibleItemCount >= totalItemCount - LOAD_OFFSET)
                && !firebaseDataViewModel.isLastReached()
                && !Boolean.TRUE.equals(firebaseDataViewModel.getIsUpdating().getValue())) {

            // Call update on ViewModel
            firebaseDataViewModel.fetchData();
        }
    }
}