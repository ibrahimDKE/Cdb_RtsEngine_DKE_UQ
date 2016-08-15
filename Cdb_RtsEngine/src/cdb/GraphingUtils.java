/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cdb;

/**
 *
 * @author himos
 */

import cdb.Constants.AggregateFunctions;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils ;


public class GraphingUtils {
	public static void createFilesForGraphs_org(List<View> views) {
		for (View v : views) { // can select only the first k
			AggregateGroupByView v_ = (AggregateGroupByView) v;
			//String viewFilePath = "/Users/manasi/Public/seedb_results/" + v_.getId() + ".txt";
                        String viewFilePath = "/home/himos/Public/cdb_results/" + v_.getId() + ".txt";
			File viewFile = new File(viewFilePath);
			if (!viewFile.exists()) {
				try {
					viewFile.createNewFile();
				} catch (IOException e) {
					System.out.println("Couldn't create file: " + viewFile);
					e.printStackTrace();
				}
			} 
			String headerLine = v_.getGroupByAttributes() + ", selected_patients, all_patients";
			Utils.writeToFile(viewFile, headerLine);
			for (String k : v_.getResult().keySet()) {
				String toWrite = k + "," + v_.getResult().get(k).datasetValues[0].sum + "," + v_.getResult().get(k).datasetValues[1].sum;
				Utils.writeToFile(viewFile, toWrite);
			}
		}
	}
        
        public static void createFilesForGraphs(List<View> views, int i,int j ,ExperimentalSettings settings, String funco) {
		//for (View v : views) { // can select only the first k
			AggregateGroupByView v_ = (AggregateGroupByView) views.get(i);
                       // views
                        boolean NormalDist=settings.normalizeDistributions;
                        settings.normalizeDistributions=false;
                        
                        
                        
                        String  filename= getcolNames(v_.getId(),settings.TestData);
                        String viewFilePath = "/home/himos/Public/cdb_results/" + (j+1)+"-"+filename + ".csv";
                        
                        
			File viewFile = new File(viewFilePath);
			if (!viewFile.exists()) {
				try {
					viewFile.createNewFile();
				} catch (IOException e) {
					System.out.println("Couldn't create file: " + viewFile);
					e.printStackTrace();
				}
			} 
                        else{
                            viewFile.delete();
                             try{
					viewFile.createNewFile();
				} catch (IOException e) {
					System.out.println("Couldn't create file: " + viewFile);
					e.printStackTrace();
				}
                            }
			//String fun=v_.getFunction().toString();
                        String headerLine = v_.getGroupByAttributes()+""+ v_.aggregateAttribute+","+ funco+"(selected),"+funco+"(all)";
			Utils.writeToFile(viewFile, headerLine);
                         AggregateFunctions Aggf=AggregateFunctions.SUM;
                         String toWrite="";
			for (String k : v_.getResult().keySet()) {
				//String toWrite = k + "," + v_.getResult().get(k).datasetValues[0].sum + "," + v_.getResult().get(k).datasetValues[1].sum;
                            
                            if (funco.equalsIgnoreCase("SUM")){
                               
                             toWrite = k + "," + v_.getResult().get(k).datasetValues[0].getValue(Aggf.SUM, settings.normalizeDistributions) + "," + v_.getResult().get(k).datasetValues[1].getValue(AggregateFunctions.SUM, settings.normalizeDistributions);
                            }
                            if (funco.equalsIgnoreCase("COUNT")){
                               
                             toWrite = k + "," + v_.getResult().get(k).datasetValues[0].getValue(Aggf.COUNT, settings.normalizeDistributions) + "," + v_.getResult().get(k).datasetValues[1].getValue(AggregateFunctions.COUNT, settings.normalizeDistributions);
                            }
                            if (funco.equalsIgnoreCase("AVG")){
                               
                             toWrite = k + "," + v_.getResult().get(k).datasetValues[0].getValue(Aggf.AVG, settings.normalizeDistributions) + "," + v_.getResult().get(k).datasetValues[1].getValue(AggregateFunctions.AVG, settings.normalizeDistributions);
                            }
                            if (funco.equalsIgnoreCase("MAX")){
                               
                             toWrite = k + "," + v_.getResult().get(k).datasetValues[0].getValue(Aggf.MAX, settings.normalizeDistributions) + "," + v_.getResult().get(k).datasetValues[1].getValue(AggregateFunctions.MAX, settings.normalizeDistributions);
                            }
                            if (funco.equalsIgnoreCase("MIN")){
                               
                             toWrite = k + "," + v_.getResult().get(k).datasetValues[0].getValue(Aggf.MIN, settings.normalizeDistributions) + "," + v_.getResult().get(k).datasetValues[1].getValue(AggregateFunctions.MIN, settings.normalizeDistributions);
                            }
                            
                                  
                            Utils.writeToFile(viewFile, toWrite);
                                //conn.executeQueryWithoutResult(InsStmt+Valus);
			}
                        settings.normalizeDistributions=NormalDist;
		//Utils.writeToFile(viewFile, v_.toString());
                        
               // }
	}

