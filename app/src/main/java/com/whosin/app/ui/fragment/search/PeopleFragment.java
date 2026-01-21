package com.whosin.app.ui.fragment.search;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPeopleBinding;
import com.whosin.app.databinding.InviteContactFreindItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.List;


public class PeopleFragment extends BaseFragment {


    private FragmentPeopleBinding binding;

    private ContactSearchAdapter<ContactListModel> contactSearchAdapter = new ContactSearchAdapter();
    private String page = "1";

    private int count = 0;

    private List<ContactListModel> allContacts;

    private String searchQuery = "";

    private Handler handler = new Handler();




    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public PeopleFragment(String searchQuery){

        this.searchQuery = searchQuery;
    }


    @Override
    public void initUi(View view) {

        binding = FragmentPeopleBinding.bind( view );

        if(!searchQuery.isEmpty()){
            requestUserSearch();

        }

        binding.contactRecycler.setLayoutManager( new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.contactRecycler.setAdapter( contactSearchAdapter );
    }

    @Override
    public void setListeners() {



    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_people;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------




    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUserSearch() {
        DataService.shared( requireActivity() ).requestUserSearch( searchQuery, page, "30", new RestCallback<ContainerListModel<ContactListModel>>(this) {
            @Override
            public void result(ContainerListModel<ContactListModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( context, model.message, Toast.LENGTH_SHORT ).show();

                if(searchQuery.isEmpty()){
                    binding.contactRecycler.setVisibility(View.GONE);
                }else {
                    contactSearchAdapter.updateData( model.data);

                }

            }
        } );
    }

    private void reqFollowUnFollow(ContactListModel contactListModel) {
        DataService.shared( requireActivity() ).requestUserFollowUnFollow( contactListModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>(this) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {

                if (!Utils.isNullOrEmpty( error ) || model == null || model.getData() == null) {
                    Toast.makeText( requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                contactListModel.setFollow(model.getData().getStatus());
                switch (model.getData().getStatus()) {
                    case "unfollowed":
                        Alerter.create(getActivity()).setTitle("Oh Snap!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("You have unfollowed " + contactListModel.getFullName()).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "approved":
                        Alerter.create(getActivity()).setTitle("Thank you!").setText("For following " + contactListModel.getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "pending":
                        Alerter.create(getActivity()).setTitle("Thank you!").setText("You have requested for follow " + contactListModel.getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "cancelled":
                        Alerter.create(getActivity()).setTitle("Oh Snap!").setText("You have cancelled follow request of " + contactListModel.getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                }
                contactSearchAdapter.notifyDataSetChanged();
            }
        } );
    }
    private void requestBlockUserAdd(String id, String name) {
        showProgress();
        DataService.shared(requireActivity()).requestBlockUser( id, new RestCallback<ContainerModel<CommonModel>>() {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Alerter.create(requireActivity()).setTitle( "Oh Snap! " ).setText( "You have blocked " + name ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();


            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class ContactSearchAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.invite_contact_freind_item ) );

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ContactListModel listModel = (ContactListModel) getItem( position );
            if (getItemViewType( position ) == 1) {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.vBinding.tvUserName.setText( listModel.getFullName() );
                Graphics.loadImageWithFirstLetter( listModel.getImage(), viewHolder.vBinding.ivUserProfile, listModel.getFullName() );

                viewHolder.vBinding.ivCheck.setVisibility( View.GONE );
                viewHolder.vBinding.optionContainer.setVisibility( View.GONE );

                if (listModel.isSynced()) {
                    viewHolder.vBinding.optionContainer.setVisibility(View.VISIBLE);
                    viewHolder.vBinding.optionContainer.setupView(listModel, requireActivity(), data -> {
                        contactSearchAdapter.notifyDataSetChanged();
                    });

                } else {
                    viewHolder.vBinding.ivCheck.setVisibility( View.VISIBLE );
                    viewHolder.itemView.setOnClickListener( view -> {

                        boolean isChecked = viewHolder.vBinding.ivCheck.isChecked();
                        viewHolder.vBinding.ivCheck.setChecked( !isChecked );
                        if (isChecked) {
                            count--;
                        } else {
                            count++;
                        }
                    } );

                }


            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private InviteContactFreindItemBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                vBinding = InviteContactFreindItemBinding.bind( itemView );
            }
        }

        @Override
        public int getItemViewType(int position) {
            ContactListModel inModel = (ContactListModel) getItem( position );
            if (inModel.getId().equals( "-1" )) {
                return 0;
            } else {
                return 1;
            }
        }
    }


    // endregion
    // --------------------------------------

}