package com.whosin.app.ui.activites.yacht;

import static com.whosin.app.comman.Graphics.context;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityYachtClubDetailBinding;
import com.whosin.app.databinding.InfoBottomsheetDialogBinding;
import com.whosin.app.databinding.ItemImageSlideRecyclerBinding;
import com.whosin.app.databinding.ItemPhoneNumberBinding;
import com.whosin.app.databinding.ItemRatingReviewRecyclerBinding;
import com.whosin.app.databinding.ItemYachtExclusiveBinding;
import com.whosin.app.databinding.ItemYachtSpecificationsBinding;
import com.whosin.app.databinding.PagerItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VenueTimingModel;
import com.whosin.app.service.models.YachtClubModel;
import com.whosin.app.service.models.YachtDetailModel;
import com.whosin.app.service.models.YachtsOfferModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.activites.home.activity.WriteReviewActivity;
import com.whosin.app.ui.activites.venue.VenueActivity;
import com.whosin.app.ui.activites.venue.VenueGalleryActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class YachtClubDetailActivity extends BaseActivity {
    private ActivityYachtClubDetailBinding binding;
    private YachtClubModel yachtClubModel;

    private ExclusiveDealAdapter<YachtDetailModel> exclusiveDealAdapter = new ExclusiveDealAdapter<>();

    private final YachtImageSlideAdapter imageSlideAdapter = new YachtImageSlideAdapter();

    private final RatingReviewAdapter<CurrentUserRatingModel> ratingReviewAdapter = new RatingReviewAdapter<>();

    private List<String> galleryList = new ArrayList<>();

    private InfoBottomsheetDialogBinding mBinding;




    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        String yachtClubId = getIntent().getStringExtra( "yachtClubId" );

        requestYachtClubDetail( yachtClubId );

        binding.exclusiveRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
        binding.exclusiveRecycler.setAdapter( exclusiveDealAdapter );

        binding.ratingReviewRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
        binding.ratingReviewRecycler.setAdapter( ratingReviewAdapter );

        Graphics.applyBlurEffect(activity,binding.blurView);
    }

    @Override
    protected void setListeners() {

        binding.imageInfo.setOnClickListener( view -> {
            Utils.preventDoubleClick( view );
            if (yachtClubModel.getPhone() == null || yachtClubModel.getEmail() == null) {
                Toast.makeText( this, "Details Not Available", Toast.LENGTH_SHORT ).show();
            } else {
                infoBottomSheetDialog();
            }
        } );

        binding.ivClose.setOnClickListener( view -> {onBackPressed();} );


        binding.linearReview.setOnClickListener( view -> {
            Utils.preventDoubleClick( view );
            WriteReviewActivity bottomSheet = new WriteReviewActivity( yachtClubModel.getId(), yachtClubModel.getCurrentUserRating(), "venues" );
            bottomSheet.activity = activity;
            bottomSheet.show( getSupportFragmentManager(), "1" );
        } );

        binding.tvSeeAll.setOnClickListener( view -> startActivity( new Intent( activity, SeeAllRatingReviewActivity.class )
                .putExtra( "id", yachtClubModel.getId() )
                .putExtra( "type", "venues" )
                .putExtra("isYacht", true)
                .putExtra( "currentUserRating", new Gson().toJson( yachtClubModel.getCurrentUserRating() ) ) ) );

        binding.ivShare.setOnClickListener( view -> {
            Intent intent = new Intent(activity, VenueShareActivity.class);
            intent.putExtra( "yachtClub",new Gson().toJson( yachtClubModel) );
            intent.putExtra( "type","yachtClub" );
            activity.startActivity(intent);

        } );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityYachtClubDetailBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void infoBottomSheetDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog( this );
        mBinding = InfoBottomsheetDialogBinding.inflate( getLayoutInflater() );
        bottomSheetDialog.setContentView( mBinding.getRoot() );
        if (yachtClubModel.getEmail() != null && !yachtClubModel.getEmail().isEmpty()) {
            mBinding.tvMail.setText( yachtClubModel.getEmail() );
        } else {
            mBinding.layoutEmail.setVisibility( View.GONE );
        }

        if (yachtClubModel.getPhone() != null && !yachtClubModel.getPhone().isEmpty()) {
            String phone = yachtClubModel.getPhone();
            String[] phoneNumbers;
            if (phone.contains( "," )) {
                phoneNumbers = phone.split( ",\\s*" );
            } else {
                phoneNumbers = new String[]{phone};
            }

            mBinding.listRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
           PhoneNumberAdapter adapter = new PhoneNumberAdapter( phoneNumbers );
            mBinding.listRecycler.setAdapter( adapter );
        } else {
            mBinding.listRecycler.setVisibility( View.GONE );
        }


        mBinding.tvMail.setOnClickListener( view -> {
            Intent i = new Intent( Intent.ACTION_SENDTO );
            i.setType( "message/rfc822" );
            i.setData( Uri.parse( "mailto:" + yachtClubModel.getEmail() ) );

            startActivity( i );
        } );
        mBinding.layoutCancel.setOnClickListener( view -> {
            bottomSheetDialog.cancel();
        } );
        bottomSheetDialog.show();
    }


    private void setDetail() {
        if (yachtClubModel != null) {

            binding.tvTitle.setText( yachtClubModel.getName());
            binding.tvAddress.setText( yachtClubModel.getAddress() );
            Graphics.loadImage( yachtClubModel.getLogo(),binding.iconImg);
            Graphics.loadImage( yachtClubModel.getCover(),binding.ivCover);

            binding.tvDescription.setText( yachtClubModel.getAbout() );
            binding.tvDescription.post( () -> {
                int lineCount = binding.tvDescription.getLineCount();
                if (lineCount > 2) {
                    Utils.makeTextViewResizable( binding.tvDescription, 3, 3, ".. See More", true );
                }
            } );

            if (yachtClubModel.isIsAllowReview()) {
                binding.linearReview.setVisibility( View.VISIBLE );
                binding.tvSeeAll.setVisibility( View.VISIBLE );
            } else {
                binding.linearReview.setVisibility( View.GONE );
                binding.tvSeeAll.setVisibility( View.GONE );
            }
            if (yachtClubModel.isIsAllowRatting()) {
                binding.linearReview.setVisibility( View.VISIBLE );
            } else {
                binding.linearRating.setVisibility( View.GONE );
                binding.ratingReviewRecycler.setVisibility( View.GONE );
                binding.linearReview.setVisibility( View.GONE );
            }
            binding.tvOpen.setTextColor( getResources().getColor( yachtClubModel.isOpen() ? R.color.green : R.color.redColor ) );
            binding.tvOpen.setText( yachtClubModel.isOpen() ? "Open" : "Closed" );

            List<VenueTimingModel> models = yachtClubModel.getTimings();
            if (!models.isEmpty()) {
                String today = Utils.formatDate( new Date(), "EEE" );
                Optional<VenueTimingModel> todayTiming = yachtClubModel.getTimings().stream().filter( p -> p.getDay().equalsIgnoreCase( today ) ).findFirst();
                if (todayTiming.isPresent()) {
                    binding.txtTime.setText( String.format( "%s - %s", todayTiming.get().getOpeningTime(), todayTiming.get().getClosingTime() ) );
                } else {
                    binding.txtTime.setText( "00:00 - 00:00" );
                }
            } else {
                binding.ilOpenTime.setVisibility( View.GONE );
            }
            binding.ivTime.setOnClickListener( view -> timeDialog( yachtClubModel.getTimings() ) );

        }
    }


    private void setYachtImageSlideAdapter() {
        binding.imageSlideRecycler.setLayoutManager( new LinearLayoutManager( this, RecyclerView.HORIZONTAL, false ) );
        binding.imageSlideRecycler.setAdapter( imageSlideAdapter );
        List<String> list = new ArrayList<>();

        //List<String> list = data.getGalleries();
        list.add( 0, yachtClubModel.getCover());
        if (yachtClubModel.getGalleries() != null && !yachtClubModel.getGalleries().isEmpty()) {
            list.addAll( yachtClubModel.getGalleries() );
        }
        galleryList = list;
        if (!list.isEmpty()) {
            imageSlideAdapter.updateData( list );
        }
    }
    private void timeDialog(List<VenueTimingModel> timingDialogs) {
        VenueTimingDialog dialog = new VenueTimingDialog( timingDialogs, activity );
        dialog.show( getSupportFragmentManager(), "1" );
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestYachtClubDetail(String id) {

        showProgress();
        DataService.shared( activity ).requestYachtClubDetail( id, new RestCallback<ContainerModel<YachtClubModel>>(this) {
            @Override
            public void result(ContainerModel<YachtClubModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.getData() != null) {
                    binding.constraintYacht.setVisibility( View.VISIBLE );
                    yachtClubModel = model.getData();
                    setDetail();
                    activity.runOnUiThread( () -> binding.yachtListView.setupData( model.getData(), activity, getSupportFragmentManager() ) );
                    exclusiveDealAdapter.updateData( model.getData().getYachts() );
                    setYachtImageSlideAdapter();
                }
            }
        } );
    }

    public class ExclusiveDealAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_yacht_exclusive ) );

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            YachtDetailModel model = (YachtDetailModel) getItem( position );
            viewHolder.mBinding.tvSubTitle.setText( model.getAbout() );
            viewHolder.mBinding.tvTitle.setText( model.getName() );
            if(model.getImages() != null && !model.getImages().isEmpty()){
                Graphics.loadImage( model.getImages().get( 0 ),viewHolder.mBinding.ivCover );
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ItemYachtExclusiveBinding mBinding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemYachtExclusiveBinding.bind( itemView );
            }
        }
    }


    public class YachtImageSlideAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

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

            if (galleryList != null) {
                String image = galleryList.get( position );
                Graphics.loadRoundImage( image, viewHolder.mBinding.image );
                viewHolder.mBinding.image.setOnClickListener( v -> startActivity( new Intent( activity, VenueGalleryActivity.class ).putExtra( "galleries", new Gson().toJson( galleryList ) ) ) );
            }

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
            return Math.min( galleryList.size(), 6 );
        }

        public class VenueImageViewHolder extends RecyclerView.ViewHolder {
            private final ItemImageSlideRecyclerBinding mBinding;

            public VenueImageViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemImageSlideRecyclerBinding.bind( itemView );
            }
        }
    }
    public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.ViewHolder> {
        private String[] phoneNumbers;

        public PhoneNumberAdapter(String[] phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.item_phone_number, parent, false );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.binding.tvPhone.setText( phoneNumbers[position] );
            holder.itemView.setOnClickListener( view -> {
                Intent callIntent = new Intent( Intent.ACTION_DIAL );
                callIntent.setData( Uri.parse( "tel:" + phoneNumbers[position] ) );
                startActivity( callIntent );
            } );
        }

        @Override
        public int getItemCount() {
            return phoneNumbers.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ItemPhoneNumberBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemPhoneNumberBinding.bind( itemView );
            }
        }
    }


    public class RatingReviewAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.item_rating_review_recycler );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            if (itemCount > 1) {
                params.width = (int) (Graphics.getScreenWidth( context ) * 0.85);
            } else {
                params.width = (int) (Graphics.getScreenWidth( context ) * 0.90);
            }
            view.setLayoutParams( params );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CurrentUserRatingModel model = (CurrentUserRatingModel) getItem( position );
            viewHolder.mBinding.txtReview.setText( model.getReview() );
            viewHolder.mBinding.txtReply.setText( model.getReply() );
            viewHolder.mBinding.replyLinear.setVisibility( View.GONE );
            if (model.getReply() == null) {
                viewHolder.mBinding.replyLinear.setVisibility( View.GONE );
            }

            viewHolder.mBinding.txtDate.setText( Utils.convertMainDateFormat( model.getCreatedAt() ) );
            viewHolder.mBinding.rating.setRating( model.getStars() );

          /*  Optional<ContactListModel> modelOptional = yachtClubModel.getUsers().stream().
                    filter( p -> p.getId().equals( model.getUserId() ) ).findFirst();
            if (modelOptional.isPresent()) {
                Graphics.loadImageWithFirstLetter( modelOptional.get().getImage(),
                        viewHolder.mBinding.ivRating, modelOptional.get().getFirstName() );
                viewHolder.mBinding.txtTitle.setText( modelOptional.get().getFullName() );
            }*/


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemRatingReviewRecyclerBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemRatingReviewRecyclerBinding.bind( itemView );
            }
        }
    }



    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------
 }