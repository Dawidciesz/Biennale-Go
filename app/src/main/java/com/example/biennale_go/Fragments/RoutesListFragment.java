package com.example.biennale_go.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.biennale_go.Adapters.MenuListAdapter;
import com.example.biennale_go.Adapters.RoadListAdapter;
import com.example.biennale_go.AsyncResponse;
import com.example.biennale_go.Classes.RetrieveFeedTask;
import com.example.biennale_go.R;
import com.example.biennale_go.Utility.MenuListItem;
import com.example.biennale_go.Utility.RoadListItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RoutesListFragment extends Fragment implements RoadListAdapter.OnRoadItemClick {
    private LinearLayout routesListPanel;
    private Button newButton;
    private View view;
    private ImageView galleryLogo;
    private List<RoadListItem> items = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.activity_routes_list, container, false);

//        galleryLogo = (ImageView) view.findViewById(R.id.galleryLogo);
//        galleryLogo.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.loading_scale));
        recyclerView = (RecyclerView) view.findViewById(R.id.road_recycler);
        layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        fetchRoutes();





        return view;
    }

    public ArrayList polylineSerialize(ArrayList polyline) {
        ArrayList newPolyline = new ArrayList();
        for(Integer i = 0; i<polyline.size(); i++) {
            GeoPoint geoPoint = (GeoPoint) polyline.get(i);
            ArrayList latLong = new ArrayList();
            latLong.add(geoPoint.getLatitude());
            latLong.add(geoPoint.getLongitude());
            newPolyline.add(latLong);
        }
        return newPolyline;
    }

    public void fetchRoutes() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("routes");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        final ArrayList streets = (ArrayList) document.getData().get("streets");
                        final String name = (String) document.getData().get("name");
                        final String description = (String) document.getData().get("description");
                        final ArrayList polyline = polylineSerialize((ArrayList) document.getData().get("polyline"));
                        final String color = (String) document.getData().get("color");
                        final String image = (String) document.getData().get("image");

//                        RetrieveFeedTask retrieve = new RetrieveFeedTask();
//                        retrieve.execute(image);
//
//                        Bitmap bmp = null;
//                        try {
//                            bmp = retrieve.get();
//
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        items.add(new RoadListItem(name, description, polyline, color));
                        RetrieveFeedTask asyncTask =new RetrieveFeedTask(new AsyncResponse() {

                            @Override
                            public void processFinish(Bitmap output) {
//                            Log.d("Response From Asynchronous task:", (String) output);
//                            Bitmap bmp = null;
//                            bmp = (Bitmap) output;
                            items.get(i).setImage(output);
                            i++;
                            adapter.notifyDataSetChanged();
                            }
                        });
                        asyncTask.execute(image);


                    }

                    }
                adapter = new RoadListAdapter(items, RoutesListFragment.this);
                recyclerView.setAdapter(adapter);

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        i=0;
    }

    @Override
    public void onRoadItemClick(int position) {

    }
}
