package com.whosin.app.ui.controller.promoter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityPromoterAddExtraGuestViewBinding;

public class PromoterAddExtraGuestView extends ConstraintLayout {

    private ActivityPromoterAddExtraGuestViewBinding binding;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public PromoterAddExtraGuestView(Context context) {
        this(context, null);
    }

    public PromoterAddExtraGuestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromoterAddExtraGuestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = LayoutInflater.from(context).inflate(R.layout.activity_promoter_add_extra_guest_view, this);
        binding = ActivityPromoterAddExtraGuestViewBinding.bind(view);

        binding.repeatEventTitle.setText(Utils.getLangValue("number_of_extra_guest"));
        binding.tvGuestAge.setText(Utils.getLangValue("guest_age"));
        binding.tvGuestDressCodeTitle.setText(Utils.getLangValue("guest_dress_code"));
        binding.tvGuestNationalityTitle.setText(Utils.getLangValue("guest_nationality"));
        binding.tvGuestGender.setText(Utils.getLangValue("guest_gender"));


        setupRadioGroup();
    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // Setup radio group listener
    private void setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            binding.specificationLayout.setVisibility(
                    checkedId == R.id.radioAnyone ? View.GONE : View.VISIBLE
            );
        });
    }

    public void setUData() {
        attachTextWatchers(
                binding.numberOfExtraGuest,
                binding.guestAgeRange1,
                binding.guestAgeRange2,
                binding.guestDressCode
        );
    }

    // Helper method to attach the same TextWatcher to multiple EditText fields
    private void attachTextWatchers(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.addTextChangedListener(new CustomTextWatcher(editText));
        }
    }

    // CustomTextWatcher that works with multiple EditText fields
    private class CustomTextWatcher implements TextWatcher {

        private final EditText editText;

        public CustomTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Optional: Implement this if needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // You can handle logic for each EditText based on its ID here
            switch (editText.getId()) {
                case R.id.numberOfExtraGuest:

                    break;
                case R.id.guestAgeRange1:

                    break;
                case R.id.guestAgeRange2:

                    break;
                case R.id.guestDressCode:

                    break;

            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}
