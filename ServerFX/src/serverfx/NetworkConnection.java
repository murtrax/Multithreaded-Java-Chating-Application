
package serverfx;

import java.io.*;
import java.util.function.Consumer;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class NetworkConnection {
    
    //defining a connection thread
    private ConnectionThread connThread = new ConnectionThread();
    
    //Consumer represents an Object that is designed to intake a object and return no results. Serialization in java is a mechanism of writing the state of an object into a byte stream
    //It is mainly used to travel object's state on the network (known as marshaling). This function.operations name is onReceiveCallBack. 
    private Consumer<Serializable> onReceiveCallBack;
    
    //create a constructor for the class that takes this as an input, Serialize will basically convert this into a byte stream so that we can send it over a network 
    public NetworkConnection(Consumer<Serializable> onReceiveCallBack)
    {
        this.onReceiveCallBack = onReceiveCallBack;
        
        //A daemon thread is a thread that does not prevent the JVM from exiting when the program finishes but the thread is still running
        connThread.setDaemon(true);
    }
    
    public void startConnection() throws Exception
    {
        //will spawn a new thread and start executing the run method within class ConnectionThread
        connThread.start();
    }
    
    public void send (Serializable data) throws Exception
    {
        //Where out message is written in an object and sent through the stream
        connThread.out.writeObject(data);
    }

    public void closeConnection() throws Exception 
    {
        //will close the connection streams when there is an exception 
            connThread.socket.close();
    }
    
    protected abstract boolean isServer();
    protected abstract String getIP();
    protected abstract int getPort();
    
    //writeing and reading froma a connection/socket cannot happen one after the other it has to happen simaltaniouslt thous the use of multithreading 
    private class ConnectionThread extends Thread
    {
        
        private Socket socket;
        private ObjectOutputStream out;
        
        @Override
        public void run() 
            {
                   //On the client we dont need to have a server socket. 
                    try ( ServerSocket server = new ServerSocket(getPort());
                           
                        //This waits for the connection from the other end. This connects it to the Server
                        Socket socket = server.accept();

                        //The ObjectInputStream defined where we send objects
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                        //The ObjectInputStream defined where we recieve objects
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream())    
                       ) 
                   {
                       this.socket= socket;
                       this.out = out;
                       
                       //disables/enables the use of Nagle's Algorithm to control the amount of buffering used when transferring data. Nagle's algorithm tries to send 
                       //full data segments by waiting, if necessary, for enough writes to come through to fill up the segment.seful to disable the use of 
                       //Nagle's algorithm (setTcpNoDelay(true)) when your communication over that socket comprises small packets and where latency is important
                       socket.setTcpNoDelay(true);
                       
                       //does not allow our JVM to exit while the chat is ongoing 
                       while(true)
                       {
                           //constantly reads the input stream coming from the other user
                           Serializable data = (Serializable) in.readObject();
                           onReceiveCallBack.accept(data);
                       }
                   }
        
            catch (Exception e) {
                onReceiveCallBack.accept("Connection Closed");
        
            }
                   
         }
    }
    
}
