package com.example.snackable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.snackable.CompareActivity.CompareActivity;
import com.example.snackable.ProductDetailActivity.ProductItemDetail;
import com.example.snackable.utils.LocalStorageManager;
import com.google.zxing.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ScanActivity extends AppCompatActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static Context context;
    private CodeScanner mCodeScanner;
    private ImageView cancelBtn;

    LocalStorageManager localStorageManager = new LocalStorageManager();
    String barcodeNum;
    Thread th;

    ProductItemModel m = new ProductItemModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        context = getApplicationContext();
        //checkCameraPermission();

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

    private void checkCameraPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "You cannot scan because you deny the camera permission.", Toast.LENGTH_LONG).show();
            }
        }
    }*/


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
                        Intent intent = new Intent (ScanActivity.this, ProductItemDetail.class);
                        if (m.getProductName()!=""){
                            m.setProductSavedTime(System.currentTimeMillis()); //add time stamp
                            intent.putExtra("Model", m);
                            localStorageManager.saveDataToHistory(context, m);
                            //saveDataToHistory(m);
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
}