//If get time, do not allow user to input same strings as keywords

import org.json.*;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ScriptWriter{

    Scanner input;

    public ScriptWriter(){
        input = new Scanner(System.in);
    }

    //Controls all the methods in the class
    public void createScript(){
        JSONArray script;
        JSONObject keyObject;
        JSONObject rules;
        JSONObject reassemblyJSON;

        ArrayList<String> reassemblyList;
        String decompositionRule;
        script = new JSONArray();

        System.out.println("Enter xx to terminate\n\n");

        String initSentence = getInitialSentence();

        if(initSentence.equals("xx"))
            System.exit(0);

        //Writes the initial sentence
        keyObject = new JSONObject();
        keyObject.put("Initial Sentence", initSentence);
        script.put(keyObject);
        
        String keyword = getKeyword();
        keyObject = new JSONObject();

        //Nested loops to write keywords, decompositionRules, and reassemblyRules
        while(!keyword.equals("xx")){

            decompositionRule = getDecompostionRule();
            rules = new JSONObject();

            while(!decompositionRule.equals("xx")){

                reassemblyJSON = new JSONObject();
                reassemblyList = getReassemblyRules();
                for(int i = 0; i < reassemblyList.size(); i++){
                    reassemblyJSON.put(Integer.toString(i), reassemblyList.get(i));
                }
                rules.put(decompositionRule, reassemblyJSON);
                decompositionRule = getDecompostionRule();
                
            }

            keyObject.put(keyword, rules);
            keyword = getKeyword();
        }

        script.put(keyObject);

        String defaultString = getDefault();
        int ctr = 0;
        JSONObject defaultSentences = new JSONObject();

        //Writing default string
        while(defaultString.equals("xx")){
            defaultSentences.put(Integer.toString(ctr), defaultString);
            defaultString = getDefault();
        }

        script.put(defaultSentences);

        writeScript(script, getFileName());
    }

    //Writing the script into the file
    void writeScript(JSONArray script, String fileName){
        try{
            String dir = "./scripts";

            File directory = new File(dir);
            if(!directory.exists())
                directory.mkdir();

            FileWriter file = new FileWriter(dir + "/" + fileName);

            file.write(script.toString());
            file.flush();
            file.close();

        }catch(IOException e){
			e.printStackTrace();
		}
    }

    //Gets file name from user
    String getFileName(){

        String fileName;

        System.out.println("Enter the file's name: ");
        fileName = input.nextLine();

        if(!fileName.endsWith(".json")){
            fileName += ".json";
        }

        return fileName;
    }

    //Gets keyword from user
    String getKeyword(){
        System.out.println("Enter a keyword: ");
        return input.nextLine();
    }

    //Gets initial sentence from user
    String getInitialSentence(){
        System.out.println("Enter the first sentence: ");
        return input.nextLine();
    }

    //Gets default sentence from user
    String getDefault(){
        System.out.println("Enter default sentence: ");
        return input.nextLine();
    }

    //Gets reassemblyRules from user
    ArrayList<String> getReassemblyRules(){
        String reassemblyRule = "";
        ArrayList<String> reassemblyRules = new ArrayList<String>();

        System.out.println("Enter reassembly rules: \n");
        reassemblyRule = input.nextLine();

        while(!reassemblyRule.equals("xx")){
            reassemblyRules.add(reassemblyRule);
            reassemblyRule = input.nextLine();
        }

        return reassemblyRules;
    }

    //Gets decompositionRules from user
    String getDecompostionRule(){
        System.out.println("Enter a decomposition rule: ");
        return input.nextLine();
    }
}
