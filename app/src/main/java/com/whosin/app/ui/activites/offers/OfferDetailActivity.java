package com.whosin.app.ui.activites.offers;

import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.DiscountCalculator;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityOfferDetailBinding;
import com.whosin.app.databinding.VenueOfferItemPackageRecyclerBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.VenueBuyNowActivity;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;

public class OfferDetailActivity extends BaseActivity {

    private ActivityOfferDetailBinding binding;
    private final OfferPackagerAdapter<PackageModel> adapter = new OfferPackagerAdapter<>();
    private OffersModel offersModel;
    private String offerId;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.tvStartDateTitle.setText(Utils.getLangValue("start_date"));
        binding.tvEndDateTitle.setText(Utils.getLangValue("end_date"));

        binding.btnBucketList.setText(Utils.getLangValue("bucketlist"));
        binding.btnBuyNow.setText(Utils.getLangValue("buy_now"));
        binding.tvExpiredTitle.setText(getValue("expired"));

        offerId = getIntent().getStringExtra("offerId");
        if (TextUtils.isEmpty(offerId)) {
            Toast.makeText( activity, "Invalid offer id", Toast.LENGTH_SHORT ).show();
            onBackPressed();
        }
        binding.packageRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        binding.packageRecycler.setAdapter( adapter );
        Graphics.applyBlurEffect( activity, binding.blurView );
        requestVenueOfferDetail();
    }

    @Override
    protected void setListeners() {

        binding.blurView.setOnClickListener( view -> Graphics.openVenueDetail( activity,  offersModel.getVenue().getId() ));

        binding.imgRecommandation.setOnClickListener( v -> reqRecommendation( offersModel.getId() ));

        binding.ivClose.setOnClickListener( view -> onBackPressed());

        binding.btnBucketList.setOnClickListener( v -> {
            Utils.preventDoubleClick( v );
            if (offersModel != null) {
                BucketListBottomSheet dialog = new BucketListBottomSheet();
                dialog.offerId = offersModel.getId();
                dialog.show(getSupportFragmentManager(), "");
            }
        } );

        binding.btnBuyNow.setOnClickListener( view -> {
            startActivity( new Intent( activity, VenueBuyNowActivity.class ).putExtra( "venueObjectModel", new Gson().toJson( offersModel.getVenue() ) ).putExtra( "offerModel", new Gson().toJson( offersModel ) ) );
        } );

        binding.layoutTimeInfo.setOnClickListener(v -> {
            if (!offersModel.isShowTimeInfo()) {
                if (offersModel == null) {
                    return;
                }
                if (offersModel.getVenue() == null) {
                    return;
                }
                VenueTimingDialog dialog = new VenueTimingDialog(offersModel.getVenue().getTiming(), activity);
                dialog.show(getSupportFragmentManager(), "1");
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityOfferDetailBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setOffersModel() {
        if (offersModel != null) {
            if (offersModel.getVenue() != null) {
                binding.venueContainer.setVenueDetail( offersModel.getVenue() );
            }
            binding.tvTitle.setText( offersModel.getTitle() );
            binding.tvDescription.setText( offersModel.getDescription() );
            binding.tvDescription.post( () -> {
                int lineCount = binding.tvDescription.getLineCount();
                if (lineCount > 2) { Utils.makeTextViewResizable( binding.tvDescription, 3, 3, "..." + Utils.getLangValue("see_more"), true );}
            } );
            if (!offersModel.getImage().isEmpty()) {
                Graphics.loadImage( offersModel.getImage(), binding.ivCover );
            } else {
                binding.ivCover.setVisibility( View.GONE );
                binding.headerLinear.setBackgroundColor( getResources().getColor( R.color.lightGray ) );
            }

            setDisclaimerData();

            binding.imgRecommandation.setColorFilter(ContextCompat.getColor(activity, offersModel.isRecommendation() ? R.color.brand_pink : R.color.white));

            binding.txtDays.setText( offersModel.getDays() );
            binding.btnTimeInfo.setVisibility(offersModel.isShowTimeInfo() ? View.GONE : View.VISIBLE);
            if (TextUtils.isEmpty(offersModel.getStartTime())) {
                binding.startDate.setText(Utils.getLangValue("ongoing"));
                binding.endDate.setVisibility(View.GONE);
            } else {
                binding.endDate.setVisibility(View.VISIBLE);
                binding.startDate.setText(Utils.convertMainDateFormat(offersModel.getStartTime()));
                binding.endDate.setText(Utils.convertMainDateFormat(offersModel.getEndTime()));
            }
            binding.txtOfferTime.setText(offersModel.getOfferTiming());

            binding.cvBuyNow.setVisibility( View.VISIBLE );
            binding.layoutExpired.setVisibility( View.GONE );
            if (offersModel.getPackages().isEmpty()) {
                binding.cvBuyNow.setVisibility( View.GONE );
                binding.packageRecycler.setVisibility( View.GONE );
            }
            adapter.updateData(offersModel.getPackages());

            boolean isAllow = offersModel.getPackages().stream().anyMatch( packageModel -> packageModel.isAllowSale() );
            if (!isAllow) {
                binding.cvBuyNow.setVisibility( View.GONE );
            }

            if (!Utils.isFutureDate(offersModel.getEndTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")) {
                binding.cvBuyNow.setVisibility( View.GONE );
                binding.btnBucketList.setVisibility(View.GONE);
                binding.layoutExpired.setVisibility( View.VISIBLE );
            }
        }
    }

    private void setDisclaimerData() {
        if (offersModel.getDisclaimerTitle() != null && !offersModel.getDisclaimerTitle().isEmpty()) {
            binding.tvDisclaimerTitle.setText(offersModel.getDisclaimerTitle());
            binding.tvDisclaimerDescription.setText(offersModel.getDisclaimerDescription());
            binding.tvDisclaimerDescription.post(() -> {
                int lineCount = binding.tvDisclaimerDescription.getLineCount();
                if (lineCount > 2) {
                    Utils.makeTextViewResizable(binding.tvDisclaimerDescription, 3, 3, "..." + Utils.getLangValue("see_more"), true);
                }
            });
        }
        else {
            binding.tvDisclaimerTitle.setVisibility(View.GONE);
            binding.tvDisclaimerDescription.setVisibility(View.GONE);
        }

    }
    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void reqRecommendation(String id) {
        showProgress();
        DataService.shared( activity ).requestFeedRecommandation( id, "offer", new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.message.equals("recommendation added successfully!")){
                    int newColor = ContextCompat.getColor(activity, R.color.brand_pink);
                    binding.imgRecommandation.setColorFilter(newColor);
                    Alerter.create(activity).setTitle(Utils.getLangValue("thank_you")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("recommending_toast",offersModel.getTitle())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }else {
                    int newColor = ContextCompat.getColor(activity, R.color.white);
                    binding.imgRecommandation.setColorFilter(newColor);
                    Alerter.create(activity).setTitle(Utils.getLangValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("recommending_remove_toast",offersModel.getTitle())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }

            }
        } );
    }

    public void requestVenueOfferDetail() {
        showProgress();
        Log.d("TAG", "requestVenueOfferDetail: "+offerId);
        DataService.shared( activity ).requestVenueOfferDetail( offerId, new RestCallback<ContainerModel<OffersModel>>(this) {
            @Override
            public void result(ContainerModel<OffersModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.getData() != null) {
                    offersModel = model.getData();
                    setOffersModel();
                    binding.mainLayout.setVisibility( View.VISIBLE );
                }
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class OfferPackagerAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.venue_offer_item_package_recycler ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            PackageModel model = (PackageModel) getItem( position );
            if (model != null) {
                viewHolder.binding.tvTitle.setText( Utils.notNullString( model.getTitle() ) );

                viewHolder.binding.soldOutTv.setText(Utils.getLangValue("sold_out"));

                if ("0".equals( model.getDiscount() )) {
                    viewHolder.binding.tvDiscount.setVisibility( View.GONE );
                    viewHolder.binding.tvOriginalPrice.setVisibility( View.GONE );
                    int colorTransparent = viewHolder.itemView.getContext().getResources().getColor(R.color.transparent);
                    viewHolder.binding.roundLinear.setBackgroundColor(colorTransparent);
                    viewHolder.binding.tvDiscountPrice.setText( Utils.notNullString( model.getAmount() ) );
                } else {
                    String modifiedString = model.getDiscount().contains( "%" ) ? model.getDiscount() : model.getDiscount() + "%";
                    viewHolder.binding.tvDiscount.setText( Utils.notNullString( modifiedString ) );
                    viewHolder.binding.tvDiscount.setVisibility( View.VISIBLE );
                    viewHolder.binding.tvOriginalPrice.setVisibility( DiscountCalculator.isDiscountPriceSame(model.getDiscount(),model.getAmount()) ? View.GONE : View.VISIBLE);
                    if (DiscountCalculator.isDiscountPriceSame(model.getDiscount(),model.getAmount())) {
                        viewHolder.binding.tvDiscountPrice.setText( Utils.notNullString( model.getAmount() ) );
                    } else {
                        viewHolder.binding.tvOriginalPrice.setText( Utils.notNullString( model.getAmount() ) );
                        viewHolder.binding.tvDiscountPrice.setText(String.valueOf(DiscountCalculator.calculateDiscount(model.getDiscount(),model.getAmount())));
                    }
                }
                viewHolder.binding.tvDescription.setText( Utils.notNullString( model.getDescription() ) );
                viewHolder.binding.tvOriginalPrice.setPaintFlags( viewHolder.binding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
                viewHolder.binding.tvMaxQty.setText(model.isShowLeftQtyAlert() ? Utils.getLangValue("remaining_quantity") + model.getRemainingQty() : "");
                viewHolder.binding.tvMaxQty.setVisibility(model.isShowLeftQtyAlert() ? View.VISIBLE : View.GONE);
                viewHolder.binding.tvMaxQty.setTextColor(model.getRemainingQty() <= 3 ? getResources().getColor(R.color.red) : getResources().getColor(R.color.amber_color));
                viewHolder.binding.soldOutTv.setVisibility(model.isAllowSale() && model.getRemainingQty() == 0 ? View.VISIBLE : View.GONE);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final VenueOfferItemPackageRecyclerBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = VenueOfferItemPackageRecyclerBinding.bind( itemView );
            }
        }
    }


    // endregion
    // --------------------------------------

}