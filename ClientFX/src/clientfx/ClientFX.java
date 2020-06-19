
package clientfx;

import java.io.*;
import java.net.ServerSocket;
import javafx.scene.control.*;
import javafx.scene.*;
import java.net.Socket;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.stage.*;



public class ClientFX extends Application {
    
    private NetworkConnection connection = createClient(); 
    
    //Defined our TextArea where the messages sent and recieved by the user will be displayed
    public TextArea messages = new TextArea();
    
    //defined out TextField where the user will enter messages
    private TextField input = new TextField();
    
    private Button btn = new Button("Recieve File");

    private Button btn1 = new Button("Send File");
     
    String address = "localhost";
    
    Socket s;
    
    DataInputStream din;
    
    DataOutputStream dout;
    
    BufferedReader br;
    
    ServerSocket ss1;
    
    Socket s1;
    
    DataInputStream din1;
    
    DataOutputStream dout1;
    
    
   private Parent createContent()
   {
       messages.setEditable(false);

       input = new TextField();
       
       input.setOnAction(event->
            {
                String message = "Client: ";
                message += input.getText();
                input.clear();
                   
                messages.appendText(message + "\n");
                   
                try 
                   {
                   connection.send(message);
                   }
                catch (Exception e)
                   {
                       messages.appendText("Failed to send Text Message" + "\n");
                   }
               });
       
       btn.setOnAction(event ->
               {
                   
                   try {
                           recieveFile();
                       } 
                        catch (IOException ex) {
                           Logger.getLogger(ClientFX.class.getName()).log(Level.SEVERE, null, ex);
                       }
               });
       
       btn1.setOnAction(event ->
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
                           Logger.getLogger(ClientFX.class.getName()).log(Level.SEVERE, null, ex);
                       }

                   
               });
       //Vbox is layout that lays out its children in a single vertical column.
       VBox root = new VBox(25, messages,input, btn1, btn);
       
       //setting the size of the layout to 400x400
       root.setPrefSize(450,450);
       
       //setting the height of the textArea to 550
       messages.setPrefHeight(350);
       
       return root;
   }
   
    @Override
    //The intialize method that is called whenever an application is intialized
   public void init() throws Exception
   {
       messages.appendText("Welcome to D-A-S-H Instant Messenger" + "\n");
       connection.startConnection();
   }
   
    public void start(Stage Stage) throws Exception {
        
        
     
        
       
        Stage.setScene(new Scene(createContent()));
        Stage.setTitle("D-A-S-H Instant Messenger");
        Stage.show();

    }
    
    public void stop() throws Exception
    {
        
        connection.closeConnection();
    }
    
    private Server createServer()
    {
        return new Server (55555, data-> {
            Platform.runLater(()-> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }
    
    private Client createClient ()
    {
        return new Client("127.0.0.1", 55555, data -> {
            Platform.runLater(()-> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }
    
    private void recieveFile() throws IOException {
               

//create the socket on port 5000
    s = new Socket(address,5000);  
    din = new DataInputStream(s.getInputStream());  
    dout = new DataOutputStream(s.getOutputStream());  
    br = new BufferedReader(new InputStreamReader(System.in));  

    
    String str="start",filename="";  
    
        while(!str.equals("start"))
	str=br.readLine(); 
 
	dout.writeUTF(str); 
	dout.flush();  
	
	filename=din.readUTF(); 
        messages.appendText("------------------------------------------------------------------------------------------\n");
	messages.appendText("Receving file: "+filename + "\n");
	filename="client"+filename;
	

//
        long sz=Long.parseLong(din.readUTF());
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
    bytesRead = din.read(b, 0, b.length);
    fos.write(b,0,b.length);
    }   
    
    while(!(bytesRead<1024));
    messages.appendText("Comleted\n");
    messages.appendText("------------------------------------------------------------------------------------------\n");
    fos.flush();
    dout.flush();
    br.close();
    //fos.close(); 
    //din.close();
    //dout.close();  	
    //s.close();  
}
    
      private void sendFile(String filename) throws IOException {
        
    

    while(true)
{
	//create server socket on port 5000
    ss1=new ServerSocket(50001);
    messages.appendText("------------------------------------------------------------------------------------------\n");
    messages.appendText ("Waiting for request\n");
    s1=ss1.accept();  
    messages.appendText ("Connected With "+s1.getInetAddress().toString());
    din1=new DataInputStream(s1.getInputStream());  
    dout1 = new DataOutputStream(s1.getOutputStream());  

    try{
    String str="";  

    str=din1.readUTF();
    messages.appendText("SendGet....Ok\n");

    if(!str.equals("stop")){  

    messages.appendText("Sending File: "+filename+"\n");
    dout1.writeUTF(filename);  
    dout1.flush();  

    File f=new File(filename);
    FileInputStream fin=new FileInputStream(f);
    long sz=(int) f.length();

    byte b[]=new byte [1024];

    int read;

    dout1.writeUTF(Long.toString(sz)); 
    dout1.flush(); 

    messages.appendText("Size: "+sz+"\n");
    messages.appendText ("Buf size: "+ss1.getReceiveBufferSize()+"\n");

    while((read = fin.read(b)) != -1){
    dout1.write(b, 0, read); 
    dout1.flush(); 
}
    
    fin.close();

    messages.appendText("..ok\n"); 
    dout1.flush(); 
}  
    
    dout1.writeUTF("stop");  
    messages.appendText("Send Complete\n");
    messages.appendText("------------------------------------------------------------------------------------------\n");
    dout1.flush();  
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
    
    
    public static void main(String[] args) {
        
        launch(args);
    }
}
    

