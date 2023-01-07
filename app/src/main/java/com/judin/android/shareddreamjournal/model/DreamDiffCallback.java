package com.judin.android.shareddreamjournal.model;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DreamDiffCallback extends DiffUtil.Callback {
    private final List<Dream> oldDreamList;
    private final List<Dream> newDreamList;

    public DreamDiffCallback(List<Dream> oldDreamList, List<Dream> newDreamList) {
        this.oldDreamList = oldDreamList;
        this.newDreamList = newDreamList;
    }

    @Override
    public int getOldListSize() {
        return oldDreamList.size();
    }

    @Override
    public int getNewListSize() {
        return newDreamList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldDreamList.get(oldItemPosition).getId()
                .equals(newDreamList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldDreamList.get(oldItemPosition)
                .equals(newDreamList.get(newItemPosition));
    }
}
