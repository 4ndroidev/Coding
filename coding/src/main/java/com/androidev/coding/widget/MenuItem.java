package com.androidev.coding.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidev.coding.R;

/**
 * Created by 4ndroidev on 16/11/11.
 */

public class MenuItem extends LinearLayout {

    private ImageView icon;
    private TextView name;

    public MenuItem(Context context) {
        this(context, null);
    }

    public MenuItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        Resources resources = getResources();
        int paddingHorizontal = resources.getDimensionPixelSize(R.dimen.coding_menu_padding_horizontal);
        int paddingVertical = resources.getDimensionPixelSize(R.dimen.coding_menu_padding_vertical);
        setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CodingMenuItem, defStyleAttr, 0);
        Drawable icon = array.getDrawable(array.getIndex(R.styleable.CodingMenuItem_icon));
        String name = array.getString(array.getIndex(R.styleable.CodingMenuItem_name));
        array.recycle();
        LayoutInflater.from(context).inflate(R.layout.coding_layout_menu_item, this, true);
        this.icon = (ImageView) findViewById(R.id.coding_icon);
        this.name = (TextView) findViewById(R.id.coding_name);
        this.icon.setImageDrawable(icon);
        this.name.setText(name);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setIcon(Drawable icon) {
        this.icon.setImageDrawable(icon);
    }
}
