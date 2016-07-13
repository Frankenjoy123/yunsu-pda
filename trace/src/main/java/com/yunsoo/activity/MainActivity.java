package com.yunsoo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunsoo.entity.AuthUser;
import com.yunsoo.exception.BaseException;
import com.yunsoo.exception.NetworkNotAvailableException;
import com.yunsoo.exception.ServerAuthException;
import com.yunsoo.manager.SessionManager;
import com.yunsoo.service.DataServiceImpl;
import com.yunsoo.service.PermanentTokenLoginService;
import com.yunsoo.util.ToastMessageHelper;

import org.json.JSONObject;

public class MainActivity extends BaseActivity implements OnClickListener{

    private RelativeLayout rl_pack_scan;
    private RelativeLayout rl_path_scan;
    private RelativeLayout rl_modify_package;

    private AuthUser tempAuthUser;
    private String permanentToken;
    private String accessToken;
    private String api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
        setupActionItems();
        checkAuthorizeStatus();
	}

    private void checkAuthorizeStatus() {
        SessionManager.getInstance().restore();
        AuthUser authUser=SessionManager.getInstance().getAuthUser();
        permanentToken=authUser.getPermanentToken();
        accessToken=authUser.getAccessToken();
        api=authUser.getApi();
        PermanentTokenLoginService service= new PermanentTokenLoginService(permanentToken);
        service.setDelegate(MainActivity.this);
        service.start();
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences=getSharedPreferences("yunsoo_pda", MODE_PRIVATE);
        Boolean isAuthorize=preferences.getBoolean("isAuthorize",false);
        if (!isAuthorize){
            finish();
        }
        super.onResume();
    }

    private void setupActionItems() {

        buildViewContent(this.findViewById(R.id.rl_yunsoo_pack), R.drawable.ic_pack, R.string.yunsoo_pack);
        buildViewContent(this.findViewById(R.id.rl_yunsoo_path), R.drawable.ic_delivery, R.string.yunsoo_path);
        buildViewContent(this.findViewById(R.id.rl_settting), R.drawable.ic_my_settings, R.string.settings);
    }

    private void buildViewContent(View view, int imageResourceId, int textResourceId) {
        ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
        iv.setImageResource(imageResourceId);
        TextView tv = (TextView) view.findViewById(R.id.tv_action_name);
        tv.setText(textResourceId);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_yunsoo_pack:
                Intent intent1=new Intent(MainActivity.this,PackMainActivity.class);
				startActivity(intent1);
                break;
            case R.id.rl_yunsoo_path:
				Intent intent2=new Intent(MainActivity.this,PathMainActivity.class);
				startActivity(intent2);
                break;
            case R.id.rl_settting:
                Intent intent3=new Intent(MainActivity.this,GlobalSettingActivity.class);
                startActivity(intent3);
                break;
        }
    }

    @Override
    public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached) {
        if (service instanceof PermanentTokenLoginService){
            String newAccessToken=data.optString("token");
            int expires_in=data.optInt("expires_in");
            SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("isAuthorize", true);
            editor.commit();
            tempAuthUser=new AuthUser();
            tempAuthUser.setAccessToken(newAccessToken);
            tempAuthUser.setApi(api);
            tempAuthUser.setPermanentToken(permanentToken);
            SessionManager.getInstance().saveLoginCredential(tempAuthUser);
        }

    }

    @Override
    public void onRequestFailed(final DataServiceImpl service, final BaseException exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (service instanceof PermanentTokenLoginService && exception instanceof ServerAuthException){
                    SharedPreferences preferences=getSharedPreferences("yunsoo_pda",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean("isAuthorize", false);
                    editor.commit();
                    SessionManager.getInstance().logout();
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.not_authorize)
                            .setMessage(R.string.not_authorize_message)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent=new Intent(MainActivity.this,AuthorizeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).create();
                    dialog.setCancelable(false);
                    dialog.show();
                }else {
                    ToastMessageHelper.showErrorMessage(MainActivity.this,exception.getMessage(),true);
                }
            }
        });

    }
}
