import java.io.*;
import java.util.HashMap;

public class IniFile {
    private HashMap<String,HashMap<String,String>> parsedFile;
    public IniFile(String inpFileName){
        parsedFile= new HashMap<>();
        File file = new File(inpFileName);
        String ex= inpFileName.substring(inpFileName.length()-4);
        try {
            if(!ex.equals(".ini")){
                throw new FormatException("Wrong file format");
            }
        } catch (FormatException e){
            System.out.println("Wrong file format");
            System.exit(0);
        }

        try (FileReader fis = new FileReader(file);
             BufferedReader bis = new BufferedReader(fis)){

            StringBuilder currDirect= new StringBuilder();
            while(true) {
                String a= bis.readLine();
                if(a == null){
                    break;
                }
                String[] words = a.split(" ");
                if(words[0].charAt(0) == '[') {
                    currDirect = new StringBuilder();
                    for (int i = 1; i < words[0].length() - 1; i++) {
                        currDirect.append(words[0].charAt(i));
                    }
                    parsedFile.put(currDirect.toString(), new HashMap<>());
                }else{
                    if(words[1].charAt(0) != '='){
                        System.out.println("Wrong punctuation in the file \nExit program");
                        System.exit(0);
                    }
                    parsedFile.get(currDirect.toString()).put(words[0],words[2]);

                }
            }
        } catch (FileNotFoundException e){
            System.out.println("Can't open file");
        }
        catch (IOException e){
            System.out.println("Input error");
        }
    }

    public Integer getInt(String key, String section) {
        try {
            return Integer.parseInt(parsedFile.get(section).get(key));
        }  catch (NullPointerException e){
            System.out.println("Can't find section or key");
            return null;
        } catch (NumberFormatException e){
            System.out.println("Wrong number format");
            return null;
        }
    }

    public Float getReal(String key, String section) {
        try {
            return Float.parseFloat(parsedFile.get(section).get(key));
        }  catch (NullPointerException e){
            System.out.println("Can't find section or key");
            return null;
        } catch (NumberFormatException e){
            System.out.println("Wrong number format");
            return null;
        }
    }

    public String getString(String key, String section) {
        try {
            return parsedFile.get(section).get(key);
        }  catch (NullPointerException e){
            System.out.println("Can't find section or key");
            return null;
        }catch (NumberFormatException e){
            System.out.println("Wrong number format");
            return null;
        }
    }

}
