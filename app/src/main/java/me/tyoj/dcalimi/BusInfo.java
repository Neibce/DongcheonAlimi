package me.tyoj.dcalimi;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;

public class BusInfo {
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final View mView;
    private static Boolean isRunning = false;

    BusInfo(FragmentManager fragmentManager, Context context, View view){
        mFragmentManager = fragmentManager;
        mContext = context;
        mView = view;
    }

    public void get(){
        if(!isRunning) {
            isRunning = true;

            ImageButton btnBusInfoRefresh = mView.findViewById(R.id.btnBusInfoRefresh);

            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);

            btnBusInfoRefresh.startAnimation(animation);

            Runnable runnable = new BusInfoDownloadRunnable(mFragmentManager, mView);
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    private static class BusInfoDownloadRunnable implements Runnable {
        private final Handler mHandler;

        BusInfoDownloadRunnable(FragmentManager fragmentManager, View view){
            mHandler = new MyHandler(fragmentManager, view);
        }

        @Override
        public void run() {
            try {
                Document document = Jsoup.connect("http://bus.busan.go.kr/busanBIMS/Ajax/map_Arrival.asp?optARSNO=21070550008").parser(Parser.xmlParser()).timeout(10000).get();
                Elements elements = document.select("Buss").select("bus");

                String strResult = elements.get(0).attr("value5");
                if(strResult.equals(""))
                    strResult = "--";
                strResult += "분";

                Message msg = mHandler.obtainMessage();
                msg.what = MyHandler.UPDATE_BUS_INFO;
                msg.obj = strResult;

                Thread.sleep(1000);

                mHandler.sendMessage(msg);
                isRunning = false;
            } catch (IOException | InterruptedException e) {
                mHandler.sendEmptyMessage(MyHandler.ERROR_TO_UPDATE_BUS_INFO);
                e.printStackTrace();
                isRunning = false;
            }
        }
    }
}
