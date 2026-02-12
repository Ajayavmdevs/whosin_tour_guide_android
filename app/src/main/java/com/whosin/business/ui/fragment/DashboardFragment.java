package com.whosin.business.ui.fragment;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.FragmentDashboardBinding;
import com.whosin.business.databinding.ItemMarkupListBinding;
import com.whosin.business.databinding.SelectDaysItemBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.CategoriesModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.setting.EditMarkupActivity;
import com.whosin.business.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends BaseFragment {

    private FragmentDashboardBinding binding;
    private final ItemListAdapter<CategoriesModel> adapterTag = new ItemListAdapter<>();
    private MarkupAdapter<RaynaTicketDetailModel> adapter;
    private boolean isEditing = false;
    private boolean isFormatting;
    private List<CategoriesModel> filterList = new ArrayList<>();
    private List<RaynaTicketDetailModel> optionsList = new ArrayList<>();

    @Override
    public void initUi(View view) {
        binding = FragmentDashboardBinding.bind(view);
        disableEditing();
        requestMarkupList(true);
        float markup = SessionManager.shared.getUser().getGlobelMarkup();

        binding.ivGlobalMarkupAmount.setText(
                markup >= 0
                        ? Utils.roundFloatValue(markup) + "%"
                        : ""
        );

        binding.ivGlobalMarkupAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;

                String text = s.toString();

                if (text.contains("%")) {
                    text = text.replace("%", "");
                }

                if (!text.isEmpty()) {
                    binding.ivGlobalMarkupAmount.setText(text + "%");
                    binding.ivGlobalMarkupAmount.setSelection(text.length());
                }

                isFormatting = false;
            }
        });


        List<CategoriesModel> TagList = new ArrayList<>();

        if (SessionManager.shared.geExploreBlockData() != null) {

            List<CategoriesModel> tagList = new ArrayList<>();

            if (SessionManager.shared.geExploreBlockData().getCities() != null) {
                tagList.addAll(SessionManager.shared.geExploreBlockData().getCities());
            }

            if (!tagList.isEmpty()) {
                TagList.addAll(tagList);
            }
        }


        binding.itemRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.itemRecycleView.setAdapter(adapterTag);
        adapterTag.updateData(TagList);


        adapter = new MarkupAdapter<>();
        binding.itemRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.itemRecycler.setAdapter(adapter);

    }

    @Override
    public void setListeners() {
        binding.btnEditGlobalMarkup.setOnClickListener(v -> {
            if (!isEditing) {
                enableEditing();
            } else {
                submitGlobalMarkup();
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            requestMarkupList(false);
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_dashboard;
    }

    private void enableEditing() {
        isEditing = true;

        binding.ivGlobalMarkupAmount.setEnabled(true);
        binding.ivGlobalMarkupAmount.setFocusable(true);
        binding.ivGlobalMarkupAmount.setFocusableInTouchMode(true);
        binding.ivGlobalMarkupAmount.requestFocus();

        String text = binding.ivGlobalMarkupAmount.getText().toString();
        if (!text.isEmpty()) {
            binding.ivGlobalMarkupAmount.setSelection(text.length());
        }

        binding.ivButtonText.setText("Done");

        binding.btnEditGlobalMarkup.setBackgroundColor(
                getResources().getColor(R.color.txtBook)
        );

        Utils.showSoftKeyboard(requireContext(), binding.ivGlobalMarkupAmount);
    }


    private void submitGlobalMarkup() {
        String value = binding.ivGlobalMarkupAmount.getText().toString().replace("%", "").trim();


        if (value.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter markup value", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("globelMarkup", value);

        requestUpdateProfile(json, () -> {

            float markup = Float.parseFloat(value);
            SessionManager.shared.getUser().setGlobelMarkup(markup);

            binding.ivGlobalMarkupAmount.setText(
                    markup >= 0
                            ? Utils.roundFloatValue(markup) + "%"
                            : ""
            );


            Toast.makeText(requireActivity(), "Markup Updated", Toast.LENGTH_SHORT).show();
            disableEditing();
        });
    }


    private void disableEditing() {
        isEditing = false;

        binding.ivGlobalMarkupAmount.setEnabled(false);
        binding.ivGlobalMarkupAmount.setFocusable(false);
        binding.ivGlobalMarkupAmount.setFocusableInTouchMode(false);
        binding.ivGlobalMarkupAmount.clearFocus();

        binding.ivButtonText.setText("Edit");

        binding.btnEditGlobalMarkup.setBackgroundResource(
                R.drawable.invitesheet_edittext_background
        );

        Utils.hideKeyboard(requireActivity());
    }


    private void requestMarkupList(boolean showLoader) {
        if (showLoader) {
            showProgress();
        }
        DataService.shared( requireActivity() ).requestMarkupList(new RestCallback<ContainerListModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaTicketDetailModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                    binding.itemRecycler.setVisibility( View.VISIBLE );
                    adapter.updateData( model.data );
                    optionsList.clear();

                    if (model.data != null) {
                        int limit = Math.min(3, model.data.size());
                        optionsList.addAll(model.data.subList(0, limit));
                    }

                } else {
                    binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                    binding.itemRecycler.setVisibility( View.GONE );
                }
            }
        } );
    }

    private void requestUpdateProfile(JsonObject jsonObject, Runnable onSuccess) {

//        showProgress();
        binding.ivButtonText.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        SessionManager.shared.updateProfile(requireActivity(), jsonObject, (success, error) -> {
            hideProgress();
            binding.progressBar.setVisibility(View.GONE);
            binding.ivButtonText.setVisibility(View.VISIBLE);

            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                return;
            }

            if (onSuccess != null) {
                onSuccess.run();
            }
        });
    }

    private class ItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.swipe_tag_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CategoriesModel model = (CategoriesModel) getItem( position );
            viewHolder.binding.iconText.setText(model.getName());

            viewHolder.itemView.setOnClickListener( view -> {
            } );

            boolean idFound = filterList.stream().anyMatch(ids -> ids.getId().equals(model.getId()));
            if (!idFound) {
                viewHolder.binding.linearMainView.setBackground(requireActivity().getResources().getDrawable(R.drawable.days_background));
            } else {
                viewHolder.binding.linearMainView.setBackground(requireActivity().getResources().getDrawable(R.drawable.selected_bg));
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final SelectDaysItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = SelectDaysItemBinding.bind( itemView );
            }
        }
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
            viewHolder.binding.tvMarkupValue.setText(String.valueOf(model.getMarkup()));
            viewHolder.binding.ivMore.setOnClickListener(v -> {

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

            viewHolder.updateOptionsList(optionsList);

            boolean isExpanded = model.isOptionsExpanded();
            viewHolder.binding.itemRecycler.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            viewHolder.binding.ivDropDown.setRotation(isExpanded ? 270f : 90f);
            viewHolder.binding.tvOptionsText.setText(
                    isExpanded ? "Hide Options" : "View Options"
            );

            viewHolder.binding.ivMore.setOnClickListener(view -> {
                Intent intent = new Intent(requireActivity(), EditMarkupActivity.class);
                intent.putExtra("ticketModel", new Gson().toJson(model));
                startActivity(intent);
            });


            // TOGGLE CLICK
            viewHolder.binding.tvOptionsView.setOnClickListener(v -> {
                boolean expanded = !model.isOptionsExpanded();
                model.setOptionsExpanded(expanded);

                viewHolder.binding.itemRecycler.setVisibility(expanded ? View.VISIBLE : View.GONE);
                viewHolder.binding.ivDropDown.animate().rotation(expanded ? 270f : 90f).setDuration(200).start();
                viewHolder.binding.tvOptionsText.setText(
                        expanded ? "Hide Options" : "View Options"
                );
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemMarkupListBinding binding;
            private OptionMarkupAdapter<RaynaTicketDetailModel> optionMarkupAdapter = new OptionMarkupAdapter<>();


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemMarkupListBinding.bind(itemView);
//                binding.itemRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
//                binding.itemRecycler.setAdapter(adapter);
                LinearLayoutManager lm = new LinearLayoutManager(itemView.getContext());
                lm.setAutoMeasureEnabled(true);

                binding.itemRecycler.setLayoutManager(lm);
                binding.itemRecycler.setNestedScrollingEnabled(false);
                binding.itemRecycler.setHasFixedSize(true);
                binding.itemRecycler.setAdapter(optionMarkupAdapter);
            }

            public void updateOptionsList(List<RaynaTicketDetailModel> options) {
                optionMarkupAdapter.updateData(options);
            }
        }
    }

    public class OptionMarkupAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

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

            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) viewHolder.binding.ivMainView.getLayoutParams();

            params.setMargins(
                    (int) Utils.dpToPx(viewHolder.itemView.getContext(), 6),
                    (int) Utils.dpToPx(viewHolder.itemView.getContext(), 4),
                    (int) Utils.dpToPx(viewHolder.itemView.getContext(), 6),
                    (int) Utils.dpToPx(viewHolder.itemView.getContext(), 4)
            );

            viewHolder.binding.ivMainView.setLayoutParams(params);
            viewHolder.binding.ivMainView.setBackgroundResource(R.color.transparent);

            Utils.hideViews(viewHolder.binding.ivMore, viewHolder.binding.tvOptionsView);

            viewHolder.binding.tvTitle.setText(model.getTitle());
            viewHolder.binding.tvMarkupValue.setText(String.valueOf(model.getMarkup()));
            viewHolder.binding.ivMore.setOnClickListener(v -> {

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
