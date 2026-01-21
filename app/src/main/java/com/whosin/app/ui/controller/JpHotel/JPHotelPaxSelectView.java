package com.whosin.app.ui.controller.JpHotel;

import android.app.Activity;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.app.R;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.CustomJpHotelPaxSelectViewBinding;

public class JPHotelPaxSelectView extends ConstraintLayout {

    private CustomJpHotelPaxSelectViewBinding binding;

    private Activity activity;

    private CommanCallback<Boolean> plusAndMinusCallBack = null;

    private CommanCallback<Integer> countCallBack = null;

    private int count = 0;

    private boolean isAdult = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public JPHotelPaxSelectView(Context context) {
        this(context, null);
    }

    public JPHotelPaxSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JPHotelPaxSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);

        View view = LayoutInflater.from(context).inflate(R.layout.custom_jp_hotel_pax_select_view, this);

        binding = CustomJpHotelPaxSelectViewBinding.bind(view);

        binding.btnPlus.setOnClickListener(v -> updateParticipantCount(true));

        binding.btnMinus.setOnClickListener(v -> updateParticipantCount(false));

    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void setUpData(Activity activity, String title, String age, int count ,boolean isAdult, CommanCallback<Boolean> callback,CommanCallback<Integer> countCallBack) {
        this.activity = activity;
        this.plusAndMinusCallBack = callback;
        this.countCallBack = countCallBack;
        this.isAdult = isAdult;
        this.count = count;
        if (binding == null) return;
        binding.tvPaxTitle.setText(title);
        binding.tvPaxAge.setText(age);
        binding.tvPaxCount.setText(String.valueOf(count));
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void updateParticipantCount(boolean increment) {
        if (increment) {
            count++;
            if (plusAndMinusCallBack != null){
                plusAndMinusCallBack.onReceive(true);
            }
        } else {
            if (count > (isAdult ? 1 : 0)) count--;
            if (plusAndMinusCallBack != null){
                plusAndMinusCallBack.onReceive(false);
            }
        }
        binding.tvPaxCount.setText(String.valueOf(count));
        if (countCallBack != null){
            countCallBack.onReceive(count);
        }
        hapticFeedback();
    }

    private void hapticFeedback() {
        if (activity == null) return;
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    // endregion
    // --------------------------------------
}
