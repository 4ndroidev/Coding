package com.androidev.coding.module.base;


import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.androidev.coding.R;

public class BaseActivity extends AppCompatActivity {

    private Dialog loading;
    private Animation animation;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAnimationIfNeeded() {
        if (animation != null) return;
        animation = AnimationUtils.loadAnimation(this, R.anim.coding_loading_rotate_animation);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(3000);
    }

    public void showLoading() {
        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }
        loadAnimationIfNeeded();
        loading = new Dialog(this, R.style.LoadingDialog);
        loading.setCancelable(true);
        loading.setCanceledOnTouchOutside(true);
        loading.setContentView(R.layout.coding_layout_loading_dialog);
        loading.show();
        View anim = loading.findViewById(R.id.coding_loading_anim);
        anim.clearAnimation();
        anim.startAnimation(animation);
    }

    public void dismissLoading() {
        if (loading == null || !loading.isShowing()) return;
        loading.findViewById(R.id.coding_loading_anim).clearAnimation();
        loading.dismiss();
    }

}
