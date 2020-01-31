package com.ampify.dictionarypopup.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ampify.dictionarypopup.Helper.DownloadHelper;
import com.ampify.dictionarypopup.Helper.ExtractWordnetHelper;
import com.ampify.dictionarypopup.MainActivity;
import com.ampify.dictionarypopup.R;
import com.ampify.dictionarypopup.databinding.FragmentDownloadsBinding;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadsFragment extends Fragment {

    public DownloadsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkWordNetExists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FragmentDownloadsBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_downloads, container, false);

        binding.downloadProgressBar.setProgress(0);

        binding.wordnetDownload.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        binding.wordnetDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wordnetDownload.setEnabled(false);
                binding.downloadText.setVisibility(View.VISIBLE);
                final File file = new File(getActivity().getExternalFilesDir(null), "WNdb-3.0.tar.gz");
                if (file.exists()) {
                    binding.downloadText.setText("extracting...");
                    new ExtractWordnetHelper(getContext(), new ExtractWordnetHelper.ExtractInterface(){
                        @Override
                        public void extractFinish() {
                            deleteAdditionalFiles();
                            Intent intentToMain = new Intent(getContext() , MainActivity.class);
                            getContext().startActivity(intentToMain);
                        }
                    }).execute();
                } else {
                    binding.downloadProgressBar.setVisibility(View.VISIBLE);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        int PERMISSION_REQUEST_CODE = 1;
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            } else {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_REQUEST_CODE);
                            }
                        }
                    }
                    new DownloadHelper(getActivity(), binding.downloadProgressBar).downloadWordNet();
                }
            }
        });

        return binding.getRoot();
    }

    public void checkWordNetExists() {
        File file = new File(getContext().getExternalFilesDir(null), "Wordnet/dict");
        if (file.exists()) {
            final NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.downloadsFragment, true)
                    .build();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_downloadsFragment_to_spinnerFragment, null, navOptions);
        }
    }

    public void deleteAdditionalFiles() {
        File dir = getContext().getExternalFilesDir(null);
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].equals("Wordnet")) {
                new File(dir, files[i]).delete();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

}
