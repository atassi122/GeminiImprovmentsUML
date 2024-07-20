package com.example.a01_app;

import android.content.Context;
import android.os.Bundle;
import com.google.gson.Gson;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import com.google.gson.reflect.TypeToken;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class HistoryFragment extends Fragment implements HistoryAdapter.OnItemClickListener {

    private static final String HISTORY_FILE = "history.json"; // File name for storing history data
    private RecyclerView historyRecyclerView;
    private HistoryAdapter adapter;
    private SearchView searchBar;
    private List<HistoryItem> allHistoryItems; // Store all history items for filtering


    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyRecyclerView = view.findViewById(R.id.history_recycler_view);
        searchBar = view.findViewById(R.id.search_bar);
        searchBar.setFocusable(false);


        // Set the LayoutManager
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load history data from internal storage
        List<HistoryItem> historyItems = loadHistoryItems();


        // Initialize adapter with loaded data
        adapter = new HistoryAdapter(historyItems, this);
        historyRecyclerView.setAdapter(adapter);
        allHistoryItems = adapter.getHistoryItems();


        // Attach ItemTouchHelper to RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(historyRecyclerView);


        // Setup search functionality
        setupSearchView();


        return view;
    }


    private void setupSearchView() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String query) {
        List<HistoryItem> filteredList = new ArrayList<>();

        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(allHistoryItems);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();

            for (HistoryItem item : allHistoryItems) {
                if (item.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(item);
                }
            }
        }

        adapter.updateHistoryItems(filteredList);
    }



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set the title for the fragment
        if (getActivity() != null) {
            getActivity().setTitle("History");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save history data to internal storage on fragment pause
        saveHistoryItems(adapter.getHistoryItems());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshHistoryItems();
    }

    private void refreshHistoryItems() {
        List<HistoryItem> historyItems = loadHistoryItems();
        adapter.updateHistoryItems(historyItems);
    }

    private List<HistoryItem> loadHistoryItems() {
        List<HistoryItem> items = new ArrayList<>();
        try {
            FileInputStream fis = requireActivity().getApplicationContext().openFileInput(HISTORY_FILE);
            StringBuilder stringBuilder = new StringBuilder();
            Scanner scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            fis.close();
            items = new Gson().fromJson(stringBuilder.toString(), new TypeToken<List<HistoryItem>>() {}.getType());
        } catch (FileNotFoundException e) {
            // Handle file not found (empty list)
            System.out.println("History file not found.");
        } catch (Exception e) {
            e.printStackTrace(); // Handle other errors gracefully
        }
        return items;
    }

    private void saveHistoryItems(List<HistoryItem> items) {
        String serializedData = new Gson().toJson(items);
        try {
            FileOutputStream fos = requireActivity().getApplicationContext().openFileOutput(HISTORY_FILE, Context.MODE_PRIVATE);
            fos.write(serializedData.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace(); // Handle file write errors gracefully
        }
    }

    @Override
    public void onItemClick(HistoryItem item) {
        // Handle item click, e.g., show detailed view of the item
    }

    // ItemTouchHelper.SimpleCallback for swipe-to-delete
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false; // We are not interested in move events
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Since we only set ItemTouchHelper.RIGHT, we don't need to check direction here
            // Get the swiped item position
            int position = viewHolder.getAdapterPosition();
            // Remove the item from the list
            List<HistoryItem> currentItems = new ArrayList<>(adapter.getHistoryItems());
            currentItems.remove(position);

            adapter.updateHistoryItems(currentItems);
            allHistoryItems = currentItems;
            // Save the updated list to internal storage
            saveHistoryItems(currentItems);
        }
    };


}
