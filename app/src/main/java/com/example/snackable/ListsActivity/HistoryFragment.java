package com.example.snackable.ListsActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HistoryFragment extends Fragment {
    ArrayList<ProductItemModel> historyList = new ArrayList<>();
    LocalStorageManager localStorageManager;
    RecyclerView recyclerView;
    YourListAdapter adapter;
    Context context;
    
    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        context = this.getContext();
        localStorageManager = new LocalStorageManager(context);
        // load History
        historyList = localStorageManager.getHistory();

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.historyRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new YourListAdapter(context, historyList, true);
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
                                AlertDialog.Builder builder = alertDialogManager.removeItemAlertDialogBuilder(context, historyList.get(pos).getProductName(), "History");
                                builder.setPositiveButton(Html.fromHtml("<font color='#FF0000'>REMOVE</font>"), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new ToastManager().removedFromListToast(context, "History");
                                        historyList.remove(pos);
                                        adapter.notifyItemRemoved(pos);
                                        localStorageManager.updateHistory(historyList);
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
                                localStorageManager.addToCompare(historyList.get(pos));
                                //adapter.notifyDataSetChanged();
                                adapter.notifyItemChanged(pos);
                            }
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "SAVE",
                        R.drawable.ic_delete,
                        Color.parseColor("#00BF7C"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                new ToastManager().savedToListToast(context, "Saved");
                                historyList.get(pos).setBookmarked(true);
                                localStorageManager.saveDataToSaved(historyList.get(pos));
                                adapter.notifyItemChanged(pos);
                                /*OnSavedButtonClickListener listener = (OnSavedButtonClickListener) getActivity();
                                listener.onSavedButtonClicked();*/
                            }
                        }
                ));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT:
                    historyList.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;

                case ItemTouchHelper.RIGHT:
                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(context, R.color.chartColorWarning))
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public interface OnSavedButtonClickListener{
        void onSavedButtonClicked();
    }

}