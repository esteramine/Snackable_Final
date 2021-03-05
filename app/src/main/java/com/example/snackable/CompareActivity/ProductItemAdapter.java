package com.example.snackable.CompareActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snackable.FoodAdditiveDetailActivity.AdditivesDatabase;
import com.example.snackable.utils.ItemClickListener;
import com.example.snackable.ProductDetailActivity.ProductItemDetail;
import com.example.snackable.ProductItemModel;
import com.example.snackable.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemHolder> {
    private static final String NAME = "Name";
    private static final String LEVEL = "Level";
    private static final String CHECK_ITEMS[] = {"kcal", "Sugar", "Fat", "Protein", "Sodium", "Fibers"};
    private static final float DRI_AMOUNT[] = {1728, 25, 60, 46, 2.3f, 25};

    Context c;
    ArrayList<ProductItemModel> models;
    private boolean[] displayOpts = {true, true, false, false, false, false, false, false, true};
    String blinkedBarcode = "";


    ViewGroup parentViewGroup;

    public ProductItemAdapter(Context c, ArrayList<ProductItemModel> models, boolean[] displayOpts, String blinkedBarcode) {
        if (displayOpts == null){
            displayOpts = new boolean[]{true, true, false, false, false, false, false, false, true};
        }
        this.c = c;
        this.models = models;
        this.blinkedBarcode = blinkedBarcode;
        for (int i = 0; i < this.displayOpts.length; i++){
            this.displayOpts[i] = displayOpts[i];
        }
    }

    @NonNull
    @Override
    public ProductItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentViewGroup = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ProductItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemHolder holder, int position) {

        holder.itemRanking.setText(String.valueOf(models.get(position).getProductRanking())); //String.valueOf(models.get(position).getProductRanking())
        if (models.get(position).getProductRanking() == 1){
            holder.itemCrownImg.setVisibility(View.VISIBLE);
        }
        else{
            holder.itemCrownImg.setVisibility(View.GONE);
        }

        if (models.get(position).isBookmarked()){
            holder.itemBookmark.setVisibility(View.VISIBLE);
        }
        else{
            holder.itemBookmark.setVisibility(View.GONE);
        }
        //TODO product image
        /*if (models.get(position).getProductImg()!= null){
            try {
                URL imgURL = new URL(models.get(position).getProductImg());
                Bitmap imgBitmap = BitmapFactory.decodeStream(imgURL.openConnection().getInputStream());
                holder.itemImg.setImageBitmap(imgBitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        //byte[] bytes = models.get(position).getProductImg();
        /*Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.itemImg.setImageBitmap(bitmap);*/

        if (models.get(position).getProductImg().isEmpty()){
            holder.itemImg.setImageResource(R.drawable.not_found);
        }
        else {
            Picasso.get().load(models.get(position).getProductImg()).placeholder(R.drawable.loading)
                    .error(R.drawable.not_found).into(holder.itemImg);
        }



        holder.itemName.setText(models.get(position).getProductName());

        //set grid view additives
        if (displayOpts[0]){
            List<Map<String, String>> foodAdditivesList = new ArrayList<>();
            FAGridAdapter faGridAdapter;
            for(String foodAdditive: models.get(position).getFoodAdditivesList()){
                //holder.itemDes.append(foodAdditive+ " ");
                //TODO analyze food additives level
                int additive_index = AdditivesDatabase.getAdditiveIndex(foodAdditive);
                if (AdditivesDatabase.getAdditiveType(additive_index) == "Avoid"){
                    Map<String, String> FAInfo = new HashMap<>();
                    FAInfo.put(NAME, foodAdditive);
                    FAInfo.put(LEVEL, AdditivesDatabase.getAdditiveType(additive_index));
                    foodAdditivesList.add(FAInfo);
                }
            }
            faGridAdapter = new FAGridAdapter(c, foodAdditivesList);
            holder.FAGridView.setAdapter(faGridAdapter);
            holder.FAGridView.setExpanded(true);
        }

        // reset holder itemDes text
        holder.itemDes.setText("");
        // append texts
        ArrayList<String> nutrients = new ArrayList<>();
        for (int i = 2; i < displayOpts.length; i ++){
            String key  = "";
            float driAmount = 1f;
            if (displayOpts[i]){
                boolean overDri = false;
                if (i == 2) {//Calories
                    key = "Energy(kcal)";
                    driAmount = DRI_AMOUNT[0];
                }
                else if (i == 3) {
                    key = "Carbohydrates";
                    //driAmount = DRI_AMOUNT[0];
                }
                else if (i == 4) {
                    key = "Fibers";
                    driAmount = DRI_AMOUNT[5];
                }
                else if (i == 5) {
                    key = "Fat";
                    driAmount = DRI_AMOUNT[2];
                }
                else if (i == 6) {
                    key = "Proteins";
                    driAmount = DRI_AMOUNT[3];
                }
                else if (i == 7) {
                    key = "Sodium";
                    driAmount = DRI_AMOUNT[4];
                }
                else if (i == 8) {
                    key = "Sugars";
                    driAmount = DRI_AMOUNT[1];
                }

                if (models.get(position).getNutritionContents().containsKey(key)){
                    String consumed = models.get(position).getNutritionContents().get(key).replaceAll("[^\\d.]", "");
                    if ( !consumed.isEmpty() && driAmount!= 1f && Float.parseFloat(consumed)/driAmount > 1){
                        if (!holder.itemDes.getText().toString().equals("")){ //not first line of text
                            holder.itemDes.append("\n");
                        }
                        String nutrientAmount = key + " (" + models.get(position).getNutritionContents().get(key) + ") ";
                        String driPercentage = String.valueOf((int)(Float.parseFloat(consumed)*100/driAmount)) +"% DRI";
                        String combined = nutrientAmount + driPercentage;
                        SpannableString ss = new SpannableString(combined);
                        ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.parseColor("#F96E6E"));
                        ss.setSpan(fcsRed, 0, combined.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        holder.itemDes.append(ss);
                    }
                    else if (consumed.isEmpty()){
                        nutrients.add("No " + key +" Info!");
                    }
                    else{
                        nutrients.add(key + " (" + models.get(position).getNutritionContents().get(key) + ")");
                    }
                }
                else{
                    nutrients.add("No " + key +" Info!");
                }
            }
        }


        //display nutrients
        /*ArrayList<String> nutrients = new ArrayList<>();
        for (int i = 2; i < displayOpts.length; i ++){
            String key  = "";
            if (displayOpts[i]){
                if (i == 2) key = "Energy(kcal)"; //Calories
                else if (i == 3) key = "Carbohydrates";
                else if (i == 4) key = "Fibers";
                else if (i == 5) key = "Fat";
                else if (i == 6) key = "Proteins";
                else if (i == 7) key = "Sodium";
                else if (i == 8) key = "Sugars";

                if (models.get(position).getNutritionContents().containsKey(key)){
                    nutrients.add(key + " (" + models.get(position).getNutritionContents().get(key) + ")");
                }
                else{
                    nutrients.add("No " + key +" Info!");
                }
            }
        }*/
        String nutrientsOutput = "";
        if (!holder.itemDes.getText().toString().equals("")){ //not first line of text
            nutrientsOutput = "\n";
        }
        for (int i = 0; i < nutrients.size(); i++){
            nutrientsOutput = nutrientsOutput + nutrients.get(i);
            if(i!=nutrients.size()-1){
                nutrientsOutput += "\n";
            }
        }
        holder.itemDes.append(nutrientsOutput);
//        holder.itemDes.setText(Html.fromHtml(nutrientsOutput));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                showPopup(models.get(position));
            }
        });


        if (models.get(position).getProductBarcode().equals(blinkedBarcode)){

            Animation blinkAnim = AnimationUtils.loadAnimation(c, R.anim.item_blink);
            holder.cardView.setAnimation(blinkAnim);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    void showPopup(ProductItemModel model){
        Intent intent = new Intent(c, ProductItemDetail.class);
        intent.putExtra("Model", model);
        intent.putExtra("Compare", true);
        c.startActivity(intent);
    }
}
