
package serverfx;

import java.io.*;
import java.util.function.Consumer;

//top level public class defined that extends Network Connection 
public class Server extends NetworkConnection{
    
    //int varible defined that will store the port number   
    private int port;
    
    //three argument constructor of Server defined 
    public Server(int port, Consumer<Serializable> onReceiveCallBack)
    {
        //superclass Constructor called 
        super(onReceiveCallBack);
        
        //
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return true;
    }

    @Override
    protected String getIP() {
        return null;
    }

    @Override
    protected int getPort() {
        return port; 
    }

    
    
}
