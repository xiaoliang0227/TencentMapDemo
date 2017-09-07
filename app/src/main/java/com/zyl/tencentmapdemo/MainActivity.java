package com.zyl.tencentmapdemo;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tencent.lbssearch.object.result.SearchResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationSource, TencentLocationListener {

    private final static int CALLBACK_FROM_PLACE_SEARCH = 1000;

    private MapView map;

    private TencentMap tenMap;

    private LocationSource.OnLocationChangedListener mChangedListener;

    private TencentLocationRequest loactionRequest;

    private TencentLocationManager loactionManager;

    private boolean isFirst = true;

    private double latitude, longitude;

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initField();
        initMap();
    }

    private void initMap() {
        tenMap = map.getMap();
        tenMap.setMapType(TencentMap.MAP_TYPE_NORMAL);
        UiSettings settings = tenMap.getUiSettings();
        // 缩放控件
        settings.setZoomControlsEnabled(true);
        // 指南针
        settings.setCompassEnabled(true);
        // 定位控件
        loactionManager = TencentLocationManager.getInstance(this);
        loactionRequest = TencentLocationRequest.create();
        //请求周期
        loactionRequest.setInterval(4000);
        settings.setMyLocationButtonEnabled(true);
        tenMap.setMyLocationEnabled(true);
        tenMap.setLocationSource(this);
    }

    private void initField() {
        map = (MapView) findViewById(R.id.map);
    }

    @Override
    protected void onStart() {
        super.onStart();
        map.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        map.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
        loactionManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
        loactionManager.requestLocationUpdates(loactionRequest, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                setMapType(TencentMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.satellite:
                setMapType(TencentMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.night_mode:
                setMapType(TencentMap.MAP_TYPE_NIGHT);
                return true;
            case R.id.navi_mode:
                setMapType(TencentMap.MAP_TYPE_NAVI);
                return true;
            case R.id.traffic_enable:
                enableTraffic();
                return true;
            case R.id.search:
                searchClicked();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setMapType(int type) {
        tenMap.setMapType(type);
    }

    private void enableTraffic() {
        tenMap.setTrafficEnabled(!tenMap.isTrafficEnabled());
    }

    private void searchClicked() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivityForResult(intent, CALLBACK_FROM_PLACE_SEARCH);
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        if (i == TencentLocation.ERROR_OK && mChangedListener != null) {
            this.latitude = tencentLocation.getLatitude();
            this.longitude = tencentLocation.getLongitude();
            Log.e("当前的点坐标****", tencentLocation.getLatitude() + " " + tencentLocation.getLongitude() + " " + tencentLocation.getAddress());
            if (isFirst) {
                this.isFirst = false;
                //当前点
                Location location = new Location(tencentLocation.getProvider());
                location.setLatitude(tencentLocation.getLatitude());
                location.setLongitude(tencentLocation.getLongitude());
                location.setAccuracy(tencentLocation.getAccuracy());


                //定位到当前位置并且设置缩放级别
                tenMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude())));
                mChangedListener.onLocationChanged(location);
            }

        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.isFirst = true;
        mChangedListener = onLocationChangedListener;
        int err = loactionManager.requestLocationUpdates(loactionRequest, this);
        switch (err) {
            case 1:
                setTitle("设备缺少使用腾讯定位服务需要的基本条件");
                break;
            case 2:
                setTitle("manifest 中配置的 key 不正确");
                break;
            case 3:
                setTitle("自动加载libtencentloc.so失败");
                break;

            default:
                break;
        }
    }

    @Override
    public void deactivate() {
        loactionManager.removeUpdates(this);
        loactionManager = null;
        loactionRequest = null;
        mChangedListener = null;
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CALLBACK_FROM_PLACE_SEARCH) {
            tenMap.clear();
            List<SearchResultObject.SearchResultData> result = DemoApplication.searchResult;
            for (SearchResultObject.SearchResultData item : result) {
                // 标注坐标
                LatLng position = new LatLng(item.location.lat, item.location.lng);
                MarkerOptions options = new MarkerOptions(position);
                options.title(item.title);
                tenMap.addMarker(new MarkerOptions(position).title(item.title));

                tenMap.setOnMarkerClickListener(new TencentMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Log.d(TAG, "marker:" + marker);
                        return false;
                    }
                });
            }
        }
     }
}
