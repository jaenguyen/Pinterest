package com.anhnhy.printerest.fragment.tab_home;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anhnhy.printerest.R;
import com.anhnhy.printerest.adapter.ExploreSearchImageAdapter;
import com.anhnhy.printerest.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExploreFragment extends Fragment implements ExploreSearchImageAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ExploreSearchImageAdapter adapter;
    private ProgressBar progressCircle;
    private FirebaseStorage fbStorage;
    private DatabaseReference dbRef;
    private DatabaseReference userRef;
    private ValueEventListener valueEventListener;
    private List<Image> images;
    private String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ExploreFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        progressCircle = view.findViewById(R.id.progress_circle);

        images = new ArrayList<>();
        adapter = new ExploreSearchImageAdapter(getContext(), images);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        fbStorage = FirebaseStorage.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference("images");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(UID);

        valueEventListener = dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                images.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Image image = postSnapshot.getValue(Image.class);
                    image.setKey(postSnapshot.getKey());
                    images.add(image);
                }
                adapter.notifyDataSetChanged();
                progressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(), "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWatchDetailClick(int position) {
        Toast.makeText(getContext(), "watch detail" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLikeImageClick(int position) {
        Image selectedItem = images.get(position);
        String imageKey = selectedItem.getKey();
        HashMap<String, String> likeIds = selectedItem.getLikeIds();
        boolean isExist = false;
        if (likeIds != null && likeIds.size() > 0) {
            if (likeIds.keySet().contains(UID)) {
                isExist = true;
                Toast.makeText(getContext(), "Bạn đã like ảnh rồi", Toast.LENGTH_SHORT).show();
            }
        }
        if (!isExist) {
            dbRef.child(imageKey).child("likeIds").child(UID).setValue(UID);
            userRef.child("likeIds").child(imageKey).setValue(imageKey);
            Toast.makeText(getContext(), "Like ảnh thành công", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDislikeImageClick(int position) {
        Image selectedItem = images.get(position);
        String imageKey = selectedItem.getKey();
        HashMap<String, String> likeIds = selectedItem.getLikeIds();
        boolean isExist = false;
        if (likeIds != null && likeIds.size() > 0) {
            if (likeIds.keySet().contains(UID)) {
                isExist = true;
                dbRef.child(imageKey).child("likeIds").child(UID).removeValue();
                userRef.child("likeIds").child(imageKey).removeValue();
                Toast.makeText(getContext(), "Bỏ like thành công", Toast.LENGTH_SHORT).show();
            }
        }
        if (!isExist) {
            Toast.makeText(getContext(), "Đã like ảnh đâu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDownload(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download");
        request.setDescription("Downloading...");

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()));
        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }
}