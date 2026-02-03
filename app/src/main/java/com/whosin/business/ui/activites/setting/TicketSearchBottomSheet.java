package com.whosin.business.ui.activites.setting;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
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
import com.whosin.business.databinding.ActivityTicketSearchSheetBinding;
import com.whosin.business.databinding.ItemMarkupListBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.CommonModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;

import java.util.ArrayList;

public class TicketSearchBottomSheet extends BaseActivity {

    private ActivityTicketSearchSheetBinding binding;
    private TicketListAdapter<RaynaTicketDetailModel> adapter;

    @Override
    protected void initUi() {
        adapter = new TicketListAdapter<>();
        binding.ticketListRecycler.setLayoutManager(new LinearLayoutManager(activity));
        binding.ticketListRecycler.setAdapter(adapter);

        adapter.updateData(SessionManager.shared.geHomeBlockData().getTickets());
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener( view -> onBackPressed() );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityTicketSearchSheetBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
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
                Alerter.create(activity).setTitle(getValue("thank_you")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("Markup Added Successfully").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                Intent data = new Intent();
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        } );
    }

    public class TicketListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

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
            viewHolder.binding.tvMarkupValue.setText(String.valueOf(model.getMarkup()));
            viewHolder.binding.ivMore.setOnClickListener(v -> {

                ArrayList<String> options = new ArrayList<>();
                options.add("Add Markup for Ticket");
                options.add("Add Markup for Tour Option");

                Graphics.showActionSheet(
                        v.getContext(),
                        v.getContext().getString(R.string.app_name),
                        options,
                        (item, index) -> {
                            if ("Add Markup for Ticket".equals(item)) {
                                openTicketMarkupDialog(model);
                            } else if ("Add Markup for Tour Option".equals(item)) {
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

        private void openTicketMarkupDialog(RaynaTicketDetailModel model) {
            AddMarkUpDialog markupDialog = new AddMarkUpDialog();
            markupDialog.markup = String.valueOf(Utils.roundFloatValue(model.getMarkup()));
            markupDialog.callback = value -> {
                if (TextUtils.isEmpty(value)) return;
                requestAddMarkup(model.getId(), "", value);
            };

            markupDialog.show(getSupportFragmentManager(), "AddMarkUpDialog");
        }
    }
}
