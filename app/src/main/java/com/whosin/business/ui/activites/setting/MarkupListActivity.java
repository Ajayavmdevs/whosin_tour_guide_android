package com.whosin.business.ui.activites.setting;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    ActivityResultLauncher<Intent> reloadLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            requestMarkupList(false);
                        }
                    }
            );


    @Override
    protected void initUi() {
        requestMarkupList(true);
        adapter = new MarkupAdapter<>();
        binding.itemRecycler.setLayoutManager(new LinearLayoutManager(activity));
        binding.itemRecycler.setAdapter(adapter);
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());
        binding.addMarkup.setOnClickListener(v -> {
            Intent intent = new Intent(activity, TicketSearchBottomSheet.class);
            reloadLauncher.launch(intent);
        });
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            requestMarkupList(false);
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

    private void requestMarkupList(boolean showLoader) {
        if (showLoader) {
            showProgress();
        }
        DataService.shared( activity ).requestMarkupList( new RestCallback<ContainerListModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaTicketDetailModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
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

    private void requestAddMarkup(String ticketId, String optionId, String markup) {
        JsonObject object = new JsonObject();
        object.addProperty("customTicketId", ticketId);
        object.addProperty("optionId", optionId);
        object.addProperty("markup", markup);

        DataService.shared( activity ).requestAddMarkup( object, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Alerter.create(activity).setTitle(getValue("thank_you")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("Markup Updated Successfully").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                requestMarkupList(true);
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
                requestMarkupList(true);
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
                options.add("Edit");
                options.add("Delete");

                Graphics.showActionSheet(
                        v.getContext(),
                        v.getContext().getString(R.string.app_name),
                        options,
                        (item, index) -> {
                            if ("Delete".equals(item)) {
                                requestRemoveMarkup(model.getId(), "");
                            } else if ("Edit".equals(item)) {
                                openTicketMarkupDialog(model);
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

        private void openTicketMarkupDialog(RaynaTicketDetailModel model) {
            AddMarkUpDialog markupDialog = new AddMarkUpDialog();
            markupDialog.markup = String.valueOf(Utils.roundFloatValue(model.getMarkup()));
            markupDialog.callback = value -> {
                if (TextUtils.isEmpty(value)) return;
                requestAddMarkup(model.getId(), "", value);
            };

            markupDialog.show(getSupportFragmentManager(), "AddMarkUpDialog");
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

