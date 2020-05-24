import java.util.Scanner;

public class MainMenu{

    //init class which parses and replies should accept integer for the person the user wants to interact to

    int askForPerson(){
        Scanner input = new Scanner(System.in);
        int personNo = 0;

        System.out.println("Welcome to ELIZA!\n\n" + 
                           "Who would you like to talk to?\n" +
                           "1. A psychotherapist\n" + 
                           "2. A tech support guy\n" +
                           "3. Chandler Bing\n" +
                           "4. Write your own script!\n" +
                           "5. Exit\n");
        //Maybe an option for more information (info about chandler?)
        personNo = input.nextInt();

        if(personNo >= 5)
            System.exit(0);

        return personNo;
    }

    //initializes objects according to the user's input
    void startEngine(int personNo){
        try{
            ElizaEngine engine;
            switch(personNo){
                case 1: 
                    engine = new ElizaEngine("elizascript.json");
                    engine.printReply();
                    break;
                case 2:
                    engine = new ElizaEngine("josephscript.json");
                    engine.printReply();
                    break;
                case 3:
                    engine = new ElizaEngine("chandlerscript.json");
                    engine.printReply();
                    break;
                case 4:
                    ScriptWriter scriptWriter = new ScriptWriter();
                    scriptWriter.createScript();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        MainMenu m = new MainMenu();
        int num = m.askForPerson(); 
        m.startEngine(num);
    }
}
