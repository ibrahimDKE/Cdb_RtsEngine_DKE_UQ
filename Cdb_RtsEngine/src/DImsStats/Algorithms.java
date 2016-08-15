/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DImsStats;

import cdb.Attribute;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author himos
 */
public class Algorithms {
    
    

    
    public static List<Attribute> Sela_Eq1(Metadata M,String Table, String Fun, DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr,String Whr2, int n,int Cost, int sizeD, int sizeQ){
    // Cost=0 nocost 1= actual 2= DBestime
        DefaultTableModel Dims=new DefaultTableModel();
        if (Cost==0){
            System.out.println("Sela No Cost");
        Dims=CompuDimsDiffEq1(ViewD,ViewQ,Whr);
        }
        if (Cost==1){
            System.out.println("Sela Actual Cost");
        Dims=CompuDimsDiffEq1_ActualCost(M,Fun,Table,ViewD,ViewQ,Whr,Whr2);
        }
        if (Cost==2){
            System.out.println("Sela DBEstmation Cost");
        Dims=CompuDimsDiffEq1_DBEstCost(M,Fun,Table,ViewD,ViewQ,Whr,Whr2);
        }
        if (Cost==3){
            System.out.println("Sela ApproX Cost Model");
        Dims=CompuDimsDiffEq1_ApproxCostModel(M,Fun,Table,ViewD,ViewQ,Whr,Whr2,sizeD,sizeQ);
        }
        
        List<Attribute> DimsAttributes=Lists.newArrayList();
        if (n>Dims.getRowCount()) n=Dims.getRowCount();
        double spaceCost=0;
        double spaceProfit=0;
        double spacegain=0;
        for ( int i=0;i<n;i++) {
            String Dname = Dims.getValueAt(i, 0).toString();
            double Pr =Double.parseDouble(Dims.getValueAt(i, 4).toString());
            double profit = Double.parseDouble(Dims.getValueAt(i, 5).toString());
             double RunCost = Double.parseDouble(Dims.getValueAt(i, 6).toString());
              double DisCost = Double.parseDouble(Dims.getValueAt(i, 7).toString());
            spaceProfit=spaceProfit+profit;
            spaceCost=spaceCost+RunCost+DisCost;
            spacegain=spacegain+Pr;
            System.out.println(Dname+" Pr:"+Pr +" Profit:"+profit+" RunCost:"+RunCost+" DistCost:"+DisCost);
            DimsAttributes.add(new Attribute(Dname));
            }
        System.out.println("Total Space Profit: "+spaceProfit+" Total Space gain: "+spacegain+" Total Space Cost: "+spaceCost);
       return DimsAttributes;
        }
    
    
    
