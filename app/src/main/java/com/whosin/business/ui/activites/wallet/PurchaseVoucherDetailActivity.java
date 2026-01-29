package com.whosin.business.ui.activites.wallet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.whosin.business.R;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.ActivityPurchaseVoucherDetailBinding;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.MyWalletModel;
import com.whosin.business.ui.activites.comman.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class PurchaseVoucherDetailActivity extends BaseActivity {

    private ActivityPurchaseVoucherDetailBinding binding;

    private MyWalletModel myWalletModel;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        Graphics.applyBlurEffectOnClaimScreen(activity, binding.blurView);
        String model = getIntent().getStringExtra("itemList");
        myWalletModel = new Gson().fromJson(model, MyWalletModel.class);
        setDetail();
    }

    @Override
    protected void setListeners() {
        binding.btnDownloadVoucher.setOnClickListener(view -> {
            Bitmap cardContentBitmap = captureCardViewContent(binding.layout);
            createPdf(cardContentBitmap);
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityPurchaseVoucherDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setDetail() {

        if (myWalletModel != null) {
            if(myWalletModel.getType().equals( "deal" )) {


                Graphics.loadImageWithFirstLetter( myWalletModel.getDeal().getVenue().getLogo(), binding.imgUserLogo, myWalletModel.getDeal().getVenue().getName() );
                binding.tvTitle.setText( myWalletModel.getDeal().getVenue().getName() );
                binding.tvAddress.setText( myWalletModel.getDeal().getVenue().getAddress() );
                Graphics.loadImage( myWalletModel.getDeal().getVenue().getCover(), binding.ivCover );
                binding.txtName.setText( myWalletModel.getDeal().getTitle() );
                binding.txtDescription.setText( myWalletModel.getDeal().getDescription() );

                binding.txtTicketHolder.setText( SessionManager.shared.getUser().getFullName() );
                binding.txtPerPaxes.setText("AED " + String.valueOf(myWalletModel.getDeal().getDiscountedPrice()));

                 if (!myWalletModel.getItems().isEmpty()){
                    binding.txtQuantity.setText( "(" + myWalletModel.getItems().get( 0 ).getQty() + "X)" );

                }

                Date date, time;
                String startDate;
                String startTime;
                try {
                    date = Utils.stringToDate( myWalletModel.getDeal().getEndDate(), "yyyy-MM-dd" );
                    time = Utils.stringToDate( myWalletModel.getDeal().getEndTime(), "HH:mm" );
                    startDate = Utils.formatDate( date, "dd MMM, yyyy" );
                    startTime = Utils.formatDate( time, "hh:mm a" );
                } catch (Exception e) {
                    throw new RuntimeException( e );
                }


                binding.txtDate.setText( startDate + " at " + startTime );

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty( "id", myWalletModel.getId() );


                String jsonString = jsonObject.toString();

                Bitmap qrCodeBitmap = generateQRCodeFromJson( jsonString, 900, 900 );
                if (qrCodeBitmap != null) {
                    binding.ivBarcode.setImageBitmap( qrCodeBitmap );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        showExpired(myWalletModel.getDeal().getEndDate(),myWalletModel.getDeal().getEndTime());
                    }
                }


            }else {

                Graphics.loadImageWithFirstLetter( myWalletModel.getActivity().getProvider().getLogo(), binding.imgUserLogo, myWalletModel.getActivity().getProvider().getName() );
                binding.tvTitle.setText( myWalletModel.getActivity().getProvider().getName() );
                binding.tvAddress.setText( myWalletModel.getActivity().getProvider().getAddress() );
                Graphics.loadImage( myWalletModel.getActivity().getGalleries().get( 0 ), binding.ivCover );
                binding.txtName.setText( myWalletModel.getActivity().getName() );
                binding.txtDescription.setText(myWalletModel.getActivity().getDescription());

                binding.txtTicketHolder.setText( SessionManager.shared.getUser().getFullName() );

                int discount = Integer.parseInt(  myWalletModel.getActivity().getDiscount() );
                int amount = myWalletModel.getActivity().getPrice();
                int  value = discount * amount/ 100;
                int discountAmount = amount - value;

                binding.txtPerPaxes.setText("AED " + String.valueOf(discountAmount));

                if (!myWalletModel.getItems().isEmpty()){

                    binding.txtQuantity.setText( "(" + myWalletModel.getItems().get( 0 ).getQty() + "X)" );



                    String startDate = "";
                    String startTime = "";

                    String detectedFormat = Utils.detectDateFormat(myWalletModel.getItems().get(0).getDate());
                    if (detectedFormat != null) {
                        startDate = Utils.convertDateFormat(myWalletModel.getItems().get(0).getDate(), detectedFormat, "dd MMM, yyyy");
                        startTime = Utils.convertTimeFormat(myWalletModel.getItems().get( 0 ).getTime());
                        Log.d("Date", "setupData: " + startDate);
                    }



                    binding.txtDate.setText( startDate + " at " + startTime );

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty( "id", myWalletModel.getId() );


                    String jsonString = jsonObject.toString();

                    Bitmap qrCodeBitmap = generateQRCodeFromJson( jsonString, 900, 900 );
                    if (qrCodeBitmap != null) {
                        binding.ivBarcode.setImageBitmap( qrCodeBitmap );
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showExpired(myWalletModel.getItems().get(0).getDate(),myWalletModel.getItems().get( 0 ).getTime());
                        }
                    }
                }



            }
        }
    }

    private Bitmap captureCardViewContent(ConstraintLayout cardView) {
        Bitmap cardBitmap = Bitmap.createBitmap(cardView.getWidth(), cardView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cardBitmap);
        cardView.draw(canvas);
        return cardBitmap;
    }

    private void createPdf(Bitmap cardContentBitmap) {
        performPdfCreation(cardContentBitmap);
    }
    private void performPdfCreation(Bitmap cardContentBitmap) {

        // Proceed with PDF creation
        Bitmap bitmap = setBitmapBackgroundWhite(cardContentBitmap);
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        canvas.drawPaint(paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        pdfDocument.finishPage(page);
        String fileName = "";
        if(myWalletModel.getType().equals( "deal" )){
             fileName = "WhosIn" + myWalletModel.getActivity().getId() + ".pdf";

        }else {
            fileName = "WhosIn" + myWalletModel.getDeal().getId() + ".pdf";

        }

        File externalDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File filePath = new File(externalDir, fileName);

        try {
            OutputStream outputStream = new FileOutputStream(filePath);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
            Toast.makeText(this, "PDF saved to " + filePath.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.d("TAG", "createPdf: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "createPdf: " + filePath);
        }

        pdfDocument.close();
    }

    private Bitmap setBitmapBackgroundWhite(Bitmap cardContentBitmap) {
        Bitmap bitmapWithWhiteBackground = Bitmap.createBitmap(
                cardContentBitmap.getWidth(),
                cardContentBitmap.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmapWithWhiteBackground);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(cardContentBitmap, 0, 0, null);
        return bitmapWithWhiteBackground;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Bitmap cardContentBitmap = captureCardViewContent(binding.layout);
                createPdf(cardContentBitmap);
            } else {
                Toast.makeText(this, "Permission denied. Cannot create PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPermissionDialog() {
        // You can customize the dialog to inform the user about the required permission
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required").setMessage("Please grant storage permission to create PDF.")
                .setPositiveButton("Grant", (dialog, which) -> {
                    // Open the app's settings to allow the user to grant the permission manually
                    Intent intent = new Intent( Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the case when the user chooses not to grant the permission
                    Toast.makeText(this, "Permission denied. Cannot create PDF.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    public static Bitmap generateQRCodeFromJson(String jsonData, int width, int height) {

        try {
            String qrCodeData = "data:" + jsonData;
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, width, height);

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[y * width + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showExpired(String dateString , String timeString) {
//        String dateString = model.getItems().get(0).getDate();
//        String timeString = model.getItems().get(0).getTime();
        String dateTimeString = dateString + " " + timeString;

        DateTimeFormatter formatter = null;

        if (dateString.length() == 10) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            }
        } else if (dateString.length() == 8) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            }
        } else {
            // Handle the case when the date format is not recognized
            throw new IllegalArgumentException("Unsupported date format");
        }

        try {
            LocalDateTime dateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateTime = LocalDateTime.parse(dateTimeString, formatter);
            }

            // Check if the specified date and time have expired
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (dateTime != null && dateTime.isBefore(LocalDateTime.now())) {
                   binding.expriedImage.setVisibility(View.VISIBLE);
                   binding.btnDownloadVoucher.setVisibility(View.GONE);
                }else {
                    binding.expriedImage.setVisibility(View.GONE);
                    binding.btnDownloadVoucher.setVisibility(View.VISIBLE);
                }
            }
        } catch (DateTimeParseException e) {
            // Handle the case when the parsing fails
            e.printStackTrace();
            // Handle the error accordingly
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

    // --------------------------------------
    // endregion


}