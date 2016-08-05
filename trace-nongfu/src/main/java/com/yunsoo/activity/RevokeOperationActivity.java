package com.yunsoo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsoo.util.Constants;
import com.yunsoo.view.TitleBar;

public class RevokeOperationActivity extends Activity implements View.OnClickListener {
    private TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeal_operation);
        init();
        setupActionItems();
    }

    private void init() {
        getActionBar().hide();
        titleBar= (TitleBar) findViewById(R.id.repeal_title_bar);
        titleBar.setTitle(getString(R.string.repeal_operation));
        titleBar.setMode(TitleBar.TitleBarMode.LEFT_BUTTON);
        titleBar.setDisplayAsBack(true);
    }

    private void setupActionItems() {
        buildViewContent(this.findViewById(R.id.rl_repeal_inbound), R.drawable.ic_synchronize, R.string.repeal_inbound);
        buildViewContent(this.findViewById(R.id.rl_repeal_outbound), R.drawable.ic_data_report, R.string.repeal_outbound);
    }

    private void buildViewContent(View view, int imageResourceId, int textResourceId) {
        ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
        iv.setImageResource(imageResourceId);
        TextView tv = (TextView) view.findViewById(R.id.tv_action_name);
        tv.setText(textResourceId);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_repeal_inbound:
                Intent intent1=new Intent(RevokeOperationActivity.this,RevokeScanActivity.class);
                intent1.putExtra(Constants.TITLE,Constants.Logistic.REVOKE_INBOUND);
                startActivity(intent1);
                break;
            case R.id.rl_repeal_outbound:
                Intent intent2=new Intent(RevokeOperationActivity.this,RevokeScanActivity.class);
                intent2.putExtra(Constants.TITLE,Constants.Logistic.REVOKE_INBOUND);
                startActivity(intent2);
                break;
        }
    }
}
