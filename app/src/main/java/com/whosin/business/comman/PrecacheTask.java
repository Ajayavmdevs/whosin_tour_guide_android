package com.whosin.business.comman;

import android.os.AsyncTask;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PrecacheTask extends AsyncTask<Void, Integer, Void> {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private int itemCount;

    public PrecacheTask(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        this.itemCount = recyclerView.getAdapter().getItemCount();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Background task to precache items one by one
        for (int i = 0; i < itemCount; i++) {
            if (!isCancelled()) {
                // Scroll to the next item smoothly
                publishProgress(i);
                try {
                    Thread.sleep(10); // Adjust the delay as needed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break; // If the task is cancelled, exit the loop
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // Smooth scroll to the next item
        layoutManager.smoothScrollToPosition(recyclerView, null, values[0]);
    }
}

