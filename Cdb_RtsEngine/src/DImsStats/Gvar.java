/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DImsStats;

import cdb.Attribute;
import cdb.ExperimentalSettings;
import com.google.common.primitives.Doubles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.distance.EarthMoversDistance;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author uqiibra2
 */
public class Gvar {
    
    public DefaultTableModel getMeasureVar(Metadata m, String Whre,String Whre2){
        DefaultTableModel Mes = new DefaultTableModel();
        
        Mes.addColumn("Measure");
        Mes.addColumn("VarD");
        Mes.addColumn("VarQ");
        Mes.addColumn("NormVarD");
        Mes.addColumn("NormVarQ");
        Mes.addColumn("VarD-VarQ");
        Mes.addColumn("NormVarD-NormVarQ");
        try{
        Map<String, String> measures =m.getMeasureColumn();
        
         for (Map.Entry<String, String> columnAttribute1 : measures.entrySet()){
		
			String name = columnAttribute1.getKey().toString();
                        double varQ=m.getMesVariance(name, Whre);
                        double varD=m.getMesVariance(name, Whre2);
                        double NormvarQ=m.getMesVarianceNormalized(name, Whre,"Sum");
                        double NormvarD=m.getMesVarianceNormalized(name, Whre2,"Sum");
                       
                        Vector textData = new Vector();
                                
                                textData.add(name);
                                textData.add(varD);
                                textData.add(varQ);
                                textData.add(NormvarD);
                                textData.add(NormvarQ);
                                textData.add(Math.abs(varD-varQ));
                                textData.add(Math.abs(NormvarD-NormvarQ));
                                Mes.addRow(textData);
         }
                        
        }catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
        return Mes;
    }
    
    public DefaultTableModel getMeasureVarNorm(Metadata m, String Whre,String Whre2){
        DefaultTableModel Mes = new DefaultTableModel();
        
        Mes.addColumn("Measure");
//        Mes.addColumn("VarD");
//        Mes.addColumn("VarQ");
        Mes.addColumn("NormVarD");
        Mes.addColumn("NormVarQ");
//        Mes.addColumn("VarD-VarQ");
        Mes.addColumn("NormVarD+NormVarQ");
        try{
        Map<String, String> measures =m.getMeasureColumn();
        
         for (Map.Entry<String, String> columnAttribute1 : measures.entrySet()){
		
			String name = columnAttribute1.getKey().toString();
//                        double varQ=m.getMesVariance(name, Whre);
//                        double varD=m.getMesVariance(name, Whre2);
                        double NormvarQ=m.getMesVarianceNormalized(name, Whre,"Sum");
                        double NormvarD=m.getMesVarianceNormalized(name, Whre2,"Sum");
                       
                        Vector textData = new Vector();
                                
                                textData.add(name);
//                                textData.add(varD);
//                                textData.add(varQ);
                                textData.add(NormvarD);
                                textData.add(NormvarQ);
//                                textData.add(Math.abs(varD-varQ));
                                textData.add(Math.abs(NormvarD+NormvarQ));
                                Mes.addRow(textData);
         }
                        
        }catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
        Vector d_data = Mes.getDataVector();
          Collections.sort(d_data, new ColumnSorter(3, false));
        return Mes;
    }
    
    public DefaultTableModel getDimDistinct(Metadata m, String Whre,String Whre2){
        DefaultTableModel Mes = new DefaultTableModel();
        
        Mes.addColumn("Dims");
        Mes.addColumn("N1-D");
        Mes.addColumn("N2-Q");
        Mes.addColumn("Delta N");
        
        try{
        Map<String, String> dims =m.getDimColumn();
        
         for (Map.Entry<String, String> columnAttribute1 : dims.entrySet()){
		
			String name = columnAttribute1.getKey().toString();
                        double varQ=m.getNumDistinctWhr(name, Whre);
                        double varD=m.getNumDistinctWhr(name, Whre2);
                       
                       
                        Vector textData = new Vector();
                                
                                textData.add(name);
                                textData.add(varD);
                                textData.add(varQ);
                                textData.add(varD-varQ);
                                Mes.addRow(textData);
         }
                        
        }catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
//         Vector d_data = Mes.getDataVector();
//         Collections.sort(d_data, new ColumnSorter(2, false));
        return Mes;
    }
    
    
    
    public DefaultTableModel ViewsVar(Metadata m, String table,String Whre,String Whre2){
        DefaultTableModel Mes = new DefaultTableModel();
        
        Mes.addColumn("View");
        Mes.addColumn("VarInD");
        Mes.addColumn("VarInQ");
        Mes.addColumn("Delta N");
        Mes.addColumn("dim");
        
        try{
        Map<String, String> dims =m.getDimColumn();
        Map<String, String> mes =m.getMeasureColumn();
        int size=m.getNumRows();
         for (Map.Entry<String, String> columnAttribute1 : dims.entrySet()){
		
			String dim = columnAttribute1.getKey().toString();
                        for (Map.Entry<String, String> MesAttribute1 : mes.entrySet()){
                            String measure=MesAttribute1.getKey().toString();
                            String SqlQuery="Select "+dim+",Sum(" +measure+") from "+table+" "+Whre+" group by "+dim;
                            ResultSet rs=m.ExecuteQryWithRS(SqlQuery);
                            
                           double [] data= new double[size+1];
                           int i=0;
                           while(rs.next()){
                               data[i]=rs.getDouble(2); i++;
                            }
                           rs.close();
                           i=0;
                           data=StatUtils.normalize(data);
                            double VarQ=StatUtils.variance(data);
                            
                            SqlQuery="Select "+dim+",Sum(" +measure+") from "+table+" "+Whre2+" group by "+dim;
                            rs=m.ExecuteQryWithRS(SqlQuery);
                            while(rs.next()){
                               data[i]=rs.getDouble(2); i++;
                            }
                            rs.close();
                            data=StatUtils.normalize(data);
                            double VarD=StatUtils.variance(data);
                            Vector textData = new Vector();
                           
                                textData.add(dim+"__"+measure);
                                textData.add(VarD);
                                textData.add(VarQ);
                                textData.add(VarD-VarQ);
                                textData.add(dim);
                                Mes.addRow(textData);
                        }
                        
                       
                       
                        
         }
                        
        }catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
        return Mes;
    }
    
