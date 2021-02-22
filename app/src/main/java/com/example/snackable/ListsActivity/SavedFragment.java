package com.example.snackable.ListsActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.snackable.ListsActivity.YourListAdapter;
import com.example.snackable.ProductItemModel;
import com.example.snackable.R;
import com.example.snackable.SwipeHelper;
import com.example.snackable.utils.LocalStorageManager;

import java.util.ArrayList;
import java.util.List;


public class SavedFragment extends Fragment {
    ArrayList<ProductItemModel> savedList = new ArrayList<>();
    LocalStorageManager localStorageManager = new LocalStorageManager();
    RecyclerView recyclerView;
    YourListAdapter adapter;
    Context context;

    public SavedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = this.getContext();
        //load Saved
        savedList = localStorageManager.getSaved(context);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        recyclerView = view.findViewById(R.id.savedRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new YourListAdapter(context, savedList, false);
        recyclerView.setAdapter(adapter);

        //Swipe left buttons
        SwipeHelper swipeHelper = new SwipeHelper(context, recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "REMOVE",
                        R.drawable.ic_delete,
                        Color.parseColor("#F84949"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Are you sure you want to remove "+ savedList.get(pos).getProductName()+" from saved list?")
                                        .setPositiveButton(Html.fromHtml("<font color='#FF0000'>DELETE</font>"), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast.makeText(context, savedList.get(pos).getProductName()+ " Removed!", Toast.LENGTH_LONG).show();
                                                savedList.remove(pos);
                                                adapter.notifyItemRemoved(pos);
                                                localStorageManager.updateSaved(context, savedList);
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // User cancelled the dialog
                                            }
                                        });
                                builder.create();
                                builder.show();
                            }
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "COMPARE",
                        R.drawable.ic_delete,
                        Color.parseColor("#FDC605"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                //Toast toast = Toast.makeText(getContext(), savedList.get(pos).getProductName()+ "\n Add to Compare!", Toast.LENGTH_LONG);
                                Toast toast = Toast.makeText(getContext(), "Added to Compare", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                localStorageManager.addToCompare(context, savedList.get(pos));
                                adapter.notifyDataSetChanged();
                            }
                        }
                ));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }
}