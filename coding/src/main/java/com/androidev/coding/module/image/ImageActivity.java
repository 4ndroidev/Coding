package com.androidev.coding.module.image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.androidev.coding.R;
import com.androidev.coding.network.GitHub;
import com.androidev.coding.widget.SwipeBackLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import static com.androidev.coding.misc.Constant.HEADER_ACCEPT;
import static com.androidev.coding.misc.Constant.MEDIA_TYPE_RAW;
import static com.androidev.coding.misc.Constant.OWNER;
import static com.androidev.coding.misc.Constant.REPO;
import static com.androidev.coding.misc.Constant.SHA;

public class ImageActivity extends Activity {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SwipeBackLayout.attachTo(this);
        setContentView(R.layout.coding_activity_image);
        image = (ImageView) findViewById(R.id.coding_image);
        loadImage();
    }

    private void startLoading() {
        findViewById(R.id.coding_loading).setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.coding_loading_rotate_animation);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(3000);
        findViewById(R.id.coding_loading_anim).startAnimation(animation);
    }

    private void stopLoading() {
        findViewById(R.id.coding_loading_anim).clearAnimation();
        findViewById(R.id.coding_loading).setVisibility(View.GONE);
    }

    private void loadImage() {
        startLoading();
        Intent intent = getIntent();
        String owner = intent.getStringExtra(OWNER);
        String repo = intent.getStringExtra(REPO);
        String sha = intent.getStringExtra(SHA);
        String url = GitHub.getInstance().url4image(owner, repo, sha);
        Headers headers = new LazyHeaders.Builder().addHeader(HEADER_ACCEPT, MEDIA_TYPE_RAW).build();
        GlideUrl image = new GlideUrl(url, headers);
        Glide.with(ImageActivity.this).load(image).listener(new RequestListener<GlideUrl, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
                stopLoading();
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                stopLoading();
                return false;
            }
        }).into(this.image);
    }

}
