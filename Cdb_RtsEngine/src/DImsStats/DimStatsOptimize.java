/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DImsStats;

import DImsStats.ColumnSorter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author himos
 */
public class DimStatsOptimize {
//    int k;
//    boolean Correlation;
//    String tblname;
//    String whr;
//     Map<String, String> DimsAttributes=ComputeOtimizedAttr( k, false,tblname,whr);
//    
    public  DefaultTableModel ComputeViewWeight(DefaultTableModel D,DefaultTableModel M){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("View");
        model.addColumn("Cardnility");
        model.addColumn("Selectvity");
        model.addColumn("Density");
        for(int i=0; i<D.getRowCount();i++){
            for(int j=0; j<M.getRowCount();j++){
                String Viewname=D.getValueAt(i, 0).toString()+"_"+M.getValueAt(j, 0).toString();
                double Card=Double.parseDouble( D.getValueAt(i, 1).toString()) * 
                        Double.parseDouble( M.getValueAt(j, 1).toString());
                double Sel=Double.parseDouble( D.getValueAt(i, 2).toString()) * 
                        Double.parseDouble( M.getValueAt(j, 2).toString());
                double Density=Double.parseDouble( D.getValueAt(i, 3).toString()) *
                        Double.parseDouble( M.getValueAt(j, 3).toString());
                Vector textData = new Vector();
                                textData.add(Viewname);
                                textData.add(Card);
                                textData.add(Sel);
                                textData.add(Density);
                                model.addRow(textData );
                //System.out.println(Viewname+"\t"+Sel );
            }
        }
        return model;
        }
    
