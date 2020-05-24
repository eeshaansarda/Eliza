import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;


public class ElizaEngine{
    
    JSONArray script;
    LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> scriptKeywords;

    public ElizaEngine(String fileName) throws IOException{
        script = fileIntoJsonArray(fileName);
        scriptKeywords = getKeywordMap();
    }
    
    //Reads the file into a JSONArray
    JSONArray fileIntoJsonArray(String fileName) throws IOException{
        String dir = "../scripts/";
        String content = Files.readString(Paths.get(dir + fileName));
        return new JSONArray(content);
    }

    //Returns opening statement
    String getInitialSentence(){
        return script.getJSONObject(0).getString("Initial Sentence");
    }

    //Returns a random default response
    String getDefault(){
        Random rand = new Random();
        int size = script.getJSONObject(2).length();
        return script.getJSONObject(2).getString(Integer.toString(rand.nextInt(size)));
    }

    //Returns the keyword object
    JSONObject getKeywordObject(){
        return script.getJSONObject(1);
    }

    //Converts the JSONObject to a LinkedHashMap
    //Using LinkedHashMap to preserve the order
    LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> getKeywordMap(){

        ArrayList<String> reassembly;
        LinkedHashMap<String, ArrayList<String>> decompostion;
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> keywords = new LinkedHashMap<>();

        Iterator<String> keysKeywords = getKeywordObject().keys(); 
        while(keysKeywords.hasNext()){
            String keyKeyword = keysKeywords.next();
            Iterator<String> keysDecomposition = getKeywordObject().getJSONObject(keyKeyword).keys();
            decompostion = new LinkedHashMap<>();
            while(keysDecomposition.hasNext()){
                String keyDecomposition = keysDecomposition.next();
                reassembly = new ArrayList<>();
                for(int i = 0; i < getKeywordObject().getJSONObject(keyKeyword).getJSONObject(keyDecomposition).length(); i++){
                    reassembly.add(getKeywordObject().getJSONObject(keyKeyword).getJSONObject(keyDecomposition).getString(Integer.toString(i)));
                }
                decompostion.put(keyDecomposition, reassembly);
            }
            keywords.put(keyKeyword, decompostion);
        }
        return keywords;
    }

    //Interacts with MainMenu and prints responses
    public void printReply(){
        Scanner input = new Scanner(System.in);

        System.out.println(getInitialSentence());

        String sentence = input.nextLine();
        //Ends when user enters bye
        while(!sentence.equals("bye")){
            reply(sentence);
            sentence = input.nextLine();
        }
    }

    //Helps printReply()
    void reply(String input){

        input = input.replaceAll("[^a-zA-Z0-9]", " ");
        input = input.toLowerCase();
    
        List<String> words = Arrays.asList(input.split(" "));
        Set<String> keys = scriptKeywords.keySet();
        Set<String> invalidKeywords = new HashSet<>();
        String output;
        invalidKeywords.clear();

        //The following code checks for keyword and generate response accordingly
        String foundKeyword = checkforKeyword(keys, words, invalidKeywords);
            
        if(foundKeyword == "xx"){                                           // Checking if a keyword exists in the sentence
            System.out.println(getDefault());}
        else{
            output = reassembler(foundKeyword, words);
            if(output.equals("NSDR")){                                      // If the keyword comesback with no suitable decomp rule
                invalidKeywords.add(foundKeyword);                          // add the keyword to a list on invalid keywords 

                for(int i = 0; i < words.size(); i++){                      // Go through the sentence again 
                    foundKeyword = checkforKeyword(keys, words, invalidKeywords);
                    if(foundKeyword.equals("xx"))
                        break;

                    output = reassembler(foundKeyword, words);
                    if (!output.equals("NSDR")){
                        break;
                    }
                    else{
                        invalidKeywords.add(foundKeyword);
                    }
                }
                if(foundKeyword.equals("xx")){
                    System.out.println(getDefault());
                }
                else if(reassembler(foundKeyword, words).equals("NSDR")){
                    System.out.println(getDefault());
                }
                else{
                    output = reassembler(foundKeyword, words);
                    System.out.println(output);
                }
            }
            else{
                System.out.println(output);
            }
        }
   }

