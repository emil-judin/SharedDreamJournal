package com.judin.android.shareddreamjournal.model;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DreamDiffCallback extends DiffUtil.Callback {
    private final List<Dream> mOldDreamList;
    private final List<Dream> mNewDreamList;

    public DreamDiffCallback(List<Dream> oldDreamList, List<Dream> newDreamList) {
        this.mOldDreamList = oldDreamList;
        this.mNewDreamList = newDreamList;
    }

    @Override
    public int getOldListSize() {
        return mOldDreamList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewDreamList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldDreamList.get(oldItemPosition).getId()
                .equals(mNewDreamList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldDreamList.get(oldItemPosition)
                .equals(mNewDreamList.get(newItemPosition));
    }
}
