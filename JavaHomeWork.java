/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javahomework;

/**
 *
 * @author cargamoni
 */
public class JavaHomeWork {

    /**
     * @param args the command line arguments
     */
    //Projenin main fonksyonu bu class içerisinde yer alır. containerFrame buradan tetiklenir ve 
    //ekranda gösterilir. Kapatıldığında ise containerFrame içerisindeki setDefaultCloseOperation
    //metodu sayesinde program durdurulur.
    public static void main(String[] args) {
        // TODO code application logic here
       java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
               containerFrame frame = new containerFrame();
               frame.setVisible(true);
          }
    });
    }
    
}
