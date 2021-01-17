package com.example.snackable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanActivity extends AppCompatActivity {
    private final static String SHARED_PREFS = "SHARED_PREFS";
    private final static String HISTORY = "HISTORY";

    private CodeScanner mCodeScanner;
    private ImageView cancelBtn;
    String barcodeNum;
    Thread th;

    ProductItemModel m = new ProductItemModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CompareActivity.class));
            }
        });

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScanActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                        barcodeNum = result.getText();
                        th=new Thread(r0); //執行緒
                        th.start();
                        Toast.makeText(ScanActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }


    private Runnable r0=new Runnable(){
        public void run(){
            try {
                //Kinder: 80310167
                //Snickers: 5000159461122
                //Gummy Bears:0042238848283
                //oreo: 7622300165628

                URL url = new URL("https://world.openfoodfacts.org/product/"+barcodeNum);
                m.setProductBarcode(barcodeNum);
                Document doc =  Jsoup.parse(url, 3000);

                //get all the info needed
                m.setProductName(doc.title()); //product name

                //productWeight
                Elements eles = doc.select("p>span.field");
                Element foundDOM = null;
                for (Element e: eles){
                    if (e.text().contains("Quantity:")){
                        foundDOM = e.parent();
                        break;
                    }
                }
                if(foundDOM!=null){
                    m.setProductWeight(Integer.valueOf(foundDOM.text().replaceAll("[^\\d.]", "")));
                }

                //product image
                Element img = doc.selectFirst("img#og_image");
                if (img != null){
                    String imgSrc = img.absUrl("src");
                    m.setProductImg(imgSrc);
                }

                //ingredients list
                Element list = doc.selectFirst("div#ingredients_list");
                if (list != null){
                    String preprocessedList = list.text().replaceAll("[`~☆★#$%^&*()+=|{}':;,\\\\[\\\\]》·.<>/?~！@#￥%……（）——+|{}【】‘；：”“’。，、？]", "");
                    String[] tokens = preprocessedList.split(",|(|)|:"); //TODO: check other product pages
                    m.clearIngredientsList();
                    for (String token: tokens){
                        m.addIngredientToList(token.replace(" ", ""));
                    }
                }

                //food additives list
                Elements elements = doc.select("div>b");
                Element requiredDOM = null;
                for (Element e: elements){
                    if (e.text().contains("Additives:")){
                        requiredDOM = e.parent();
                        break;
                    }
                }
                if (requiredDOM!=null){
                    Elements additives = requiredDOM.select("li");
                    if (additives!=null){
                        m.clearFoodAdditiveList();
                        for (Element additive: additives){
                            String wholeText = additive.selectFirst("a").text();
                            String trimText = wholeText.substring(wholeText.indexOf("-")+1).trim();
                            m.addFoodAdditiveToList(trimText);
                        }
                    }
                }

                //product nutrition content
                Element nutritionTable = doc.getElementById("nutrition_data_table").selectFirst("tbody");
                Elements nutritionInfo = nutritionTable.getElementsByTag("tr");
                m.clearNutritionContents();
                for (Element info: nutritionInfo){

                    m.addNutritionContentToList(info.select("td.nutriment_label").text().replaceAll("- ","").replaceAll(" ", ""), info.selectFirst("td.nutriment_value").text().replaceAll("[^\\u0000-\\uFFFF]",""));
                }

                //set alert dialog
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                        builder.setMessage("Product Scanned:\n"+ m.getProductName() + "\n(" + barcodeNum + ")")
                                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent (ScanActivity.this, CompareActivity.class);
                                        if (m.getProductName()!=""){
                                            m.setProductSavedTime(System.currentTimeMillis()); //add time stamp
                                            intent.putExtra("NewModel", m);
                                            saveDataToHistory(m);
                                        }
                                        ScanActivity.this.startActivity(intent);
                                    }
                                })
                                .setNegativeButton(Html.fromHtml("<font color='#FF0000'>Scan Again</font>"), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                        builder.create();
                        builder.show();*/
                        Intent intent = new Intent (ScanActivity.this, ProductItemDetail.class);
                        if (m.getProductName()!=""){
                            m.setProductSavedTime(System.currentTimeMillis()); //add time stamp
                            intent.putExtra("ModelScanned", m);
                            saveDataToHistory(m);
                        }
                        ScanActivity.this.startActivity(intent);
                    }
                });

                //Thread.sleep(100);    //避免執行緒跑太快而UI執行續顯示太慢,覆蓋掉te01~03內容所以設個延遲,也可以使用AsyncTask-異步任務
            }  catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            } catch (IOException ioException) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                        builder.setMessage("Product Barcode: " + barcodeNum + "\n\n"+"No info of this product is found in the database!")
                                .setNegativeButton("Scan Another Product", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                        builder.create();
                        builder.show();
                    }
                });

                ioException.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    public void saveDataToHistory(ProductItemModel model){
        ArrayList<ProductItemModel> historyList = new ArrayList<>();
        // load history from shared prefs
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductItemModel>>(){}.getType();
        String json = sharedPreferences.getString(HISTORY, "");
        if (json!=""){
            historyList = gson.fromJson(json, type);
        }

        //save new product item model to history list
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        historyList.add(0, model);
        Gson gsonSave = new Gson();
        String jsonSave = gsonSave.toJson(historyList);
        editor.putString(HISTORY, jsonSave);
        editor.commit();

    }
}