     public static DefaultTableModel CompuViewsDiff(DefaultTableModel ViewD,DefaultTableModel ViewQ){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("View");
        model.addColumn("Diff_Cardnility");
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                double Card=Double.parseDouble( ViewD.getValueAt(i, 1).toString()) - 
                        Double.parseDouble( ViewQ.getValueAt(j, 1).toString());
                double Sel=Double.parseDouble( ViewD.getValueAt(i, 2).toString()) - 
                        Double.parseDouble( ViewQ.getValueAt(j, 2).toString());
                double Density=Double.parseDouble( ViewD.getValueAt(i, 3).toString()) - 
                        Double.parseDouble( ViewQ.getValueAt(j, 3).toString());
                Vector textData = new Vector();
                                textData.add(ViewnameD);
                                textData.add(Card);
                                textData.add(Sel);
                                textData.add(Density);
            model.addRow(textData );                    
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
            
        }
        return model;
        }
     
    public static DefaultTableModel CompuDimsDiff(DefaultTableModel ViewD,DefaultTableModel ViewQ,String Whr){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Attribute");
        model.addColumn("Diff_Cardnility");
        model.addColumn("Diff_Selectvity");
        model.addColumn("Diff_Density");
        model.addColumn("Total Divid_Dif");
        model.addColumn("Total total_Dif");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewQ.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
                String ViewnameQ=ViewQ.getValueAt(j, 0).toString();
                if(ViewnameQ.equals(ViewnameD)){
                double deltaCard= Double.parseDouble( ViewD.getValueAt(i, 1).toString()) - 
                        Double.parseDouble( ViewQ.getValueAt(j, 1).toString());
                
                deltaCard=deltaCard/(Double.parseDouble( ViewD.getValueAt(i, 1).toString()) + 
                        Double.parseDouble( ViewQ.getValueAt(j, 1).toString()));
                
                double deltaSel=Math.abs( Double.parseDouble( ViewD.getValueAt(i, 2).toString()) - 
                        Double.parseDouble( ViewQ.getValueAt(j, 2).toString()));
                
                deltaSel=deltaSel/(Double.parseDouble( ViewD.getValueAt(i, 2).toString()) + 
                        Double.parseDouble( ViewQ.getValueAt(j, 2).toString()));
                
                double deltaDensity=Math.abs( Double.parseDouble( ViewD.getValueAt(i, 3).toString()) - 
                        Double.parseDouble( ViewQ.getValueAt(j, 3).toString()));
                
                deltaDensity=deltaDensity/(Double.parseDouble( ViewD.getValueAt(i, 3).toString()) + 
                        Double.parseDouble( ViewQ.getValueAt(j, 3).toString()));
                double totaldif=1/(deltaSel/deltaCard);
                double totaladddif=deltaSel+deltaCard;
                if(!Whr.contains(ViewnameQ)) {
                Vector textData = new Vector();
                                textData.add(ViewnameD);
                                textData.add(deltaCard);
                                textData.add(deltaSel);
                                textData.add(deltaDensity);
                                textData.add(totaldif);
                                textData.add(totaladddif);
            model.addRow(textData );   }                 
                //System.out.println(Viewname+"\t"+Sel );
                }
            }
            
        }
        return model;
        }
    
    public static  Map<String, String> GetOptimzedDims(DefaultTableModel DimsunClustred,DefaultTableModel Clustrd,DefaultTableModel zer0Density, int k, DefaultTableModel ViewsDiff){
        
        int DimsunClustred_size=DimsunClustred.getRowCount();
        int Clustrd_size=Clustrd.getRowCount();
        int zer0Density_size=zer0Density.getRowCount();
        int totalsize=DimsunClustred_size+Clustrd_size+zer0Density_size;
        if (k>totalsize) k=totalsize;
        //Sort data Decending
        Vector DimsunClustred_data = DimsunClustred.getDataVector();
        Collections.sort(DimsunClustred_data, new ColumnSorter(1, false));
        DimsunClustred.fireTableDataChanged();
        
        Vector Clustrd_data = Clustrd.getDataVector();
        Collections.sort(Clustrd_data, new ColumnSorter(1, false));
        Clustrd.fireTableDataChanged();
        
        Vector zer0Density_data = zer0Density.getDataVector();
        Collections.sort(zer0Density_data, new ColumnSorter(1, false));
        zer0Density.fireTableDataChanged();
        // find size of unclustered
        double clusval=10000000+1;
        if (Clustrd_size==0 && zer0Density_size!=0 && DimsunClustred_size!=0) {zer0Density_size=k/2; DimsunClustred_size=k/2;}
        else if(zer0Density_size==0 && DimsunClustred_size!=0  && Clustrd_size!=0) {Clustrd_size=k/2; DimsunClustred_size=k/2; clusval=Double.parseDouble( Clustrd.getValueAt(0, 1).toString())+1 ;}
        else if(DimsunClustred_size==0 && Clustrd_size!=0  && zer0Density_size!=0) {Clustrd_size=k/2; zer0Density_size=k/2; clusval=Double.parseDouble( Clustrd.getValueAt(0, 1).toString())+1 ;}
        
        else if(DimsunClustred_size==0 && Clustrd_size==0  && zer0Density_size!=0) {zer0Density_size=k;}
        else if(DimsunClustred_size==0 && Clustrd_size!=0  && zer0Density_size==0) {Clustrd_size=k; clusval=Double.parseDouble( Clustrd.getValueAt(0, 1).toString())+1 ;}
        else if(DimsunClustred_size!=0 && Clustrd_size==0  && zer0Density_size==0) {DimsunClustred_size=k;}
        else
        {
         DimsunClustred_size=k/3;
         Clustrd_size=k/3;
         zer0Density_size=k/3;
         clusval=Double.parseDouble( Clustrd.getValueAt(0, 1).toString())+1 ;
        }
        
        //adjusting the number of dims in each category
        
        //System.out.println(totdiff);
        //totdiff=0;
        if((DimsunClustred_size>DimsunClustred.getRowCount() )&& (DimsunClustred_size==k/3 || DimsunClustred_size==k/2)){
            int diffunClustred=DimsunClustred_size-DimsunClustred.getRowCount();
            Clustrd_size=Clustrd_size+(diffunClustred*Clustrd.getRowCount()/(Clustrd.getRowCount()+zer0Density.getRowCount()));
            zer0Density_size=zer0Density_size+(diffunClustred*zer0Density.getRowCount()/(Clustrd.getRowCount()+zer0Density.getRowCount()));
                }
        else if((Clustrd_size>Clustrd.getRowCount() )&& (Clustrd_size==k/3 || Clustrd_size==k/2)){
            int diffClustrd=Clustrd_size-Clustrd.getRowCount();
            DimsunClustred_size=DimsunClustred_size+(diffClustrd*DimsunClustred.getRowCount()/(DimsunClustred.getRowCount()+zer0Density.getRowCount()));
            zer0Density_size=zer0Density_size+(diffClustrd*zer0Density.getRowCount()/(DimsunClustred.getRowCount()+zer0Density.getRowCount()));
                }
        else if((zer0Density_size>zer0Density.getRowCount()) && (zer0Density_size==k/3 || zer0Density_size==k/2)){
            int diffzer0Density=zer0Density_size-zer0Density.getRowCount();
            DimsunClustred_size=DimsunClustred_size+(diffzer0Density*DimsunClustred.getRowCount()/(DimsunClustred.getRowCount()+Clustrd.getRowCount()));
            Clustrd_size=Clustrd_size+(diffzer0Density*Clustrd.getRowCount()/(DimsunClustred.getRowCount()+Clustrd.getRowCount()));
                }
        
        Map<String, String> DimsAttributes = new HashMap<String, String>();
        int remainclust=0;
        int remainzerodensity=0;
        int remainzerange=0;
        
        for(int j=0; j<zer0Density.getRowCount();j++){
            String ViewnameDz=zer0Density.getValueAt(j, 0).toString();
            if(remainzerodensity<zer0Density_size){
            DimsAttributes.put(ViewnameDz, "zer0Density");
            System.out.println(ViewnameDz+ "\t zer0Density");
            remainzerodensity++;
                }
                   }
       // double clusval=Double.parseDouble( Clustrd.getValueAt(0, 1).toString())+1 ;
       // double clusval=10000000+1;
        for(int j=0; j<Clustrd.getRowCount();j++){
            String ViewnameDz=Clustrd.getValueAt(j, 0).toString();
            double dimval=Double.parseDouble( Clustrd.getValueAt(j, 1).toString()) ;
            if (dimval<clusval && remainclust<Clustrd_size){
                clusval=dimval;
                remainclust++;
                DimsAttributes.put(ViewnameDz, "Clustered");
                System.out.println(ViewnameDz+ "\t Clustered");
                    }
                   }
//        DimsunClustred_size=k-remainclust-remainzerodensity;
        for(int i=0; i<DimsunClustred.getRowCount();i++){
            String ViewnameD=DimsunClustred.getValueAt(i, 0).toString();
                if(remainzerange<DimsunClustred_size) {
                    remainzerange++;
                DimsAttributes.put(ViewnameD, "Range");
                System.out.println(ViewnameD+ "\t Range");
                        }
                   }
        
        //Print remaining
        //int  totdiff=k-DimsunClustred_size-Clustrd_size-zer0Density_size;
         int  totdiff=k-remainzerange-remainclust-remainzerodensity;
        if(totdiff>0){
            int counter=0;
           
            Vector ViewsDiff_data = ViewsDiff.getDataVector();
            Collections.sort(ViewsDiff_data, new ColumnSorter(1, false));
            ViewsDiff.fireTableDataChanged();
            
            for(int i=0; i<ViewsDiff.getRowCount();i++){
           //    for (Map.Entry<String, String> columnAttribute1 : DimsAttributes2.entrySet()){
                    
              //  String ColnamSel=columnAttribute1.getKey();
                String ColnamAval=ViewsDiff.getValueAt(i, 0).toString();
                if(! DimsAttributes.containsKey(ColnamAval) &&counter<totdiff) {
                    DimsAttributes.put(ColnamAval, "Remain Diff");
                    System.out.println(ColnamAval+ "\t Remain Diff");
                                 counter++;

                }
            //   }
            }
        }
        return DimsAttributes;
        }
    
    public   Map<String, String> ComputeOtimizedAttr(int K, boolean Correlated,String tble,String Wher){
       Metadata mg = new Metadata(tble, 
				"postgresql", "127.0.0.1/postgres", "postgres", "himos");
        
        
        
        DefaultTableModel DimsD=mg.getDimsSelectivity2(0,"",Correlated);
        long start = System.currentTimeMillis();
        DefaultTableModel DimsQ=mg.getDimsSelectivity2(0,Wher,Correlated);
        
        //DefaultTableModel viewsD=ComputeViewWeight(DimsD,MesD);
        //DefaultTableModel viewsQ=ComputeViewWeight(DimsQ,MesQ);
        
        DefaultTableModel ViewsDiff=CompuDimsDiff(DimsD,DimsQ,Wher);
        DefaultTableModel Dims0Density=CompuDims0deltaDensity(ViewsDiff);
        DefaultTableModel DimsClustred=CompuDimsClustred(ViewsDiff);
        DefaultTableModel DimsunClustred=CompuDimsUnClustred(ViewsDiff,DimsClustred,Dims0Density);
       
        Map<String, String> DimsAttributes = GetOptimzedDims(DimsunClustred,DimsClustred,Dims0Density,K,ViewsDiff);
        long Stop = System.currentTimeMillis()-start;
        System.out.println("Total Time\t"+Stop);
        return DimsAttributes;
   }
    
    public static DefaultTableModel CompuDims0deltaDensity(DefaultTableModel ViewD){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Attribute");
       // model.addColumn("Diff_Cardnility");
        model.addColumn("Diff_Selectvity");
      //  model.addColumn("Diff_Density");
       // model.addColumn("Total Dif");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            
       //     for(int j=0; j<ViewD.getRowCount();j++){
                String ViewnameD=ViewD.getValueAt(i, 0).toString();
         
         
                    double deltadensity=Double.parseDouble(ViewD.getValueAt(i, 3).toString());
                    double deltaSel=Double.parseDouble(ViewD.getValueAt(i, 2).toString());
                    if (deltadensity==0){
                        Vector textData = new Vector();
                                textData.add(ViewnameD);
                                
                                textData.add(deltaSel);
                                
                                
                        model.addRow(textData );                    
                    }
               
               
                
                
                
                //System.out.println(Viewname+"\t"+Sel );
                }
         
            
        
        return model;
        }
    
    public static DefaultTableModel CompuDimsClustred(DefaultTableModel ViewD){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("AttributeClustred");
       // model.addColumn("Attribute2");
       // model.addColumn("Diff_Cardnility");
        //model.addColumn("Diff_Selectvity");
      //  model.addColumn("Diff_Density");
        model.addColumn("Total Dif");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            
            for(int j=0; j<ViewD.getRowCount();j++){
                String ViewnameD1=ViewD.getValueAt(i, 0).toString();
                String ViewnameD2=ViewD.getValueAt(j, 0).toString();
                if(!ViewnameD1.equals(ViewnameD2)){
                    double deltaChange1=Double.parseDouble(ViewD.getValueAt(i, 4).toString());
                    double deltaChange2=Double.parseDouble(ViewD.getValueAt(j, 4).toString());
                    if(deltaChange1==deltaChange2 && deltaChange2>0 ){
                      //  ViewD.removeRow(i);
                        Vector textData = new Vector();
                            //    textData.add(i);
                                textData.add(ViewnameD2);
                                textData.add(deltaChange1);
                          model.addRow(textData );   
                    }
                        
                    }
                }
                    
                                
                                       
                    }
               
               
                
                
                return model;
                //System.out.println(Viewname+"\t"+Sel );
                }
    
    public static DefaultTableModel CompuDimsUnClustred(DefaultTableModel ViewD,DefaultTableModel Clustrd,DefaultTableModel zer0Density){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("AttributeClustred");
       // model.addColumn("Attribute2");
       // model.addColumn("Diff_Cardnility");
        //model.addColumn("Diff_Selectvity");
      //  model.addColumn("Diff_Density");
        model.addColumn("Total Dif");
        
        for(int i=0; i<ViewD.getRowCount();i++){
            String ViewnameD1=ViewD.getValueAt(i, 0).toString();
            int Cflag=0;
            int Dflag=0;
            if (Clustrd.getRowCount()>0){
            for(int j=0; j<Clustrd.getRowCount();j++){
                
                String ViewnameD2=Clustrd.getValueAt(j, 0).toString();
                if(ViewnameD1.equals(ViewnameD2)){
                    Cflag=1;
                    break;
                                }
                     }
            } 
            else if(zer0Density.getRowCount()>0){
                for(int j=0; j<zer0Density.getRowCount();j++){
                
                String ViewnameD2=zer0Density.getValueAt(j, 0).toString();
                if(ViewnameD1.equals(ViewnameD2)){
                    Dflag=1;
                    break;
                                }
                     }
            }
            
            if(Cflag==0 &&  Dflag==0){
                double deltaChange1=Double.parseDouble( ViewD.getValueAt(i, 4).toString());
                    
                        Vector textData = new Vector();
                            //    textData.add(i);
                                textData.add(ViewnameD1);
                                textData.add(deltaChange1);
                          model.addRow(textData );   
                    }
                        
         }
                
                    
           
                
                return model;
                //System.out.println(Viewname+"\t"+Sel );
                }
    
}
