package com.whosin.business.ui.activites.setting;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ActivityEditMarkupBinding;
import com.whosin.business.databinding.ItemEditMarkupBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.CommonModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;

public class EditMarkupActivity extends BaseActivity {

    private ActivityEditMarkupBinding binding;
    private RaynaTicketDetailModel ticketModel;
    private EditMarkupAdapter<RaynaTicketDetailModel> adapter;
    private boolean isEditing = false;


    @Override
    protected void initUi() {
        String ticketModelString = getIntent().getStringExtra("ticketModel");
        ticketModel = new Gson().fromJson(ticketModelString, RaynaTicketDetailModel.class);

        disableEditing();

        binding.ivTicketName.setText(ticketModel.getTitle());
        binding.ivTicketMarkupAmount.setText(
                ticketModel.getMarkup() >= 0
                        ? Utils.roundFloatValue(ticketModel.getMarkup()) + "%"
                        : ""
        );

        String image = null;

        if (ticketModel.getImages() != null
                && !ticketModel.getImages().isEmpty()
                && !Utils.isNullOrEmpty(ticketModel.getImages().get(0))) {

            image = ticketModel.getImages().get(0);
        }

        Graphics.loadImage(
                activity,
                image != null ? image : "",
                binding.ivImage
        );

        binding.ivTicketMarkupAmount.addTextChangedListener(
                percentageTextWatcher(binding.ivTicketMarkupAmount)
        );


        adapter = new EditMarkupAdapter<>();
        binding.itemRecycleView.setLayoutManager(new LinearLayoutManager(activity));
        binding.itemRecycleView.setAdapter(adapter);
        adapter.updateData(SessionManager.shared.geHomeBlockData().getTickets());
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());
        binding.btnEditGlobalMarkup.setOnClickListener(v -> {
            if (!isEditing) {
                enableEditing();
            } else {
                submitTicketMarkup();
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityEditMarkupBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    private TextWatcher percentageTextWatcher(EditText editText) {
        return new TextWatcher() {
            boolean isFormatting;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;
                String text = s.toString().replace("%", "");

                if (!text.isEmpty()) {
                    editText.setText(text + "%");
                    editText.setSelection(text.length());
                }
                isFormatting = false;
            }
        };
    }

    private void toggleEditing(boolean enable, EditText editText, View button, View buttonTextView) {
        editText.setEnabled(enable);
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);

        if (enable) {

            editText.post(() -> {
                editText.requestFocus();

                String text = editText.getText().toString();
                if (!text.isEmpty()) {
                    editText.setSelection(text.length());
                }

                Utils.showSoftKeyboard(activity, editText);
            });

            ((android.widget.TextView) buttonTextView).setText("Done");
            button.setBackgroundColor(
                    editText.getContext().getResources().getColor(R.color.txtBook)
            );

        } else {

            editText.clearFocus();

            ((android.widget.TextView) buttonTextView).setText("Edit");
            button.setBackgroundResource(R.drawable.invitesheet_edittext_background);

            Utils.hideKeyboard(activity);
        }
    }



    private void enableEditing() {
        isEditing = true;

        toggleEditing(
                true,
                binding.ivTicketMarkupAmount,
                binding.btnEditGlobalMarkup,
                binding.ivButtonText
        );
    }

    private void disableEditing() {
        isEditing = false;

        toggleEditing(
                false,
                binding.ivTicketMarkupAmount,
                binding.btnEditGlobalMarkup,
                binding.ivButtonText
        );
    }

    private void submitTicketMarkup() {
        String value = binding.ivTicketMarkupAmount.getText().toString().replace("%", "").trim();


        if (value.isEmpty()) {
            Toast.makeText(activity, "Please enter markup value", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float markupValue = Float.parseFloat(value);
            if (markupValue < 0) {
                Toast.makeText(activity, "Markup value cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }
            requestAddMarkup(ticketModel.getId(), "", value);
            disableEditing();
        } catch (NumberFormatException e) {
            Toast.makeText(activity, "Please enter a valid number for markup", Toast.LENGTH_SHORT).show();
        }

    }

    private void requestAddMarkup(String ticketId, String optionId, String markup) {
        JsonObject object = new JsonObject();
        object.addProperty("customTicketId", ticketId);
        object.addProperty("optionId", optionId);
        object.addProperty("markup", markup);

        binding.ivButtonText.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        DataService.shared( activity ).requestAddMarkup( object, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivButtonText.setVisibility(View.VISIBLE);
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.progressBar.setVisibility(View.GONE);
                binding.ivButtonText.setVisibility(View.VISIBLE);
                float value = Float.parseFloat(markup);

                binding.ivTicketMarkupAmount.setText(
                        value >= 0
                                ? Utils.roundFloatValue(value) + "%"
                                : ""
                );
                Alerter.create(activity).setTitle(getValue("thank_you")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("Markup Updated Successfully").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
            }
        } );
    }


    public class EditMarkupAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private int editingPosition = RecyclerView.NO_POSITION;


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_edit_markup));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTicketDetailModel model = (RaynaTicketDetailModel) getItem(position);

            if (model == null) return;

            viewHolder.binding.tvTitle.setText(model.getTitle());
            viewHolder.binding.ivTicketMarkupAmount.setText(
                    model.getMarkup() >= 0
                            ? Utils.roundFloatValue(model.getMarkup()) + "%"
                            : ""
            );

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

            boolean isEditing = position == editingPosition;
            toggleEditing(
                    isEditing,
                    viewHolder.binding.ivTicketMarkupAmount,
                    viewHolder.binding.btnEditMarkup,
                    viewHolder.binding.ivButtonText
            );

            if (viewHolder.watcher != null) {
                viewHolder.binding.ivTicketMarkupAmount.removeTextChangedListener(viewHolder.watcher);
            }
            viewHolder.watcher = percentageTextWatcher(viewHolder.binding.ivTicketMarkupAmount);
            viewHolder.binding.ivTicketMarkupAmount.addTextChangedListener(viewHolder.watcher);


            viewHolder.binding.btnEditMarkup.setOnClickListener(v -> {
                if (editingPosition == position) {
                    submitTicketMarkup(viewHolder, model);
                } else {
                    int oldPosition = editingPosition;
                    editingPosition = position;

                    if (oldPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(oldPosition);
                    }
                    notifyItemChanged(editingPosition);
                }
            });

        }

