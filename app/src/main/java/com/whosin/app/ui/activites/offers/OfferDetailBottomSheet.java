package com.whosin.app.ui.activites.offers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.DiscountCalculator;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.OfferDetailBottomSheetBinding;
import com.whosin.app.databinding.VenueOfferItemPackageRecyclerBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.BrunchListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.VenueBuyNowActivity;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class OfferDetailBottomSheet extends DialogFragment {

    private OfferDetailBottomSheetBinding binding;
    private final OfferPackagerAdapter<PackageModel> adapter = new OfferPackagerAdapter<>();
    private OffersModel offersModel;
    public String offerId;
    public boolean venue = false;
    private Context mContext;
    int SCROLL_THRESHOLD = 10;
    private boolean blurViewVisible = false;

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
        binding = OfferDetailBottomSheetBinding.bind(view);

        binding.tvStartDateTitle.setText(Utils.getLangValue("start_date"));
        binding.tvEndDateTitle.setText(Utils.getLangValue("end_date"));

        binding.btnAddBucket.setTxtTitle(Utils.getLangValue("bucketlist"));
        binding.btnBuyNow.setTxtTitle(Utils.getLangValue("buy_now"));
        binding.btnInvite.setTxtTitle(Utils.getLangValue("invite_your_friends"));
        binding.btnClaim.setTxtTitle(Utils.getLangValue("claim_discount"));

        binding.packageRecycler.setLayoutManager( new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.packageRecycler.setAdapter( adapter );
        requestVenueOfferDetail();


    }

    public void setListener() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.linearHeader.setOnClickListener( view -> {
            if (!venue) {
                if (offersModel == null) { return; }
                if (offersModel.getVenue() == null) { return; }
                Graphics.openVenueDetail(getActivity(), offersModel.getVenue().getId());
                dismiss();
            }
            else {
                dismiss();
            }
        });

        binding.imgRecommandation.setOnClickListener( v -> reqRecommendation( offersModel.getId() ));

        binding.btnAddBucket.setOnClickListener( v -> {
            Utils.preventDoubleClick( v );
            if (offersModel != null) {
                BucketListBottomSheet dialog = new BucketListBottomSheet();
                dialog.offerId = offersModel.getId();
                dialog.show(getChildFragmentManager(), "");
            }
        } );

        binding.btnBuyNow.setOnClickListener( view -> {
            startActivity( new Intent( getActivity(), VenueBuyNowActivity.class ).putExtra( "venueObjectModel", new Gson().toJson( offersModel.getVenue() ) ).putExtra( "offerModel", new Gson().toJson( offersModel ) ) );
            dismiss();
        } );

//        binding.btnInvite.setOnClickListener( view -> {
//            if (offersModel.getVenue() == null) { return; }
//            Utils.openInviteButtonSheet(offersModel, offersModel.getVenue(), getChildFragmentManager());
//        } );

        binding.btnClaim.setOnClickListener( view -> {
            if (getActivity() == null) { return; }
            if (offersModel.getVenue() == null) { return; }
            if (offersModel.getSpecialOfferModel() == null) { return; }
            Utils.openClaimScreen(offersModel.getSpecialOfferModel(), offersModel.getVenue(), getActivity());
        } );

        binding.layoutTimeInfo.setOnClickListener(v -> {
            if (!offersModel.isShowTimeInfo()) {
                if (offersModel == null) {
                    return;
                }
                if (offersModel.getVenue() == null) {
                    return;
                }
                VenueTimingDialog dialog = new VenueTimingDialog(offersModel.getVenue().getTiming(), getActivity());
                dialog.show(getChildFragmentManager(), "1");
            }
        });

        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int scrollDistance = scrollY - oldScrollY;

            if (Math.abs(scrollDistance) > SCROLL_THRESHOLD) {
                if (scrollDistance > 0) {
                    // Scrolling down
                    if (!blurViewVisible) {
                        binding.blurViewHeader.setBlurEnabled(true);
                        Graphics.applyBlurEffect(requireActivity(), binding.blurViewHeader);
                        blurViewVisible = true;
                    }
                } else {
                    // Scrolling up
                    if (scrollY <= 10) {
                        if (blurViewVisible) {
                            binding.blurViewHeader.setBackgroundColor(Color.TRANSPARENT);
                            binding.blurViewHeader.setBackground(null);
                            binding.blurViewHeader.setBlurEnabled(false);
                            blurViewVisible = false;
                        }
                    }
                }
            }
        });
    }

    public int getLayoutRes() {
        return R.layout.offer_detail_bottom_sheet;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            getView().post(() -> {
                View parent = (View) getView().getParent();
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
                int peekHeight =(int) (parent.getHeight() * 0.95);
                behavior.setPeekHeight(peekHeight);
            });
            int heightPixels =  (int) (Graphics.getScreenHeight( getActivity() ) * 0.95);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPixels);
            binding.progress.setLayoutParams(layoutParams);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setOffersModel() {
        if (offersModel != null) {
            if (offersModel.getVenue() != null) {
                binding.venueContainer.setOfferVenueDetail( offersModel.getVenue());
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

            binding.imgRecommandation.setColorFilter(ContextCompat.getColor(mContext, offersModel.isRecommendation() ? R.color.brand_pink : R.color.white));

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

            if (!offersModel.getPackages().isEmpty()) {
                binding.packageRecycler.setVisibility( View.VISIBLE );
                adapter.updateData(offersModel.getPackages());
            } else {
                binding.packageRecycler.setVisibility( View.VISIBLE );
            }

            binding.btnBuyNow.setVisibility( offersModel.isAvailableToBuy() ? View.VISIBLE : View.GONE);
//            binding.btnInvite.setVisibility( offersModel.isExpired() ? View.GONE : View.VISIBLE);
            binding.btnClaim.setVisibility( offersModel.isSpecialOffer() ? View.VISIBLE : View.GONE);
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
    // region public
    // --------------------------------------


    public void setShareListener(CommanCallback<BrunchListModel> listener) {
        // this.listener = listener;
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------
    private void reqRecommendation(String id) {
        binding.progress.setVisibility(View.VISIBLE);
        DataService.shared( getActivity() ).requestFeedRecommandation( id, "offer", new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                binding.progress.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( getActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.message.equals("recommendation added successfully!")){
                    int newColor = ContextCompat.getColor(getActivity(), R.color.brand_pink);
                    binding.imgRecommandation.setColorFilter(newColor);
                    Alerter.create(getActivity()).setTitle(Utils.getLangValue("thank_you")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("recommending_toast",offersModel.getTitle())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }else {
                    int newColor = ContextCompat.getColor(getActivity(), R.color.white);
                    binding.imgRecommandation.setColorFilter(newColor);
                    Alerter.create(getActivity()).setTitle(Utils.getLangValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("recommending_remove_toast",offersModel.getTitle())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }

            }
        } );
    }

    public void requestVenueOfferDetail() {
        binding.progress.setVisibility(View.VISIBLE);
        Log.d("TAG", "requestVenueOfferDetail: "+offerId);
        DataService.shared( getActivity() ).requestVenueOfferDetail( offerId, new RestCallback<ContainerModel<OffersModel>>(this) {
            @Override
            public void result(ContainerModel<OffersModel> model, String error) {
                binding.progress.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
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
//                    viewHolder.binding.tvDiscountPrice.setText( Utils.notNullString( model.getAmount() ) );
                    Utils.setStyledText(requireActivity(),viewHolder.binding.tvDiscountPrice,Utils.notNullString( model.getAmount()) );
                } else {
                    String modifiedString = model.getDiscount().contains( "%" ) ? model.getDiscount() : model.getDiscount() + "%";
                    viewHolder.binding.tvDiscount.setText( Utils.notNullString( modifiedString ) );
                    viewHolder.binding.tvDiscount.setVisibility( View.VISIBLE );
                    viewHolder.binding.tvOriginalPrice.setVisibility(DiscountCalculator.isDiscountPriceSame(model.getDiscount(),model.getAmount()) ? View.GONE : View.VISIBLE);
                    if (DiscountCalculator.isDiscountPriceSame(model.getDiscount(),model.getAmount())) {
//                        viewHolder.binding.tvDiscountPrice.setText( Utils.notNullString( model.getAmount() ) );
                        Utils.setStyledText(requireActivity(),viewHolder.binding.tvDiscountPrice,Utils.notNullString( model.getAmount()) );
                    } else {
//                        viewHolder.binding.tvOriginalPrice.setText( Utils.notNullString( model.getAmount() ) );
                        Utils.setStyledText(requireActivity(),viewHolder.binding.tvOriginalPrice,Utils.notNullString(model.getAmount()));
//                        viewHolder.binding.tvDiscountPrice.setText(String.valueOf(DiscountCalculator.calculateDiscount(model.getDiscount(),model.getAmount())));
                        Utils.setStyledText(requireActivity(),viewHolder.binding.tvDiscountPrice,String.valueOf(DiscountCalculator.calculateDiscount(model.getDiscount(),model.getAmount())));
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