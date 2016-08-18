package com.yunsu.common.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android_library.R;


public class TitleBar extends FrameLayout {

    private CheckedTextView titleTextView;
    private View rightButton;
    private View leftButton;
    private TextView rightButtonText;
    private TextView leftButtonText;
    private ImageButton rightImageButton;

    private String title;
    private TitleBarMode mode;


    public enum TitleBarMode {
        TITLE_ONLY,
        BOTH_BUTTONS,
        LEFT_BUTTON,
        RIGHT_BUTTON
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        View rootView = inflater.inflate(R.layout.title_bar_layout, this, false);

        titleTextView = (CheckedTextView) rootView.findViewById(R.id.tv_title);
        rightButton = rootView.findViewById(R.id.ll_btn_right);
        rightButtonText = (TextView) rootView.findViewById(R.id.btn_right);
        leftButton = rootView.findViewById(R.id.ll_btn_left);
        leftButtonText = (TextView) rootView.findViewById(R.id.btn_left);
        rightImageButton = (ImageButton) rootView.findViewById(R.id.btn_right_img);
        addView(rootView);
    }

    public void setMode(TitleBarMode mode) {
        this.mode = mode;
        switch (mode) {
            case TITLE_ONLY:
                rightButton.setVisibility(View.GONE);
                leftButton.setVisibility(View.GONE);
                break;
            case BOTH_BUTTONS:
                rightButton.setVisibility(View.VISIBLE);
                leftButton.setVisibility(View.VISIBLE);
                break;
            case LEFT_BUTTON:
                rightButton.setVisibility(View.GONE);
                leftButton.setVisibility(View.VISIBLE);
                break;
            case RIGHT_BUTTON:
                rightButton.setVisibility(View.VISIBLE);
                leftButton.setVisibility(View.GONE);
                break;
            default:

        }

    }

    public void setOnLeftButtonClickedListener(OnClickListener listener) {
        leftButton.setOnClickListener(listener);
    }

    public void setOnRightButtonClickedListener(OnClickListener listener) {
        rightButton.setOnClickListener(listener);
        //rightImageButton.setOnClickListener(listener);
    }

    public void setLeftButtonText(String text) {
        leftButtonText.setText(text);
    }

    public void setRightButtonText(String text) {

        if (rightButtonText.getVisibility() == View.GONE) {
            rightButtonText.setVisibility(View.VISIBLE);
        }
        rightButtonText.setText(text);
        rightImageButton.setVisibility(View.GONE);
    }


    public void setTitle(String title) {
        this.title = title;
        this.titleTextView.setText(title);
    }

    public void setDisplayAsBack(boolean show) {
        if (show) {
            leftButton.setVisibility(View.VISIBLE);
            leftButtonText.setText(R.string.back);
            leftButtonText.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.drawable_padding));
            leftButtonText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.back), null, null, null);
            leftButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).onBackPressed();
                }
            });
        }
    }

    public void setRightButtonDrawable(int res_id) {
        if (rightImageButton.getVisibility() == View.GONE) {
            rightImageButton.setVisibility(View.VISIBLE);
        }
        rightImageButton.setImageResource(res_id);
        rightButtonText.setVisibility(View.GONE);
    }

    public View getRightView() {
        /*if (rightButton.getVisibility() == View.VISIBLE)
            return rightButton;
        if (rightImageButton.getVisibility() == View.VISIBLE)
            return rightImageButton;
        return null;*/
        return rightButton;
    }

}