        private void disableEditing(ViewHolder holder) {
            int oldPosition = editingPosition;
            editingPosition = RecyclerView.NO_POSITION;

            if (oldPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldPosition);
            }
        }


        private void submitTicketMarkup(ViewHolder holder, RaynaTicketDetailModel model) {
            String value = holder.binding.ivTicketMarkupAmount
                    .getText().toString().replace("%", "").trim();

            if (value.isEmpty()) {
                Toast.makeText(activity, "Please enter markup value", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float markupValue = Float.parseFloat(value);
                if (markupValue < 0) {
                    Toast.makeText(activity, "Markup value cannot be negative", Toast.LENGTH_SHORT).show();
                    return;
                }

                requestAddMarkup(holder, model.getId(), "", value);
            } catch (NumberFormatException e) {
                Toast.makeText(activity, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        }


        private void requestAddMarkup(ViewHolder holder, String ticketId, String optionId, String markup) {
            JsonObject object = new JsonObject();
            object.addProperty("customTicketId", ticketId);
            object.addProperty("optionId", optionId);
            object.addProperty("markup", markup);

            holder.binding.ivButtonText.setVisibility(View.GONE);
            holder.binding.progressBar.setVisibility(View.VISIBLE);

            DataService.shared(activity).requestAddMarkup(
                    object,
                    new RestCallback<ContainerModel<CommonModel>>() {
                        @Override
                        public void result(ContainerModel<CommonModel> model, String error) {

                            holder.binding.progressBar.setVisibility(View.GONE);
                            holder.binding.ivButtonText.setVisibility(View.VISIBLE);

                            if (!Utils.isNullOrEmpty(error) || model == null) {
                                Toast.makeText(activity,
                                        R.string.service_message_something_wrong,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            float value = Float.parseFloat(markup);
                            holder.binding.ivTicketMarkupAmount.setText(
                                    Utils.roundFloatValue(value) + "%"
                            );

                            disableEditing(holder);

                            Alerter.create(activity).setTitle(getValue("thank_you")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("Markup Updated Successfully").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    }
            );
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemEditMarkupBinding binding;
            TextWatcher watcher;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemEditMarkupBinding.bind(itemView);
            }
        }
    }
}