    public static List<Attribute> Sela_Eq3(Metadata M,String Table, String Fun,DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr,  String Whr2,int n, int sizeD, int sizeQ,int Cost){
         DefaultTableModel Dims=new DefaultTableModel();
        if (Cost==0){
            System.out.println("Sela N-N' No Cost");
           
        Dims=CompuDimsDiffEq3(ViewD,ViewQ,Whr, sizeD,sizeQ);
        }
        if (Cost==1){
            System.out.println("Sela N-N' Actual Cost");
        Dims=CompuDimsDiffEq3_ActualCost(M,Fun,Table,ViewD,ViewQ,Whr,Whr2,sizeD,sizeQ);
        }
        if (Cost==2){
            System.out.println("Sela N-N' DBEstmation Cost");
        Dims=CompuDimsDiffEq3_DBEstCost(M,Fun,Table,ViewD,ViewQ,Whr,Whr2,sizeD,sizeQ);
        }
         if (Cost==3){
         System.out.println("Sela N-N' ApproX Cost Model");
        Dims=CompuDimsDiffEq3_AprroXCostModel(M,Fun,Table,ViewD,ViewQ,Whr,Whr2,sizeD,sizeQ);
        }
      // Def
      // DefaultTableModel Dims=CompuDimsDiffEq3(ViewD,ViewQ,Whr, sizeD,sizeQ);
        if (n>Dims.getRowCount()) n=Dims.getRowCount();
        List<Attribute> DimsAttributes=Lists.newArrayList();
        double spaceCost=0;
        double spaceProfit=0;
        double spacegain=0;
        for ( int i=0;i<n;i++) {
            String Dname = Dims.getValueAt(i, 0).toString();
           // System.out.println(Dname+"\t"+Dims.getValueAt(i, 1).toString());
            DimsAttributes.add(new Attribute(Dname));
            
            
            double Pr =Double.parseDouble(Dims.getValueAt(i, 1).toString());
            double profit = Double.parseDouble(Dims.getValueAt(i, 2).toString());
             double RunCost = Double.parseDouble(Dims.getValueAt(i, 3).toString());
              double DisCost = Double.parseDouble(Dims.getValueAt(i, 4).toString());
            spaceProfit=spaceProfit+profit;
            spaceCost=spaceCost+RunCost+DisCost;
            spacegain=spacegain+Pr;
            System.out.println(Dname+" Pr:"+Pr +" Profit:"+profit+" RunCost:"+RunCost+" DistCost:"+DisCost);
           // DimsAttributes.add(new Attribute(Dname));
            }
        System.out.println("Total Space Profit: "+spaceProfit+" Total Space gain: "+spacegain+" Total Space Cost: "+spaceCost);
       return DimsAttributes;
        }
    
    //////////////
    //Not Used Anymore
    /////////////
    //////////////////////////////////////////
    /////***************************///////////
    ////Used With Costs
    
