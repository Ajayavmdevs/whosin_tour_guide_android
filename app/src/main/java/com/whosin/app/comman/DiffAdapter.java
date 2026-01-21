/* (C) 2020 */
package com.whosin.app.comman;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class DiffAdapter<T extends DiffIdentifier, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<T> data;

    public DiffAdapter() {
        this.data = new ArrayList<>();
    }

    public DiffAdapter(@Nullable List<T> data) {
        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = new ArrayList<>(data);
        }
    }

    public void updateData(List<T> updatedData) {


        this.data = updatedData;
        this.notifyDataSetChanged();
    }

    public void updateDataForPaggination(List<T> updatedData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(GenericDiffCallback.from(data, updatedData));
        this.data = new ArrayList<>(updatedData);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    protected T getItem(int i) {
        return data.get(i);
    }

    public List<T> getData() {
        return data;
    }

    public void addItem(T item) {
        data.add(item);
        if (data.size() == 1) {
            this.notifyDataSetChanged();
        } else {
            this.notifyItemInserted(data.size());
        }
    }

    public void addItems(List<T> items) {
        int startPosition = data.size();
        data.addAll(items);
        if (startPosition == 0) {
            this.notifyDataSetChanged();
        } else {
            this.notifyItemRangeInserted(startPosition, data.size());
        }
    }

    public void removeItem(int i) {
        if (data.size() > i) {
            data.remove(i);
        }
    }

    public void refreshData(List<T> updatedData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(GenericDiffCallback.from(data, updatedData));
        this.data = new ArrayList<>(updatedData);
        diffResult.dispatchUpdatesTo(this);
    }

}
