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

import DImsStats.DimStatsOptimize;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Lists;
import java.sql.Statement;
import java.util.Map;
import DImsStats.*;
import cdb.ExperimentalSettings;

/**
 * Obtains metadata about attributes. Currently only determines if attribute
 * is measure or dimension. 
 * 
 * TODO: determine number of distinct values, type of attribute etc.
 * @author manasi
 *
 */
public class InputTablesMetadata {
	//private List<Attribute> dimensionAttributes;
	//private List<Attribute> measureAttributes;
        public List<Attribute> dimensionAttributes;
	public List<Attribute> measureAttributes;
	private List<String> tables;
	private int numRows;
	
	public int getNumRows() {
		return numRows;
	}
	
	public List<Attribute> getDimensionAttributes() {
		return dimensionAttributes;
	}
	
	public List<Attribute> getMeasureAttributes() {
		return measureAttributes;
	}

	public InputTablesMetadata(List<String> tables,ExperimentalSettings es ,String Wher,String Wher2,
			DBConnection connection, boolean Correldetect, int Selective, int Shallow) {
		dimensionAttributes = Lists.newArrayList();
		measureAttributes = Lists.newArrayList();
		this.tables = tables;
                if(Wher2.isEmpty()|| Wher2==null)
                    Wher2="";
                else
                    Wher2=" where "+Wher2;
                //Added by Himos
                
                
		if (Correldetect==true && Selective>0){
                   // getAttributeMetadataByNameCorrelated(connection);
                    //getAttributeMetadataByNameCorrelatedWithstats(connection,Selective);
                getUsedAttributeMetadataByNameCorrelated(connection,Wher+" "+Wher2);
                String table = tables.get(0);
                Algorithms SelaAlgos=new Algorithms();
                
                if(Shallow==1)
                
                this.dimensionAttributes = SelaAlgos.AlgoSela_Eq1(this.dimensionAttributes,Selective, table, "where "+Wher,Wher2, es.Globalfunc.toString(),es.Compute_Cost);
               
                else if(Shallow==3)
                  this.dimensionAttributes = SelaAlgos.AlgoSela_Eq3(this.dimensionAttributes,Selective, table, "where "+Wher,Wher2, es.Globalfunc.toString(),es.Compute_Cost);
                
                else if(Shallow==4){
                    Gvar AGvari=new Gvar();
                    AGvari.Gvari(this.dimensionAttributes, this.measureAttributes, Selective, table, " where "+Wher, Wher2);
                                }
                else if(Shallow==5){ 
                    Gvar AGvari=new Gvar();
                    AGvari.GvariHisto(this.dimensionAttributes, Selective, table, " where "+Wher, Wher2,es.distanceMetric, es.Globalfunc.toString(),es.Compute_Cost);
                                }
                else if(Shallow==6){
                    this.dimensionAttributes.clear();
                    this.measureAttributes.clear();
                    getUsedAttributeMetadataByNameRnd(connection,Wher+" "+Wher2,Selective);
                                }
                //getAttributeMetadataByNameWithstatsOpt(connection,m);

                }
                else if  (Correldetect==false && Selective>0){
                    //getAttributeMetadataByName(connection);
                    getUsedAttributeMetadataByName(connection,Wher+" "+Wher2);
                    String table = tables.get(0);
                Algorithms SelaAlgos=new Algorithms();
                Map<String, String> m= null;
                if(Shallow==1)
               this.dimensionAttributes = SelaAlgos.AlgoSela_Eq1(this.dimensionAttributes,Selective, table, "where "+Wher,Wher2, es.Globalfunc.toString(),es.Compute_Cost);
                
                else if(Shallow==3)
                  this.dimensionAttributes = SelaAlgos.AlgoSela_Eq3(this.dimensionAttributes,Selective, table, "where "+Wher,Wher2, es.Globalfunc.toString(),es.Compute_Cost);
                
               // getAttributeMetadataByNameWithstatsOpt(connection,m);
                //getAttributeMetadataByNameWithstatsOpt(connection,m);
                }
                
                else if  (Correldetect==true && Selective<=0){
                   // getAttributeMetadataByNameCorrelated(connection);
                    getUsedAttributeMetadataByNameCorrelated(connection,Wher+" "+Wher2);
                }
                else if  (Correldetect==false && Selective<=0){
                    //getAttributeMetadataByName(connection); 1-8-2015
                    getUsedAttributeMetadataByName(connection,Wher+" "+Wher2);
                }
//                else{
//		getAttributeMetadataByName(connection);
//                }
		//getAttributeMetadataByCount(connection);
		this.numRows = queryForNumRows(connection);
	}
	
	

