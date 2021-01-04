package com.example.withserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.withserver.data.Item;
import com.example.withserver.databinding.ActivityItemBinding;
import com.example.withserver.db.LocalDatabase;

import java.util.ArrayList;

public class ItemActivity extends AppCompatActivity {
    private ActivityItemBinding binding ;
    private LocalDatabase db ;
    private Adapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        binding = ActivityItemBinding.inflate(getLayoutInflater()) ;
        setContentView(binding.getRoot()) ;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        db = LocalDatabase.getInstance(getApplicationContext()) ;
        Intent i = getIntent() ;

        Item item = (Item) i.getSerializableExtra("item");
        new Road().execute(item.title) ;

        if (item.mainimage != null)
            Glide.with(this).load(item.mainimage).into(binding.imageView);
        else {
            binding.imageView.setImageResource(R.drawable.ic_image_not_supported_24px);
        }

        if(item != null) {
            binding.title.setText(item.title);
            binding.description.setText(item.summary);
            binding.place.setText(item.addr);
        }

        String addrs = item.addr.replace(" ", "%20") ;

        binding.mapView.setOnClickListener(v->{
            binding.webView.loadUrl("http://map.naver.com/search2/search.nhn?query=" + addrs) ;
        });
        binding.good.setOnClickListener(v->{
            if (binding.good.getColorFilter() == null) {
                new DdInsert().execute(item) ;
                binding.good.setColorFilter(Color.parseColor("#4fc3f7")) ;
                Toast.makeText(this, "나의 관광지에 추가 되었습니다.", Toast.LENGTH_SHORT).show() ;
            }else {
                new Delete().execute(item) ;
                Toast.makeText(this, "나의 관광지에서 삭제 되었습니다.", Toast.LENGTH_SHORT).show() ;
            }
        });
    }

    class DdInsert extends AsyncTask<Item, Void, Void> {

        @Override
        protected Void doInBackground(Item... item) {
            db.dao().insertLocal(item[0]) ;
            return null;
        }
    }

    class Road extends AsyncTask<String, Void, Item> {

        @Override
        protected Item doInBackground(String... strings) {
            Item item = db.dao().get(strings[0]) ;
            return item ;
        }

        @Override
        protected void onPostExecute(Item item) {
            super.onPostExecute(item);

            if(item != null) {
                binding.good.setColorFilter(Color.parseColor("#4fc3f7"));
            }
        }
    }

    class Delete extends AsyncTask<Item, Void, ArrayList<Item>> {

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items);
            finish() ;
        }

        @Override
        protected ArrayList<Item> doInBackground(Item... items) {
            db.dao().delete(items[0]) ;
            return null ;
        }
    }
}
