import java.io.*;

public class ItemReader {

    BufferedReader bufferedReader;

    public ItemReader(String fileName){

        try {
            bufferedReader= new BufferedReader(new FileReader(new File(fileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Item getNext(){

        Item item=new Item();

        try {
            String fullString= bufferedReader.readLine();
            if(fullString==null){
                System.out.println("eof");
                return null;
            }
            String[] split=fullString.split(":");
            if(split.length!=3){
                return item;
            }
            String itemID=split[1];
            StringBuilder stringBuilder=new StringBuilder(split[2]);
            for(int i=1;i<stringBuilder.length();i++){
                if(Character.isUpperCase(stringBuilder.charAt(i))&&stringBuilder.charAt(i-1)!='('){
                    stringBuilder.insert(i," ");
                    i++;
                }
                if(stringBuilder.charAt(i)=='('){
                    stringBuilder.insert(i," ");
                    i++;
                }
            }
            String englishItemName=stringBuilder.toString();
            item=new Item(englishItemName,itemID);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return item;
    }

}
