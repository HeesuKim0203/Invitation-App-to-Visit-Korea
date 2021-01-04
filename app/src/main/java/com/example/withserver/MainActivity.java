package com.example.withserver;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.withserver.data.Data;
import com.example.withserver.data.Item;
import com.example.withserver.databinding.ActivityMainBinding;
import com.example.withserver.db.LocalDatabase;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements DataAdapter.ItemClick {
    private LocalDatabase db ;
    private DataAdapter adapter ;
    private ActivityMainBinding binding ;
    private int totalData = 0 ;
    private int pageNo = 0 ;
    private int areaNumber = 0 ;
    private int status = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()) ;
        setContentView(binding.getRoot()) ;

        db = LocalDatabase.getInstance(getApplicationContext()) ;

        adapter = new DataAdapter(this) ;
        LinearLayoutManager manager = new LinearLayoutManager(this) ;
        binding.recyclerView.setLayoutManager(manager) ;
        binding.recyclerView.setAdapter(adapter) ;

        binding.meun.setOnClickListener(v-> menu(v)) ;
        binding.go.setOnClickListener(v-> go());
        binding.back.setOnClickListener(v-> back());
        binding.mode.setOnClickListener(v-> sortMenu(v));

        binding.pageNo.setVisibility(View.GONE);
        binding.go.setVisibility(View.GONE) ;
        binding.back.setVisibility(View.GONE) ;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(status == 1) {
            new Imdata().execute() ;
        }
    }

    public void sortMenu(View v) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), v) ;

        getMenuInflater().inflate(R.menu.sort, popup.getMenu()) ;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.noSelect :
                        status = 0 ;
                        break ;
                    case R.id.viewsSort :
                        adapter.sortData();
                        status = 0 ;
                        break ;
                    case R.id.im :
                        new Imdata().execute() ;
                        status = 1 ;
                        binding.areaText.setText(item.getTitle().toString());
                        break ;
                    default:
                        break ;
                }
                binding.mode.setText(item.getTitle().toString()) ;
                return false;
            }
        });
        popup.show() ;
    }

    public void menu(View v) {
        PopupMenu popup = new PopupMenu(getApplicationContext(), v) ;

        getMenuInflater().inflate(R.menu.option_menu_xml, popup.getMenu()) ;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.area1 :
                        areaNumber = 1 ;
                        break ;
                    case R.id.area2 :
                        areaNumber = 2 ;
                        break ;
                    case R.id.area3 :
                        areaNumber = 3 ;
                        break ;
                    case R.id.area4 :
                        areaNumber = 4 ;
                        break ;
                    case R.id.area5 :
                        areaNumber = 5 ;
                        break ;
                    case R.id.area6 :
                        areaNumber = 6 ;
                        break ;
                    case R.id.area7 :
                        areaNumber = 7 ;
                        break ;
                    case R.id.area8 :
                        areaNumber = 8 ;
                        break ;
                    case R.id.area31 :
                        areaNumber = 31 ;
                        break ;
                    case R.id.area32 :
                        areaNumber = 32 ;
                        break ;
                    case R.id.area33 :
                        areaNumber = 33 ;
                        break ;
                    case R.id.area34 :
                        areaNumber = 34 ;
                        break ;
                    case R.id.area35 :
                        areaNumber = 35 ;
                        break ;
                    case R.id.area36 :
                        areaNumber = 36 ;
                        break ;
                    case R.id.area37 :
                        areaNumber = 37 ;
                        break ;
                    case R.id.area38 :
                        areaNumber = 38 ;
                        break ;
                    case R.id.area39 :
                        areaNumber = 39 ;
                        break ;
                    default:
                        break ;
                }

                if(status == 1)
                    status = 0 ;
                pageNo = 1 ;
                binding.areaText.setText(item.getTitle().toString());
                binding.mode.setText("기준없음");
                new GetData().execute(Integer.toString(pageNo), Integer.toString(areaNumber));
                return false;
            }
        });

        popup.show();
    }

    public void go() {
        if(pageNo > 1) {
            pageNo-- ;
            binding.mode.setText("기준없음");
            new GetData().execute(Integer.toString(pageNo), Integer.toString(areaNumber)) ;
        }
    }

    public void back() {
        if(pageNo * 10 > totalData || (pageNo == 1 && areaNumber == 6) || (pageNo == 3 && areaNumber == 35)) {
            Toast.makeText(this, "더 이상 데이터가 없습니다.", Toast.LENGTH_SHORT).show() ;
            return ;
        }
        binding.mode.setText("기준없음");
        pageNo++ ;
        new GetData().execute(Integer.toString(pageNo), Integer.toString(areaNumber)) ;
    }

    @Override
    public void itemClickListener(Item item) {
        Intent intent = new Intent(this, ItemActivity.class) ;
        intent.putExtra("addr", item.addr) ;
        intent.putExtra("mainimage", item.mainimage) ;
        intent.putExtra("summary", item.summary) ;
        intent.putExtra("title", item.title) ;
        intent.putExtra("telname", item.telname) ;
        intent.putExtra("readcount", item.readcount) ;
        intent.putExtra("item", item) ;

        startActivity(intent) ;
    }


    class GetData extends AsyncTask<String, Void, Data> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Data doInBackground(String... strings) {
            StringBuilder urlBuilder = new StringBuilder("http://api.visitkorea.or.kr/openapi/service/rest/GreenTourService/areaBasedList");
            String result ;
            Data data = null ;
            try {
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=QmDltwvuQUfdht31c9vaia00j0%2B96qH3nU9fAR%2FBODZUtYD61R7BQTUSg1im1fyRqYtEZgYoldo79WdYD%2FpOOw%3D%3D");
                urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(strings[0], "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "=" + URLEncoder.encode("AND", "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "=" + URLEncoder.encode("withServer", "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("areaCode", "UTF-8") + "=" + URLEncoder.encode(strings[1], "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
                URL url = new URL(urlBuilder.toString()) ;
                HttpURLConnection conn = (HttpURLConnection)url.openConnection() ;
                conn.setRequestMethod("GET") ;
                InputStream is = conn.getInputStream() ;
                StringBuilder builder = new StringBuilder() ;
                BufferedReader dr = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line ;
                while ((line = dr.readLine()) != null) {
                    builder.append(line).append("\n") ;
                }
                result = builder.toString() ;
                Gson gson = new Gson() ;
                data = gson.fromJson(result, Data.class) ;
            }catch (IOException e ) {
                e.printStackTrace() ;
            }catch (Exception exception) { // 데이터 없음 예외
                data = null ;
            }

            return data ;
        }

        @Override
        protected void onPostExecute(Data data) {
            super.onPostExecute(data) ;
            if(binding.mainImg.getVisibility() == View.VISIBLE) {
                binding.mainImg.setVisibility(View.GONE) ;
                binding.mainTitle.setVisibility(View.GONE) ;
            }
            if(data != null) {
                ArrayList<Item> items = data.response.body.items.item ;
                adapter.setData(items) ;
                // 데이터 없음 예외 처리
                if(binding.nonData.getVisibility() == View.VISIBLE || binding.pageNo.getVisibility() == View.GONE
                        || binding.pageNo.getVisibility() == View.INVISIBLE) {
                    binding.nonData.setVisibility(View.GONE) ;
                    binding.pageNo.setVisibility(View.VISIBLE);
                    binding.go.setVisibility(View.VISIBLE) ;
                    binding.back.setVisibility(View.VISIBLE) ;
                }
                // 페이지 이동 데이터 맴버로 받기
                totalData = Integer.parseInt(data.response.body.totalCount) ;
                pageNo = Integer.parseInt(data.response.body.pageNo) ;
                binding.pageNo.setText(Integer.toString(pageNo)) ;
            }else {
                adapter.setData(null) ;
                // 데이터 없음 예외 처리
                if (binding.nonData.getVisibility() == View.GONE) {
                    binding.nonData.setVisibility(View.VISIBLE);
                    binding.pageNo.setVisibility(View.GONE);
                    binding.go.setVisibility(View.GONE) ;
                    binding.back.setVisibility(View.GONE) ;
                }
            }
        }
    }

    class Imdata extends AsyncTask<Void, Void, ArrayList<Item>> {

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items) ;
            adapter.setData(items) ;
            binding.pageNo.setVisibility(View.INVISIBLE);
            binding.go.setVisibility(View.INVISIBLE) ;
            binding.back.setVisibility(View.INVISIBLE) ;
            if(items.size() != 0) {
                binding.nonData.setVisibility(View.GONE) ;
            }else {
                binding.nonData.setVisibility(View.VISIBLE) ;
            }
            if(binding.mainImg.getVisibility() == View.VISIBLE) {
                binding.mainImg.setVisibility(View.GONE) ;
                binding.mainTitle.setVisibility(View.GONE) ;
            }
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... voids) {
            return (ArrayList<Item>) db.dao().getData() ;
        }
    }
}
