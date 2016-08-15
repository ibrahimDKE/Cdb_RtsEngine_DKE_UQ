/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DImsStats;

/**
 *
 * @author himos
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.postgresql.util.PSQLException;

import cdb.Attribute;
import com.google.common.collect.Lists;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class Metadata {
	//private int working_mem;
	private String table;
	private Connection connection;
	public enum StrDistanceMetric{JaroWinkler,Levenshtein,NGram};
        public double StrDisThrld=0.80;
        
	public Connection getConnection(String dbType, String dbAddress, String dbUser, String dbPassword) {
		Connection connection = null;
		try {
		    Class.forName("org." + dbType + ".Driver");
		} catch (ClassNotFoundException e) {
			connection = null;
		    System.out.println("DB driver not found");
		    e.printStackTrace();
		    return null;
		}
		
		//attempt to connect
		try {
			connection = DriverManager.getConnection(
					"jdbc:" + dbType + "://" + dbAddress, dbUser,
					dbPassword);
                        Statement stmt = connection.createStatement();
                        stmt.execute("set work_mem='" + 300 + "MB';");
		} catch (SQLException e) {
			connection = null;
			System.out.println("DB connection failed.");
			e.printStackTrace();
			return null;
		}
		return connection;
	}
	
	public Metadata(String table, String dbType, String dbAddress, String dbUser, String dbPassword) {
		this.table = table;
		//this.working_mem = working_mem;
		this.connection = this.getConnection(dbType, dbAddress, dbUser, dbPassword);
	}
	
        public Map<String, String> getColumnAttributes() throws SQLException {
		
		Map<String, String> columnAttributes = new HashMap<String, String>();

		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			return columnAttributes;
		}
		
		List<String> dimensionAttributes = Lists.newArrayList();
		List<String> measureAttributes = Lists.newArrayList();
		
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			String type = rs.getString("TYPE_NAME");
			columnAttributes.put(name, type);
		}

		return columnAttributes;
	}
        
        
        //Added by Himos Get Dimessions
        public Map<String, String> getDimColumn() throws SQLException {
		
		Map<String, String> columnAttributes = new HashMap<String, String>();

		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			return columnAttributes;
		}
		
		List<String> dimensionAttributes = Lists.newArrayList();
		List<String> measureAttributes = Lists.newArrayList();
		
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			String type = rs.getString("TYPE_NAME");
                        if (name.startsWith("dim")){
			columnAttributes.put(name, type);}
		}

		return columnAttributes;
	}
        
        public Map<String, String> getDimColumnCorr() throws SQLException {
		
		Map<String, String> columnAttributes = new HashMap<String, String>();
              

		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		
		ResultSet CorrelCols = null; 
		try {
                    
		
                         
                   String CorrSt=" Select corrdim from corr_dims  where tlbname='"+table.toString()+"'";
                   CorrelCols=ExecuteQryWithRS(CorrSt);

		} catch (Exception e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			return columnAttributes;
		}
		
		
                 
                
		while (CorrelCols.next()) {
			String name = CorrelCols.getString(1);
                      
			//String type = CorrelCols.getString("TYPE_NAME");
                        if (name.startsWith("dim")){
			columnAttributes.put(name, "null");}
                      
                    }
                 
		return columnAttributes;
	}
        
        public Map<String, String> getDimColumnUnCorr() throws SQLException {
		
		Map<String, String> columnAttributes = new HashMap<String, String>();
              Map<String, String> CorrAttributes = getDimColumnCorr();

		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
                
		
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);


                
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
                    if(  !CorrAttributes.keySet().contains(name) ) {
			//String type = CorrelCols.getString("TYPE_NAME");
                        if (name.startsWith("dim")){
			columnAttributes.put(name, "null");
                            }
                            }
                    }
                
                 
		
                } catch (Exception e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			return columnAttributes;
		}
		return columnAttributes;
	}
        // Added by Himos Get Measures
        public Map<String, String> getMeasureColumn() throws SQLException {
		
		Map<String, String> columnAttributes = new HashMap<String, String>();

		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			return columnAttributes;
		}
		
		List<String> dimensionAttributes = Lists.newArrayList();
		List<String> measureAttributes = Lists.newArrayList();
		
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			String type = rs.getString("TYPE_NAME");
                        if (name.startsWith("measure")){
			columnAttributes.put(name, type);}
		}

		return columnAttributes;
	}

	public Integer getNumRows() throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT count(*) FROM " + table;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getInt(1);
		    
		    
		} catch (PSQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}
        
        public int getAttsizeWidth(String Col) throws SQLException {
		
		int size=-1;

		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);
                     

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			
		}
		
		
		
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			//String type = rs.getString("TYPE_NAME");
                        if (name.equals(Col)){
                            
                            size=rs.getInt("COLUMN_SIZE");
			return size;
                        }
                       
		}

		return size;
	}
        
        public Integer getNumRowsWhre(String Whre) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT count(*) FROM " + table+" "+Whre;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getInt(1);
		    
		    
		} catch (PSQLException e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}

	public Float getVariance(String columnName) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT var_pop(" + columnName + ") FROM " + table;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getFloat(1);
		    
		    
		} catch (PSQLException e) {
			if (e.getMessage().contains("var_pop") && e.getMessage().contains("does not exist")) {
			 	return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return null;
	}
        
          public Float getMax(String columnName,String Whre) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT max(" + columnName + ") FROM " + table+" "+Whre;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getFloat(1);
		    
		    
		} catch (PSQLException e) {
			if (e.getMessage().contains("Max") && e.getMessage().contains("does not exist")) {
			 	return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return null;
	}
          
          public Float getSum(String columnName,String Whre) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT sum(" + columnName + ") FROM " + table+" "+Whre;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getFloat(1);
		    
		    
		} catch (PSQLException e) {
			if (e.getMessage().contains("Max") && e.getMessage().contains("does not exist")) {
			 	return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return null;
	}
        
          public Float getMin(String columnName,String Whre) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT min(" + columnName + ") FROM " + table+" "+Whre;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getFloat(1);
		    
		    
		} catch (PSQLException e) {
			if (e.getMessage().contains("Min") && e.getMessage().contains("does not exist")) {
			 	return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return null;
	}
        
        public Float getMesVariance(String columnName,String Whr) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT var_pop(" + columnName + ") FROM " + table+" "+Whr;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getFloat(1);
		    
		    
		} catch (PSQLException e) {
			if (e.getMessage().contains("var_pop") && e.getMessage().contains("does not exist")) {
			 	return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return null;
	}
        
        public double getMesVarianceNormalized(String columnName,String Whr,String NormalizeTo) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
                double NornalizeVal=0;
                if(NormalizeTo.equalsIgnoreCase("Max")){
                    NornalizeVal=getMax(columnName,Whr);
                    if(NornalizeVal==0.0) return 0;
                }
                else if	(NormalizeTo.equalsIgnoreCase("Sum")){
                        NornalizeVal=getSum(columnName,Whr);
                        if(NornalizeVal==0.0) return 0;
                }
		String sqlQuery = "SELECT var_pop(" + columnName + "/"+NornalizeVal +") FROM " + table+" "+Whr;
            //    System.out.println(sqlQuery);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getFloat(1);
		    
		    
		} catch (PSQLException e) {
			if (e.getMessage().contains("var_pop") && e.getMessage().contains("does not exist")) {
			 	return 0;
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return 0;
	}
        
        public Float getCorr(String columnName1,String columnName2) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT corr(" + columnName1+","+columnName2 + ") FROM " + table;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getFloat(1);
		    
		    
		} catch (PSQLException e) {
			if (e.getMessage().contains("var_pop") && e.getMessage().contains("does not exist")) {
			 	return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return null;
	}
	
        public DefaultTableModel getColsSelectivity( ){
             DefaultTableModel model = new DefaultTableModel();		
            
            if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
		
		try{
		String Stmt="Select ";
                int count=0;
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			//String type = rs.getString("TYPE_NAME");
                        if (name.startsWith("dim")){
                        String colstmt="count (distinct "+name+") as "+name+"_Cardnility, ";
                        Stmt=Stmt+colstmt;
                       count++;
                        }
		}
                rs.close();
                Stmt=Stmt.substring(0,Stmt.length()-2)+" from "+table.toString();
                int tblesize=getNumRows();
                //System.out.println(Stmt);
                ResultSet rsdata=null;
                rsdata=ExecuteQryWithRS(Stmt);
                //Save data 
               
                 model.addColumn("ColName");
                 model.addColumn("Cardinality");
                 model.addColumn("Selectvity");
                 model.addColumn("Density");
                 
                //Compute cols Stats
                while(rsdata.next()){
                    for (int i=1;i<=count;i++){
                        String Colname=rsdata.getMetaData().getColumnLabel(i);
                        
                        Colname=Colname.substring(0,Colname.indexOf("_cardnility"));
                        double colval=rsdata.getDouble(i);
                        double selectvity=colval/tblesize;
                        double density=1/colval;
                        Vector textData = new Vector();
                                textData.add(Colname);
                                textData.add(colval);
                                textData.add(selectvity);
                                textData.add(density);
                        System.out.println(Colname+"\t"+colval+"\t"+selectvity+"\t"+density);
                         model.addRow(textData );
                    }
                }
                } catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }
        
        public DefaultTableModel getColsSelectivity2( int ins, String WHRESTMT){
             DefaultTableModel model = new DefaultTableModel();		
            
            if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
		
		try{
		String Stmt="Select ";
                int count=0;
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			//String type = rs.getString("TYPE_NAME");
                        if (name.startsWith("dim")){
                        String colstmt="count (distinct "+name+") as "+name+"_Cardnility, ";
                        Stmt=Stmt+colstmt;
                       count++;
                        }
		}
                rs.close();
                Stmt=Stmt.substring(0,Stmt.length()-2)+" from "+table.toString()+" "+WHRESTMT;
                double tblesize=0;
                if(WHRESTMT.isEmpty()&&WHRESTMT!=null){
                tblesize=getNumRowsWhre(WHRESTMT);
                    System.out.println(WHRESTMT+"+++++===="+tblesize);
                }
                else{
                 tblesize=getNumRows();}
                //System.out.println(Stmt);
                ResultSet rsdata=null;
                rsdata=ExecuteQryWithRS(Stmt);
                //Save data 
               
                 model.addColumn("ColName");
                 model.addColumn("Cardinality");
                 model.addColumn("Selectvity");
                 model.addColumn("Density");
                 
                //Compute cols Stats
                while(rsdata.next()){
                    String values="";
                    for (int i=1;i<=count;i++){
                        String Colname=rsdata.getMetaData().getColumnLabel(i);
                        
                        Colname=Colname.substring(0,Colname.indexOf("_cardnility"));
                        double colval=rsdata.getDouble(i);
                        double selectvity=colval/tblesize;
                        double density=1/colval;
                        Vector textData = new Vector();
                                textData.add(Colname);
                                textData.add(colval);
                                textData.add(selectvity);
                                textData.add(density);
                                if(ins==1){
                                
                                       values =values+"('"+Colname+"',"+colval+","+selectvity+","+density+",'"+table.toString()+"'),";
                                }
                        System.out.println(Colname+"\t"+colval+"\t"+selectvity+"\t"+density);
                         model.addRow(textData );
                         
                    }
                    if(ins==1){
                    values="insert into cols_stats (ColName,Cardinality,Selectvity,Density,tblname) values "+
                                 values.substring(0, values.length()-1)+" ";
                         System.out.println(values);
                         ExecuteQry("delete from cols_stats where tblname='"+table.toString()+"'");
                         ExecuteQry(values);
                    }
                }
                } catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }
        
        public DefaultTableModel getMesursVarPop( String WHRESTMT){
             DefaultTableModel model = new DefaultTableModel();		
            
            if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
		
		try{
		//Save data 
               
                 model.addColumn("MesaureName");
                 model.addColumn("Var_PopQ");
                 model.addColumn("Var_PopD");
                 model.addColumn("Difference");
                 model.addColumn("MinQ");
                 model.addColumn("MaxQ");
                 model.addColumn("MinD");
                 model.addColumn("MaxD");
                 
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			//String type = rs.getString("TYPE_NAME");
                        if (name.startsWith("measure")){
                        double varpopQ=getMesVariance(name,WHRESTMT);
                        double varpopD=getVariance(name);
                        double MaxD=getMax(name,"");
                        double MinD=getMin(name,"");
                        double MaxQ=getMax(name,WHRESTMT);
                        double MinQ=getMin(name,WHRESTMT);
                        
                       Vector textData = new Vector();
                                textData.add(name);
                                textData.add(varpopQ);
                                textData.add(varpopD);
                                textData.add(varpopD-varpopQ);
                                textData.add(MinQ);
                                textData.add(MaxQ);
                                textData.add(MinD);
                                textData.add(MaxD);
                                
                        model.addRow(textData );
                        }
		}
                rs.close();
                
                
              
                } catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }
        
        public DefaultTableModel getDimDistinct( String WHRESTMT){
             DefaultTableModel model = new DefaultTableModel();		
            
            if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
		
		try{
		//Save data 
               
                 model.addColumn("DimName");
                 model.addColumn("NDistinct_Q");
                 model.addColumn("NDistinct_D");
                 model.addColumn("Difference");
                 
		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			//String type = rs.getString("TYPE_NAME");
                        if (name.startsWith("dim")){
                        double varpopQ=getNumDistinctWhr(name,WHRESTMT);
                        double varpopD=getNumDistinct(name);
                       Vector textData = new Vector();
                                textData.add(name);
                                textData.add(varpopQ);
                                textData.add(varpopD);
                                textData.add(varpopD-varpopQ);
                        model.addRow(textData );
                        }
		}
                rs.close();
                
                
              
                } catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }
        
        public DefaultTableModel getDim_GroupSelectivity( Map<String, String> Dims, int ins, String WHRESTMT){
             DefaultTableModel model = new DefaultTableModel();		
            
           
		try{
		String Stmt="Select ";
                int count=0;
                  for (Map.Entry<String, String> columnAttribute1 : Dims.entrySet()){
		
			String name = columnAttribute1.getKey().toString();
			//String type = rs.getString("TYPE_NAME");
                        String colstmt="count (distinct "+name+") as "+name+"_Cardnility, ";
                        Stmt=Stmt+colstmt;
                       count++;
		}
                
                Stmt=Stmt.substring(0,Stmt.length()-2)+" from "+table.toString()+" "+WHRESTMT;
                double tblesize=0;
                if(!WHRESTMT.isEmpty()&&WHRESTMT!=null){
                tblesize=getNumRowsWhre(WHRESTMT);}
                else{
                 tblesize=getNumRows();}
                //System.out.println(tblesize);
                ResultSet rsdata=null;
                rsdata=ExecuteQryWithRS(Stmt);
                //Save data 
               
                 model.addColumn("ColName");
                 model.addColumn("Cardinality");
                 model.addColumn("Selectvity");
                 model.addColumn("Density");
                 
                //Compute cols Stats
                while(rsdata.next()){
                    String values="";
                    for (int i=1;i<=count;i++){
                        String Colname=rsdata.getMetaData().getColumnLabel(i);
                        
                        Colname=Colname.substring(0,Colname.indexOf("_cardnility"));
                        if(Colname.startsWith("dim")){
                        double colval=rsdata.getDouble(i);
                        double selectvity=colval/tblesize;
                        double density=1/colval;
                        Vector textData = new Vector();
                                textData.add(Colname);
                                textData.add(colval);
                                textData.add(selectvity);
                                textData.add(density);
                                if(ins==1){
                                
                                       values =values+"('"+Colname+"',"+colval+","+selectvity+","+density+",'"+table.toString()+"'),";
                                }
                      //  System.out.println(Colname+"\t"+colval+"\t"+selectvity+"\t"+density);
                         model.addRow(textData );
                    }
                    }
                    if(ins==1){
                    values="insert into cols_stats (ColName,Cardinality,Selectvity,Density,tblname) values "+
                                 values.substring(0, values.length()-1)+" ";
                         System.out.println(values);
                         ExecuteQry("delete from cols_stats where tblname='"+table.toString()+"'");
                         ExecuteQry(values);
                    }
                }
                } catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }
        
        public DefaultTableModel getDim_GroupSelectivity_Modified( Map<String, String> Dims,String WHRESTMT){
             DefaultTableModel model = new DefaultTableModel();		
                 model.addColumn("ColName");
                 model.addColumn("Cardinality");
                 model.addColumn("Selectvity");
                 model.addColumn("Density");
           
		try{
		
                double tblesize=getNumRowsWhre(WHRESTMT);
                  for (Map.Entry<String, String> columnAttribute1 : Dims.entrySet()){
		
			String name = columnAttribute1.getKey().toString();
			//String type = rs.getString("TYPE_NAME");
                      int Att_Distinct=getNumDistinctWhr(name,WHRESTMT);
                       double selectvity=Att_Distinct/tblesize;
                        double density=1/Att_Distinct;
                         Vector textData = new Vector();
                                textData.add(name);
                                textData.add(Att_Distinct);
                                textData.add(selectvity);
                                textData.add(density);
                                 model.addRow(textData );
		}
                
                
                //Save data 
               
               
                 
              
            
                } catch (SQLException e) {
			System.out.println("Error in Collecting metadata [Selectivity]");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }
       
        public DefaultTableModel getDimsSelectivity2new( int ins, String WHRESTMT, boolean Correlated){
             DefaultTableModel model = new DefaultTableModel();		
            
           if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
		
		
		try{
                    Map<String, String> corrAttributes=null;
                   if(Correlated){
                    corrAttributes =getDimColumnUnCorr();
                   }
                   else{
                   corrAttributes=getDimColumn();
                   }
              
                    double tblesize=0;
                if(!WHRESTMT.isEmpty()&& WHRESTMT!=null){
                tblesize=getNumRowsWhre(WHRESTMT);
                //System.out.println(WHRESTMT+"+++++===="+tblesize);
                }
                else{
                 tblesize=getNumRows();
                }
                
                 model.addColumn("ColName");
                 model.addColumn("Cardinality");
                 model.addColumn("Selectvity");
                 model.addColumn("Density");
                 
                 for (Map.Entry<String, String> columnAttribute1 : corrAttributes.entrySet()){
                    
                String CorrColnam=columnAttribute1.getKey();
		int Disval=getNumDistinctWhr(CorrColnam,WHRESTMT);
                
                      if(CorrColnam.startsWith("dim")){
                        
                        double selectvity=Disval/tblesize;
                        double density=1.0/Disval;
                        Vector textData = new Vector();
                                textData.add(CorrColnam);
                                textData.add(Disval);
                                textData.add(selectvity);
                                textData.add(density);
                                model.addRow(textData );
                               // System.out.println("\t"+selectvity+"\t"+density);
                           }
                 }
               
                       
                         
                  
                                    
                } catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }
        
        public DefaultTableModel getDimsSelectivity2( int ins, String WHRESTMT, boolean Correlated){
             DefaultTableModel model = new DefaultTableModel();		
            
           if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
                    
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
		
		
		try{
                    Map<String, String> corrAttributes=null;
                   if(Correlated){
                    corrAttributes =getDimColumnCorr();
                   }
                   else{
                   corrAttributes=getDimColumn();
                   }
                int count=0;
                String Stmt="Select ";
		while(rs.next()){
                    String name= rs.getString("COLUMN_NAME");
                    int flag=0;
                 for (Map.Entry<String, String> columnAttribute1 : corrAttributes.entrySet()){
                    
                String CorrColnam=columnAttribute1.getKey();
			
                       if(Correlated){
                        if (CorrColnam.equals(name)){
                          flag=1;
                  //        System.out.println(CorrColnam+" &&&&&&&&");
                         // break;
                                 }
                                      } 
                        }
                if (flag==0){
                        String colstmt="count (distinct "+name+") as "+name+"_Cardnility, ";
                            Stmt=Stmt+colstmt;
                            count++;
                    }
             //   CorrelCols.beforeFirst();
                }
                rs.close();
               
                Stmt=Stmt.substring(0,Stmt.length()-2)+" from "+table.toString()+" "+WHRESTMT;
                double tblesize=0;
                if(!WHRESTMT.isEmpty()&& WHRESTMT!=null){
                tblesize=getNumRowsWhre(WHRESTMT);
                //System.out.println(WHRESTMT+"+++++===="+tblesize);
                }
                else{
                 tblesize=getNumRows();}
                //System.out.println(Stmt);
                ResultSet rsdata=null;
                rsdata=ExecuteQryWithRS(Stmt);
                //Save data 
               
                 model.addColumn("ColName");
                 model.addColumn("Cardinality");
                 model.addColumn("Selectvity");
                 model.addColumn("Density");
                 
                //Compute cols Stats
                while(rsdata.next()){
                    String values="";
                    for (int i=1;i<=count;i++){
                        String Colname=rsdata.getMetaData().getColumnLabel(i);
                        
                        Colname=Colname.substring(0,Colname.indexOf("_cardnility"));
                        if(Colname.startsWith("dim")){
                        double colval=rsdata.getDouble(i);
                        double selectvity=colval/tblesize;
                        double density=1/colval;
                        Vector textData = new Vector();
                                textData.add(Colname);
                                textData.add(colval);
                                textData.add(selectvity);
                                textData.add(density);
                                if(ins==1){
                                
                                       values =values+"('"+Colname+"',"+colval+","+selectvity+","+density+",'"+table.toString()+"'),";
                                }
                     //   System.out.println(Colname+"\t"+colval+"\t"+selectvity+"\t"+density);
                         model.addRow(textData );
                    }
                    }
                    if(ins==1){
                    values="insert into cols_stats (ColName,Cardinality,Selectvity,Density,tblname) values "+
                                 values.substring(0, values.length()-1)+" ";
                      //   System.out.println(values);
                         ExecuteQry("delete from cols_stats where tblname='"+table.toString()+"'");
                         ExecuteQry(values);
                    }
                }
                } catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }
	
	public Integer getNumDistinct(String columnName) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
					
		String sqlQuery = "SELECT count(distinct(" + columnName + ")) FROM " + table;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);

		    rs.next();
		    return rs.getInt(1);
		    
		    
		} catch (PSQLException e) {
			if (e.getMessage().contains("variance") && e.getMessage().contains("does not exist")) {
				return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return null;
	}
        public Integer getNumDistinctWhr(String columnName,String Whre) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
					
		String sqlQuery = "SELECT count(distinct(" + columnName + ")) FROM " + table +" "+Whre;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);

		    rs.next();
		    return rs.getInt(1);
		    
		    
		} catch (PSQLException e) {
			if (e.getMessage().contains("variance") && e.getMessage().contains("does not exist")) {
				return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		
		return null;
	}
	
	public String getType(String columnName, String columnType, Integer numRows, Integer numDistinct ) throws SQLException {
	 
		if (columnType.contains("char") || columnType.contains("bit")) {
			if (numDistinct < 20 || numDistinct * 10 < numRows) {
				return "Categorical";
			} else {
				return "String"; //Geographic?
			}
		} else if (columnType.contains("time")) {
			return "Time";
		} else if (columnType.contains("date")) {
			return "Date";
		}
		//Ordinal?
		return "Numeric";
	}	
        
        public ResultSet GetStringData(String Att1,String Att2){
            	Statement stmt = null;
		ResultSet rs = null;
		try {
			// Get a statement from the connection
		    stmt = connection.createStatement() ;

		    // Execute the query
                    String query="Select "+Att1+","+Att2+" from "+table;
		    rs = stmt.executeQuery(query) ;
		}
		catch(Exception e)
		{
			System.out.println("Error in executing query: " + e.getMessage());
			e.printStackTrace();
		}
	return rs;
            }
        
        public void insertCorDims(String STmT){
            Statement st=null;
            try{
            //    System.out.println(STmT);
             st=  connection.createStatement();
             st.executeUpdate(STmT);
            }
            catch(Exception e)
		{
			System.out.println("Error in executing query: " + e.getMessage());
			e.printStackTrace();
		}
        }
        
        public void ExecuteQry(String STmT){
            Statement st=null;
            try{
            //    System.out.println(STmT);
             st=  connection.createStatement();
             st.executeUpdate(STmT);
             st.close();
           //  connection.close();
            }
            catch(Exception e)
		{
			System.out.println("Error in executing query: " + e.getMessage());
			e.printStackTrace();
		}
        }
        
        public ResultSet ExecuteQryWithRS(String STmT){
            	Statement stmt = null;
		ResultSet rs = null;
		try {
			// Get a statement from the connection
		    stmt = connection.createStatement() ;

		    // Execute the query
                   /// String query="Select "+Att1+","+Att2+" from "+table;
		    rs = stmt.executeQuery(STmT) ;
		}
		catch(Exception e)
		{
			System.out.println("Error in executing query: " + e.getMessage());
			e.printStackTrace();
		}
	return rs;
            }
        
        
        public DefaultTableModel getDim_GroupSelectivityList( List<Attribute> Dims, int ins, String WHRESTMT){
             DefaultTableModel model = new DefaultTableModel();		
            
           
		try{
		String Stmt="Select ";
                int count=0;
                  for (int i=0;i< Dims.size();i++){
		
			String name = Dims.get(i).name.toString();
			//String type = rs.getString("TYPE_NAME");
                        String colstmt="count (distinct "+name+") as "+name+"_Cardnility, ";
                        Stmt=Stmt+colstmt;
                       count++;
		}
                
                Stmt=Stmt.substring(0,Stmt.length()-2)+" from "+table.toString()+" "+WHRESTMT;
                double tblesize=0;
                if(!WHRESTMT.isEmpty()&&WHRESTMT!=null){
                tblesize=getNumRowsWhre(WHRESTMT);}
                else{
                 tblesize=getNumRows();}
                //System.out.println(tblesize);
                ResultSet rsdata=null;
                rsdata=ExecuteQryWithRS(Stmt);
                //Save data 
               
                 model.addColumn("ColName");
                 model.addColumn("Cardinality");
                 model.addColumn("Selectvity");
                 model.addColumn("Density");
                 
                //Compute cols Stats
                while(rsdata.next()){
                    String values="";
                    for (int i=1;i<=count;i++){
                        String Colname=rsdata.getMetaData().getColumnLabel(i);
                        
                        Colname=Colname.substring(0,Colname.indexOf("_cardnility"));
                        if(Colname.startsWith("dim")){
                        double colval=rsdata.getDouble(i);
                        double selectvity=colval/tblesize;
                        double density=1/colval;
                        Vector textData = new Vector();
                                textData.add(Colname);
                                textData.add(colval);
                                textData.add(selectvity);
                                textData.add(density);
                                if(ins==1){
                                
                                       values =values+"('"+Colname+"',"+colval+","+selectvity+","+density+",'"+table.toString()+"'),";
                                }
                      //  System.out.println(Colname+"\t"+colval+"\t"+selectvity+"\t"+density);
                         model.addRow(textData );
                    }
                    }
                    if(ins==1){
                    values="insert into cols_stats (ColName,Cardinality,Selectvity,Density,tblname) values "+
                                 values.substring(0, values.length()-1)+" ";
                         System.out.println(values);
                         ExecuteQry("delete from cols_stats where tblname='"+table.toString()+"'");
                         ExecuteQry(values);
                    }
                }
                } catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }
        
        
        public DefaultTableModel getDim_GroupSelectivityList_Modified( List<Attribute> Dims,  String WHRESTMT){
             DefaultTableModel model = new DefaultTableModel();		
             	
                 model.addColumn("ColName");
                 model.addColumn("Cardinality");
                 model.addColumn("Selectvity");
                 model.addColumn("Density");
           
		try{
                     double tblesize=getNumRowsWhre(WHRESTMT);
		for (int i=0;i< Dims.size();i++){
		
			String name = Dims.get(i).name.toString();
                 int Att_Distinct=getNumDistinctWhr(name,WHRESTMT);
                       double selectvity=Att_Distinct/tblesize;
                        double density=1/Att_Distinct;
                         Vector textData = new Vector();
                                textData.add(name);
                                textData.add(Att_Distinct);
                                textData.add(selectvity);
                                textData.add(density);
                                 model.addRow(textData );
                }
                
                } catch (SQLException e) {
			System.out.println("Error in Collecting metadata query [Selectivity]");
			e.printStackTrace();
			//return columnAttributes;
		}
                return model;
        }

}
