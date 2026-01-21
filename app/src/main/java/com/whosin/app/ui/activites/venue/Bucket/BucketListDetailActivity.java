package com.whosin.app.ui.activites.venue.Bucket;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityBucketListDetailBinding;
import com.whosin.app.databinding.ItemImageSlideRecyclerBinding;
import com.whosin.app.databinding.ItemSelectFriendsUserBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.ImageUploadModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.venue.VenueGalleryActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BucketListDetailActivity extends BaseActivity {

    private ActivityBucketListDetailBinding binding;
    private CreateBucketListModel bucketListModel = new CreateBucketListModel();
    private InviteFriendAdapter<ContactListModel> inviteFriendAdapter = new InviteFriendAdapter();
    private final BucketImageUpdate<RatingModel> bucketImageUpdate = new BucketImageUpdate<>();
    private List<String> galleryList = new ArrayList<>();
    private String name;
    private Uri imageData;
    String bucketId = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {
        bucketId = getIntent().getStringExtra( "bucketId" );

        requestBucketDetail( bucketId );
        setupTabView();
        Graphics.applyBlurEffect( activity, binding.blurView );
        name = getIntent().getStringExtra( "name" );
        binding.tvName.setText( name );
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener( view -> {
            onBackPressed();
            finish();
        } );

        binding.rlContact.setOnClickListener( view -> {
            Utils.preventDoubleClick( view );

            ContactShareBottomSheet contactDialog = new ContactShareBottomSheet();
            contactDialog.defaultUsersList = bucketListModel.getSharedWith().stream().map( p -> p.getId() ).collect( Collectors.toList() );
            contactDialog.bucketUserId = bucketListModel.getUserId();
            contactDialog.setShareListener( this::requestBucketUpdate );
            contactDialog.show( getSupportFragmentManager(), "1" );
        } );

        binding.layoutAdd.setOnClickListener( view -> {
            Utils.preventDoubleClick( view );
            getImagePicker();
        } );

        binding.chatBtn.setOnClickListener( view -> {
            ChatModel chatModel = new ChatModel( bucketListModel );
            Intent intent = new Intent( activity, ChatMessageActivity.class );
            intent.putExtra( "chatModel", new Gson().toJson( chatModel ) );
            startActivity( intent );
        } );


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityBucketListDetailBinding.inflate( getLayoutInflater() );
        return binding.getRoot();

    }

    ActivityResultLauncher<Intent> startActivity = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            imageData = result.getData().getData();
            if (result.getData() != null) {
                imageData = result.getData().getData();
                binding.ivCover.setImageURI( imageData );
                if (imageData != null) {
                    binding.constraint.setVisibility( View.VISIBLE );
                }
            }
            requestImageUpload( imageData );
        }
    } );


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupViewPager(CreateBucketListModel model) {
    }

    private void setupTabView() {
        binding.tabLayout.addTab( binding.tabLayout.newTab().setText( "Offers" ) );
        binding.tabLayout.addTab( binding.tabLayout.newTab().setText( "Activity" ) );
        binding.tabLayout.addTab( binding.tabLayout.newTab().setText( "Event" ) );

        binding.tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                setupViewPager( bucketListModel );
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );

    }

    private void setInviteFriendAdapter(List<ContactListModel> sharedWith) {
        binding.friendRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
        binding.friendRecycler.setAdapter( inviteFriendAdapter );
        if (bucketListModel.getSharedWith() != null) {
            inviteFriendAdapter.updateData( sharedWith );
        } else {
            Toast.makeText( activity, "Data Null", Toast.LENGTH_SHORT ).show();
        }
    }

    private void setBucketImage(CreateBucketListModel data) {

        if (!data.getGalleries().isEmpty()) {
            binding.constraint.setVisibility( View.VISIBLE );
            binding.slideImagesLayout.setVisibility( View.VISIBLE );
            Graphics.loadImage( data.getGalleries().get( 0 ), binding.ivCover );
            galleryList = data.getGalleries();
            binding.imageSlideRecycler.setLayoutManager( new LinearLayoutManager( this, RecyclerView.HORIZONTAL, false ) );
            binding.imageSlideRecycler.setAdapter( bucketImageUpdate );
            List<RatingModel> models = data.getGalleries().stream().map( RatingModel::new ).collect( Collectors.toList() );
            if (!models.isEmpty()) {
                bucketImageUpdate.updateData( models );
            }

        } else {
            binding.slideImagesLayout.setVisibility( View.GONE );
            binding.constraint.setVisibility( View.GONE );
        }

    }

    private void getImagePicker() {
        ArrayList<String> data = new ArrayList<>();
        data.add( "Gallery" );
        data.add( "Camera" );
        Graphics.showActionSheet( activity, "Choose Any One", data, (data1, position) -> {
            switch (position) {
                case 0:
                    ImagePicker.with( activity ).galleryOnly().cropSquare().createIntent( intent -> {
                        startActivity.launch( intent );
                        return null;
                    } );
                    break;
                case 1:
                    ImagePicker.with( activity ).cameraOnly().cropSquare().createIntent( intent -> {
                        startActivity.launch( intent );
                        return null;
                    } );
                    break;
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestBucketDetail(String id) {
        Log.d( "TAG", "requestBucketDetail: " + id );
        showProgress();
        DataService.shared( activity ).requestBucketDetail( id, new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.getData() != null) {
                    bucketListModel = model.getData();

                    binding.tvName.setText( model.getData().getName() );
                    binding.tvEdit.setVisibility( SessionManager.shared.getUser().getId().equals( model.getData().getUserId() ) ? View.VISIBLE : View.GONE );

                    setBucketImage( model.getData() );
                    setInviteFriendAdapter( model.getData().getSharedWith() );
                    setupViewPager( model.getData() );

                    binding.scroll.setVisibility( View.VISIBLE );

                }
            }
        } );
    }

    private void requestBucketUpdate(List<ContactListModel> selectedContacts) {
        showProgress();
        String userIds = TextUtils.join( ",", selectedContacts.stream().map( ContactListModel::getId ).collect( Collectors.toList() ) );
        DataService.shared( activity ).requestBucketShare( bucketListModel.getId(), userIds, "", "", new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                requestBucketDetail( bucketListModel.getId() );
                Alerter.create( activity ).setTitle( "Thank you!" ).setText( model.message )
                        .setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle )
                        .setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
            }
        } );
    }

    public void requestImageUpload(Uri imageData) {
        showProgress();
        DataService.shared( activity ).requestUploadImage( activity, imageData, new RestCallback<ContainerModel<ImageUploadModel>>(this) {
            @Override
            public void result(ContainerModel<ImageUploadModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error )) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                requestAddGallery( model.getData().getUrl() );
            }
        } );
    }

    private void requestAddGallery(String imageUrl) {
        showProgress();
        DataService.shared( activity ).requestAddBucketGallery( bucketId, imageUrl, new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    hideProgress();
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }

//                if (model.getData() != null){
//                    galleryList = model.getData().getGalleries();
//                    imageSlideAdapter.updateData(galleryList);
//                }
                finish();
                startActivity( getIntent() );
//                overridePendingTransition(0, 0);

            }
        } );

    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class InviteFriendAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_select_friends_user ) );

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem( position );
            Graphics.loadImageWithFirstLetter( model.getImage(), viewHolder.binding.addImage, model.getFirstName() );
            viewHolder.binding.tvName.setText( model.getFirstName() );

            viewHolder.itemView.setOnClickListener( v -> {
                startActivity( new Intent( activity, OtherUserProfileActivity.class ).putExtra( "friendId", model.getId() ) );
            } );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemSelectFriendsUserBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemSelectFriendsUserBinding.bind( itemView );
            }
        }
    }

    public class BucketImageUpdate<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.item_image_slide_recycler );
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize( com.intuit.sdp.R.dimen._minus10sdp );
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize( com.intuit.sdp.R.dimen._48sdp );
            }
            return new VenueImageViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            VenueImageViewHolder viewHolder = (VenueImageViewHolder) holder;
            RatingModel model = (RatingModel) getItem( position );
            Graphics.loadRoundImage(model.getImage(), viewHolder.mBinding.image);
            viewHolder.mBinding.image.setOnClickListener( v -> {
                startActivity( new Intent( activity, VenueGalleryActivity.class )
                        .putExtra( "galleries", new Gson().toJson( galleryList ) ) );

            } );
        }

        @Override
        public int getItemViewType(int position) {

            if (getItemCount() == position + 1) {
                return 0;
            } else {
                return 1;

            }
        }

        @Override
        public int getItemCount() {
            if (galleryList.size() > 6) {
                return 6;
            } else {
                return galleryList.size();
            }
        }

        public class VenueImageViewHolder extends RecyclerView.ViewHolder {

            private final ItemImageSlideRecyclerBinding mBinding;

            public VenueImageViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemImageSlideRecyclerBinding.bind( itemView );
            }
        }
    }

    // endregion
    // --------------------------------------


}