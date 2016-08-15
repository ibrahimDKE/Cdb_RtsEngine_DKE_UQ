/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cdb;

import cdb.SeeDB;
/**
 *
 * @author himos
 */
public class pp {
   
    public static void main(String args[]){
        
//    DBSettings s = new DBSettings();
//    DBConnection c= new DBConnection();
//    ExperimentalSettings es= new ExperimentalSettings();
//    //s.getPostgresDefault();
//    c.connectToDatabase(s.getPostgresDefault().database, s.getPostgresDefault().databaseType, s.getPostgresDefault().username, s.getPostgresDefault().password);
//    System.out.println(c.getDatabaseType().toString()+" gggg\t"+c.getDatabaseName().toString()+"\t");
//    ResultSet rs=c.executeQuery("select count(*) from seedb_e2e_test");
//    try{
//    rs.next();
//    System.out.println(rs.getString(1));
//    SeeDB ccdb= new SeeDB();
//    ccdb.connectToDatabase(s.getDefault());
//    ccdb.initialize("select * from olympicathletes where dim6='Swimming'", null,es.getDefault());
//    ccdb.computeDifference();
//    
//        }
//    catch (Exception ex)
//            {
//                System.out.println(ex);
//            }
//    
        
        Cdb_Gui Form= new Cdb_Gui();
        Form.setVisible(true);
    
    }
    
    
    
}