   //Looks for keyword
   String checkforKeyword(Set<String> keys, List<String> words, Set<String> invalidKeywords){
    String vipKey = "xx";
    boolean control = false;
    
    for(String key: keys){
        for(String word: words){
            if(key.equals(word) && !invalidKeywords.contains(word)){
                vipKey = word;
                control = true;
                break;
            }
        }
        if(control)
            break;
    }
    return vipKey;
   }

    //Uses the keyword and tries all decompRules and applies where applicable
    String reassembler(String keyword, List<String> sentence){

        Set<String> decompRules = scriptKeywords.get(keyword).keySet();
        List<List<String>> stars = new ArrayList<>();
        String finalDecomprule = "xx";              //To keep track if there is no decompRule
        boolean matchesDecompRule = true;           //This too

        for(String decompRuleString: decompRules){
            List<String> decompRuleList = Arrays.asList(decompRuleString.split(" "));
            int inputPos = 0;
            matchesDecompRule = true;
            stars = new ArrayList<>();

            for(int i = 0; i < decompRuleList.size(); i++){
            
                if(inputPos > sentence.size()-1){
                    matchesDecompRule = false;
                    break;
                }
                
                String sentenceWord = sentence.get(inputPos);

                //Adds words to star if there is a star in the decompRule
                if(decompRuleList.get(i).equals("*")){
                    List<String> star = new ArrayList<>();

                    if(i == decompRuleList.size()-1){
                        //end
                        while(inputPos < sentence.size()){
                            sentenceWord = sentence.get(inputPos);
                            star.add(sentenceWord);
                            inputPos++;
                        }
                    }
                    else{
                        //middle
                        //start
                        String nextWord;
                        nextWord = decompRuleList.get(i+1);
    
                        while(!sentence.get(inputPos).equals(nextWord)){
                            if(inputPos == sentence.size()-1){
                                matchesDecompRule = false;
                                break;
                            }
                            sentenceWord = sentence.get(inputPos);
                            star.add(sentenceWord); 
                            inputPos++;
                        }
                    }
                    if(star.size()==0){
                        matchesDecompRule = false;
                    }else{
                        stars.add(star);
                    }
                }
                else{
                    //checks if the decompRule is correct for the sentence
                    if(!sentenceWord.equals(decompRuleList.get(i))){
                        matchesDecompRule = false;
                    }
                    if(sentenceWord.equals(decompRuleList.get(i))){
                        inputPos++;
                    }
                }
                
                if(inputPos != sentence.size() && i==decompRuleList.size()-1){
                    matchesDecompRule = false;
                    break;
                }

                if(matchesDecompRule && inputPos==sentence.size()){
                    break;
                }
            }
            if (matchesDecompRule){ 
                finalDecomprule = decompRuleString;
                break;
            }
        }
        //Reassembles the sentence after randomly selecting the reassembly rule
        if(matchesDecompRule){
            Random rand = new Random();
            int size = scriptKeywords.get(keyword).get(finalDecomprule).size();
            String reassemblyRule = scriptKeywords.get(keyword).get(finalDecomprule).get(rand.nextInt(size));
            String[] reassemblyArray = reassemblyRule.split(" ");
            String output = "";
            
            //creates the output string
            for(int i = 0; i < reassemblyArray.length; i++){
                try{
                    int number = Integer.parseInt(reassemblyArray[i]);
                    for(int j = 0; j < stars.get(number).size(); j++){
                        output += (stars.get(number)).get(j) + " " ;
                    }
                } catch(Exception e){
                    output += reassemblyArray[i] + " " ;
                }
            }
            return output;
        }
        return new String("NSDR");
        
    }
}
