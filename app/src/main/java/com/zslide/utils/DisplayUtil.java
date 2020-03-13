package com.zslide.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class DisplayUtil {

    public static final int MINIMUM_TEXTURE_SIZE = 2048;
    private static int maxTextureSize = 0;
    private static int tryCount = 0;

    public static int dpToPx(float dp, Resources res) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                res.getDisplayMetrics());
    }

    public static int getNavigationHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenHeight;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            screenHeight = display.getHeight();
        } else {
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            screenWidth = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static int getStatusBarHeight(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = 0;
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    public static int getActionBarHeight(Context context) {
        TypedValue typedValue = new TypedValue();
        int resourceId = android.support.v7.appcompat.R.attr.actionBarSize;
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(resourceId, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    typedValue.data,
                    context.getResources().getDisplayMetrics());
        }

        return actionBarHeight;
    }

    public static int getMaxTextureSize() {
        if (maxTextureSize > 0) {
            return maxTextureSize;
        }
        EGLDisplay dpy = null;
        EGLSurface surf = null;
        EGLContext ctx = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            int[] vers = new int[2];
            EGL14.eglInitialize(dpy, vers, 0, vers, 1);

            int[] configAttr = {
                    EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
                    EGL14.EGL_LEVEL, 0,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfig = new int[1];
            EGL14.eglChooseConfig(dpy, configAttr, 0,
                    configs, 0, 1, numConfig, 0);
            if (numConfig[0] == 0) {
                // TROUBLE! No config found.
            }
            EGLConfig config = configs[0];

            int[] surfAttr = {
                    EGL14.EGL_WIDTH, 64,
                    EGL14.EGL_HEIGHT, 64,
                    EGL14.EGL_NONE
            };
            surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);

            int[] ctxAttrib = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);

            EGL14.eglMakeCurrent(dpy, surf, surf, ctx);
        }

        int[] maxTextureSizeGL10 = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxTextureSizeGL10, 0);

        int[] maxTextureSizeGL20 = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSizeGL20, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(dpy, surf);
            EGL14.eglDestroyContext(dpy, ctx);
            EGL14.eglTerminate(dpy);
        }

        int max;
        if (maxTextureSizeGL10[0] != 0 && maxTextureSizeGL20[0] != 0) {
            max = Math.min(maxTextureSizeGL10[0], maxTextureSizeGL20[0]);
        } else if (maxTextureSizeGL10[0] == 0 && maxTextureSizeGL20[0] == 0) {
            if (tryCount++ > 3) {
                tryCount = 0;
                ZLog.e(DisplayUtil.class, "fail to getZummaCast max texture size");
                return MINIMUM_TEXTURE_SIZE;
            } else {
                max = getMaxTextureSize();
            }
        } else {
            max = Math.max(maxTextureSizeGL10[0], maxTextureSizeGL20[0]);
        }
        maxTextureSize = max;
        ZLog.i(DisplayUtil.class, "max texture size : " + max);
        return max;
    }

    public static void showKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(view, 0);
    }

    public static void hideKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            hideKeyboard(activity, view);
        }
    }
}
