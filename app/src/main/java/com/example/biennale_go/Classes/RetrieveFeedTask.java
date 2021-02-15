package com.example.biennale_go.Classes;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.example.biennale_go.AsyncResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RetrieveFeedTask extends AsyncTask<String,Void, Bitmap> {
    public AsyncResponse delegate = null;


    public RetrieveFeedTask(AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        delegate.processFinish(bitmap);
        super.onPostExecute(bitmap);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        URL url = null;
        try { url = new URL(params[0]); }
        catch (MalformedURLException e) { e.printStackTrace(); }
        Bitmap result = null;
        try { result = BitmapFactory.decodeStream(url.openConnection().getInputStream()); }
        catch (IOException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
