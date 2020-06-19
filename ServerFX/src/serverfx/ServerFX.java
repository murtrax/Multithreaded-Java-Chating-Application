
package serverfx;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.*;


//Top level ServerFX class defined that extends Application 
public class ServerFX extends Application {
    
    //Network connection class object created
    private NetworkConnection connection = createServer(); 
    
    //Defined our TextArea where the messages sent and recieved by the user will be displayed
    public TextArea messages = new TextArea();
    
    //defined out TextField where the user will enter messages
    private TextField input = new TextField();
    
    private Button btn = new Button("Send File");
    
    private Button btn1 = new Button ("Recieve File");
    
    ServerSocket ss;
    
    Socket s;
    
    DataInputStream din;
    
    DataOutputStream dout;
    
    ServerSocket ss1;
    
    Socket s1;
    
    DataInputStream din1;
    
    DataOutputStream dout1;
    
    BufferedReader br;
    
    String address = "localhost";
    
   //createContent method defined that sets up the content and layout of the GUI ` 
   private Parent createContent()
   {

       //textArea messages set to unEditable
       messages.setEditable(false);
       
       //actionListener defined on the TextField where the user enters his/her message 
       input.setOnAction(event->
            {
                //adds the users name to the begining of every string to differentiate between who sent each induviual message
                String message = "Server: ";
                
                //gets the contents of the message entered into the text field by the user and stores them in the string message 
                message += input.getText();
                
                //Once the user ivokes the actionListener on the TextField, it is cleared for the next input 
                input.clear();
                   
                //The TextArea is updated with the message 
                messages.appendText(message + "\n");
                   
                //send method of NetworkConnection invoked that takes the string message as an input and is enclosed in try statement 
                try 
                   {
                   connection.send(message);
                   }
                
                //If exception caught then the program informs the user it cannot send the message 
                catch (Exception e)
                   {
                       messages.appendText("Failed to send Text Message" + "\n");
                   }
               });
       
       btn.setOnAction(event ->
               {
                   
                   try 
                        {
                            connection.send("------------------------------------------------------------------------------------------");
                            connection.send("You are Recieving a file\nPress the Recieve file button to accept it");
                        }

                        catch (Exception E)
                        {
                            E.printStackTrace();
                        } 
                   FileChooser fc = new FileChooser();
                 
                   
                   File selectedFile = fc.showOpenDialog(null);
                   fc.setTitle("Send File");
                   
                   System.out.println(selectedFile.getAbsolutePath());
                       try {
                           sendFile(selectedFile.getAbsolutePath());
                       } catch (IOException ex) {
                           Logger.getLogger(ServerFX.class.getName()).log(Level.SEVERE, null, ex);
                       }

                   
               });
       
       btn1.setOnAction(event ->
               {
                   
                   try {
                           recieveFile();
                       } 
                        catch (IOException ex) {
                           Logger.getLogger(ServerFX.class.getName()).log(Level.SEVERE, null, ex);
                       }
               });
       
       //Vbox is layout that lays out its children in a single vertical column.
       VBox root = new VBox(25, messages, input, btn, btn1);
       
       //setting the size of the layout to 400x400
       root.setPrefSize(450,450);
       
       //setting the height of the textArea to 550
       messages.setPrefHeight(350);
       
       return root;
   }
   
    @Override
   //init method overidded that invokes the startConnection method of NetworkConnection and displays a welcome message on startup
    public void init() throws Exception
   {
       messages.appendText("Welcome to D-A-S-H Instant Messenger" + "\n");
       connection.startConnection();
   }
   
    //
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.setTitle("D-A-S-H Instant Messenger");
        primaryStage.show();
    }
    
    //stop method defined that closes the connection between the server and client 
    public void stop() throws Exception
    {
        
        connection.closeConnection();
    }
    
    //createServer method defined that 
    private Server createServer()
    {
        return new Server (55555, data-> {
            Platform.runLater(()-> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }
    
    private void sendFile(String filename) throws IOException {
        
    

    while(true)
{
	//create server socket on port 5000
    ss=new ServerSocket(5000);
    messages.appendText("------------------------------------------------------------------------------------------\n");
    messages.appendText ("Waiting for request\n");
    s=ss.accept();  
    messages.appendText ("Connected With "+s.getInetAddress().toString());
    din=new DataInputStream(s.getInputStream());  
    dout = new DataOutputStream(s.getOutputStream());  

    try{
    String str="";  

    str=din.readUTF();
    messages.appendText("SendGet....Ok\n");

    if(!str.equals("stop")){  

    messages.appendText("Sending File: "+filename+"\n");
    dout.writeUTF(filename);  
    dout.flush();  

    File f=new File(filename);
    FileInputStream fin=new FileInputStream(f);
    long sz=(int) f.length();

    byte b[]=new byte [1024];

    int read;

    dout.writeUTF(Long.toString(sz)); 
    dout.flush(); 

    messages.appendText("Size: "+sz+"\n");
    messages.appendText ("Buf size: "+ss.getReceiveBufferSize()+"\n");

    while((read = fin.read(b)) != -1){
    dout.write(b, 0, read); 
    dout.flush(); 
}
    
    fin.close();

    messages.appendText("..ok\n"); 
    dout.flush(); 
}  
    
    dout.writeUTF("stop");  
    messages.appendText("Send Complete\n");
    messages.appendText("------------------------------------------------------------------------------------------\n");
    dout.flush();  
}
catch(Exception e)
{
	e.printStackTrace();
	messages.appendText("An error occured\n");
}
       
    //din.close();
    //dout.close();
    //s.close();  
    //ss.close();  
    
  }
 }
    
     private void recieveFile() throws IOException {
               

//create the socket on port 5000
    s1 = new Socket(address,50001);  
    din1 = new DataInputStream(s1.getInputStream());  
    dout1 = new DataOutputStream(s1.getOutputStream());  
    br = new BufferedReader(new InputStreamReader(System.in));  

    
    String str="start",filename="";  
    
        while(!str.equals("start"))
	str=br.readLine(); 
 
	dout1.writeUTF(str); 
	dout1.flush();  
	
	filename=din1.readUTF(); 
        messages.appendText("------------------------------------------------------------------------------------------\n");
	messages.appendText("Receving file: "+filename + "\n");
	filename="client"+filename;
	

//
        long sz=Long.parseLong(din1.readUTF());
        messages.appendText("File Size: "+(sz/(1024*1024))+" MB\n");

        byte b[]=new byte [1024];
        messages.appendText("Receving file..\n");
        FileChooser fileChooser = new FileChooser();
         fileChooser.setInitialFileName( (new File(filename)).getName());
        //Show save file dialog
              File file = fileChooser.showSaveDialog(null);
              file = new File(file.getParentFile(), (new File(filename)).getName());
              messages.appendText("Saving as file: "+(new File(filename)).getName() + "\n");
              FileOutputStream fos = null;
             String pathToFile="";
              if(file != null){
                  
                  fos =new FileOutputStream(file,true);
              }   
    long bytesRead;
    do
    {
    bytesRead = din1.read(b, 0, b.length);
    fos.write(b,0,b.length);
    }   
    
    while(!(bytesRead<1024));
    messages.appendText("Comleted\n");
    messages.appendText("------------------------------------------------------------------------------------------\n");
    fos.flush();
    dout1.flush();
    br.close();
    //fos.close(); 
    //din.close();
    //dout.close();  	
    //s.close();  
}
    
    
    //main method defined 
    public static void main(String[] args) {
        
        //lauch method called to launch program 
        launch(args);
    }
}
   