    public static DefaultTableModel CompuDimsDiffEq1(DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr){
        
        //This implements the Equ= (NQ * SelQ) + (NQ/ND * SelD)
        // Prints top K utility
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Attribute");
        model.addColumn("Diff_Cardnility");
        
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        model.addColumn("Total Divid_Dif");
        model.addColumn("totaldifn");
        model.addColumn("totaldifDela");
         model.addColumn("totaldifDela");
//        Whr=Whr+" "+Whr2;
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                    double DoubeCardQ=Double.parseDouble( ViewQ.getValueAt(i, 1).toString());
                    double DoubeCardD=Double.parseDouble( ViewD.getValueAt(i, 1).toString());
                double deltaCard= DoubeCardD-DoubeCardQ;
                    double DoubeSelQ=Double.parseDouble( ViewQ.getValueAt(i, 2).toString());
                    double DoubeSelD=Double.parseDouble( ViewD.getValueAt(i, 2).toString());
                double deltaSel=Math.abs(DoubeSelD-DoubeSelQ);
                    double DoubeDensityQ=Double.parseDouble( ViewQ.getValueAt(i, 3).toString());
                    double DoubeDensityD=Double.parseDouble( ViewD.getValueAt(i, 3).toString());
                
                double deltaDensity=Math.abs(DoubeDensityQ-DoubeDensityD);
                
                double totaldifn=DoubeCardQ*DoubeSelQ;
                double totaladddif=DoubeCardQ/DoubeCardD;
                double totaldifDela=totaladddif*DoubeSelD;
                
               double totaldif=totaldifn+totaldifDela;
                if(!Whr.contains(ViewnameQ)) {
                Vector textData = new Vector();
                                textData.add(ViewnameD);
                                textData.add(deltaCard);
                                textData.add(deltaSel);
                                textData.add(deltaDensity);
                                textData.add(totaldif);
                                textData.add("0");
                                textData.add("0");
                                textData.add("0");
            model.addRow(textData );   }                 
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
        
        }
        Vector DimsunClustred_data = model.getDataVector();
        Collections.sort(DimsunClustred_data, new ColumnSorter(4, false));
       // model.fireTableDataChanged();
        return model;
        }
    
    public static DefaultTableModel CompuDimsDiffEq1_ActualCost(Metadata M,String Func,String Table,DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr, String Whr2){
        
        //This implements the Equ= (NQ * SelQ) + (NQ/ND * SelD)
        // Prints top K utility
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Attribute");
        model.addColumn("Diff_Cardnility");
        
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        model.addColumn("Total Divid_Dif");
        model.addColumn("totaldifn");
        model.addColumn("totaldifDela");
         model.addColumn("Util");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                    double DoubeCardQ=Double.parseDouble( ViewQ.getValueAt(i, 1).toString());
                    double DoubeCardD=Double.parseDouble( ViewD.getValueAt(i, 1).toString());
                double deltaCard= DoubeCardD-DoubeCardQ;
                    double DoubeSelQ=Double.parseDouble( ViewQ.getValueAt(i, 2).toString());
                    double DoubeSelD=Double.parseDouble( ViewD.getValueAt(i, 2).toString());
                double deltaSel=Math.abs(DoubeSelD-DoubeSelQ);
                    double DoubeDensityQ=Double.parseDouble( ViewQ.getValueAt(i, 3).toString());
                    double DoubeDensityD=Double.parseDouble( ViewD.getValueAt(i, 3).toString());
                
                double deltaDensity=Math.abs(DoubeDensityQ-DoubeDensityD);
                
                double totaldifn=DoubeCardQ*DoubeSelQ;
                double totaladddif=DoubeCardQ/DoubeCardD;
                double totaldifDela=totaladddif*DoubeSelD;
                
               double totaldif=totaldifn+totaldifDela;
                if(!Whr.contains(ViewnameQ) && !Whr2.contains(ViewnameQ)) {
                    Gvar g=new Gvar();
                Vector textData = new Vector();
                                textData.add(ViewnameD);
                                textData.add(deltaCard);
                                textData.add(deltaSel);
                                textData.add(deltaDensity);
                                textData.add(totaldif);
                                double RunDimCost=g.ComputeDimActualCost(M, ViewnameD, Func, Table, Whr, Whr2);
                                double DistDimCost=g.ComputeDimDistceCost (M,"L2 Norm", ViewnameD, Func, Table, DoubeCardQ,DoubeCardD); 
                                double TRunDimCost=RunDimCost+DistDimCost;
                                textData.add(totaldif/TRunDimCost);
                                textData.add(RunDimCost);
                                textData.add(DistDimCost);
                                
                                 
            model.addRow(textData );   }                 
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
        
        }
        Vector DimsunClustred_data = model.getDataVector();
        Collections.sort(DimsunClustred_data, new ColumnSorter(5, false));
       // model.fireTableDataChanged();
        return model;
        }
    
    public static DefaultTableModel CompuDimsDiffEq1_DBEstCost(Metadata M,String Func,String Table,DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr, String Whr2){
        
        //This implements the Equ= (NQ * SelQ) + (NQ/ND * SelD)
        // Prints top K utility
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Attribute");
        model.addColumn("Diff_Cardnility");
        
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        model.addColumn("Total Divid_Dif");
        model.addColumn("Util");
        model.addColumn("totaldifDela");
         model.addColumn("Util");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                    double DoubeCardQ=Double.parseDouble( ViewQ.getValueAt(i, 1).toString());
                    double DoubeCardD=Double.parseDouble( ViewD.getValueAt(i, 1).toString());
                double deltaCard= DoubeCardD-DoubeCardQ;
                    double DoubeSelQ=Double.parseDouble( ViewQ.getValueAt(i, 2).toString());
                    double DoubeSelD=Double.parseDouble( ViewD.getValueAt(i, 2).toString());
                double deltaSel=Math.abs(DoubeSelD-DoubeSelQ);
                    double DoubeDensityQ=Double.parseDouble( ViewQ.getValueAt(i, 3).toString());
                    double DoubeDensityD=Double.parseDouble( ViewD.getValueAt(i, 3).toString());
                
                double deltaDensity=Math.abs(DoubeDensityQ-DoubeDensityD);
                
                double totaldifn=DoubeCardQ*DoubeSelQ;
                double totaladddif=DoubeCardQ/DoubeCardD;
                double totaldifDela=totaladddif*DoubeSelD;
                
               double totaldif=totaldifn+totaldifDela;
                if(!Whr.contains(ViewnameQ) && !Whr2.contains(ViewnameQ)) {
                    Gvar g=new Gvar();
                Vector textData = new Vector();
                                textData.add(ViewnameD);
                                textData.add(deltaCard);
                                textData.add(deltaSel);
                                textData.add(deltaDensity);
                                textData.add(totaldif);
                                double RunDimCost=g.ComputeDimDBEstCost(M, ViewnameD, Func, Table, Whr, Whr2);
                                double DistDimCost=g.ComputeDimDistceCost(M,"L2 Norm", ViewnameD, Func, Table, DoubeCardQ, DoubeCardD); 
                                textData.add(totaldif/(RunDimCost+DistDimCost));
                                textData.add(RunDimCost);
                                textData.add(DistDimCost);
                                
                                
                                 
            model.addRow(textData );   }                 
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
        
        }
        Vector DimsunClustred_data = model.getDataVector();
        Collections.sort(DimsunClustred_data, new ColumnSorter(5, false));
       // model.fireTableDataChanged();
        return model;
        }
    
    public static DefaultTableModel CompuDimsDiffEq1_ApproxCostModel(Metadata M,String Func,String Table,DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr, String Whr2,double sizeD,double sizeQ){
        
        //This implements the Equ= (NQ * SelQ) + (NQ/ND * SelD)
        // Prints top K utility and estimating the cost of each view.
         DefaultTableModel model = new DefaultTableModel();
  try{
       
        model.addColumn("Attribute");
        model.addColumn("Diff_Cardnility");
        
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        model.addColumn("Total Divid_Dif");
        model.addColumn("Util");
        model.addColumn("totaldifDela");
         model.addColumn("Util");
      
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                    double DoubeCardQ=Double.parseDouble( ViewQ.getValueAt(i, 1).toString());
                    double DoubeCardD=Double.parseDouble( ViewD.getValueAt(i, 1).toString());
                double deltaCard= DoubeCardD-DoubeCardQ;
                    double DoubeSelQ=Double.parseDouble( ViewQ.getValueAt(i, 2).toString());
                    double DoubeSelD=Double.parseDouble( ViewD.getValueAt(i, 2).toString());
                double deltaSel=Math.abs(DoubeSelD-DoubeSelQ);
                    double DoubeDensityQ=Double.parseDouble( ViewQ.getValueAt(i, 3).toString());
                    double DoubeDensityD=Double.parseDouble( ViewD.getValueAt(i, 3).toString());
                
                double deltaDensity=Math.abs(DoubeDensityQ-DoubeDensityD);
                
                double totaldifn=DoubeCardQ*DoubeSelQ;
                double totaladddif=DoubeCardQ/DoubeCardD;
                double totaldifDela=totaladddif*DoubeSelD;
                
               double totaldif=totaldifn+totaldifDela;
                if(!Whr.contains(ViewnameQ) && !Whr2.contains(ViewnameQ)) {
                    Gvar g=new Gvar();
                Vector textData = new Vector();
                                textData.add(ViewnameD);
                                textData.add(deltaCard);
                                textData.add(deltaSel);
                                textData.add(deltaDensity);
                                textData.add(totaldif);
                                double RunDimCost=  g.ComputeDimApprCost(  M, ViewnameD, Func, Table,  sizeQ , DoubeCardQ, sizeD, DoubeCardD);
                                double DistDimCost=g.ComputeDimDistceCost(M,"L2 Norm", ViewnameD, Func, Table,DoubeCardQ,DoubeCardD); 
                                textData.add(totaldif/(RunDimCost+DistDimCost));
                                textData.add(RunDimCost);
                                textData.add(DistDimCost);
                                
                                
                                 
            model.addRow(textData );   }                 
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
        
        }
        Vector DimsunClustred_data = model.getDataVector();
        Collections.sort(DimsunClustred_data, new ColumnSorter(5, false));
        
    } catch(Exception e){
                e.printStackTrace();
                }
    return model;
       // model.fireTableDataChanged();
        
        }
    
    
    
    
    
    public static DefaultTableModel CompuDimsDiffEq3(DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr, int sizeD, int sizeQ){
        
        //This implements the Equ= (N-N')
        // Prints top K utility
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Attribute");
        model.addColumn("Diff_Cardnility");
        
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        model.addColumn("Total Divid_Dif");
        model.addColumn("LenMin");
//        model.addColumn("LenMax");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                    double DoubeCardQ=Double.parseDouble( ViewQ.getValueAt(i, 1).toString());
                    double DoubeCardD=Double.parseDouble( ViewD.getValueAt(i, 1).toString());
                double deltaCard= Math.abs(DoubeCardD-DoubeCardQ);
//                    double DoubeSelQ=Double.parseDouble( ViewQ.getValueAt(i, 2).toString());
//                    double DoubeSelD=Double.parseDouble( ViewD.getValueAt(i, 2).toString());
//                double deltaSel=Math.abs(DoubeSelD-DoubeSelQ);
//                    double DoubeDensityQ=Double.parseDouble( ViewQ.getValueAt(i, 3).toString());
//                    double DoubeDensityD=Double.parseDouble( ViewD.getValueAt(i, 3).toString());
//                
//                double deltaDensity=Math.abs(DoubeDensityQ-DoubeDensityD);
//                
//                                double VminQ=sizeQ/DoubeCardQ;
//                                double VmaxQ=sizeQ+1-DoubeCardQ;
//                                double VminD=sizeD/DoubeCardD;
//                                double VmaxD=sizeD+1-DoubeCardD;
//                                double LenMax=VmaxD-VmaxQ;
//                                double LenDMin=VminD-VminQ;
//                                double totaldif=LenMax-LenDMin;
                if(!Whr.contains(ViewnameQ)) {
                Vector textData = new Vector();
                                textData.add(ViewnameD);
                                textData.add(deltaCard);
                                textData.add("0");
                                textData.add("0");
                                textData.add("0");
//                                textData.add(LenDMin);
//                                textData.add(LenMax);
            model.addRow(textData );   }                 
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
        
        }
        Vector DimsunClustred_data = model.getDataVector();
        Collections.sort(DimsunClustred_data, new ColumnSorter(1, false));
        //model.fireTableDataChanged();
        return model;
        }
 
    public static DefaultTableModel CompuDimsDiffEq3_ActualCost(Metadata M,String Func,String Table,DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr, String Whr2, int sizeD, int sizeQ){
        
        //This implements the Equ= (N-N')
        // Prints top K utility
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Attribute");
        model.addColumn("Diff_Cardnility");
        model.addColumn("Ut");
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        model.addColumn("Total Divid_Dif");
//        model.addColumn("LenMin");
//        model.addColumn("LenMax");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                    double DoubeCardQ=Double.parseDouble( ViewQ.getValueAt(i, 1).toString());
                    double DoubeCardD=Double.parseDouble( ViewD.getValueAt(i, 1).toString());
                double deltaCard= Math.abs(DoubeCardD-DoubeCardQ);
//                    double DoubeSelQ=Double.parseDouble( ViewQ.getValueAt(i, 2).toString());
//                    double DoubeSelD=Double.parseDouble( ViewD.getValueAt(i, 2).toString());
//                double deltaSel=Math.abs(DoubeSelD-DoubeSelQ);
//                    double DoubeDensityQ=Double.parseDouble( ViewQ.getValueAt(i, 3).toString());
//                    double DoubeDensityD=Double.parseDouble( ViewD.getValueAt(i, 3).toString());
//                
//                double deltaDensity=Math.abs(DoubeDensityQ-DoubeDensityD);
//                
//                                double VminQ=sizeQ/DoubeCardQ;
//                                double VmaxQ=sizeQ+1-DoubeCardQ;
//                                double VminD=sizeD/DoubeCardD;
//                                double VmaxD=sizeD+1-DoubeCardD;
//                                double LenMax=VmaxD-VmaxQ;
//                                double LenDMin=VminD-VminQ;
//                                double totaldif=LenMax-LenDMin;
                if(!Whr.contains(ViewnameQ) && !Whr2.contains(ViewnameQ)) {
                Vector textData = new Vector();
                Gvar g= new Gvar();
                                textData.add(ViewnameD);
                                textData.add(deltaCard);
                                double RunCost=g.ComputeDimActualCost(M, ViewnameD, Func, Table, Whr, Whr2);
                                double DistDimCost=g.ComputeDimDistceCost (M,"L2 Norm", ViewnameD, Func, Table,DoubeCardQ, DoubeCardD); 
                                textData.add(deltaCard/(RunCost+DistDimCost));
                                textData.add(RunCost);
                                textData.add(DistDimCost);
//                                textData.add(totaldif);
//                                textData.add(LenDMin);
//                                textData.add(LenMax);
            model.addRow(textData );   }                 
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
        
        }
        Vector DimsunClustred_data = model.getDataVector();
        Collections.sort(DimsunClustred_data, new ColumnSorter(2, false));
        //model.fireTableDataChanged();
        return model;
        }
    
    public static DefaultTableModel CompuDimsDiffEq3_DBEstCost(Metadata M,String Func,String Table,DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr, String Whr2, int sizeD, int sizeQ){
        
        //This implements the Equ= (N-N')
        // Prints top K utility
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Attribute");
        model.addColumn("Diff_Cardnility");
        model.addColumn("Ut");
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        model.addColumn("Total Divid_Dif");
        model.addColumn("LenMin");
        model.addColumn("LenMax");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                    double DoubeCardQ=Double.parseDouble( ViewQ.getValueAt(i, 1).toString());
                    double DoubeCardD=Double.parseDouble( ViewD.getValueAt(i, 1).toString());
                double deltaCard= Math.abs(DoubeCardD-DoubeCardQ);
//                    double DoubeSelQ=Double.parseDouble( ViewQ.getValueAt(i, 2).toString());
//                    double DoubeSelD=Double.parseDouble( ViewD.getValueAt(i, 2).toString());
//                double deltaSel=Math.abs(DoubeSelD-DoubeSelQ);
//                    double DoubeDensityQ=Double.parseDouble( ViewQ.getValueAt(i, 3).toString());
//                    double DoubeDensityD=Double.parseDouble( ViewD.getValueAt(i, 3).toString());
//                
//                double deltaDensity=Math.abs(DoubeDensityQ-DoubeDensityD);
//                
//                                double VminQ=sizeQ/DoubeCardQ;
//                                double VmaxQ=sizeQ+1-DoubeCardQ;
//                                double VminD=sizeD/DoubeCardD;
//                                double VmaxD=sizeD+1-DoubeCardD;
//                                double LenMax=VmaxD-VmaxQ;
//                                double LenDMin=VminD-VminQ;
//                                double totaldif=LenMax-LenDMin;
                if(!Whr.contains(ViewnameQ) && !Whr2.contains(ViewnameQ)) {
                Vector textData = new Vector();
                Gvar g= new Gvar();
                                textData.add(ViewnameD);
                                textData.add(deltaCard);
                                double RunCost=g.ComputeDimDBEstCost(M, ViewnameD, Func, Table, Whr, Whr2);
                                double DistDimCost=g.ComputeDimDistceCost (M,"L2 Norm", ViewnameD, Func, Table,DoubeCardQ, DoubeCardD); 
                                textData.add(deltaCard/(RunCost+DistDimCost));
                                textData.add(RunCost);
                                textData.add(DistDimCost);
//                                textData.add(totaldif);
//                                textData.add(LenDMin);
//                                textData.add(LenMax);
            model.addRow(textData );   }                 
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
        
        }
        Vector DimsunClustred_data = model.getDataVector();
        Collections.sort(DimsunClustred_data, new ColumnSorter(2, false));
        //model.fireTableDataChanged();
        return model;
        }
    
    public static DefaultTableModel CompuDimsDiffEq3_AprroXCostModel(Metadata M,String Func,String Table,DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr, String Whr2, int sizeD, int sizeQ){
        
        //This implements the Equ= (N-N')
        // Prints top K utility
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Attribute");
        model.addColumn("Diff_Cardnility");
        model.addColumn("Ut");
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        model.addColumn("Total Divid_Dif");
        model.addColumn("LenMin");
        model.addColumn("LenMax");
        try{
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                    double DoubeCardQ=Double.parseDouble( ViewQ.getValueAt(i, 1).toString());
                    double DoubeCardD=Double.parseDouble( ViewD.getValueAt(i, 1).toString());
                double deltaCard= Math.abs(DoubeCardD-DoubeCardQ);

                if(!Whr.contains(ViewnameQ) && !Whr2.contains(ViewnameQ)) {
                Vector textData = new Vector();
                Gvar g= new Gvar();
//                DoubeCardQ=M.getNumDistinctWhr(ViewnameQ, Whr);
//                DoubeCardD=M.getNumDistinctWhr(ViewnameQ, Whr2);
                                textData.add(ViewnameD);
                                textData.add(deltaCard);
                                double RunCost= g.ComputeDimApprCost(  M, ViewnameD, Func, Table,  sizeQ , DoubeCardQ, sizeD, DoubeCardD);
                                double DistDimCost=g.ComputeDimDistceCost (M,"L2 Norm", ViewnameD, Func, Table, DoubeCardQ, DoubeCardD); 
                                textData.add(deltaCard/(RunCost+DistDimCost));
                                textData.add(RunCost);
                                textData.add(DistDimCost);
//                                textData.add(totaldif);
//                                textData.add(LenDMin);
//                                textData.add(LenMax);
            model.addRow(textData );   }                 
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
        
        }
        Vector DimsunClustred_data = model.getDataVector();
        Collections.sort(DimsunClustred_data, new ColumnSorter(2, false));
    }catch(Exception e){
                e.printStackTrace();
                }
        
        //model.fireTableDataChanged();
        return model;
        }
    
   
   //Not Used anymore
 
   //Tested
   public   List<Attribute> AlgoSela_Eq1( List<Attribute>  columnAttributes,int K,String tble,String Wher1,String Wher2, String Fun, int Cost){
       Metadata mg = new Metadata(tble, 
				"postgresql", "127.0.0.1/postgres", "postgres", "himos");
       List<Attribute> dimensionAttributes= columnAttributes;
       try{
        int sizeD=mg.getNumRowsWhre(Wher2);
        int sizeQ=mg.getNumRowsWhre(Wher1);
        //Optimizing Getting Distinct Values
       // DefaultTableModel DimsD=mg.getDim_GroupSelectivityList(columnAttributes,0,Wher2);
        DefaultTableModel DimsD=mg.getDim_GroupSelectivityList_Modified(columnAttributes,Wher2); 
        long start = System.currentTimeMillis();
        //DefaultTableModel DimsQ=mg.getDim_GroupSelectivityList(columnAttributes,0,Wher1);
        DefaultTableModel DimsQ=mg.getDim_GroupSelectivityList_Modified(columnAttributes,Wher1);
        
        dimensionAttributes=Sela_Eq1(mg,tble,Fun,DimsD,DimsQ,Wher1,Wher2,K,Cost,sizeD,sizeQ);
    
        
      //  Me(DimsD,DimsQ,Wher,K);
        long Stop = System.currentTimeMillis()-start;
       System.out.println("Sela Eq1 Execution:"+Stop);
       
       }catch(Exception e){
       e.printStackTrace();;
       }
       return dimensionAttributes;
   }   
   
   
   
   public   List<Attribute> AlgoSela_Eq3( List<Attribute>  columnAttributes,int K, String tble,String Wher1,String Wher2, String Fun, int Cost){
   
       Metadata mg = new Metadata(tble, 
				"postgresql", "127.0.0.1/postgres", "postgres", "himos");
        
        DefaultTableModel DimsD=mg.getDim_GroupSelectivityList_Modified(columnAttributes,Wher2);
          DimsD=normalizeNforDims(DimsD);
        List<Attribute> dimensionAttributes=null;
        try{
        int sizeD=mg.getNumRowsWhre(Wher2);
        int sizeQ=mg.getNumRowsWhre(Wher1);
        
        long start = System.currentTimeMillis();
        DefaultTableModel DimsQ=mg.getDim_GroupSelectivityList_Modified(columnAttributes,Wher1);
         DimsQ=normalizeNforDims(DimsQ);
        //DefaultTableModel  DimsQNormalized=normalizeNforDims(DimsQ);
          dimensionAttributes=Sela_Eq3(mg,tble,Fun,DimsD,DimsQ,Wher1,Wher2,K, sizeD,sizeQ,Cost);
    
        
     
        long Stop = System.currentTimeMillis()-start;
       System.out.println("Sela N-N' Execution:"+Stop);
      
       }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return dimensionAttributes;
   }    
   
   public DefaultTableModel normalizeNforDimsOrg (DefaultTableModel t) {
       
       double sum=0;
       for (int i=0;i<t.getRowCount();i++){
           sum=sum + Double.parseDouble( t.getValueAt(i, 1).toString());
       }
       
       for (int i=0;i<t.getRowCount();i++){
           double CurrVal=Double.parseDouble( t.getValueAt(i, 1).toString())/sum ;
           t.setValueAt(  (CurrVal), i, 1) ;
       }
       return t;
   }
   
   public DefaultTableModel normalizeNforDims1 (DefaultTableModel t) {
       
       double sum=0;
       double minv=Double.parseDouble( t.getValueAt(0, 1).toString());
       double maxv=0;
       for (int i=0;i<t.getRowCount();i++){
           if (Double.parseDouble( t.getValueAt(i, 1).toString())>=maxv) maxv=Double.parseDouble( t.getValueAt(i, 1).toString());
           if (Double.parseDouble( t.getValueAt(i, 1).toString())<=minv) minv=Double.parseDouble( t.getValueAt(i, 1).toString());
           sum=sum + Double.parseDouble( t.getValueAt(i, 1).toString());
       }
       System.out.println(maxv+"\t"+minv);
       for (int i=0;i<t.getRowCount();i++){
           double CurrVal=Double.parseDouble( t.getValueAt(i, 1).toString()) ;
           CurrVal=(CurrVal-minv)/(maxv-minv);
           t.setValueAt(  (CurrVal), i, 1) ;
           System.out.println(CurrVal);
       }
       return t;
   }
   
    public DefaultTableModel normalizeNforDims (DefaultTableModel t) {
       
       double sum=0;
       for (int i=0;i<t.getRowCount();i++){
           sum=sum + Double.parseDouble( t.getValueAt(i, 1).toString());
       }
       sum=sum/t.getRowCount();
       sum=1000000;
       for (int i=0;i<t.getRowCount();i++){
           double CurrVal=Double.parseDouble( t.getValueAt(i, 1).toString())/sum ;
           t.setValueAt(  (CurrVal), i, 1) ;
       }
       return t;
   }
}
