package com.whosin.app.ui.activites.venue.Bucket;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.ncorti.slidetoact.SlideToActView;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentBucketListBottomSheetBinding;
import com.whosin.app.databinding.ImageListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.rest.RestCallback;

import java.util.List;
import java.util.stream.Collectors;


public class BucketListBottomSheet extends DialogFragment implements SlideToActView.OnSlideCompleteListener {
    private FragmentBucketListBottomSheetBinding binding;
    private BucketListModel bucketListModel;

    public String offerId = "";

    public String eventId = "";

    public String activityId = "";
    public String bucketId = "";
    public boolean isBucketRemove = false;

    public CommanCallback<Boolean> callBack;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setStyle( DialogFragment.STYLE_NORMAL, R.style.DialogStyle );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListener();
        return view;
    }

    private void setListener() {
        binding.createBucket.setOnSlideCompleteListener( this );
        binding.ivClose.setOnClickListener( view -> dismiss() );

    }

    private void initUi(View view) {
        binding = FragmentBucketListBottomSheetBinding.bind( view );
        Glide.with( requireActivity() ).load( R.drawable.icon_close_btn ).into( binding.ivClose );
        binding.createBucket.setSliderIcon(R.drawable.icon_swipe);
    }

    public int getLayoutRes() {
        return R.layout.fragment_bucket_list_bottom_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog( requireActivity(), R.style.BottomSheetDialogThemeNoFloating );
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void showProgress() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestAddBucket(CreateBucketListModel bucketsModel) {
        binding.progress.setVisibility(View.VISIBLE);
        JsonObject object = new JsonObject();
        object.addProperty( "id", bucketsModel.getId());
        object.addProperty( "action", "add" );

        if (eventId.isEmpty() && activityId.isEmpty()) {
            object.addProperty("offerId", offerId);
        } else if (activityId.isEmpty() && offerId.isEmpty()) {
            object.addProperty("eventId", eventId);
        } else {
            object.addProperty("activityId", activityId);
        }

        DataService.shared( requireActivity() ).requestUpdateBucket( object, new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (getActivity() != null) {
                    if (!model.message.equals("Bucket list updated successfully")){
                        Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
                    }else {
                        Alerter.create( requireActivity() ).setText("Bucket list updated successfully").setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();

                    }
                }

                if (isBucketRemove == true) {
                    if (!bucketsModel.getId().equals(bucketId)) {
                        requestRemoveBucket();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                }
                dismiss();
            }
        });
    }

    private void requestRemoveBucket() {
        JsonObject object = new JsonObject();
        object.addProperty("id", bucketId);
        object.addProperty("action", "delete");

        if (eventId.isEmpty() && activityId.isEmpty()) {
            object.addProperty("offerId", offerId);
        } else if (activityId.isEmpty() && offerId.isEmpty()) {
            object.addProperty("eventId", eventId);
        } else {
            object.addProperty("activityId", activityId);
        }

        DataService.shared(requireContext()).requestUpdateBucket(object, new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (callBack != null) {
                    callBack.onReceive(true);
                }
                binding.progress.setVisibility(View.GONE);
                dismiss();
            }
        });
    }

    @Override
    public void onSlideComplete(@NonNull SlideToActView slideToActView) {
        resetSlider( slideToActView );
    }

    private void resetSlider(SlideToActView slideToActView) {
        Animation animation = new TranslateAnimation( 0, 0, 0, 0 );
        animation.setDuration( 0 );
        slideToActView.startAnimation( animation );
        slideToActView.resetSlider();
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class PlayerAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.image_list );
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize( com.intuit.sdp.R.dimen._minus8sdp );
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize( com.intuit.sdp.R.dimen._32sdp );
            }
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem( position );
            Graphics.loadImageWithFirstLetter( model.getImage(), viewHolder.vBinding.civPlayers, model.getFirstName() );
        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == position + 1) {
                return 0;
            } else {
                return 1;
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageListBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                vBinding = ImageListBinding.bind( itemView );
            }
        }

    }


    // endregion
    // --------------------------------------

}