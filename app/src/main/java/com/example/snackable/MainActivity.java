package com.example.snackable;
//package com.wwb.test.python;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

//jsoup
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.snackable.CompareActivity.CompareActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {
    private final static String SHARED_PREFS = "SHARED_PREFS";
    private final static String HISTORY = "HISTORY";
    Button test;
    TextView testText;
    Button testBtn;
    EditText editText;
    Thread th;
    String barcodeNum = "5000159461122";

    String productName;
    Integer productWeight = 100;
    String productImg = "";
    ArrayList<String> ingredientsList = new ArrayList<>();
    ArrayList<String> foodAdditivesList = new ArrayList<>();
    HashMap<String, String> nutritionContents = new HashMap();

    ProductItemModel m = new ProductItemModel();


    public void showDonutOrder(View view) {
        Toast.makeText(getApplicationContext(), "hola",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hooks();
        //initPython();
        //callPythonCode();

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeNum = editText.getText().toString();
                th=new Thread(r0);                //執行緒
                th.start();                    //讓執行緒開始工作;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Product Scanned: "+ m.getProductName())
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                            }
                        })
                        .setNegativeButton("Scan Again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create();
                builder.show();
            }
        });

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MainActivity.this, CompareActivity.class);
                if (m.getProductName()!=""){
                    intent.putExtra("NewModel", m);
                }
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void hooks(){
        test = findViewById(R.id.test);
        testText = findViewById(R.id.testText);
        testBtn = findViewById(R.id.testBtn);
        editText = findViewById(R.id.input);
    }

    public void saveData(ProductItemModel model){
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

    private Runnable r0=new Runnable(){
        public void run(){
            try {
                //Kinder: 80310167
                //Snickers: 5000159461122
                //Gummy Bears:0042238848283
                //oreo: 7622300165628

                URL url = new URL("https://world.openfoodfacts.org/product/"+barcodeNum);
                Document doc =  Jsoup.parse(url, 3000);

                //get all the info needed
                if (doc.title().contains("Search Results")){
                    testText.setText("No Product Found!");
                }
                else{
                    productName = doc.title(); //product name

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
                        productWeight = Integer.valueOf(foundDOM.text().replaceAll("[^\\d.]", ""));
                    }

                    //product image
                    Element img = doc.selectFirst("img#og_image");
                    if (img != null){
                        String imgSrc = img.absUrl("src");
                        //URL imgUrl = new URL(imgSrc);
                        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        //Bitmap bitmap = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                        //InputStream input = new java.net.URL(imgSrc).openStream(); //Download image from url
                        //productImg = BitmapFactory.decodeStream(input); // Decode Bitmap
                        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        //productImg = stream.toByteArray();
                        productImg = imgSrc;
                    }

                    //ingredients list
                    Element list = doc.selectFirst("div#ingredients_list");
                    if (list != null){
                        String preprocessedList = list.text().replaceAll("[`~☆★#$%^&*()+=|{}':;,\\\\[\\\\]》·.<>/?~！@#￥%……（）——+|{}【】‘；：”“’。，、？]", "");
                        String[] tokens = preprocessedList.split(",|(|)|:"); //TODO: check other product pages
                        for (String token: tokens){
                            ingredientsList.add(token.replace(" ", ""));
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
                            for (Element additive: additives){
                                String wholeText = additive.selectFirst("a").text();
                                String trimText = wholeText.substring(wholeText.indexOf("-")+1).trim();
                                foodAdditivesList.add(trimText);
                            }
                        }
                    }

                    //product nutrition content
                    Element nutritionTable = doc.getElementById("nutrition_data_table").selectFirst("tbody");
                    Elements nutritionInfo = nutritionTable.getElementsByTag("tr");
                    for (Element info: nutritionInfo){
                        System.out.println(info.select("td.nutriment_label").text().replaceAll("- ","").replaceAll(" ", ""));
                        nutritionContents.put(info.select("td.nutriment_label").text().replaceAll("- ","").replaceAll(" ", ""), info.selectFirst("td.nutriment_value").text());
                    }

                    //set up the model
                    m.setProductBarcode(barcodeNum);
                    m.setProductName(productName);
                    m.setProductWeight(productWeight);
                    m.setProductImg(productImg);
                    m.setFoodAdditivesList(foodAdditivesList);
                    m.setNutritionContents(nutritionContents);
                    saveData(m);
                }

                    runOnUiThread(new Runnable() {             //將內容交給UI執行緒做顯示
                        public void run(){
                            testText.append(productName +"\n");
                            testText.append(productWeight+"\n");
                            // Get a set of the entries
                            for (String entry: nutritionContents.keySet()){
                                String key = entry;
                                String value = nutritionContents.get(entry);
                                testText.append(key + " " + value+"\n");
                            }
                            for(int i = 0; i < foodAdditivesList.size(); i++){
                                testText.append(foodAdditivesList.get(i)+"\n");
                            }
                        }
                    });
                    Thread.sleep(100);    //避免執行緒跑太快而UI執行續顯示太慢,覆蓋掉te01~03內容所以設個延遲,也可以使用AsyncTask-異步任務
                } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            } catch (IOException ioException) {
                testText.setText("No Product Found!");
                ioException.printStackTrace();
            }
        }
    };

    /*void getInfo() {
        try {
            String url = "https://world.openfoodfacts.org/product/80310167/kinder-chocolate";
            Document document = Jsoup.connect(url).get();
            String title = document.title();
            Log.d(TAG,title);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    void initPython(){ //initialize python environment
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    void callPythonCode(){
        Python py = Python.getInstance();
        PyObject obj1 = py.getModule("openFoodFacts").callAttr("getFoodInfo");
        String str = obj1.toJava(String.class);
        Log.d(TAG,str);
    }*/

}