	private int queryForNumRows(DBConnection connection) {
		ResultSet rs = connection.getNumRows(tables.get(0));
		try {
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}

	private void getAttributeMetadataByName(DBConnection connection) {
		for (String table : tables) {
			ResultSet rs = connection.getTableColumns(table);
			try {
				while (rs.next()) {
					String attribute = rs.getString("COLUMN_NAME");
					String[] splitParts = attribute.split("_");
					if (attribute.startsWith("measure"))
					{
						if (splitParts.length > 1) {
							try {
								this.measureAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.measureAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.measureAttributes.add(new Attribute(attribute));
						}
					}
					else if (attribute.startsWith("dim")) {
						if (splitParts.length > 1) {
							try {
								this.dimensionAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.dimensionAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.dimensionAttributes.add(new Attribute(attribute));
						}
					}
				}
			} catch (SQLException e) {
				continue;
			}
		}
		
	}
        // Added BY Me After Dolap
        private void getUsedAttributeMetadataByName(DBConnection connection, String Whr) {
		for (String table : tables) {
			ResultSet rs = connection.getTableColumns(table);
			try {
				while (rs.next()) {
					String attribute = rs.getString("COLUMN_NAME");
					String[] splitParts = attribute.split("_");
                                        //System.out.println("all attr"+attribute);
                                        if(!Whr.contains(attribute) ){
                                        //    System.out.println("used attr"+attribute);
					if (attribute.startsWith("measure"))
					{
						if (splitParts.length > 1) {
							try {
								this.measureAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.measureAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.measureAttributes.add(new Attribute(attribute));
						}
					}
					else if (attribute.startsWith("dim")) {
						if (splitParts.length > 1) {
							try {
								this.dimensionAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.dimensionAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.dimensionAttributes.add(new Attribute(attribute));
						}
					}
				}
                        }
			} catch (SQLException e) {
				continue;
			}
		}
		
	}
        
        // Used For Random Space
        private void getUsedAttributeMetadataByNameRnd(DBConnection connection, String Whr, int k) {
            int count=1;
		for (String table : tables) {
			ResultSet rs = connection.getTableColumns(table);
			try {
				while (rs.next()) {
					String attribute = rs.getString("COLUMN_NAME");
					String[] splitParts = attribute.split("_");
                                        //System.out.println("all attr"+attribute);
                                        if(!Whr.contains(attribute) ){
                                        //    System.out.println("used attr"+attribute);
					if (attribute.startsWith("measure"))
					{
						if (splitParts.length > 1) {
							try {
								this.measureAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.measureAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.measureAttributes.add(new Attribute(attribute));
						}
					}
					else if (attribute.startsWith("dim")&& count<=k) {
						if (splitParts.length > 1 ) {
							try {
								this.dimensionAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
                                                               // count++;
                                                               // System.out.println(attribute.toString());
							} catch (NumberFormatException e) {
								this.dimensionAttributes.add(new Attribute(attribute));
							}
						}
						else {
                                                   
							this.dimensionAttributes.add(new Attribute(attribute));
                                                   
						}
                                                 System.out.println(attribute.toString());
                                                count++;
					}
				}
                        }
			} catch (SQLException e) {
				continue;
			}
		}
		
	}
        
        private void getUsedAttributeMetadataByNameCorrelated(DBConnection connection,String Whr) {
		for (String table : tables) {
			ResultSet rs = connection.getTableColumns(table);
			try {
				while (rs.next()) {
					String attribute = rs.getString("COLUMN_NAME");
                                    if(IsAttributeCorrelated(attribute,connection)==0)  {  
					String[] splitParts = attribute.split("_");
                                        if(!Whr.contains(attribute)){
					if (attribute.startsWith("measure"))
					{
						if (splitParts.length > 1) {
							try {
								this.measureAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.measureAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.measureAttributes.add(new Attribute(attribute));
						}
					}
					else if (attribute.startsWith("dim")) {
						if (splitParts.length > 1) {
							try {
								this.dimensionAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.dimensionAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.dimensionAttributes.add(new Attribute(attribute));
						}
					}
				}
                        }
                        }
                                
			} catch (SQLException e) {
				continue;
			}
		}
		
	}
        // Ended
        
        private void getAttributeMetadataByNameCorrelated(DBConnection connection) {
		for (String table : tables) {
			ResultSet rs = connection.getTableColumns(table);
			try {
				while (rs.next()) {
					String attribute = rs.getString("COLUMN_NAME");
                                    if(IsAttributeCorrelated(attribute,connection)==0)  {  
					String[] splitParts = attribute.split("_");
					if (attribute.startsWith("measure"))
					{
						if (splitParts.length > 1) {
							try {
								this.measureAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.measureAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.measureAttributes.add(new Attribute(attribute));
						}
					}
					else if (attribute.startsWith("dim")) {
						if (splitParts.length > 1) {
							try {
								this.dimensionAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.dimensionAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.dimensionAttributes.add(new Attribute(attribute));
						}
					}
				}
                        }
                                
			} catch (SQLException e) {
				continue;
			}
		}
		
	}
        
       
        
        private void getAttributeMetadataByNameCorrelatedWithstats(DBConnection connection, double Selective) {
		for (String table : tables) {
			ResultSet rs = connection.getTableColumns(table);
                        List<Attribute> AttStats= getstats(table,connection);
			try {
				while (rs.next()) {
					String attribute = rs.getString("COLUMN_NAME");
                                    if(IsAttributeCorrelated(attribute,connection)==0)  {  
					String[] splitParts = attribute.split("_");
					if (attribute.startsWith("measure"))
					{
						if (splitParts.length > 1) {
							try {
								this.measureAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.measureAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.measureAttributes.add(new Attribute(attribute));
						}
					}
					else if (attribute.startsWith("dim")) {
                                            //loop to check selectvity
                                            for(Attribute r : AttStats ){
                                                if(attribute.equals(r.name) && r.Attselectvity>=Selective)
                                                {
                                            
						if (splitParts.length > 1) {
							try {
								this.dimensionAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.dimensionAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.dimensionAttributes.add(new Attribute(attribute));
						}
                                            }
                                        }
					}
				}
                                    
                        }
			} catch (SQLException e) {
				continue;
			}
		}
		
	}
        
        private void getAttributeMetadataByNameWithstatsOpt(DBConnection connection, Map m) {
		for (String table : tables) {
			ResultSet rs = connection.getTableColumns(table);
                     //   List<Attribute> AttStats= getstats(table,connection);
			try {
				while (rs.next()) {
					String attribute = rs.getString("COLUMN_NAME");
                                    if(1==1)  {  
					String[] splitParts = attribute.split("_");
					if (attribute.startsWith("measure"))
					{
						if (splitParts.length > 1) {
							try {
								this.measureAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.measureAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.measureAttributes.add(new Attribute(attribute));
						}
					}
					else if (attribute.startsWith("dim")) {
                                            //loop to check selectvity
                                            
                                            
                                                if(m.containsKey(attribute))
                                                {
                                            
						if (splitParts.length > 1) {
							try {
								this.dimensionAttributes.add(new Attribute(attribute, Integer.parseInt(splitParts[1])));
							} catch (NumberFormatException e) {
								this.dimensionAttributes.add(new Attribute(attribute));
							}
						}
						else {
							this.dimensionAttributes.add(new Attribute(attribute));
						}
                                            }
                                        
					}
				}
                        }
			} catch (SQLException e) {
				continue;
			}
		}
		
	}
        
        private Integer IsAttributeCorrelated(String attr,DBConnection connection){
        // This function Checks if the attribute is correlated of not
//            Statement stmt = null;
		ResultSet rs = null;
                int count=-1;
		try {
			// Get a statement from the connection
		    String query="Select count(corrdim) from corr_dims where corrdim='"+attr+"'";
                   // System.out.println(query);

		    // Execute the query
                    //stmt=connection.
		    rs = connection.executeQuery(query) ;
                    rs.next();
                     count=rs.getInt(1);
		}
		catch(Exception e)
		{
			System.out.println("Error in executing query: " + e.getMessage());
			e.printStackTrace();
		}
                return count;
        }
        
        private List getstats(String taable,DBConnection connection){
        // This function Checks if the attribute is correlated of not
//            Statement stmt = null;
            List<Attribute> dimensionAttributesStats = Lists.newArrayList();
		ResultSet rs = null;
                int count=-1;
		try {
			// Get a statement from the connection
                   
		    String query="Select colname,cardinality,selectvity,density from cols_stats where tblname='"+taable+"'";
                   // System.out.println(query);

		    // Execute the query
                    //stmt=connection.
		    rs = connection.executeQuery(query) ;
                    while(rs.next()){
                            String Colname=rs.getString(1);
                            Attribute Att= new Attribute(Colname);
                            Att.name=Colname;
                            Att.Attcardnility=rs.getDouble(2);
                            Att.Attselectvity=rs.getDouble(3);
                            Att.Attdensity=rs.getDouble(4);
                            dimensionAttributesStats.add(Att);

                    }
		}
		catch(Exception e)
		{
			System.out.println("Error in executing query: " + e.getMessage());
			e.printStackTrace();
		}
                return dimensionAttributesStats;
        }
}
