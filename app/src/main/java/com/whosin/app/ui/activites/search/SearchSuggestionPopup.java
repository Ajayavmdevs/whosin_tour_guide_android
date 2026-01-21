package com.whosin.app.ui.activites.search;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.whosin.app.R;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionPopup {
    private final Context context;
    private final EditText anchorEditText;
    private final PopupWindow popupWindow;
    private final ListView listView;
    private final SearchPopupAdapter adapter;

    public SearchSuggestionPopup(Context context, EditText anchorEditText) {
        this.context = context;
        this.anchorEditText = anchorEditText;

        listView = new ListView(context);
        listView.setDivider(null);
        listView.setVerticalScrollBarEnabled(false);
        listView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        listView.setBackgroundColor(Color.TRANSPARENT); // Important

        adapter = new SearchPopupAdapter(context);
        listView.setAdapter(adapter);

        // 👇 Wrapper to apply rounded background + stroke
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackground(ContextCompat.getDrawable(context, R.drawable.popup_menu_bg));
        container.setPadding(0, 0, 0, 0);
        container.setClipToPadding(false);
        container.setClipChildren(false);
        container.addView(listView);

        popupWindow = new PopupWindow(container,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(false);

        // Adjust popup width same as EditText
        anchorEditText.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (anchorEditText.getWidth() > 0) {
                popupWindow.setWidth(anchorEditText.getWidth());
            }
        });

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            if (!adapter.isLoading()) {
                String selected = adapter.getItem(position);
                anchorEditText.setText(selected);
                anchorEditText.setSelection(selected.length());
                dismiss();
            }
        });
    }


    public void showLoading() {
        adapter.showLoading();

        int loaderHeight = dpToPx(50);
        popupWindow.setHeight(loaderHeight);
        popupWindow.setWidth(anchorEditText.getWidth());

        if (!popupWindow.isShowing()) {
            popupWindow.showAsDropDown(anchorEditText);
        } else {
            popupWindow.update(anchorEditText, 0, 0, anchorEditText.getWidth(), loaderHeight);
        }
    }


    public void showSuggestions(List<String> suggestions) {
        adapter.updateItems(suggestions);

        listView.post(() -> {
            int totalItems = suggestions.size();
            int maxVisibleItems = 7;

            // Safeguard: don't crash if adapter is empty
            if (totalItems == 0 || listView.getChildCount() == 0) return;

            View itemView = listView.getChildAt(0);
            itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(anchorEditText.getWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.UNSPECIFIED
            );

            int itemHeight = itemView.getMeasuredHeight();
            int totalHeight = Math.min(totalItems, maxVisibleItems) * itemHeight;

            popupWindow.setHeight(totalHeight);
            popupWindow.setWidth(anchorEditText.getWidth());

            if (!popupWindow.isShowing()) {
                popupWindow.showAsDropDown(anchorEditText);
            } else {
                popupWindow.update(anchorEditText, 0, 0, anchorEditText.getWidth(), totalHeight);
            }
        });
    }






    public void dismiss() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public static class SearchPopupAdapter extends BaseAdapter {
        private final Context context;
        private List<String> items = new ArrayList<>();
        private boolean isLoading = false;

        public SearchPopupAdapter(Context context) {
            this.context = context;
        }

        public void updateItems(List<String> newItems) {
            this.items = newItems;
            this.isLoading = false;
            notifyDataSetChanged();
        }

        public void showLoading() {
            this.items.clear();
            this.isLoading = true;
            notifyDataSetChanged();
        }

        public boolean isLoading() {
            return isLoading;
        }

        @Override
        public int getCount() {
            return isLoading ? 1 : items.size();
        }

        @Override
        public String getItem(int position) {
            return isLoading ? "" : items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (isLoading) {
                return LayoutInflater.from(context).inflate(R.layout.popup_loading_item, parent, false);
            }

            View view = LayoutInflater.from(context).inflate(R.layout.search_popup_menu_item, parent, false);
            TextView textView = view.findViewById(R.id.popup_item);
            textView.setText(items.get(position));

            return view;
        }
    }
}



