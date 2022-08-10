import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Main {


    public static void main(String[] args) {

        long time=System.currentTimeMillis();

        Calendar calendar=Calendar.getInstance();

        int dateEnd=calendar.get(Calendar.DAY_OF_MONTH);
        int monthEnd=calendar.get(Calendar.MONTH);
        int yearEnd=calendar.get(Calendar.YEAR);
        monthEnd++;

        IniFile file= new IniFile("res/parameters.ini");
        String location= file.getString("LOCATION","PARAMS");
        int timeScale=24;
        int quality;

        String filePath="market/"+location+"-"+dateEnd+"-"+monthEnd+"-"+yearEnd+".txt";
        FileWriter fileWriter;
        try {
            fileWriter= new FileWriter(filePath,false);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ItemReader itemReader=new ItemReader("res/items.txt");
        Item item;

        MarketAnalyzer analyzer= new MarketAnalyzer();

        int i=0;

        while ((item= itemReader.getNext())!=null){
            if(i%10==0){
                System.out.println(((float)i/8209.0)*100.0+"% complete");
            }

            if(item.getItemID().equals("wrongItem")){
                continue;
            }
            for (quality=1;quality<=5;quality++){
                Item requestItem=HTTPRequester.itemRequest(item.getItemID(),item.getEnglishItemName(),location,quality,timeScale);
                if (requestItem==null) break;
                if (requestItem.getItemID().equals("wait")){
                    System.out.println("wait");
                    try {
                        Thread.sleep(310000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("waitComplete");
                    quality--;
                    continue;
                }
                analyzer.put(requestItem);

            }
            i++;
        }

        for (Map.Entry<Float,Item>
                entry: analyzer.getItemTop().entrySet()){
            try {
                fileWriter.write("Rentability: "+entry.getKey()+";"+entry.getValue().toString()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        time=System.currentTimeMillis()-time;
        System.out.println("program done in "+time+" ms");
        try {
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
