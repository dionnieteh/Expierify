package com.example.expierify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class TipsPage extends AppCompatActivity {

        ViewPager2 viewPager2;
        ArrayList<ViewPagerItem> viewPagerItemArrayList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tips_page);

            ImageButton backBtn= (ImageButton)findViewById(R.id.backBtn);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }


            });

            viewPager2 = findViewById(R.id.viewpager);
            int[] images = {R.drawable.tips1_5, R.drawable.tips8,R.drawable.tips9, R.drawable.tips10 };

            viewPagerItemArrayList = new ArrayList<>();

            for (int i =0; i< images.length ; i++){
                ViewPagerItem viewPagerItem = new ViewPagerItem(images[i]);
                viewPagerItemArrayList.add(viewPagerItem);
            }

            VPAdapter vpAdapter = new VPAdapter(viewPagerItemArrayList);

            viewPager2.setAdapter(vpAdapter);

            viewPager2.setClipToPadding(false);

            viewPager2.setClipChildren(false);

            viewPager2.setOffscreenPageLimit(2);

            viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

            Button nextBtn = (Button) findViewById(R.id.nextBtn);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewPager2.getCurrentItem() < viewPagerItemArrayList.size() - 1) {
                        viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
                    }
                }
            });

            Button prevBtn = findViewById(R.id.prevBtn);
            prevBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewPager2.getCurrentItem() > 0) {
                        viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
                    }
                }
            });
        }

}