
package com.test;

import com.tencent.tencentmap.streetviewsdk.StreetThumbListener;
import com.tencent.tencentmap.streetviewsdk.StreetViewListener;
import com.tencent.tencentmap.streetviewsdk.StreetViewShow;

import com.tencent.tencentmap.streetviewsdk.map.basemap.GeoPoint;
import com.tencent.tencentmap.streetviewsdk.overlay.ItemizedOverlay;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class SosoStreeViewActivityTestActivity extends Activity implements StreetViewListener {
    
    /**
     * View Container
     */
    private ViewGroup mContainer;

    /**
     * 缩略图View
     */
    private ImageView mThumbImage;

    private Handler mHandler;
    
    /**
     * 街景View
     */
    private View mStreetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContainer = (LinearLayout)findViewById(R.id.layout);
        mThumbImage = (ImageView)findViewById(R.id.image);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mThumbImage.setImageBitmap((Bitmap)msg.obj);
            }
        };
                        
        // 使用经纬度获取街景
        // GeoPoint center = new GeoPoint((int)(31.216073 * 1E6),
        // (int)(121.595304 * 1E6));
        // StreetViewShow.getInstance().showStreetView(this, center, 300, this,
        // -170, 0);
        
        // 使用svid获取街景
        StreetViewShow.getInstance().showStreetView(this, "10011026130910162137500", this, -170, 0);
    }

    @Override
    protected void onDestroy() {
    	StreetViewShow.getInstance().destory();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StreetViewShow.getInstance().requestStreetThumb("10011024120916113135600",//"10011505120412110900000",
                new StreetThumbListener() {

                    @Override
                    public void onGetThumbFail() {
                       
                    }

                    @Override
                    public void onGetThumb(Bitmap bitmap) {
                        Message msg = new Message();
                        msg.obj = bitmap;
                        mHandler.sendMessage(msg);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onViewReturn(final View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	mStreetView = v;
                mContainer.addView(mStreetView);
                Log.d("street", StreetViewShow.getInstance().getStreetStatus().toString());
            }
        });
    }

    @Override
    public void onNetError() {
        // 网络错误处理
    }

    @Override
    public void onDataError() {
        // 解析数据错误处理
    }

    CustomerOverlay overlay;
    
    @Override
    public ItemizedOverlay getOverlay() {
        if (overlay == null) {
            ArrayList<CustomPoiData> pois = new ArrayList<CustomPoiData>();
            pois.add(new CustomPoiData(39984066, 116307968, getBm(R.drawable.poi_center),
                    getBm(R.drawable.poi_center_pressed), 0));
            pois.add(new CustomPoiData(39984166, 11630800, getBm(R.drawable.pin_green),
                    getBm(R.drawable.pin_green_pressed), 40));
            pois.add(new CustomPoiData(39984000, 116307968, getBm(R.drawable.pin_yellow),
                    getBm(R.drawable.pin_yellow_pressed), 80));
            pois.add(new CustomPoiData(39984066, 116308088, getBm(R.drawable.pin_red),
                    getBm(R.drawable.pin_red_pressed), 120));
            overlay = new CustomerOverlay(pois);
            overlay.populate();
        }
        return overlay;
    }

    private Bitmap getBm(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inScaled = false;

        return BitmapFactory.decodeResource(getResources(), resId, options);
    }

	@Override
	public void onLoaded() {
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	mStreetView.setVisibility(View.VISIBLE);
            }
        });
	}

    @Override
    public void onAuthFail() {
        // 验证失败
    }
}
