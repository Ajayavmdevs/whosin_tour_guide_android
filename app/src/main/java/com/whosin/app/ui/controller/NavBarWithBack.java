package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.helper.widget.CircularFlow;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.databinding.LayoutNavBarWithBackBinding;

import org.checkerframework.common.returnsreceiver.qual.This;

import de.hdodenhof.circleimageview.CircleImageView;


public class NavBarWithBack extends ConstraintLayout {

    private LayoutNavBarWithBackBinding binding;
    private Activity activity;

    public NavBarWithBack(Context context) {
        this( context, null );
    }

    public NavBarWithBack(Context context, AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public NavBarWithBack(Context context, AttributeSet attrs, int defStyle) {
        super( context, attrs, defStyle );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.NavBar, 0, 0 );
        String titleText = a.getString( R.styleable.NavBar_navTitle );
        String rightBtnTitle = a.getString( R.styleable.NavBar_rightBtnTitle );
        String CartItemText = a.getString( R.styleable.NavBar_caertItem );


        int titleColorRes = a.getColor( R.styleable.NavBar_navTitleColor, Color.BLACK );
        int bgColorRes = a.getColor( R.styleable.NavBar_navBgColor, Color.WHITE );

        Boolean isShowBackBtn = a.getBoolean( R.styleable.NavBar_showBackButton, false );
        Boolean isShowRightBtn = a.getBoolean( R.styleable.NavBar_showRightButton, false );
        Boolean isShowCenterBtn = a.getBoolean( R.styleable.NavBar_showCartItem, false );
        Boolean isShowLeftBtn = a.getBoolean( R.styleable.NavBar_showLeftButton, false );
        Boolean isShowSaperator = a.getBoolean( R.styleable.NavBar_showSaperator, false );
        Boolean isShowCart = a.getBoolean( R.styleable.NavBar_showCartButton, false );
        Boolean isLinear = a.getBoolean( R.styleable.NavBar_linear, false );
        Boolean isShowSearchButton = a.getBoolean( R.styleable.NavBar_showSerachButton, false );

        a.recycle();

        View view = LayoutInflater.from( context ).inflate( R.layout.layout_nav_bar_with_back, this );
        binding = LayoutNavBarWithBackBinding.bind( view );
        binding.getRoot().setBackgroundColor( bgColorRes );

        binding.txtTitleNav.setText( titleText );
        binding.txtTitleNav.setTextColor( titleColorRes );
        binding.txtRightTitle.setText( rightBtnTitle );
        binding.tvCartItem.setText( CartItemText );

//        Graphics.applyBlurEffect(activity,binding.blurViewHeader);

        binding.txtRightTitle.setVisibility( TextUtils.isEmpty( rightBtnTitle ) ? GONE : VISIBLE );
        binding.txtTitleNav.setVisibility( TextUtils.isEmpty( titleText ) ? GONE : VISIBLE );
        binding.tvCartItem.setVisibility( TextUtils.isEmpty( CartItemText ) ? GONE : VISIBLE );

        // binding.imageBackNav.setImageDrawable(leftImgRes);
//        binding.imageBackNav.setRotation( 90 );

        binding.imageBackNav.setVisibility( Boolean.TRUE.equals( isShowBackBtn ) ? VISIBLE : GONE );
        binding.imageMenu.setVisibility( Boolean.TRUE.equals( isShowRightBtn ) ? VISIBLE : GONE );
        binding.imageNotification.setVisibility( Boolean.TRUE.equals( isShowLeftBtn ) ? VISIBLE : GONE );
        binding.notificationLayout.setVisibility( Boolean.TRUE.equals( isShowLeftBtn ) ? VISIBLE : GONE );
        binding.imageSetting.setVisibility( Boolean.TRUE.equals( isShowSaperator ) ? VISIBLE : GONE );
        binding.imageMyCart.setVisibility( Boolean.TRUE.equals( isShowCart ) ? VISIBLE : GONE );
        binding.imageCartLayout.setVisibility( Boolean.TRUE.equals( isLinear ) ? VISIBLE : GONE );
        binding.imgSearch.setVisibility( Boolean.TRUE.equals( isShowSearchButton ) ? VISIBLE : GONE );



    }

    public void applyBlurEffect(Activity activity) {
        Graphics.applyBlurEffect( activity, binding.blurViewHeader );
    }

    public RoundButton getBackBtn() {
        return binding.imageBackNav;
    }
    public ImageView getSearchBtn() {
        return binding.imgSearch;
    }

    public ImageView getRightBtn() {
        return binding.imageMenu;
    }

    public CircleImageView getSettingBtn() {
        return binding.imageSetting;
    }

    public ImageView getLeftBtn() {
        return binding.imageNotification;
    }

    public ImageView getCartBtn() {
        return binding.imageMyCart;
    }

    public ConstraintLayout getLinearBtn() {
        return binding.imageCartLayout;
    }


    public void setTxtTitle(String title) {
        binding.txtTitleNav.setText( title );
    }

    public void setTxtRightTitle(String title) {
        binding.txtRightTitle.setText( title );
    }

    public void setgetGreetingText(String text) {
        binding.txtTitleNav.setText( text );
    }

    public void setCartItem(String title) {
        binding.tvCartItem.setText( title );
    }

    public void setCount(String title) {
        binding.tvCount.setText( title );
    }

    public void setCountVisible(boolean isVisible) {
        binding.notificationCountLayout.setVisibility( isVisible ? View.VISIBLE : View.GONE );
    }

    public void setItemCartVisible(boolean isVisible) {
        binding.linear.setVisibility( isVisible ? View.VISIBLE : View.GONE );
    }

    public TextView getTextRightBtn() {
        return binding.txtRightTitle;
    }


}
