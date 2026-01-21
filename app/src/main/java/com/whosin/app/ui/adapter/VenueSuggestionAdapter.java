package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.SuggestedVenueListItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.venue.VenueActivity;

import java.text.DecimalFormat;

public class VenueSuggestionAdapter <T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private Activity activity;

    private FragmentManager fragmentManager;

    private final String follow = Utils.getLangValue("follow");

    private final String following = Utils.getLangValue("following");

    public VenueSuggestionAdapter(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.suggested_venue_list_item);
        view.getLayoutParams().width = (int) (Graphics.getScreenWidth(activity) * 0.35);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        VenueObjectModel model = (VenueObjectModel) getItem(position);
        viewHolder.mBinding.tvName.setText(model.getName());
        double i = model.getDistance();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedDistance = decimalFormat.format(i);

        if (model.getDistance() == 0) {
            viewHolder.mBinding.tvDistance. setVisibility(View.GONE);
        } else {
            viewHolder.mBinding.tvDistance.setVisibility(View.VISIBLE);
            viewHolder.mBinding.tvDistance.setText(formattedDistance + "km");
        }
        Graphics.loadImageWithFirstLetter(model.getLogo(),viewHolder.mBinding.image,model.getName());
        viewHolder.setFollowUnFollowStatus(model);

        viewHolder.mBinding.btnFollow.setOnClickListener(v -> viewHolder.reqFollowUnFollow(model, (success, message) -> {
            if (success) {
                if (message.equals(follow)) {
                    viewHolder.mBinding.btnFollow.setBackgroundResource(R.drawable.follow_button_bg);
                } else {
                    viewHolder.mBinding.btnFollow.setBackgroundResource(R.drawable.contact_follow_btn_bg);
                }
            }
        }));
        viewHolder.mBinding.ivClose.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            viewHolder.requestUserRemoveSuggestion(model, (success, error1) -> {
                if (success) {
                    removeItem(position);
                    notifyDataSetChanged();
                }
            });
        });
        viewHolder.itemView.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            activity.startActivity(new Intent(activity, VenueActivity.class).putExtra("venueId", model.getId()));
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final SuggestedVenueListItemBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = SuggestedVenueListItemBinding.bind(itemView);
        }

        private void setFollowUnFollowStatus(VenueObjectModel model) {
            if (model == null) {
                return;
            }
            if (model.isIsFollowing()) {
                mBinding.btnFollow.setText(following);
            } else {
                mBinding.btnFollow.setText(follow);
            }
        }


        private void reqFollowUnFollow(VenueObjectModel venueObjectModel, BooleanResult callBack) {

            if (TextUtils.isEmpty(venueObjectModel.getId())) {
                Alerter.create(activity).setTitle("Opps!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("No venue found").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
            }
            Graphics.showProgress(activity);
            DataService.shared(activity).requestVenueFollow(venueObjectModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>(null) {
                @Override
                public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                    Graphics.hideProgress(activity);
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (model.message.equals("Unfollowed!")) {
                        mBinding.btnFollow.setText(follow);
                        callBack.success(true, follow);
                        Alerter.create(activity).setTitle(Utils.getLangValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(Utils.setLangValue("unfollow_toast",venueObjectModel.getName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    } else {
                        mBinding.btnFollow.setText(following);
                        callBack.success(true, following);
                        Alerter.create(activity).setTitle(Utils.getLangValue("thank_you")).setText(Utils.setLangValue("follow_venue",venueObjectModel.getName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    }

                }
            });
        }

        private void requestUserRemoveSuggestion(VenueObjectModel venueObjectModel, BooleanResult callBack) {
            DataService.shared( activity ).requestUserRemoveSuggestion( "venue",venueObjectModel.getId(), new RestCallback<ContainerModel<CommonModel>>(null) {
                @Override
                public void result(ContainerModel<CommonModel> model, String error) {
                    if (!Utils.isNullOrEmpty( error ) || model == null) {
                        Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                        return;
                    }
                    Alerter.create( activity ).setTitle(Utils.getLangValue("thank_you")).setText(Utils.setLangValue("recommending_remove_toast",venueObjectModel.getName())).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                    callBack.success(true,"");
                }
            } );
        }

    }

}
