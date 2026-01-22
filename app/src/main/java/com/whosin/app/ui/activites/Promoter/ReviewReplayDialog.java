package com.whosin.app.ui.activites.Promoter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentReviewReplayDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.ReviewReplayModel;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;


public class ReviewReplayDialog extends DialogFragment {

    private FragmentReviewReplayDialogBinding binding;
    public String replayId = "";
    public boolean isPromoter = false;
    public String replay = "";

    public CommanCallback<String> callback;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListeners();
        return view;
    }

    public void initUi(View view) {

        binding = FragmentReviewReplayDialogBinding.bind( view );

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        binding.WriteReplyTitle.setText(Utils.getLangValue("write_a_reply"));
        binding.tvName.setHint(Utils.getLangValue("enter_your_reply"));
        binding.tvCancel.setText(Utils.getLangValue("cancel"));
        binding.tvDone.setText(Utils.getLangValue("submit"));


        if (!replay.isEmpty()){
            binding.tvName.setText(replay);
        }

    }

    private void setListeners() {

        binding.tvCancel.setOnClickListener( view -> dismiss() );

        binding.tvDone.setOnClickListener( view -> {
            requestReplayAddUpdateReview();
        } );
    }

    private int getLayoutRes() {
        return R.layout.fragment_review_replay_dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestReplayAddUpdateReview() {
        String message = binding.tvName.getText().toString();
        JsonObject object = new JsonObject();
        object.addProperty("reviewId", replayId);
        object.addProperty("reply",message  );
        Graphics.showProgress(requireActivity());

        DataService.shared(requireActivity()).requestReplayAddUpdateReview(object, new RestCallback<ContainerModel<ReviewReplayModel>>(this) {
            @Override
            public void result(ContainerModel<ReviewReplayModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                EventBus.getDefault().post(new MessageEvent());
                dismiss();
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter


    // endregion
    // --------------------------------------


}

