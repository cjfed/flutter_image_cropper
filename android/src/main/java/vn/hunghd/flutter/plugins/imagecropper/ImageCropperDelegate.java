package vn.hunghd.flutter.plugins.imagecropper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import com.steelkiwi.cropiwa.*;

import java.io.File;
import java.util.Date;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

import static android.app.Activity.RESULT_OK;

public class ImageCropperDelegate implements PluginRegistry.ActivityResultListener {
    private final Activity activity;
    private MethodChannel.Result pendingResult;
    private MethodCall methodCall;
    private FileUtils fileUtils;
    public static final int REQUEST_CROP = 69;

    public ImageCropperDelegate(Activity activity) {
        this.activity = activity;
        fileUtils = new FileUtils();
    }

    public void startCrop(MethodCall call, MethodChannel.Result result) {
        String sourcePath = call.argument("source_path");
        Integer maxWidth = call.argument("max_width");
        Integer maxHeight = call.argument("max_height");
        Double ratioX = call.argument("ratio_x");
        Double ratioY = call.argument("ratio_y");
        Boolean circleShape = call.argument("circle_shape");
        String title = call.argument("toolbar_title");
        Long toolbarColor = call.argument("toolbar_color");
        Long statusBarColor = call.argument("statusbar_color");
        Long toolbarWidgetColor = call.argument("toolbar_widget_color");
        Long actionBackgroundColor = call.argument("action_background_color");
        Long actionActiveColor = call.argument("action_active_color");
        methodCall = call;
        pendingResult = result;

        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra(MainActivity.IMAGE_PATH, sourcePath);
        activity.startActivityForResult(intent, REQUEST_CROP);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CROP) {
            if (resultCode == 1101) {
                final Uri resultUri = data.getParcelableExtra(MainActivity.OUTPUT_URI);
                finishWithSuccess(fileUtils.getPathFromUri(activity, resultUri));
                return true;
            } else {
                pendingResult.success(null);
                clearMethodCallAndResult();
                return true;
            }
        }
        return false;
    }

    private void finishWithSuccess(String imagePath) {
        pendingResult.success(imagePath);
        clearMethodCallAndResult();
    }

    private void finishWithError(String errorCode, String errorMessage, Throwable throwable) {
        pendingResult.error(errorCode, errorMessage, throwable);
        clearMethodCallAndResult();
    }


    private void clearMethodCallAndResult() {
        methodCall = null;
        pendingResult = null;
    }

    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
}
