package com.whosin.app.ui.controller.promoter;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemSelectContactBinding;
import com.whosin.app.databinding.LayoutAddPromoterUserViewBinding;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.AddUserBottomSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PromoterAddUsersView  extends ConstraintLayout {

    private LayoutAddPromoterUserViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;

    public List<String> selctedUserId = new ArrayList<>();

    private  SelectContactAdapter<UserDetailModel> contactAdapter;


    public PromoterAddUsersView(Context context) {
        this( context, null );
    }

    public PromoterAddUsersView(Context context, AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public PromoterAddUsersView(Context context, AttributeSet attrs, int defStyle) {
        super( context, attrs, defStyle );
        LayoutInflater.from( context ).inflate( R.layout.offer_info_view_loader, this, true );

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater( context );
        asyncLayoutInflater.inflate( R.layout.layout_add_promoter_user_view, this, (view, resid, parent) -> {
            binding = LayoutAddPromoterUserViewBinding.bind( view );

            binding.title.setText(Utils.getLangValue("add_user"));

            binding.contactRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._5ssp);
            binding.contactRecycler.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
            contactAdapter = new SelectContactAdapter<>( supportFragmentManager );
            binding.contactRecycler.setAdapter( contactAdapter );
//            if (userList != null) {
//                activity.runOnUiThread( () -> contactAdapter.updateData( userList ) );
//            }

            binding.layoutContact.setOnClickListener(v -> {
                Utils.preventDoubleClick( v );
                if (supportFragmentManager != null) {
                    AddUserBottomSheet bottomSheet = new AddUserBottomSheet();
                    if (!selctedUserId.isEmpty()){
                        bottomSheet.selectedUserId.addAll(selctedUserId);
                    }
                    bottomSheet.setShareListener( data -> {
                        if (!data.isEmpty()){
                            addSelectedUsers( data );
                        }else {
                            selctedUserId.clear();
                            contactAdapter.updateData(new ArrayList<>());
                        }
                    } );
                    bottomSheet.show(supportFragmentManager, "1");
                } else {
                    Log.e("PromoterAddUsersView", "supportFragmentManager is null");
                }
            });

            PromoterAddUsersView.this.removeAllViews();
            PromoterAddUsersView.this.addView( view );
        } );


    }

    public void setUpData(Activity activity, FragmentManager supportFragmentManager) {
        this.activity = activity;

        this.supportFragmentManager = supportFragmentManager;

        if (binding == null) {
            return;
        }

    }

    public void addSelectedUsers(List<UserDetailModel> selectedUsers) {
        if (selectedUsers != null && !selectedUsers.isEmpty()) {
            selctedUserId.clear();
            selctedUserId.addAll(selectedUsers.stream().map(UserDetailModel::getUserId).collect(Collectors.toList()));
            contactAdapter.updateData(selectedUsers);
        }
    }

    public  class SelectContactAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final FragmentManager fragmentManager;

        public SelectContactAdapter(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_select_contact);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel model = (UserDetailModel) getItem(position);
            if (model != null) {
                String formattedName = model.getFullName().replace(" ", "\n");
                viewHolder.binding.txtName.setText(formattedName);
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.ivContact, model.getFirstName());
            }
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemSelectContactBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSelectContactBinding.bind(itemView);
            }
        }
    }

}
