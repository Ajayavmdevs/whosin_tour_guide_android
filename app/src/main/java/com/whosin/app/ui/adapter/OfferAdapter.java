package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.CategoryListItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.VenueShareActivity;

import java.util.ArrayList;

public class OfferAdapter <T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private Activity activity;
    private FragmentManager fragmentManager;
    private OfferType offerType;
    public enum OfferType {
        VENUE, CATEGORY, EXPLORE, SEARCH, FEED,NONE
    }

    public OfferAdapter(Activity activity, FragmentManager fragmentManager, OfferType type) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.offerType = type;
    }

    public OfferAdapter(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.offerType = OfferType.CATEGORY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.category_list_item));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        OffersModel model = (OffersModel) getItem(position);
        if (model == null){return;}

        if (offerType == OfferType.VENUE) {
            viewHolder.mBinding.venueOfferHeader.setVisibility(View.VISIBLE);
            viewHolder.mBinding.venueContainer.setVisibility(View.GONE);
            viewHolder.mBinding.linearHeader.setVisibility(View.GONE);
            viewHolder.mBinding.txtVenueOfferTitle.setText(model.getTitle());
            viewHolder.mBinding.tvVenueOfferDescription.setText(model.getDescription());
        }

        if (offerType == OfferType.SEARCH) {
            float cornerRadius = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
            viewHolder.mBinding.constraint.setCornerRadius(cornerRadius, CornerType.ALL);
        }

        viewHolder.mBinding.txtTitle.setText(model.getTitle());
        viewHolder.mBinding.tvDescription.setText(model.getDescription());
        viewHolder.mBinding.tvDescription.post( () -> {
            int lineCount = viewHolder.mBinding.tvDescription.getLineCount();
            if (lineCount > 2) {
                Utils.makeTextViewResizable( viewHolder.mBinding.tvDescription, 3, 3, ".. See More", true );
            }
        } );

        if (model.getVenue() != null && offerType != OfferType.VENUE) {
            viewHolder.mBinding.venueContainer.setVenueDetail(model.getVenue());
        }

        viewHolder.mBinding.offerInfoView.setOfferDetail(model, activity, fragmentManager);
        viewHolder.mBinding.offerButtonView.setupButtons(model, activity, fragmentManager);


        if (!model.getPackages().isEmpty()) {
            viewHolder.mBinding.venueSubRecycler.setVisibility(View.VISIBLE);
            viewHolder.setDataInPacakgeAdapter(model);
        } else {
            viewHolder.mBinding.venueSubRecycler.setVisibility(View.GONE);
        }

        viewHolder.mBinding.iconMenu.setOnClickListener(v -> {
            Utils.preventDoubleClick( v );
            ArrayList<String> data = new ArrayList<>();
            if (offerType == OfferType.VENUE) {
//                data.add("Add to Bucketlist");
                data.add(Utils.getLangValue("share"));
                data.add(model.getVenue().isRecommendation() ? Utils.getLangValue("remove_recommendation") : Utils.getLangValue("add_recommendation"));
                Graphics.showActionSheet(activity, model.getTitle(), data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            BucketListBottomSheet dialog = new BucketListBottomSheet();
                            dialog.offerId = model.getId();
                            dialog.show(fragmentManager, "");
                            break;
                        case 1:
                            Intent intent = new Intent(activity, VenueShareActivity.class);
                            intent.putExtra( "offer",new Gson().toJson( model) );
                            intent.putExtra( "type","offer" );
                            activity.startActivity(intent);
                            break;
                        case 2:
                            reqRecommendation(model.getVenue());
                            break;
                    }
                });
            }
            else {
//                data.add("Add to Bucketlist");
                data.add(Utils.getLangValue("share_venue"));
                data.add(Utils.getLangValue("share_offer"));
                if (model.getVenue() != null) {
                    data.add(model.getVenue().isIsFollowing() ? Utils.getLangValue("unfollow") : Utils.getLangValue("follow"));
                    data.add(model.getVenue().isRecommendation() ? Utils.getLangValue("remove_recommendation") : Utils.getLangValue("add_recommendation"));
                }
                String title = model.getVenue() != null ? model.getVenue().getName() : model.getTitle();
                Graphics.showActionSheet(activity, title, data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            BucketListBottomSheet dialog = new BucketListBottomSheet();
                            dialog.offerId = model.getId();
                            dialog.show(fragmentManager, "");
                            break;
                        case 1:
                            Intent shareIntent = new Intent(activity, VenueShareActivity.class);
                            shareIntent.putExtra( "venue",new Gson().toJson( model.getVenue()) );
                            shareIntent.putExtra( "type","venue" );
                            activity.startActivity(shareIntent);
                            break;
                        case 2:
                            Intent intent = new Intent(activity, VenueShareActivity.class);
                            intent.putExtra( "offer",new Gson().toJson( model) );
                            intent.putExtra( "type","offer" );
                            activity.startActivity(intent);
                            break;
                        case 3:
                            reqOfferFollowUnFollow(model.getVenue());
                            break;

                        case 4:
                            reqRecommendation(model.getVenue());
                            break;
                    }
                });
            }
        });

        viewHolder.mBinding.getRoot().setOnClickListener(v -> {
            OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
            dialog.offerId = model.getId();
            if (offerType == OfferType.VENUE) {
                dialog.venue = true;
            }
            dialog.show(fragmentManager, "");
        });

