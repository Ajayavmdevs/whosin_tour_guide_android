
package com.whosin.business.comman;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class GenericDiffCallback<T extends DiffIdentifier> extends DiffUtil.Callback {
    private final List<T> oldList;
    private final List<T> newList;

    @NonNull
    public static <T extends DiffIdentifier> GenericDiffCallback<T> from(
            List<T> oldList, List<T> newList) {
        return new GenericDiffCallback<>(oldList, newList);
    }

    private GenericDiffCallback(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getIdentifier()
                == newList.get(newItemPosition).getIdentifier();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
