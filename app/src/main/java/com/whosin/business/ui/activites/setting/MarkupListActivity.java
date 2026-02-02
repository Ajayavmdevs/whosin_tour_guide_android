package com.whosin.business.ui.activites.setting;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ActivityMarkupListBinding;
import com.whosin.business.databinding.ItemMarkupListBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.CommonModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;

import java.util.ArrayList;

public class MarkupListActivity extends BaseActivity {

    private ActivityMarkupListBinding binding;
    private MarkupAdapter<RaynaTicketDetailModel> adapter;

    @Override
    protected void initUi() {
        requestMarkupList();
        adapter = new MarkupAdapter<>();
        binding.itemRecycler.setLayoutManager(new LinearLayoutManager(activity));
        binding.itemRecycler.setAdapter(adapter);
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());
        binding.addMarkup.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMarkupListBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    private void requestMarkupList() {
        showProgress();
        DataService.shared( activity ).requestMarkupList( new RestCallback<ContainerListModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaTicketDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                    binding.itemRecycler.setVisibility( View.VISIBLE );
                    adapter.updateData( model.data );

                } else {
                    binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                    binding.itemRecycler.setVisibility( View.GONE );
                }
            }
        } );
    }

    private void requestRemoveMarkup(String ticketId, String optionId) {
        JsonObject object = new JsonObject();
        object.addProperty("customTicketId", ticketId);
        object.addProperty("optionId", optionId);

        DataService.shared( activity ).requestRemoveMarkup( object, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("You have removed markup from this ticket").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                requestMarkupList();
            }
        } );
    }

    public class MarkupAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_markup_list));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTicketDetailModel model = (RaynaTicketDetailModel) getItem(position);

            if (model == null) return;

            viewHolder.binding.tvTitle.setText(model.getTitle());
//            Utils.setStyledText(viewHolder.itemView.getContext(), viewHolder.binding.tvMarkupValue, String.valueOf(model.getMarkup()));
            viewHolder.binding.tvMarkupValue.setText(String.valueOf(model.getMarkup()));
            viewHolder.binding.ivMore.setOnClickListener(v -> {

                ArrayList<String> options = new ArrayList<>();
                options.add("Remove Markup");

                Graphics.showActionSheet(
                        v.getContext(),
                        v.getContext().getString(R.string.app_name),
                        options,
                        (item, index) -> {
                            if ("Remove Markup".equals(item)) {
                                requestRemoveMarkup(model.getId(), "");
                            }
                        }
                );
            });


            String image = null;

            if (model.getImages() != null
                    && !model.getImages().isEmpty()
                    && !Utils.isNullOrEmpty(model.getImages().get(0))) {

                image = model.getImages().get(0);
            }

            Graphics.loadImage(
                    viewHolder.itemView.getContext(),
                    image != null ? image : "",
                    viewHolder.binding.ivImage
            );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemMarkupListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemMarkupListBinding.bind(itemView);
            }
        }
    }
}