    public DefaultTableModel ViewsEq(DefaultTableModel views, DefaultTableModel dims){
        DefaultTableModel Mes = new DefaultTableModel();
        
        Mes.addColumn("View");
        Mes.addColumn("Utility");
//        Mes.addColumn("VarInQ");
//        Mes.addColumn("Delta N");
//        Mes.addColumn("dim");
        
        try{
        
         for (int i=0;i<dims.getRowCount();i++){
		
			String dim = dims.getValueAt(i, 0).toString();
                               double nD=Double.parseDouble(dims.getValueAt(i, 1).toString());
                               double nq=Double.parseDouble(dims.getValueAt(i, 2).toString());
                        for (int j=0;j<views.getRowCount();j++){
                            String viewdim=views.getValueAt(j, 4).toString();
                            if(dim.equals(viewdim )){
                               double varD=Double.parseDouble(views.getValueAt(j, 1).toString());
                               double varQ=Double.parseDouble(views.getValueAt(j, 2).toString());
                               double Vutil=(nq*Math.abs(varD+varQ))+((nD-nq)*varD);
                               //double Vutil=(nq*(varQ/varD))+((nD-nq)*varD);
                               Vector textData = new Vector();
                               textData.add(views.getValueAt(j, 0).toString());
                               textData.add(Vutil);
                               Mes.addRow(textData);
                            }
                            
                        }
         }
                               
        }catch (Exception e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
         Vector d_data = Mes.getDataVector();
          Collections.sort(d_data, new ColumnSorter(1, false));
        return Mes;
    }
    
    public DefaultTableModel SuggestedViewsEq(DefaultTableModel Mesuers, DefaultTableModel dims) {
        DefaultTableModel Mes = new DefaultTableModel();
        
        Mes.addColumn("View");
        Mes.addColumn("Utility");
        Mes.addColumn("Dim");
        Mes.addColumn("Measure");
//        Mes.addColumn("dim");
        
        try{
        
         for (int i=0;i<dims.getRowCount();i++){
                        double DimPriority=0.0;
			String dim = dims.getValueAt(i, 0).toString();
                               double nD=Double.parseDouble(dims.getValueAt(i, 1).toString());
                               double nq=Double.parseDouble(dims.getValueAt(i, 2).toString());
                        for (int j=0;j<Mesuers.getRowCount();j++){
                            String Mesname=Mesuers.getValueAt(j, 0).toString();
                          
                               double varD=Double.parseDouble(Mesuers.getValueAt(j, 1).toString());
                               double varQ=Double.parseDouble(Mesuers.getValueAt(j, 2).toString());
                               //double Vutil=(nq*Math.abs(varD-varQ))+((nD-nq)*varD);
                               
                               double Vutil=0;
                               if (nq>nD) Vutil=(nD*Math.abs(varD+varQ))+(Math.abs(nD-nq)*varQ);
                               else Vutil=(nq*Math.abs(varD+varQ))+(Math.abs(nD-nq)*varD);
                               //double Vutil=(nq*(varQ/varD))+((nD-nq)*varD);
                               DimPriority=DimPriority+Vutil;
                               Vector textData = new Vector();
                               textData.add(dim+"__"+Mesname);
                               textData.add(Vutil);
                               textData.add(dim);
                               textData.add(Mesname);
                               Mes.addRow(textData);
                           
                            
                        }
                        Vector textData = new Vector();
                               textData.add(dim+"__Total");
                               textData.add(DimPriority/dims.getRowCount());
                               textData.add(dim+"__Total");
                               textData.add(dim+"__Total");
                               Mes.addRow(textData);
                           
         }
                               
        }catch (Exception e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
         Vector d_data = Mes.getDataVector();
          Collections.sort(d_data, new ColumnSorter(1, false));
        return Mes;
    }
    
   
    /*
    public double ComputeSpaceUtil(DefaultTableModel Mesuers, DefaultTableModel dims, int Topd, int Topm){
        
            
        for(int i=0;i<=Topd;i++){
                
            }
    }
    
    */
    // The Main Algorithm 
    public  void  Gvari( List<Attribute>  DimsAttributes,List<Attribute>  MeasureAttributesnt, double K, String tble,String WherQ,String WherD) {
   
       Metadata mg = new Metadata(tble, 
				"postgresql", "127.0.0.1/postgres", "postgres", "himos");
       
       long start= System.currentTimeMillis();
       DefaultTableModel Mestats=getMeasureVarNormList(mg,MeasureAttributesnt, WherQ,WherD);
       int MaxMes=(int) Math.round( K*Mestats.getRowCount()/100);
       if (WherD.equalsIgnoreCase("where ")) WherD="";
       MeasureAttributesnt.clear();
       for (int i=0;i<MaxMes;i++){
           try {
		MeasureAttributesnt.add(new Attribute(Mestats.getValueAt(i, 0).toString()));
                System.out.println("Mes :"+Mestats.getValueAt(i, 0).toString()+" Selected"); 
		} catch (NumberFormatException e) {
			MeasureAttributesnt.add(new Attribute(Mestats.getValueAt(i, 0).toString()));
		}
       }
       DefaultTableModel Dimstats=getDimDistinctList(mg, DimsAttributes,WherQ,WherD);
       DimsAttributes.clear();
       DefaultTableModel Res=SuggestedViewsEq(Mestats, Dimstats);
       
       int MaxDims=(int) Math.round( K*Dimstats.getRowCount()/100);
       int count=0;
       for (int i=0;i<Res.getRowCount();i++){
            //Get Correlation Value
            String  dimTot=  Res.getValueAt(i, 2).toString();
            if (dimTot.endsWith("__Total")){
                dimTot=dimTot.substring(0, dimTot.length()-7);
                System.out.println("Dim :"+dimTot+" Selected"); 
                try {
		DimsAttributes.add(new Attribute(dimTot));
		} catch (NumberFormatException e) {
			DimsAttributes.add(new Attribute(dimTot));
		}
                count++;
                if(count>=MaxDims) break;
            }
                    
    }
       System.out.println("Total Dimension Count: "+Dimstats.getRowCount()+"  Total Measures Count: "+Mestats.getRowCount());
       System.out.println("Optimized Dimension Count: "+MaxDims+"  Optimized Measures Count: "+(MaxMes));
       System.out.println("Gvari Execution Time: "+(System.currentTimeMillis()-start));
}
    
    public DefaultTableModel getDimDistinctList(Metadata m, List<Attribute>  DimsAttributes,String Whre,String Whre2){
        DefaultTableModel Mes = new DefaultTableModel();
        
        Mes.addColumn("Dims");
        Mes.addColumn("N1-D");
        Mes.addColumn("N2-Q");
        //Mes.addColumn("Delta N");
        
        try{
       // Map<String, String> dims =m.getDimColumn();
        
         for (int i=0;i<DimsAttributes.size();i++){
		
			String name = DimsAttributes.get(i).name;
                        double varQ=m.getNumDistinctWhr(name, Whre);
                        double varD=m.getNumDistinctWhr(name, Whre2);
                       
                       
                        Vector textData = new Vector();
                                
                                textData.add(name);
                                textData.add(varD);
                                textData.add(varQ);
                              //  textData.add(varD-varQ);
                                Mes.addRow(textData);
         }
                        
        }catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
//         Vector d_data = Mes.getDataVector();
//         Collections.sort(d_data, new ColumnSorter(2, false));
        return Mes;
    }
    
    public DefaultTableModel getMeasureVarNormList(Metadata m,List<Attribute>  MeasureAttributesnt ,String Whre,String Whre2){
        DefaultTableModel Mes = new DefaultTableModel();
        
        Mes.addColumn("Measure");
//        Mes.addColumn("VarD");
//        Mes.addColumn("VarQ");
        Mes.addColumn("NormVarD");
        Mes.addColumn("NormVarQ");
//        Mes.addColumn("VarD-VarQ");
        Mes.addColumn("NormVarD+NormVarQ");
        try{
        //Map<String, String> measures =m.getMeasureColumn();
        
         for (int i=0;i<MeasureAttributesnt.size();i++){
		
			String name = MeasureAttributesnt.get(i).name;
//                        double varQ=m.getMesVariance(name, Whre);
//                        double varD=m.getMesVariance(name, Whre2);
                        double NormvarQ=m.getMesVarianceNormalized(name, Whre,"Sum");
                        double NormvarD=m.getMesVarianceNormalized(name, Whre2,"Sum");
                       
                        Vector textData = new Vector();
                                
                                textData.add(name);
//                                textData.add(varD);
//                                textData.add(varQ);
                                textData.add(NormvarD);
                                textData.add(NormvarQ);
//                                textData.add(Math.abs(varD-varQ));
                                textData.add(Math.abs(NormvarD+NormvarQ));
                                Mes.addRow(textData);
         }
                        
        }catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
        Vector d_data = Mes.getDataVector();
          Collections.sort(d_data, new ColumnSorter(3, false));
        return Mes;
    }
    
    // Testing Histogram
    public  void  GvariHisto( List<Attribute>  DimsAttributes, double K, String tble,String WherQ,String WherD, ExperimentalSettings.DistanceMetric Metric, String Fun,int cost) {
   
       Metadata mg = new Metadata(tble, 
				"postgresql", "127.0.0.1/postgres", "postgres", "himos");
        DefaultTableModel dims= new  DefaultTableModel();
        try{
        double sizeQ=mg.getNumRowsWhre(WherQ);
        double sizeD=mg.getNumRowsWhre(WherD);
       long start= System.currentTimeMillis();
       if (cost==0){
           System.out.println("Computing No Costs.....");
        dims=ComputeDimsHistgorams(mg,DimsAttributes,tble, WherQ,WherD,  Metric);
       }
       else if (cost==1) {
            System.out.println("Computing Actual Costs.....");
            dims=ComputeDimsHistgorams_ActualCost(mg,DimsAttributes,Fun,tble, WherQ,WherD,  Metric,sizeQ,sizeD);
       }
        else if (cost==2){
            System.out.println("Computing Estimating Costs.....");
             dims=ComputeDimsHistgorams_DBEstCost(mg,DimsAttributes,Fun,tble, WherQ,WherD,  Metric,sizeQ,sizeD);
        }
       else if (cost==3){
            System.out.println("Computing ApproX Cost Model.....");
             dims=ComputeDimsHistgorams_AprroXCostModel(mg,DimsAttributes,Fun,tble, WherQ,WherD,  Metric,sizeQ,sizeD);
        }
       
       DimsAttributes.clear();
       if(K>dims.getRowCount()) K=dims.getRowCount();
       double spaceCost=0;
        double spaceProfit=0;
        double spacegain=0;
       for (int i=0;i<K;i++){
           try {
		DimsAttributes.add(new Attribute(dims.getValueAt(i, 0).toString()));
               // System.out.println(dims.getValueAt(i, 0).toString());
                 String Dname = dims.getValueAt(i, 0).toString();
             double Pr =Double.parseDouble(dims.getValueAt(i, 1).toString());
                double profit = Double.parseDouble(dims.getValueAt(i, 2).toString());
                 double RunCost = Double.parseDouble(dims.getValueAt(i, 3).toString());
              double DisCost = Double.parseDouble(dims.getValueAt(i, 4).toString());
               System.out.println(Dname+" Pr:"+Pr +" Profit:"+profit+" RunCost:"+RunCost+" DistCost:"+DisCost);
               spaceProfit=spaceProfit+profit;
            spaceCost=spaceCost+RunCost+DisCost;
            spacegain=spacegain+Pr;
		} catch (NumberFormatException e) {
			DimsAttributes.add(new Attribute(dims.getValueAt(i, 0).toString()));
		}
       
       
                    
    }
        System.out.println("Total Space Profit: "+spaceProfit+" Total Space gain: "+spacegain+" Total Space Cost: "+spaceCost);
       System.out.println("Gvari-Histo Execution Time: "+(System.currentTimeMillis()-start));
    }catch(Exception e){
    e.printStackTrace();
    }
}
    
     
   
    
    public DefaultTableModel ComputeDimsHistgorams( Metadata M,List<Attribute>  DimsAttributes,String Table, String Whr1, String Whr2, ExperimentalSettings.DistanceMetric Metric){
        DefaultTableModel Dims = new DefaultTableModel();
        
        Dims.addColumn("Dim");
        Dims.addColumn("Distance");
        Dims.addColumn("Distance");
        Dims.addColumn("Distance");
        Dims.addColumn("Distance");
         try{
       for (int i=0;i<DimsAttributes.size();i++){
		
			String name = DimsAttributes.get(i).name;
		
			
                        ResultSet Rs1 =M.ExecuteQryWithRS("Select count("+name+") from "+Table+Whr1+" group by "+ name);
                        ResultSet Rs2 =M.ExecuteQryWithRS("Select count("+name+") from "+Table+Whr2+" group by "+ name);
                        double [] r1=covertToDoublrArry(Rs1,1);
                        double [] r2=covertToDoublrArry(Rs2,1);
                        double dis=0;
                        if (Metric==ExperimentalSettings.DistanceMetric.EARTH_MOVER_DISTANCE){
                        EarthMoversDistance EMD= new EarthMoversDistance();
                        dis=EMD.compute(r1, r2);
                       // dis=Classic_EMD(r1,r2);
                        }
                        else {
                        EuclideanDistance ED= new EuclideanDistance();
                        
                          dis=ED.compute(r1, r2);
                        }
                       
//                        double dis=ED.compute(r1, r2);
                       System.out.println("Dim: "+name+" Distance: "+dis );
                        Vector textData = new Vector();
                                
                                textData.add(name);
                                textData.add(dis);
                                textData.add("0");
                                textData.add("0");
                                textData.add("0");
                                
                                Dims.addRow(textData);
         }
        
         }catch (Exception e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			
		}
          Vector d_data = Dims.getDataVector();
          Collections.sort(d_data, new ColumnSorter(1, false));
         return Dims;
        
    }
    
     // First Algo with Actual Cost
    public DefaultTableModel ComputeDimsHistgorams_ActualCost( Metadata M,List<Attribute>  DimsAttributes,String ZFun,String Table, String Whr1, String Whr2, ExperimentalSettings.DistanceMetric Metric, double sizeQ,double sizeD){
        DefaultTableModel Dims = new DefaultTableModel();
        
        Dims.addColumn("Dim");
        Dims.addColumn("Distance");
        Dims.addColumn("Cost");
        Dims.addColumn("Ut");
             Dims.addColumn("Ut");
        
         try{
       for (int i=0;i<DimsAttributes.size();i++){
		
			String name = DimsAttributes.get(i).name;
                        double nD=M.getNumDistinctWhr(name, Whr2);
                        double nDQ=M.getNumDistinctWhr(name, Whr1);
			double RunCost=ComputeDimActualCost(M,name,ZFun,Table,Whr1,Whr2);
                        double DistanCost= ComputeDimDistceCost (M,"L2 Norm",name,ZFun,Table,nDQ,nD);
                        double TotaCost=RunCost+DistanCost;
                        ResultSet Rs1 =M.ExecuteQryWithRS("Select count("+name+") from "+Table+Whr1+" group by "+ name);
                        ResultSet Rs2 =M.ExecuteQryWithRS("Select count("+name+") from "+Table+Whr2+" group by "+ name);
                        double [] r1=covertToDoublrArry(Rs1,1);
                        double [] r2=covertToDoublrArry(Rs2,1);
                        double dis=0;
                        if (Metric==ExperimentalSettings.DistanceMetric.EARTH_MOVER_DISTANCE){
                        EarthMoversDistance EMD= new EarthMoversDistance();
                        dis=EMD.compute(r1, r2);
                        }
                        else {
                        EuclideanDistance ED= new EuclideanDistance();
                        
                          dis=ED.compute(r1, r2);
                        }
                       
//                        double dis=ED.compute(r1, r2);
                     //  System.out.println("Dim: "+name+"\t Distance: "+dis+"\t Profit:"+(dis/TotaCost)+"\t Actual Run Cost: "+RunCost +"\t Distance Cost: "+DistanCost);
                        Vector textData = new Vector();
                                
                                textData.add(name);
                                textData.add(dis);
                                textData.add(dis/TotaCost);
                                textData.add(RunCost);
                                textData.add(DistanCost);
                                
                                Dims.addRow(textData);
         }
        
         }catch (Exception e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			
		}
          Vector d_data = Dims.getDataVector();
          Collections.sort(d_data, new ColumnSorter(2, false));
         return Dims;
        
    }
    
    
    public DefaultTableModel ComputeDimsHistgorams_DBEstCost( Metadata M,List<Attribute>  DimsAttributes,String ZFun,String Table, String Whr1, String Whr2, ExperimentalSettings.DistanceMetric Metric, double sizeQ,double sizeD){
        DefaultTableModel Dims = new DefaultTableModel();
        
        Dims.addColumn("Dim");
        Dims.addColumn("Distance");
        Dims.addColumn("Cost");
        Dims.addColumn("Ut");
             Dims.addColumn("Ut");
        
         try{
       for (int i=0;i<DimsAttributes.size();i++){
		
			String name = DimsAttributes.get(i).name;
                        double nD=M.getNumDistinctWhr(name, Whr2);
                        double nDQ=M.getNumDistinctWhr(name, Whr1);
			double RunCost=ComputeDimDBEstCost(M,name,ZFun,Table,Whr1,Whr2);
                        double DistanCost= ComputeDimDistceCost (M,"L2 Norm",name,ZFun,Table,nDQ,nD);
                        double TotaCost=RunCost+DistanCost;
                        ResultSet Rs1 =M.ExecuteQryWithRS("Select count("+name+") from "+Table+Whr1+" group by "+ name);
                        ResultSet Rs2 =M.ExecuteQryWithRS("Select count("+name+") from "+Table+Whr2+" group by "+ name);
                        double [] r1=covertToDoublrArry(Rs1,1);
                        double [] r2=covertToDoublrArry(Rs2,1);
                        double dis=0;
                        if (Metric==ExperimentalSettings.DistanceMetric.EARTH_MOVER_DISTANCE){
                        EarthMoversDistance EMD= new EarthMoversDistance();
                        dis=EMD.compute(r1, r2);
                        }
                        else {
                        EuclideanDistance ED= new EuclideanDistance();
                        
                          dis=ED.compute(r1, r2);
                        }
                       
//                        double dis=ED.compute(r1, r2);
                     //  System.out.println("Dim: "+name+"\t Distance: "+dis+"\t Profit:"+(dis/TotaCost)+"\t Actual Run Cost: "+RunCost +"\t Distance Cost: "+DistanCost);
                        Vector textData = new Vector();
                                
                                textData.add(name);
                                textData.add(dis);
                                textData.add(dis/TotaCost);
                                textData.add(RunCost);
                                textData.add(DistanCost);
                                
                                Dims.addRow(textData);
         }
        
         }catch (Exception e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			
		}
          Vector d_data = Dims.getDataVector();
          Collections.sort(d_data, new ColumnSorter(2, false));
         return Dims;
        
    }
    
    public DefaultTableModel ComputeDimsHistgorams_AprroXCostModel( Metadata M,List<Attribute>  DimsAttributes,String ZFun,String Table, String Whr1, String Whr2, ExperimentalSettings.DistanceMetric Metric, double sizeQ,double sizeD){
        DefaultTableModel Dims = new DefaultTableModel();
        
        Dims.addColumn("Dim");
        Dims.addColumn("Distance");
        Dims.addColumn("Cost");
        Dims.addColumn("Ut");
             Dims.addColumn("Ut");
        
         try{
       for (int i=0;i<DimsAttributes.size();i++){
		
			String name = DimsAttributes.get(i).name;
                        double nD=M.getNumDistinctWhr(name, Whr2);
                        double nDQ=M.getNumDistinctWhr(name, Whr1);
			//double RunCost=ComputeDimDBEstCost(M,name,ZFun,Table,Whr1,Whr2);
                        double RunCost= ComputeDimApprCost(  M, name, ZFun, Table,  sizeQ , nDQ, sizeD, nD);
                        double DistanCost= ComputeDimDistceCost (M,"L2 Norm",name,ZFun,Table,nDQ,nD);
                        double TotaCost=RunCost+DistanCost;
                        ResultSet Rs1 =M.ExecuteQryWithRS("Select count("+name+") from "+Table+Whr1+" group by "+ name);
                        ResultSet Rs2 =M.ExecuteQryWithRS("Select count("+name+") from "+Table+Whr2+" group by "+ name);
                        double [] r1=covertToDoublrArry(Rs1,1);
                        double [] r2=covertToDoublrArry(Rs2,1);
                        double dis=0;
                        if (Metric==ExperimentalSettings.DistanceMetric.EARTH_MOVER_DISTANCE){
                        EarthMoversDistance EMD= new EarthMoversDistance();
                        dis=EMD.compute(r1, r2);
                        }
                        else {
                        EuclideanDistance ED= new EuclideanDistance();
                        
                          dis=ED.compute(r1, r2);
                        }
                       
//                        double dis=ED.compute(r1, r2);
                     //  System.out.println("Dim: "+name+"\t Distance: "+dis+"\t Profit:"+(dis/TotaCost)+"\t Actual Run Cost: "+RunCost +"\t Distance Cost: "+DistanCost);
                        Vector textData = new Vector();
                                
                                textData.add(name);
                                textData.add(dis);
                                textData.add(dis/TotaCost);
                                textData.add(RunCost);
                                textData.add(DistanCost);
                                
                                Dims.addRow(textData);
         }
        
         }catch (Exception e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			
		}
          Vector d_data = Dims.getDataVector();
          Collections.sort(d_data, new ColumnSorter(2, false));
         return Dims;
        
    }
    // First Algo with DBEst Cost
    
    
    public double[] covertToDoublrArry(ResultSet a, int normalize){
        List<Double> ColVal=new ArrayList<Double>();
         double sum=0;
        try{
         
         
        
        
         while (a.next()){
         //for (int i=0; i<a.length;i++){
            ColVal.add( a.getDouble(1));
            sum=sum+a.getDouble(1);
        }
         
         }
        catch(Exception e){
             e.printStackTrace();
         }
         double Arr[]= Doubles.toArray(ColVal) ;
         if (normalize==1){
        for (int j=0;j<Arr.length;j++){
            Arr[j]=Arr[j]/sum;
            }    
        }
        
         return Arr;
    }
    
    public double[] covertToDoublrArry_n(ResultSet a, int normalize){
        List<Double> ColVal=new ArrayList<Double>();
         double sum=0;
         double min=0;
         double max=0;
        try{
         
         
        
        
         while (a.next()){
         //for (int i=0; i<a.length;i++){
             double temp=a.getDouble(1);
            ColVal.add( temp);
            if (temp<=min) min=temp;
            else if(temp>max) max=temp;
            
            sum=sum+a.getDouble(1);
        }
         
         }
        catch(Exception e){
             e.printStackTrace();
         }
         double Arr[]= Doubles.toArray(ColVal) ;
         if (normalize==1){
        for (int j=0;j<Arr.length;j++){
            Arr[j]=(Arr[j]-min)/(max-min);
           // Arr[j]=Arr[j]/sum;
            }    
        }
        
         return Arr;
    }
    
// Start Computing Costs 
    
    public double ComputeDimActualCost( Metadata M,String DimAtt,String Func,String Table, String Whr1, String Whr2){
        double ExeTime=0;
         try{
              Map<String, String> Mes =M.getMeasureColumn();
             
              if (Func.equalsIgnoreCase("All")){
              
            for (int i=1;i<=5;i++){
                if(i==1)      Func="Count";
                else if(i==2) Func="Sum";
                else if(i==3) Func="Avg";
                else if(i==4) Func="min";
                else if(i==5) Func="max";
        
		
			
                        for (Map.Entry<String, String> MesAttribute1 : Mes.entrySet()){
                            String Mname = MesAttribute1.getKey().toString();
                            long startQ=System.currentTimeMillis();
                        ResultSet Rs1 =M.ExecuteQryWithRS("Select "+Func+"("+Mname+"),"+DimAtt+" from "+Table+" "+Whr1+" group by "+ DimAtt);
                            long StopQ=System.currentTimeMillis()-startQ;
                            long startD=System.currentTimeMillis();
                        ResultSet Rs2 =M.ExecuteQryWithRS("Select "+Func+"("+Mname+"),"+DimAtt+" from "+Table+" "+Whr2+" group by "+ DimAtt);
                            long StopD=System.currentTimeMillis()-startD;
                       ExeTime=ExeTime+StopQ+StopD;
                       
                        }
                     
                 }
         } else
              {
                  for (Map.Entry<String, String> MesAttribute1 : Mes.entrySet()){
                            String Mname = MesAttribute1.getKey().toString();
                            long startQ=System.currentTimeMillis();
                        ResultSet Rs1 =M.ExecuteQryWithRS("Select "+Func+"("+Mname+"),"+DimAtt+" from "+Table+" "+Whr1+" group by "+ DimAtt);
                            long StopQ=System.currentTimeMillis()-startQ;
                            long startD=System.currentTimeMillis();
                        ResultSet Rs2 =M.ExecuteQryWithRS("Select "+Func+"("+Mname+"),"+DimAtt+" from "+Table+" "+Whr2+" group by "+ DimAtt);
                            long StopD=System.currentTimeMillis()-startD;
                       ExeTime=ExeTime+StopQ+StopD;
                  }
              }
        
         }catch (Exception e) {
			System.out.println("Error in Computing Costs of  "+DimAtt);
			e.printStackTrace();
			
		}
          
         return ExeTime;
        
    }
    
    public double ComputeViewActualRunCost( Metadata M,String DimAtt,String Mesure,String Func,String Table, String Whr1, String Whr2){
        double ExeTime=0;
         try{
                     
                         
                 
                            long startQ=System.currentTimeMillis();
                        ResultSet Rs1 =M.ExecuteQryWithRS("Select "+Func+"("+Mesure+"),"+DimAtt+" from "+Table+" "+Whr1+" group by "+ DimAtt);
                            long StopQ=System.currentTimeMillis()-startQ;
                            long startD=System.currentTimeMillis();
                        ResultSet Rs2 =M.ExecuteQryWithRS("Select "+Func+"("+Mesure+"),"+DimAtt+" from "+Table+" "+Whr2+" group by "+ DimAtt);
                            long StopD=System.currentTimeMillis()-startD;
                       ExeTime=ExeTime+StopQ+StopD;
                  
              
        
         }catch (Exception e) {
			System.out.println("Error in Computing Costs of  "+DimAtt);
			e.printStackTrace();
			
		}
          
         return ExeTime;
        
    }
    
    public double ComputeViewActualDBRunCost( Metadata M,String DimAtt,String Mesure,String Func,String Table, String Whr1, String Whr2){
        double ExeTime=0;
         try{
                     
                         
                 
                            
                        ResultSet Rs1 =M.ExecuteQryWithRS("Explain  Select "+Func+"("+Mesure+"),"+DimAtt+" from "+Table+" "+Whr1+" group by "+ DimAtt);
                            double StopQ=getEstDBtimeV2(Rs1);
                            
                        ResultSet Rs2 =M.ExecuteQryWithRS("Explain  Select "+Func+"("+Mesure+"),"+DimAtt+" from "+Table+" "+Whr2+" group by "+ DimAtt);
                            double StopD=getEstDBtimeV2(Rs2);
                       ExeTime=StopQ+StopD;
                  
              
        
         }catch (Exception e) {
			System.out.println("Error in Computing Costs of  "+DimAtt);
			e.printStackTrace();
			
		}
          
         return ExeTime;
        
    }
    
    public double ComputeViewActualDistCost( Metadata M,String DistMes,String DimAtt,String Mesure,String Func,String Table, String Whr1, String Whr2){
        double ExeTime=0;
        double dt=0.000001;
         try{
                         double DvalDQ=M.getNumDistinctWhr(DimAtt, Whr1);
                         double DvalD= M.getNumDistinctWhr(DimAtt, Whr2);
                         double lage=0;
                         if(DvalDQ>DvalD) lage=DvalDQ;
                         else lage=DvalD;
                        double costD=0;    
                        if  (DistMes.equalsIgnoreCase("EARTH_MOVER_DISTANCE")) costD=dt*Math.pow(lage, 3);
                        else
                            costD=dt*Math.pow(lage, 1);
                            
                       ExeTime=costD;
                  
              
        
         }catch (Exception e) {
			System.out.println("Error in Computing Costs of  "+DimAtt);
			e.printStackTrace();
			
		}
          
         return ExeTime;
        
    }
    
    public double ComputeDimDistceCost( Metadata M,String DistMes,String DimAtt,String Func,String Table, double DvalDQ, double DvalD){
        
        double ExeTime=0;
        double dt=0.000001;
        double lage=0;
                         if(DvalDQ>DvalD) lage=DvalDQ;
                         else lage=DvalD;
                         
         try{
                          Map<String, String> Mes =M.getMeasureColumn();
                          double DimDisCost=0;
                           if (Func.equalsIgnoreCase("All")){
                               
			
                                    for (Map.Entry<String, String> MesAttribute1 : Mes.entrySet()){
                                        
                                        double viewDisCost=0;
                                        if  (DistMes.equalsIgnoreCase("EARTH_MOVER_DISTANCE")) viewDisCost=5*dt*Math.pow(lage, 3);
                                        else         viewDisCost=5*dt*Math.pow(lage, 1);

                                        DimDisCost=DimDisCost+viewDisCost;
                                    }
                             }
                           else
                           {
                               
                                    for (Map.Entry<String, String> MesAttribute1 : Mes.entrySet()){
                                        
                                        double viewDisCost=0;
                                        if  (DistMes.equalsIgnoreCase("EARTH_MOVER_DISTANCE")) viewDisCost=1*dt*Math.pow(lage, 3);
                                        else         viewDisCost=1*dt*Math.pow(lage, 1);

                                        DimDisCost=DimDisCost+viewDisCost;
                                    }
                           }
                       
                         
                      ExeTime =DimDisCost;
                  
              
        
         }catch (Exception e) {
			System.out.println("Error in Computing Costs of  "+DimAtt);
			e.printStackTrace();
			
		}
          
         return ExeTime;
        
    }
    
    public double ComputeDimDBEstCost( Metadata M,String DimAtt,String Func,String Table, String Whr1, String Whr2){
        double ExeTime=0;
         try{
              Map<String, String> Mes =M.getMeasureColumn();
             
              if (Func.equalsIgnoreCase("All")){
              
            for (int i=1;i<=5;i++){
                if(i==1)      Func="Count";
                else if(i==2) Func="Sum";
                else if(i==3) Func="Avg";
                else if(i==4) Func="min";
                else if(i==5) Func="max";
        
                        
		
			
                        for (Map.Entry<String, String> MesAttribute1 : Mes.entrySet()){
                            String Mname = MesAttribute1.getKey().toString();
                           
                        ResultSet Rs1 =M.ExecuteQryWithRS("Explain Select "+Func+"("+Mname+"),"+DimAtt+" from "+Table+" "+Whr1+" group by "+ DimAtt);
                            double EstimateTimeQ= getEstDBtimeV2(Rs1);
                            
                        ResultSet Rs2 =M.ExecuteQryWithRS("Explain Select "+Func+"("+Mname+"),"+DimAtt+" from "+Table+" "+Whr2+" group by "+ DimAtt);
                            double EstimateTimeD= getEstDBtimeV2(Rs2);
                       ExeTime=ExeTime+EstimateTimeQ+EstimateTimeD;
                       
                        }
                     
                 }
         } else
              {
                  for (Map.Entry<String, String> MesAttribute1 : Mes.entrySet()){
                            String Mname = MesAttribute1.getKey().toString();
                          ResultSet Rs1 =M.ExecuteQryWithRS("Explain analyze Select "+Func+"("+Mname+"),"+DimAtt+" from "+Table+" "+Whr1+" group by "+ DimAtt);
                            double EstimateTimeQ= getEstDBtime(Rs1);
                            
                        ResultSet Rs2 =M.ExecuteQryWithRS("Explain analyze Select "+Func+"("+Mname+"),"+DimAtt+" from "+Table+" "+Whr2+" group by "+ DimAtt);
                            double EstimateTimeD= getEstDBtime(Rs2);
                        ExeTime=ExeTime+EstimateTimeQ+EstimateTimeD;
                  }
              }
        
         }catch (Exception e) {
			System.out.println("Error in Computing DB Estimated Costs of  "+DimAtt);
			e.printStackTrace();
			
		}
          
         return ExeTime;
        
    }
    
    public double ComputeDimApprCost( Metadata M,String DimAtt,String Func,String Table, double sizeQ ,double DavalQ,double sizeD,double DavalD){
        double ExeTime=0;
         try{
              Map<String, String> Mes =M.getMeasureColumn();
             double AttSize=M.getAttsizeWidth(DimAtt);
             double pagesize=8*1024; 
             double CplxF=0;
             
              if (Func.equalsIgnoreCase("All")){
              
                
                        
		
			
                        for (Map.Entry<String, String> MesAttribute1 : Mes.entrySet()){
                              String Mname = MesAttribute1.getKey().toString();
                              
                             for (int i=1;i<=5;i++){
                                        if(i==1)     { Func="Count"; CplxF=0.02; }
                                        else if(i==2){ Func="Sum"; CplxF=0.022;}
                                        else if(i==3) {Func="Avg"; CplxF=0.042;}
                                        else if(i==4) {Func="min"; CplxF=0.03;}
                                        else if(i==5) { Func="max"; CplxF=0.03;}
                          
                           double MesSize=M.getAttsizeWidth(Mname);
                           
                           double InterMedResultsizeQ= (AttSize+MesSize)* sizeQ;
                           double InterMedResultsizeD= (AttSize+MesSize)* sizeD;
                           double InterResultPagesQ=InterMedResultsizeQ/pagesize;
                           double InterResultPagesD=InterMedResultsizeD/pagesize;
                           // Cost V=Size Intermediate in pages * function Complexity * Number of groups
                           double ApprCostVQ= DavalQ* InterResultPagesQ * CplxF;
                           double ApprCostVD= DavalD* InterResultPagesD * CplxF;
                       ExeTime=ExeTime+ApprCostVD+ApprCostVQ;
                       
                            }
                        }
                     
                 
         } else
              {
                  if( Func.equalsIgnoreCase("Count"))   CplxF=0.02; 
                else if( Func.equalsIgnoreCase("Sum"))  CplxF=0.022;
                else if( Func.equalsIgnoreCase("Avg"))  CplxF=0.042;
                else if( Func.equalsIgnoreCase("min"))  CplxF=0.03;
                else if( Func.equalsIgnoreCase("max"))  CplxF=0.03;
             
                  for (Map.Entry<String, String> MesAttribute1 : Mes.entrySet()){
                      String Mname = MesAttribute1.getKey().toString();
                         double MesSize=M.getAttsizeWidth(Mname);
                           double InterMedResultsizeQ= (AttSize+MesSize)* sizeQ;
                           double InterMedResultsizeD= (AttSize+MesSize)* sizeD;
                           double InterResultPagesQ=InterMedResultsizeQ/pagesize;
                           double InterResultPagesD=InterMedResultsizeD/pagesize;
                           // Cost V=Size Intermediate in pages * function Complexity * Number of groups
                           double ApprCostVQ= DavalQ* InterResultPagesQ * CplxF;
                           double ApprCostVD= DavalD* InterResultPagesD * CplxF;
                       ExeTime=ExeTime+ApprCostVD+ApprCostVQ;
                  }
              }
        
         }catch (Exception e) {
			System.out.println("Error in Computing DB Estimated Costs of  "+DimAtt);
			e.printStackTrace();
			
		}
          
         return ExeTime;
        
    }
    
    
     public double getEstDBtime(ResultSet Rs){
         double EstTimme=-1;
         int i=0;
         try{
         while(Rs.next()){
             i++;
             //System.out .println(Rs.getString(1)); 
             String EstimateTimeQ=Rs.getString(1);
             if (EstimateTimeQ.startsWith("Execution time:")){
                 
                 
                        EstimateTimeQ=EstimateTimeQ.substring(EstimateTimeQ.indexOf("Execution time:")+15, EstimateTimeQ.length()-2);
                        EstTimme=Double.parseDouble(EstimateTimeQ);
                     //   System.out .println("Trimmed "+EstimateTimeQ); 
             }
         }
         }catch (Exception e) {
			System.out.println("Error in Getting Estimated Time ");
			e.printStackTrace();
			//return columnAttributes;
		}
         return EstTimme;
     }
     
     public double getEstDBtimeV2(ResultSet Rs){
         double EstTimme=-1;
        
         try{
      Rs.next();
            
             //System.out .println(Rs.getString(1)); 
             String EstimateTimeQ=Rs.getString(1);
             if (EstimateTimeQ.contains("cost=")){
                 
                 
                        EstimateTimeQ=EstimateTimeQ.substring(EstimateTimeQ.lastIndexOf("..")+2, EstimateTimeQ.lastIndexOf(" ro"));
                        EstTimme=Double.parseDouble(EstimateTimeQ);
                     //   System.out .println("Trimmed "+EstimateTimeQ); 
             }
        /// }
         }catch (Exception e) {
			System.out.println("Error in Getting Estimated Time ");
			e.printStackTrace();
			//return columnAttributes;
		}
         return EstTimme;
     }
     //added 21 Oct 2015
     public double Classic_EMD(double a[],double b[]){
     
         double lastDistance = 0;
                double totalDistance = 0;
                int len_a= a.length;
                int len_b= b.length;
                int len=0;
                if (len_a>len_b) len= len_a;
                else len=len_b;
                double va=0; double vb=0;
		for (int i=0;i<len;i++) {
		//	utility += Math.abs(dist.get(key).datasetValues[0].getValue(func, normalize) - 
		//			dist.get(key).datasetValues[1].getValue(func, normalize));
                    
                    if (i<len_a) va=a[i]; 
                    else va=0;
                    
                    if (i<len_b) vb=b[i]; 
                    else vb=0;
                         final double currentDistance = va+lastDistance-vb;
                                 //(dist.get(key).datasetValues[0].getValue(func, normalize)+ lastDistance) - dist.get(key).datasetValues[1].getValue(func, normalize);
                         totalDistance += FastMath.abs(currentDistance);
                         lastDistance = currentDistance;
		}

		//utility /= 2;
		return totalDistance;
     }

   
     
    
    
}
