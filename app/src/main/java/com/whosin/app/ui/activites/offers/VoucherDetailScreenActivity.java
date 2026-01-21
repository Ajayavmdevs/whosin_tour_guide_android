package com.whosin.app.ui.activites.offers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityVoucherDetailScreenBinding;
import com.whosin.app.databinding.ItemActivityFeaturesBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ActivityAvailableFeatureModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ExclusiveDealModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.venue.ui.BuyNowActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class VoucherDetailScreenActivity extends BaseActivity {
    private ActivityVoucherDetailScreenBinding binding;
    private ExclusiveDealModel voucherModel;
    private String outingId = "";
    private final FeatureAdapter<ActivityAvailableFeatureModel> adapter = new FeatureAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        outingId = getIntent().getStringExtra( "id" );
        Graphics.applyBlurEffect( activity, binding.blurView );
        requestDealDetail( outingId );
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener( view -> {
            onBackPressed();
        } );

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityVoucherDetailScreenBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }



    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvStartDateTitle, "start_date");
        map.put(binding.tvEndDateTitle, "end_date");
        map.put(binding.btnBuyNow, "buy_now!");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setDetail() {
        if (voucherModel != null) {
            Graphics.loadImage( voucherModel.getImage(), binding.ivCover );
            if (voucherModel.getVenue() != null) {
                binding.venueContainer.setVenueDetail( voucherModel.getVenue() );
            }

            binding.featuresRecycler.setLayoutManager( new GridLayoutManager( activity, 2, LinearLayoutManager.VERTICAL, false ) );
            binding.featuresRecycler.setAdapter( adapter );

            binding.tvTitle.setText( voucherModel.getTitle() );
            binding.tvDescription.setMaxLines( 3 );
            binding.tvDescription.post( () -> {
                int lineCount = binding.tvDescription.getLineCount();
                if (lineCount > 2) {
                    Utils.makeTextViewResizable( binding.tvDescription, 3, 3, "..." + getValue("see_more"), true );
                }
            } );

            binding.tvDescription.setText( voucherModel.getDescription() );
            if (voucherModel.getDiscountedPrice() == voucherModel.getActualPrice()) {
                binding.tvAED.setVisibility( View.GONE );
            } else {
                binding.tvAED.setVisibility( View.VISIBLE );
            }

            if (voucherModel.getDisclaimerTitle() != null && !voucherModel.getDisclaimerTitle().isEmpty()) {
                binding.tvDisclaimerTitle.setText( voucherModel.getDisclaimerTitle() );
                binding.tvDisclaimerDescription.setText( voucherModel.getDisclaimerDescription() );
                binding.tvDisclaimerDescription.post( () -> {
                    int lineCount = binding.tvDisclaimerDescription.getLineCount();
                    if (lineCount > 2) {
                        Utils.makeTextViewResizable( binding.tvDisclaimerDescription, 3, 3, "..." + getValue("see_more"), true );
                    }
                } );
            } else {
                binding.tvDisclaimerTitle.setVisibility( View.GONE );
                binding.tvDisclaimerDescription.setVisibility( View.GONE );
            }

            Utils.setStyledText(activity,binding.tvPrice,String.valueOf(voucherModel.getDiscountedPrice()));
            Utils.setStyledText(activity,binding.tvAED,String.valueOf(voucherModel.getActualPrice()));
            binding.tvAED.setPaintFlags( binding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );

            if (voucherModel.getFeatures() != null) {
                adapter.updateData( voucherModel.getFeatures() );
            } else {
                binding.featuresRecycler.setVisibility( View.GONE );
            }

            try {
                if (voucherModel.getStartDate() != null && voucherModel.getEndDate() != null) {
                    String startDate = Utils.changeDateFormat(voucherModel.getStartDate(), "yyyy-MM-dd", "E, dd MMM yyyy");
                    String endDate = Utils.changeDateFormat(voucherModel.getEndDate(), "yyyy-MM-dd", "E, dd MMM yyyy");
                    binding.startDate.setText( startDate );
                    binding.endDate.setText( endDate );
                }
                if (voucherModel.getStartTime() != null && !voucherModel.getStartTime().isEmpty() && voucherModel.getEndTime() != null) {
//                    String startTime = Utils.changeDateFormat(voucherModel.getStartTime(), "HH:mm", "hh:mm a");
//                    String endTime = Utils.changeDateFormat(voucherModel.getEndTime(), "HH:mm", "hh:mm a");
                    binding.txtDate.setText(String.format("%s-%s", voucherModel.getStartTime(), voucherModel.getEndTime()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (voucherModel.getDays() != null) {
                binding.txtDays.setText( Utils.convertToDayName( voucherModel.getDays() ) );
            }

            binding.btnBuyNow.setOnClickListener( view -> {
                if(voucherModel.getVenue() == null) { return; }
                startActivity( new Intent( activity, BuyNowActivity.class )
                        .putExtra( "offerModel", new Gson().toJson( voucherModel ) )
                        .putExtra( "venueDetail", new Gson().toJson( voucherModel.getVenue() ) ) );
            });

            showExpried();
        }
    }


    private void showExpried() {
        String dateString = voucherModel.getEndDate();
        String timeString = voucherModel.getEndTime();
        String dateTimeString = dateString + " " + timeString;

        DateTimeFormatter formatter = null;

        if (dateString.length() == 10) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm" );
            }
        } else if (dateString.length() == 8) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern( "dd-MM-yyyy HH:mm" );
            }
        } else {
            // Handle the case when the date format is not recognized
            throw new IllegalArgumentException( "Unsupported date format" );
        }

        try {
            LocalDateTime dateTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dateTime = LocalDateTime.parse( dateTimeString, formatter );
            }

            // Check if the specified date and time have expired
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (dateTime.isBefore( LocalDateTime.now() )) {
                    binding.btnBuyNow.setText(getValue("expired") );
                    binding.btnBuyNow.setTypeface( null, Typeface.BOLD );
                    binding.btnBuyNow.setBackgroundResource( android.R.color.transparent );
                    binding.btnBuyNow.setEnabled( false );
                    binding.btnBuyNow.setTextColor( getResources().getColor( R.color.brand_pink ) );
                }
            }
        } catch (@SuppressLint({"NewApi", "LocalSuppress"}) DateTimeParseException e) {
            // Handle the case when the parsing fails
            e.printStackTrace();
            // Handle the error accordingly
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestDealDetail(String dealId) {
        showProgress();
        DataService.shared( activity ).requestDealDetail( dealId, new RestCallback<ContainerModel<ExclusiveDealModel>>(this) {
            @Override
            public void result(ContainerModel<ExclusiveDealModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.getData() != null) {
                    voucherModel = model.getData();
                    setDetail();
                    binding.scrollView.setVisibility( View.VISIBLE );
                }
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class FeatureAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_activity_features ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ActivityAvailableFeatureModel model = (ActivityAvailableFeatureModel) getItem( position );
            viewHolder.binding.tvName.setText( model.getTitle() );
            Glide.with( activity ).load( model.getIcon() ).into( viewHolder.binding.ivLogo );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemActivityFeaturesBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemActivityFeaturesBinding.bind( itemView );
            }
        }
    }

    // endregion
    // --------------------------------------
}