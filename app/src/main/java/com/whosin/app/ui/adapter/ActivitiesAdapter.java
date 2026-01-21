package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.SwipeTagItemBinding;
import com.whosin.app.service.models.AppSettingTitelCommonModel;

import java.util.List;
import java.util.stream.Collectors;

public class ActivitiesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
    private Activity activity;

    public ActivitiesAdapter(Activity activity) {

        this.activity = activity;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.swipe_tag_item));
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;
        AppSettingTitelCommonModel item = (AppSettingTitelCommonModel) getItem(position);

        viewHolder.vBinding.iconText.setText(item.getTitle());
        viewHolder.vBinding.iconText.setTextColor(activity.getResources().getColor(R.color.white_85));

        viewHolder.itemView.setOnClickListener(view -> {

            viewHolder.vBinding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.selected_bg));
            if (!item.isSelected()) {
                item.setSelected(true);
                viewHolder.vBinding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.swipe_tag_bg));
            } else {
                item.setSelected(false);
                viewHolder.vBinding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.selected_bg));
            }
            notifyDataSetChanged();
        });

        if (!item.isSelected()) {
            viewHolder.vBinding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.swipe_tag_bg));

        } else {
            viewHolder.vBinding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.selected_bg));
        }




    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeTagItemBinding vBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vBinding = SwipeTagItemBinding.bind(itemView);
        }
    }

    private void extractTitlesAndIds(List<String> inputIds, List<AppSettingTitelCommonModel> commonModels, List<String> idList, List<String> titleList) {
        for (String inputId : inputIds) {
            List<AppSettingTitelCommonModel> models = commonModels.stream()
                    .filter( p -> inputId.contains( p.getId() ) )
                    .collect( Collectors.toList() );

            for (AppSettingTitelCommonModel commonModel : models) {
                titleList.add( commonModel.getTitle() );
            }
            idList.add( inputId );
        }
    }
}