//        viewHolder.mBinding.layoutOfferDetail.setOnClickListener(v -> {
//            OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
//            dialog.offerId = model.getId();
//            if (offerType == OfferType.VENUE) {
//                dialog.venue = true;
//            }
//            dialog.show(fragmentManager, "");
//        });

        viewHolder.mBinding.layoutVenueDetail.setOnClickListener(v -> {
            if (model.getVenue() != null && offerType != OfferType.VENUE) {
                Graphics.openVenueDetail(activity, model.getVenue().getId());
            } else {
                OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                dialog.offerId = model.getId();
                if (offerType == OfferType.VENUE) {
                    dialog.venue = true;
                }
                dialog.show(fragmentManager, "");
            }
        });
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CategoryListItemBinding mBinding;
        private OfferPackagesAdapter<PackageModel> packageAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = CategoryListItemBinding.bind(itemView);
            mBinding.venueSubRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        }

        private void setDataInPacakgeAdapter(OffersModel model) {
            packageAdapter = new OfferPackagesAdapter<>(model.getId(),fragmentManager);
            mBinding.venueSubRecycler.setAdapter(packageAdapter);
            packageAdapter.updateData(model.getPackages());
        }

    }

    private void reqOfferFollowUnFollow(VenueObjectModel venueObjectModel) {
        if (venueObjectModel == null) { return; }
        Graphics.showProgress(activity);
        DataService.shared( activity ).requestVenueFollow(venueObjectModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>(null) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                Graphics.hideProgress(activity);
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                AppExecutors.get().mainThread().execute(() -> {
                    venueObjectModel.setIsFollowing(!model.message.equals("Unfollowed!"));
                    Alerter.create( activity ).setTitle( venueObjectModel.isIsFollowing() ? Utils.getLangValue("thank_you") : Utils.getLangValue("oh_snap") ).setText( venueObjectModel.isIsFollowing() ? Utils.setLangValue("following_toast",venueObjectModel.getName()): Utils.setLangValue("unfollow_toast",venueObjectModel.getName()) ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                    notifyDataSetChanged();
                });


            }
        } );
    }

    private void reqRecommendation(VenueObjectModel venueModel) {
        if (venueModel == null) { return; }
        Graphics.showProgress(activity);
        DataService.shared(activity).requestFeedRecommandation(venueModel.getId(), "venue", new RestCallback<ContainerModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                Graphics.hideProgress(activity);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                AppExecutors.get().mainThread().execute(() -> {
                    if (!venueModel.isRecommendation()) {
                        Alerter.create(activity).setTitle(Utils.getLangValue("thank_you")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("recommending_toast",venueModel.getName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    } else {
                        Alerter.create(activity).setTitle(Utils.getLangValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("recommending_remove_toast",venueModel.getName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    }
                    venueModel.setRecommendation(!venueModel.isRecommendation());
                    notifyDataSetChanged();
                });

            }
        });
    }

}