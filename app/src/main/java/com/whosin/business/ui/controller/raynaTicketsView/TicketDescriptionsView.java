package com.whosin.business.ui.controller.raynaTicketsView;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentManager;

import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.TicketDescriptionViewBinding;
import com.whosin.business.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;

public class TicketDescriptionsView extends ConstraintLayout {

    private TicketDescriptionViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private boolean isViewClose = true;


    public TicketDescriptionsView(Context context) {
        this(context, null);
    }

    public TicketDescriptionsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TicketDescriptionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.ticket_description_view, this, (view, resid, parent) -> {
            binding = TicketDescriptionViewBinding.bind(view);

            TicketDescriptionsView.this.removeAllViews();
            TicketDescriptionsView.this.addView(view);
        });
    }

    public void setUpData(Activity activity,FragmentManager fragmentManager,String titleTv,String descrption){
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;

        if (binding == null) return;

        binding.tvTitle.setText(titleTv);
        binding.tvReadMore.setText(Utils.getLangValue("read_more"));

        binding.tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvDescription.setText(Html.fromHtml(descrption
                .replace("<li>", "<li>&nbsp;&nbsp;")
                .replace("</li>", "</li>")
                .replace("</ul>", "</ul><br>"), HtmlCompat.FROM_HTML_MODE_LEGACY
        ));

//        if (isViewClose) {
//            binding.descrptionView.setVisibility(View.VISIBLE);
//            binding.ivDropDown.setRotation(270f);
//        } else {
//            binding.descrptionView.setVisibility(View.GONE);
//            binding.ivDropDown.setRotation(90f);
//        }
//
//        isViewClose = !isViewClose;



        binding.headerLayout.setOnClickListener(v -> {
            if (isViewClose) {
                binding.descrptionView.setVisibility(View.VISIBLE);
                binding.ivDropDown.setRotation(270f);
            } else {
                binding.descrptionView.setVisibility(View.GONE);
                binding.ivDropDown.setRotation(90f);
            }
            isViewClose = !isViewClose;
        });


        binding.tvReadMore.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
            bottomSheet.title = titleTv;
            bottomSheet.formattedDescription = descrption;
            bottomSheet.show(supportFragmentManager,"");
        });


        binding.tvDescription.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int lineCount = binding.tvDescription.getLineCount();
            if (lineCount <= 5) {
                 binding.tvReadMore.setVisibility(View.GONE);
            } else {
                binding.tvReadMore.setVisibility(View.VISIBLE);
            }
        });


    }









}
