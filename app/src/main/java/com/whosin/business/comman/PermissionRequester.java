package com.whosin.business.comman;

import android.Manifest;
import android.net.Uri;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionRequester implements ActivityResultCallback<Map<String, Boolean>> {

    private PermissionRequest permissionRequest;
    private ValueCallback<Uri[]> uploadMessageCallback;

    public ActivityResultLauncher<String[]> multiplePermissionLauncher;
    public ActivityResultLauncher<PickVisualMediaRequest> fileChooserLauncher;

    public WebChromeClient getWebChromeClient() {
        return new WebChromeClient() {

            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                uploadMessageCallback = filePathCallback;

                if (fileChooserLauncher != null) {
                    fileChooserLauncher.launch(
                            new PickVisualMediaRequest.Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                    .build()
                    );
                }
                return true;
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                permissionRequest = request;
                List<String> permissions = getRuntimePermissions(request.getResources());
                if (permissions.isEmpty()) {
                    request.grant(request.getResources());
                } else {
                    if (multiplePermissionLauncher != null) {
                        multiplePermissionLauncher.launch(permissions.toArray(new String[0]));
                    }
                }
            }
        };
    }

    @Override
    public void onActivityResult(Map<String, Boolean> result) {
        boolean allGranted = true;
        for (Boolean granted : result.values()) {
            if (!granted) {
                allGranted = false;
                break;
            }
        }
        if (allGranted && permissionRequest != null) {
            permissionRequest.grant(permissionRequest.getResources());
        } else if (permissionRequest != null) {
            permissionRequest.deny();
        }
    }

    public void handleFileChooserResult(Uri result) {
        if (uploadMessageCallback != null) {
            Uri[] uris = result != null ? new Uri[]{result} : null;
            uploadMessageCallback.onReceiveValue(uris);
            uploadMessageCallback = null;
        }
    }

    private List<String> getRuntimePermissions(String[] resources) {
        List<String> permissions = new ArrayList<>();
        for (String resource : resources) {
            switch (resource) {
                case PermissionRequest.RESOURCE_VIDEO_CAPTURE:
                    permissions.add(Manifest.permission.CAMERA);
                    break;
                case PermissionRequest.RESOURCE_AUDIO_CAPTURE:
                    permissions.add(Manifest.permission.RECORD_AUDIO);
                    break;
            }
        }
        return permissions;
    }
}
