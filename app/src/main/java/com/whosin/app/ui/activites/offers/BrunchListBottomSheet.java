package com.whosin.app.ui.activites.offers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.BrunchItemBinding;
import com.whosin.app.databinding.ContactShareBottomSheetBinding;
import com.whosin.app.databinding.FragmentBrunchListBottomSheetBinding;
import com.whosin.app.databinding.InviteContactFreindItemBinding;
import com.whosin.app.databinding.ItemSectionHeaderBinding;
import com.whosin.app.service.Repository.ContactRepository;
import com.whosin.app.service.manager.ContactManager;
import com.whosin.app.service.models.BrunchListModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.ui.activites.venue.Bucket.ContactShareBottomSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BrunchListBottomSheet extends DialogFragment {

    private FragmentBrunchListBottomSheetBinding binding;
    public List<BrunchListModel> brunchListModel = new ArrayList<>();
    public String brunchId = "";
    private CommanCallback<BrunchListModel> listener;
    private BrunchListAdapter<BrunchListModel> brunchListAdapter = new BrunchListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    public void initUi(View view) {
        binding = FragmentBrunchListBottomSheetBinding.bind(view);
        binding.offersRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.offersRecycler.setAdapter(brunchListAdapter);
        updateList();
    }

    public void setListener() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(view -> {
            dismiss();
        });
    }

    public int getLayoutRes() {
        return R.layout.fragment_brunch_list_bottom_sheet;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            getView().post(() -> {
                View parent = (View) getView().getParent();
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
                int peekHeight = parent.getHeight();
                behavior.setPeekHeight(peekHeight);
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void updateList() {
        if (!brunchListModel.isEmpty()){
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            brunchListAdapter.updateData(brunchListModel);
        }else {
            binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
        }
    }

    // endregion
    // --------------------------------------
    // region public
    // --------------------------------------

    public void setShareListener(CommanCallback<BrunchListModel> listener) {
        this.listener = listener;
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class BrunchListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.brunch_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            BrunchListModel listModel = (BrunchListModel) getItem(position);

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.vBinding.tvUserName.setText(listModel.getTitle());
            Graphics.loadImageWithFirstLetter(listModel.getImage(), viewHolder.vBinding.ivUserProfile, listModel.getTitle());
            viewHolder.vBinding.tvUserNumber.setText(listModel.getDescription());
            viewHolder.vBinding.ivCheck.setVisibility(View.VISIBLE);

            if (brunchListModel != null) {
                boolean isSelected =  brunchId.equals(listModel.getId());
                viewHolder.vBinding.ivCheck.setChecked(isSelected);
            }

            viewHolder.vBinding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (listener != null) {
                        listener.onReceive(listModel);
                    }
                    dismiss();
                }
            });

            viewHolder.vBinding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReceive(listModel);
                }
                dismiss();
                notifyDataSetChanged();
            });


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public BrunchItemBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = BrunchItemBinding.bind(itemView);
            }
        }


    }

    // endregion
    // --------------------------------------


}