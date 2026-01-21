package com.whosin.app.ui.fragment.search;

import android.view.View;

import com.whosin.app.R;
import com.whosin.app.databinding.FragmentVenueBinding;
import com.whosin.app.ui.fragment.comman.BaseFragment;


public class VenueFragment extends BaseFragment {

    private FragmentVenueBinding binding;
    private String page = "1";

    private String search = "";

//    private VenueSearchAdapter<VenueObjectModel> adapter = new VenueSearchAdapter();

    private boolean isFollow;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentVenueBinding.bind( view );
        setVenueAdapter();
//        requestVenueSearch();

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_venue;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setVenueAdapter() {
//        binding.venueRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
//        binding.venueRecycler.setAdapter( adapter );
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------
//    private void requestVenueSearch() {
//        DataService.shared( requireActivity() ).requestVenueSearch( search, page, "30", new RestCallback<ContainerListModel<VenueObjectModel>>() {
//            @Override
//            public void result(ContainerListModel<VenueObjectModel> model, String error) {
//                if (!Utils.isNullOrEmpty( error ) || model == null) {
//                    Toast.makeText( requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
//                    return;
//                }
//                adapter.updateData( model.data );
//
//
//            }
//        } );
//    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

//    public class VenueSearchAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
//
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_search_venue ) );
//
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            ViewHolder viewHolder = (ViewHolder) holder;
//            VenueObjectModel model = (VenueObjectModel) getItem( position );
//            viewHolder.binding.tvTitle.setText( model.getName() );
//            viewHolder.binding.tvAddress.setText( model.getAddress() );
//            Graphics.loadImage( model.getLogo(), viewHolder.binding.iconImg );
//            viewHolder.binding.tvDiscription.setText( TextUtils.join( ", ", model.getCuisine() ) );
//
//            if (model.isIsFollowing()) {
//                viewHolder.binding.btnBucketList.setText( "Following" );
//            } else {
//                viewHolder.binding.btnBucketList.setText( "Follow" );
//            }
//
//            viewHolder.binding.btnBucketList.setOnClickListener( view -> {
//                viewHolder.reqFollowUnFollow( model.getId() );
//            } );
//
//
//            if (!model.getBookingUrl().isEmpty() || model.getBookingUrl() != null) {
//                viewHolder.binding.cardBook.setVisibility( View.VISIBLE );
//
//            } else {
//                viewHolder.binding.cardBook.setVisibility( View.GONE );
//
//            }
//            viewHolder.binding.btnBookNow.setOnClickListener( view -> {
//                viewHolder.openUrlInChrome( model.getBookingUrl() );
//            } );
//
//            viewHolder.binding.linearHeader.setOnClickListener( view -> {
//                Graphics.openVenueDetail(model, model.getId() );
//            } );
//
//
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//
//            private ItemSearchVenueBinding binding;
//
//            public ViewHolder(@NonNull View itemView) {
//                super( itemView );
//                binding = ItemSearchVenueBinding.bind( itemView );
//            }
//
//            private void reqFollowUnFollow(String id) {
//                DataService.shared( requireActivity() ).requestVenueFollow( id, new RestCallback<ContainerModel<FollowUnfollowModel>>() {
//                    @Override
//                    public void result(ContainerModel<FollowUnfollowModel> model, String error) {
//
//                        if (!Utils.isNullOrEmpty( error ) || model == null) {
//                            Toast.makeText( requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
//                            return;
//                        }
//                        model.getData();
//
//                        if (!isFollow) {
//                            binding.btnBucketList.setText( "Follow" );
//                            Alerter.create( requireActivity() ).setTitle( "Oh Snap!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "You have unfollowed " ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
//                            isFollow = true;
//                        } else {
//                            binding.btnBucketList.setText( "Following" );
//                            Alerter.create( requireActivity() ).setTitle( "Thank you!" ).setText( "For following " ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
//                            isFollow = false;
//                        }
//                    }
//                } );
//            }
//
//            private void openUrlInChrome(String url) {
//                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
//                intent.setPackage( "com.android.chrome" );
//
//                if (intent.resolveActivity( getActivity().getPackageManager() ) != null) {
//                    startActivity( intent );
//                }
//            }
//
//        }
//
//
//    }


    // endregion
    // --------------------------------------

}