        public static void cleanDir(File dir){
           //Added by Himos
                    for (File file: dir.listFiles()) {
                                if (!file.isDirectory()) file.delete();
                                }

        }
        
        public static void MoveOldData(File Source, File Dest){
               
                   //Files.copy(Source.toPath(), Dest.toPath(),REPLACE_EXISTING);
            try{       
            FileUtils.copyDirectory(Source,Dest);
            } catch(Exception ex){
            System.out.println(ex);
            }
        }
        
        public static String getcolNames(String Filename, String Tblname){
           
                String dim=Filename.substring(0,Filename.indexOf("_"));
                String measure=Filename.substring(Filename.lastIndexOf("_")+1,Filename.length());
                String Temp=""; 
            if (Tblname.equals("olympicathletes")){
    
                
        
                if (dim.endsWith("dim1")){ Temp="PlayerName";}
                else if (dim.endsWith("dim2")){Temp="Age";} 
                else if (dim.endsWith("dim3")){
                Temp="Country";
                }
                else if (dim.endsWith("dim4")){Temp="Year";}
                else if (dim.endsWith("dim5")){Temp="CelebrationDate";}
                else if (dim.endsWith("dim6")){Temp="Sport";}
                else {Temp=dim;}
        
                if (measure.endsWith("measure1")){ Temp=Temp+"__GoldenMedal";}
                else if (measure.endsWith("measure2")){Temp=Temp+"__SilverMedal";} 
                else if (measure.endsWith("measure3")){Temp=Temp+"__PronzeMedal";}
                else if (measure.endsWith("measure4")){Temp=Temp+"__TotalMedal";}
                else {Temp=Temp+"__"+measure;}
                
                } else if (Tblname.equals("votesus")){
                
                    if (dim.endsWith("dim1")){ Temp="CountyName";}
                else if (dim.endsWith("dim2")){Temp="Party1";} 
                else if (dim.endsWith("dim3")){
                Temp="Firstname1";
                }
                else if (dim.endsWith("dim4")){Temp="Lastname1";}
                else if (dim.endsWith("dim5")){Temp="Party2";}
                else if (dim.endsWith("dim6")){Temp="Firstname2";}
                else if (dim.endsWith("dim7")){Temp="Lastname2";}
                else {Temp=dim;}
        
                if (measure.endsWith("measure1")){ Temp=Temp+"__PrecinctsReporting";}
                else if (measure.endsWith("measure2")){Temp=Temp+"__TotalPrecincts";} 
                else if (measure.endsWith("measure3")){Temp=Temp+"__TotalVotes";}
                else if (measure.endsWith("measure4")){Temp=Temp+"__Votes1";}
                else if (measure.endsWith("measure5")){Temp=Temp+"__Votes2";}
                else {Temp=Temp+"__"+measure;}
                
                }
            
            else if (Tblname.equals("ontime")){
                
                    if (dim.endsWith("dim1")){ Temp="Month";}
                else if (dim.endsWith("dim2")){Temp="DayofWeek";} 
                else if (dim.endsWith("dim4")){
                Temp="UniqueCarrier";
                }
                else if (dim.endsWith("dim5")){Temp="flightnum";}
                else if (dim.endsWith("dim6")){Temp="Origin";}
                else if (dim.endsWith("dim7")){Temp="Dest";}
                else if (dim.endsWith("dim8")){Temp="CancelCode";}
                else {Temp=dim;}
        
                if (measure.endsWith("measure1")){ Temp=Temp+"__ActualElapsedTime";}
                else if (measure.endsWith("measure2")){Temp=Temp+"__SchedulElapsedTime";} 
                else if (measure.endsWith("measure3")){Temp=Temp+"__AirTime";}
                else if (measure.endsWith("measure4")){Temp=Temp+"__ArrivalDelay";}
                else if (measure.endsWith("measure5")){Temp=Temp+"__DepatureDelay";}
                else if (measure.endsWith("measure6")){Temp=Temp+"__Distance";}
                else if (measure.endsWith("measure7")){Temp=Temp+"__CarrierDelay";}
                else if (measure.endsWith("measure8")){Temp=Temp+"__WeatherDelay";}
                else if (measure.endsWith("measure9")){Temp=Temp+"__NASDelay";}
                else if (measure.endsWith("measure10")){Temp=Temp+"__SecurityDelay";}
                else if (measure.endsWith("measure11")){Temp=Temp+"__LateAirraftDelay";}
                
                else {Temp=Temp+"__"+measure;}
                
                }
            return Temp;
        }
}
