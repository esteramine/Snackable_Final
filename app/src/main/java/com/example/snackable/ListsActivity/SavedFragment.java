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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.snackable.ProductItemModel;
import com.example.snackable.R;
import com.example.snackable.utils.AlertDialogManager;
import com.example.snackable.utils.SwipeHelper;
import com.example.snackable.utils.LocalStorageManager;
import com.example.snackable.utils.ToastManager;

import java.util.ArrayList;
import java.util.List;


public class SavedFragment extends Fragment {
    ArrayList<ProductItemModel> savedList = new ArrayList<>();
    LocalStorageManager localStorageManager;
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
        localStorageManager= new LocalStorageManager(context);
        //load Saved
        savedList = localStorageManager.getSaved();
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
                                AlertDialogManager alertDialogManager = new AlertDialogManager();
                                AlertDialog.Builder builder = alertDialogManager.removeItemAlertDialogBuilder(context, savedList.get(pos).getProductName(), "Saved");

                                builder.setPositiveButton(Html.fromHtml("<font color='#FF0000'>REMOVE</font>"), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id){
                                        new ToastManager().removedFromListToast(context, "Saved");
                                        //update compare
                                        localStorageManager.updateBookmarkedItemInCompare(savedList.get(pos), false);
                                        //update savedList
                                        savedList.remove(pos);
                                        adapter.notifyItemRemoved(pos);
                                        //update local storage saved
                                        localStorageManager.updateSaved(savedList);
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
                                new ToastManager().savedToListToast(context, "Compare");
                                localStorageManager.addToCompare(savedList.get(pos));
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