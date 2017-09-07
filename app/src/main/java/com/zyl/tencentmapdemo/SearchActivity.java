package com.zyl.tencentmapdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.DistrictChildrenParam;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.param.SuggestionParam;
import com.tencent.lbssearch.object.result.DistrictResultObject;
import com.tencent.lbssearch.object.result.SearchResultObject;
import com.tencent.lbssearch.object.result.SuggestionResultObject;

import java.util.List;

/**
 * Created by zhaoyongliang on 2017/9/4.
 */

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = "SearchActivity";

    private Context context;

    private TextView currentRegional;
    
    private AutoCompleteTextView searchContent;

    private ImageView btnClear;

    private Button cate, hotel, market, fitness;

    private CheckBox useDistance;

    private RadioButton km_1, km_5, km_10, km_20;

    private ListView resultList;

    private double latitude, longitude;

    private String region = "上海", regional = "浦东新区";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);
        init();
    }

    private void init() {
        initIntentData();
        initField();
        initEvent();
        initRegionals();
    }

    private void initRegionals() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TencentSearch search = new TencentSearch(SearchActivity.this);
                DistrictChildrenParam param = new DistrictChildrenParam();
                param.id(310000);
                search.getDistrictChildren(param, new HttpResponseListener() {
                    @Override
                    public void onSuccess(int i, BaseObject baseObject) {
                        DistrictResultObject result = (DistrictResultObject) baseObject;
                        if (null != result.result) {
                            for (DistrictResultObject.DistrictResult item : result.result.get(0)) {
                                Log.d(TAG, "item:" + item.name + "," + item.id + "," + item.fullname);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {

                    }
                });
            }
        }).start();
    }

    private void initIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("latitude")) {
            latitude = intent.getDoubleExtra("latitude", 0);
        }
        if (intent.hasExtra("longitude")) {
            longitude = intent.getDoubleExtra("longitude", 0);
        }
    }

    private void initField() {
        context = this;
        currentRegional = (TextView) findViewById(R.id.current_reginal);
        searchContent = (AutoCompleteTextView) findViewById(R.id.search_content);
        btnClear = (ImageView) findViewById(R.id.btn_clear);
        cate = (Button) findViewById(R.id.cate);
        hotel = (Button) findViewById(R.id.hotel);
        market = (Button) findViewById(R.id.market);
        fitness = (Button) findViewById(R.id.fitness);
        useDistance = (CheckBox) findViewById(R.id.use_distance);
        km_1 = (RadioButton) findViewById(R.id.km_1);
        km_5 = (RadioButton) findViewById(R.id.km_5);
        km_10 = (RadioButton) findViewById(R.id.km_10);
        km_20 = (RadioButton) findViewById(R.id.km_20);
        resultList = (ListView) findViewById(R.id.result_list);
    }

    private void initEvent() {
        cate.setOnClickListener(this);
        hotel.setOnClickListener(this);
        market.setOnClickListener(this);
        fitness.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        searchContent.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_clear) {
            searchContent.setText("");
        } else {
            Button button = (Button) view;
            categoryClicked(button.getText().toString());
        }
    }

    private void categoryClicked(String category) {
        SearchParam param = null;
        if (!useDistance.isChecked()) {
            SearchParam.Region r = new SearchParam.Region().poi(region);
            param = new SearchParam().keyword(category).boundary(r);
        } else {
            Location location = new Location().lat((float) latitude).lng((float) longitude);
            SearchParam.Nearby nearby = new SearchParam.Nearby().point(location);
            int distance = 1000;
            if (km_1.isChecked()) {
                distance = 1000;
            } else if (km_5.isChecked()) {
                distance = 5000;
            } else if (km_10.isChecked()) {
                distance = 10000;
            } else if (km_20.isChecked()) {
                distance = 20000;
            }
            nearby.r(distance);
            param = new SearchParam().keyword(category).boundary(nearby);
        }
        param.orderby(true);
        TencentSearch tencentSearch = new TencentSearch(this);
        tencentSearch.search(param, new HttpResponseListener() {
            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                SearchResultObject obj = (SearchResultObject) baseObject;
                if (obj.data != null) {
                    showSearchResult(obj.data);
                }
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                Toast.makeText(context, "检索失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSearchResult(List<SearchResultObject.SearchResultData> data) {
        DemoApplication.searchResult = data;
        PlaceListAdapter adapter = new PlaceListAdapter(context, data);
        resultList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_result_in_map:
                showResultInMap();
                return true;
            case R.id.huangpu:
                changeRegional("黄浦区");
                return true;
            case R.id.xuhui:
                changeRegional("徐汇区");
                return true;
            case R.id.changning:
                changeRegional("长宁区");
                return true;
            case R.id.jingan:
                changeRegional("静安区");
                return true;
            case R.id.putuo:
                changeRegional("普陀区");
                return true;
            case R.id.hongkou:
                changeRegional("虹口区");
                return true;
            case R.id.yangpu:
                changeRegional("杨浦区");
                return true;
            case R.id.pudong:
                changeRegional("浦东新区");
                return true;
            case R.id.baoshan:
                changeRegional("宝山区");
                return true;
            case R.id.qingpu:
                changeRegional("青浦区");
                return true;
            case R.id.songjiang:
                changeRegional("松江区");
                return true;
            case R.id.minhang:
                changeRegional("闵行区");
                return true;
            case R.id.fengxian:
                changeRegional("奉贤区");
                return true;
            case R.id.jinshan:
                changeRegional("金山区");
                return true;
            case R.id.chongming:
                changeRegional("崇明区");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeRegional(String regional) {
        currentRegional.setText(String.format("当前行政区划：%s", regional));
    }

    private void showResultInMap() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        showSuggestion(charSequence.toString());
    }

    private void showSuggestion(String keyword) {
        TencentSearch tencentSearch = new TencentSearch(this);
        SuggestionParam param = new SuggestionParam().region(region).keyword(keyword);
        tencentSearch.suggestion(param, new HttpResponseListener() {
            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                final SuggestionResultObject obj = (SuggestionResultObject) baseObject;
                if (null != obj) {
                    SuggestionListAdapter adapter = new SuggestionListAdapter(context, obj.data);
                    searchContent.setAdapter(adapter);
                    searchContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SuggestionResultObject.SuggestionData item = obj.data.get(i);
                            StringBuilder sb = new StringBuilder();
                            sb.append(item.province).append("\n");
                            sb.append(item.city).append("\n");
                            sb.append(item.title).append("\n");
                            sb.append(item.address).append("\n");
                            Toast.makeText(context, sb, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }
        });
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
