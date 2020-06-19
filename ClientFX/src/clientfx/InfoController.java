/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientfx;

import com.jfoenix.controls.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 *
 * @author aashi
 */
public class InfoController implements Initializable  {
    
     @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }  
    
    @FXML JFXTextField name;
    @FXML JFXTextField ip;
    @FXML JFXButton ok;
    

    public void nAction(){
   
       String Uname = name.getText();
       if(Uname == null)
           name.setText("Re-Enter");
   } 

    public void pAction(){
   
       String address = ip.getText();
        if(ip == null)
           ip.setText("Re-Enter");
       
   }   


    public void ok(){
        
        
        nAction();
        pAction();
        
    
    }

}
