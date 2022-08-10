import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

public class HTTPRequester {

    static int numberOfRetry=0;

    static Item itemRequest(String itemID, String englishItemName,String location, int quality, int timeScale){

        Calendar calendar=Calendar.getInstance();

        int dateEnd=calendar.get(Calendar.DAY_OF_MONTH);
        int monthEnd=calendar.get(Calendar.MONTH);
        int yearEnd=calendar.get(Calendar.YEAR);

        calendar.add(Calendar.DAY_OF_MONTH,-14);

        int dateBegin=calendar.get(Calendar.DAY_OF_MONTH);
        int monthBegin=calendar.get(Calendar.MONTH);
        int yearBegin=calendar.get(Calendar.YEAR);

        String query="https://www.albion-online-data.com/api/v2/stats/charts/"+itemID+"?date="+yearBegin+
                "-" + monthBegin+"-"+dateBegin+"&end_date="+yearEnd+"-"+monthEnd+"-"+dateEnd+
                "&locations="+location+"&qualities="+quality+"&time-scale="+timeScale;

        HttpURLConnection connection=null;
        JsonObject chartJSON=null;

        try {
            connection= (HttpURLConnection) new URL(query).openConnection();

            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(250);
            connection.setReadTimeout(250);

            connection.connect();

            StringBuilder sb = new StringBuilder();

            if(HttpURLConnection.HTTP_OK==connection.getResponseCode()){
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line=in.readLine())!=null){
                    sb.append(line);
                    sb.append("\n");
                }

                String chartString=sb.toString();
                chartString=chartString.replace("}","");
                chartString=chartString.substring(9,chartString.length()-2);
                chartString+="}";

                chartJSON= JsonParser.parseString(chartString).getAsJsonObject();

            }else {
                System.out.println("fail: "+connection.getResponseCode()+", "+connection.getResponseMessage());
                if (connection.getResponseCode()==429){
                    return new Item("wait","wait");
                }
            }

        } catch (IOException e) {
            System.out.println("ERROR: "+e.getMessage()+"\nOn: "+query+"\n number of retry: "+numberOfRetry);
            System.out.println(e.getMessage());
            numberOfRetry++;
            return itemRequest(itemID, englishItemName, location, quality, timeScale);
        } catch (Throwable e){
            return null;
        }finally {
            if(connection!=null){
                connection.disconnect();
            }
        }

        query="https://www.albion-online-data.com/api/v2/stats/prices/"+itemID+
                "?&locations="+location+"&qualities="+quality;

        connection=null;
        JsonObject pricesJSON=null;

        try {
            connection= (HttpURLConnection) new URL(query).openConnection();

            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(250);
            connection.setReadTimeout(250);

            connection.connect();

            StringBuilder sb = new StringBuilder();

            if(HttpURLConnection.HTTP_OK==connection.getResponseCode()){
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line=in.readLine())!=null){
                    sb.append(line);
                    sb.append("\n");
                }

                String chartString=sb.toString();
                chartString=chartString.substring(1,chartString.length()-2);

                pricesJSON= JsonParser.parseString(chartString).getAsJsonObject();

            }else {
                System.out.println("fail: "+connection.getResponseCode()+", "+connection.getResponseMessage());
            }

        }catch (Throwable e){
            System.out.println("ERROR: "+e.getMessage()+"\nOn: "+query+"\n number of retry: "+numberOfRetry);
            numberOfRetry++;
            return itemRequest(itemID, englishItemName, location, quality, timeScale);
        } finally {
            if(connection!=null){
                connection.disconnect();
            }
        }

        if(pricesJSON==null||chartJSON==null){
            return null;
        }

        float averageQuantity=0;
        float buyPrice=0;
        float sellPrice=0;

        try {
            buyPrice = Float.parseFloat(pricesJSON.get("buy_price_max").toString());
            sellPrice = Float.parseFloat(pricesJSON.get("sell_price_min").toString());

            String itemCountString = chartJSON.get("item_count").toString();
            itemCountString = itemCountString.substring(1, itemCountString.length() - 1);

            String[] itemCountArray = itemCountString.split(",");

            if (itemCountArray.length < 14) {
                String last = itemCountArray[itemCountArray.length - 1];
                itemCountString = itemCountString.substring(0, itemCountString.length() - last.length() - 1) + ",0".repeat(14 - itemCountArray.length) +
                        "," + last;
                itemCountArray = itemCountString.split(",");
            }
            int min=Integer.MAX_VALUE;
            int max=Integer.MIN_VALUE;



            for(int i=1;i<itemCountArray.length-1;i++){

                int currAmount=Integer.parseInt(itemCountArray[i]);

                if(min>currAmount){
                    min=currAmount;
                }
                if(max<currAmount){
                    max=currAmount;
                }

            }

            int sum=0;

            for (int i=1;i<itemCountArray.length-1;i++){

                int currAmount=Integer.parseInt(itemCountArray[i]);

                if(currAmount!=min&&currAmount!=max){
                    sum+=currAmount;
                }

            }

            averageQuantity=(float) sum/(float) (itemCountArray.length-4);
        }catch (Throwable e){
            e.printStackTrace();
            System.out.println(query+"\n===========");
            return null;
        }



        return new Item(englishItemName,quality, itemID,averageQuantity,buyPrice,sellPrice);
    }

}
