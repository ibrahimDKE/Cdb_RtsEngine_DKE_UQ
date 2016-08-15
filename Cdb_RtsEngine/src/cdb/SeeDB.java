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

import DImsStats.ColumnSorter;
import cdb.Constants.AggregateFunctions;
import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cdb.ExperimentalSettings.ComparisonType;
import cdb.ExperimentalSettings.DifferenceOperators;
import cdb.ExperimentalSettings.DistanceMetric;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;


/**
 * This class defines all the functions that the front end can use to 
 * communicate with the backend
 * 
 * @author manasi
 */
public class SeeDB {
	private DBConnection connection;							// connection used to access the database
	private InputQuery[] inputQueries;							// input queries in parsed format
	private int numQueries;										// number of queries (datasets) passed in
	private ExperimentalSettings settings;						// settings used to run SeeDB
	private HashMap<ExperimentalSettings.DifferenceOperators, DifferenceOperator> supportedOperators = 
		new HashMap<ExperimentalSettings.DifferenceOperators, DifferenceOperator>();
	private ConnectionPool pool;								// connection pool used to execute queries
	private File logFile;										// file to which logging information is written
	
	/**
	 * default constructor	
	 */
	public SeeDB() {
		settings = ExperimentalSettings.getDefault();
		connection = new DBConnection();
		inputQueries = new InputQuery[]{null, null};
		numQueries = 0;
		supportedOperators.put(DifferenceOperators.CARDINALITY, new CardinalityDifferenceOperator());
		supportedOperators.put(DifferenceOperators.AGGREGATE, new AggregateGroupByDifferenceOperator());
		supportedOperators.put(DifferenceOperators.DATA_SAMPLE, new RowSampleDifferenceOperator());
	}
	
	/**
	 * few getters
	 * @return
	 */
	
	public DBConnection getConnection() {
		return connection;
	}
	
	public int getNumDatasets() {
		return numQueries;
	}
	
	public ExperimentalSettings getSettings() {
		return settings;
	}
	
	public InputQuery[] getInputQueries() {
		return inputQueries;
	}
	
	/**
	 * Connect to a database. Each dataset can connect to only one database
	 * @param database String with which to connect to the database
	 * @param databaseType Type of database to connect to
	 * @param username username for database
	 * @param password password for database
	 * @return true if connection is successful    	
	 */
	public boolean connectToDatabase(String database, String databaseType, 
			String username, String password) {
		return connection.connectToDatabase(database, databaseType, 
				username, password);
	}
	
	/**
	 * connectToDatabase with default settings
	 * @param datasetNum
	 * @return
	 */
	public boolean connectToDatabase() {
		return connection.connectToDatabase(DBSettings.getDefault());
	}

	/**
	 * Initialize SeeDB engine with the two queries and optionally experimental settings. 
	 * Also sanitize the input
	 * @param query1
	 * @param query2
	 * @param settings
	 * @return true if init is successful
	 * @throws Exception 
	 */
	public boolean initialize(String query1, String query2, 
			ExperimentalSettings settings) throws Exception {
		long start = System.currentTimeMillis();
		
		System.out.println(query1 + ";" + query2);
		// create log file
		if (settings.logFile != null) {
			this.logFile = new File(settings.logFile);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
		}
		// first process query1, this cannot be empty
		if (query1 == null) {
			throw new Exception("query1 cannot be null");
		}
		if (!connection.hasConnection()) {
			System.out.println("DB connection not specified, using default connection params");
			// populate connection from settings
			if (!connection.connectToDatabase(DBSettings.getDefault())) {
				System.out.println("Cannot connect to DB with default settings");
				return false;
			}
		}
		inputQueries[0] = QueryParser.parse(query1);

		// if dataset2 is not empty
		if (query2 != null) {
			this.numQueries = 2;
			settings.comparisonType = ExperimentalSettings.ComparisonType.TWO_DATASETS;
			inputQueries[1] = QueryParser.parse(query2);
		}
		// there is only 1 dataset
		else {
			this.numQueries = 1;
			// fix comparison settings
			if (settings.comparisonType == ExperimentalSettings.ComparisonType.TWO_DATASETS) {
				settings.comparisonType = ExperimentalSettings.ComparisonType.ONE_DATASET_FULL;
			}
			if (settings.comparisonType == ExperimentalSettings.ComparisonType.ONE_DATASET_FULL) {
				inputQueries[1] = InputQuery.deepCopy(inputQueries[0]);
				inputQueries[1].whereClause = "";
				// TODO: update raw query for input query 1?
			}
			else {
				// assume ONE_DATASET_DIFF
				inputQueries[1] = InputQuery.deepCopy(inputQueries[0]);
				inputQueries[1].whereClause = "NOT(" + inputQueries[0].whereClause + ")";
			}
		}
		this.settings = settings;
		if (this.settings.useParallelExecution) {
			this.pool = new ConnectionPool(settings.maxDBConnections, connection.database, connection.databaseType,
					connection.username, connection.password);
		}
		Utils.writeToFile(logFile, "Initialize: " + (System.currentTimeMillis()-start));
		return true;
	}
	
	/**
	 * Initialize SeeDB with default setings
	 * @param dataset1
	 * @param dataset2
	 * @return true if init is successful
	 * @throws Exception 
	 */
	public boolean initialize(String query1, String query2) throws Exception {
		return this.initialize(query1, query2, ExperimentalSettings.getDefault());
	}
	
	/**
	 * initialize queries, distance metric and comparison type
	 * @param query1
	 * @param query2
	 * @param distanceMetric
	 * @param comparisonType
	 * @return
	 * @throws Exception
	 */
	public boolean initializeWeb(String query1, String query2, String distanceMetric, 
			String comparisonType) throws Exception {
		try {
			this.initialize(query1, query2);
			this.setDistanceMeasure(distanceMetric);
			this.setComparisonType(comparisonType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * set distance metric: helper function used from web
	 * @param measure
	 */
	public void setDistanceMeasure(String measure) {
		if (measure.equalsIgnoreCase("EarthMoverDistance")) {
			settings.distanceMetric = DistanceMetric.EARTH_MOVER_DISTANCE;
		} else if (measure.equalsIgnoreCase("EuclideanDistance")){
			settings.distanceMetric = DistanceMetric.EUCLIDEAN_DISTANCE;
		} else if (measure.equalsIgnoreCase("CosineDistance")) {
			settings.distanceMetric = DistanceMetric.COSINE_DISTANCE;
		} else if (measure.equalsIgnoreCase("FidelityDistance")) {
			settings.distanceMetric = DistanceMetric.FIDELITY_DISTANCE;
		} else if (measure.equalsIgnoreCase("ChiSquaredDistance")) {
			settings.distanceMetric = DistanceMetric.CHI_SQUARED_DISTANCE;
		} else if (measure.equalsIgnoreCase("EntropyDistance")) {
			settings.distanceMetric = DistanceMetric.KULLBACK_LEIBLER_DISTANCE;
		}
		System.out.println("setDistanceMetric to " + measure + " done");
	}
	
	/**
	 * set comparison type: helper function used from web
	 * @param type
	 */
	public void setComparisonType(String type) {
		if (type.equalsIgnoreCase("TwoDatasets")) {
			settings.comparisonType = ComparisonType.TWO_DATASETS;
		} else if (type.equalsIgnoreCase("OneDatasetFull")){
			settings.comparisonType = ComparisonType.ONE_DATASET_FULL;
		} else if (type.equalsIgnoreCase("OneDatasetDifference")) {
			settings.comparisonType = ComparisonType.ONE_DATASET_DIFF;
		}
		System.out.println("setComparisonType to " + type + " done");
	}
	
	/**
	 * Compute the metadata for both the queries. we send lists of tables b/c technically join queries
	 * are ok
	 * @return
	 */
	public InputTablesMetadata[] getMetadata(List<String> tables1, List<String> tables2,int numDatasets) {
		InputTablesMetadata[] queryMetadatas = new InputTablesMetadata[] {null, null};
                // Added Gvari Algorithm
                if(settings.DimSelectvity>0){  
                    queryMetadatas[0] = new InputTablesMetadata(tables1,settings,this.inputQueries[0].whereClause,this.inputQueries[1].whereClause, this.connection,settings.CorrelationDetection,settings.DimSelectvity, settings.Sela_shallow);
                    if (numDatasets == 2) {
                    queryMetadatas[1] =queryMetadatas[0]; 
                            }
                    }
                else{
                    
		queryMetadatas[0] = new InputTablesMetadata(tables1,settings,this.inputQueries[0].whereClause,this.inputQueries[1].whereClause, this.connection,settings.CorrelationDetection,settings.DimSelectvity, settings.Sela_shallow);
		if (numDatasets == 2) {
			queryMetadatas[1] = new InputTablesMetadata(tables2,settings,this.inputQueries[0].whereClause,this.inputQueries[1].whereClause, this.connection,settings.CorrelationDetection,settings.DimSelectvity,settings.Sela_shallow);
		}
                
                }
		return queryMetadatas;
	}
	
	public InputTablesMetadata[] getMetadata() {
		return this.getMetadata(inputQueries[0].tables, inputQueries[1].tables,this.numQueries);
	}
	
	/**
	 * get list of difference operators from their enum constants
	 * @return
	 */
	public List<DifferenceOperator> getDifferenceOperators() {
		List<DifferenceOperator> ops = Lists.newArrayList();
		for (ExperimentalSettings.DifferenceOperators op : 
			settings.differenceOperators) {
			ops.add(supportedOperators.get(op));
		}
		return ops;
	}

	/**
	 * fix optimizations to avoid anything dumb
	 */
	public void fixOptimizationSettings() {
		if (settings.optimizeAll) {
			settings.combineMultipleAggregates = true;
			settings.combineMultipleGroupBys = true;
			settings.mergeQueries = true;
		} else if (settings.noAggregateQueryOptimization) {
			settings.combineMultipleAggregates = false;
			settings.combineMultipleGroupBys = false;
			settings.mergeQueries = false;
		}
	}
	
	/**
	 * Computes the differences between the two datasets and returns the 
	 * serialized results of calling each of the difference operators 
	 * registered with the system
	 * @return List of serialized difference results
	 */
        
	public List<View> computeDifference_Old() {
		// get the table metadata and identify the attributes that we want to analyze
		InputTablesMetadata[] queryMetadatas = this.getMetadata(inputQueries[0].tables,
				inputQueries[1].tables, this.numQueries);
		
		// we do not want to consider dimension attributes from the where clause
		for (Attribute attr : queryMetadatas[0].getDimensionAttributes()) {
			if (inputQueries[0].whereClause.contains(attr.name)) {
                            
			
                                queryMetadatas[0].getDimensionAttributes().remove(attr);
                            //queryMetadatas[0].getDimensionAttributes().remove(idx);
				break;
                        
			}
                
		}
		
		if (queryMetadatas[0].getDimensionAttributes().isEmpty() ||
			queryMetadatas[0].getMeasureAttributes().isEmpty()) {
			System.out.println("There are no dimensions or measures, quitting");
			return Lists.newArrayList();
		}
		fixOptimizationSettings();
		
		// compute queries we want to execute
		List<DifferenceOperator> ops = this.getDifferenceOperators();
		List<DifferenceQuery> queries = Lists.newArrayList();
		for (DifferenceOperator op : ops) {
			queries.addAll(op.getDifferenceQueries(inputQueries, queryMetadatas, 
					numQueries, settings));
		}
		
		// ask optimizer to optimize queries  
		Optimizer optimizer = new Optimizer(settings, logFile);
		List<DifferenceQuery> optimizedQueries = 
				optimizer.optimizeQueries(queries, queryMetadatas[0]);
		//System.out.println(optimizedQueries.size());
		
		List<View> views = null;
		QueryExecutor qe = new QueryExecutor(pool, settings, logFile);
		try {
			views = qe.execute(optimizedQueries, queries, connection, numQueries);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		if (settings.useParallelExecution) {
			try {
				pool.closeAllConnections();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error in closing connections");
			}
		}
		
		/* int numViews = views.size();
		
		for (int i = 0; i < numViews; i++) {
			views.addAll(views.get(0).constituentViews());
			views.remove(0);
		}
		*/
                //Added by Himos
                AggregateFunctions orgAggFun=settings.Globalfunc;
                
		List<AllDataViews> AllDviews=new ArrayList<AllDataViews>();
                //Start Computing Distance utility for the views 
                long start = System.currentTimeMillis();
                
		if (settings.Globalfunc==AggregateFunctions.ALL){
                
                
                for (int m=1;m<=5;m++){
                    if (m==1) {settings.Globalfunc=AggregateFunctions.SUM;}
                    else if (m==2) {settings.Globalfunc=AggregateFunctions.COUNT;}
                    else if (m==3) {settings.Globalfunc=AggregateFunctions.AVG;}
                    else if (m==4) {settings.Globalfunc=AggregateFunctions.MAX;}
                    else if (m==5) {settings.Globalfunc=AggregateFunctions.MIN;}
                   
                for (int g=0;g<views.size();g++){
                        AllDataViews alv=new AllDataViews();
                        alv.Viewid=((AggregateGroupByView) views.get(g)).getId() ;
                        alv.util= views.get(g).getUtilitywithfunc(settings.distanceMetric,settings.Globalfunc ,settings.normalizeDistributions);
                        alv.Funct= settings.Globalfunc.toString();
                        AllDviews.add(alv);
                        //System.out.println(alv.Viewid+"\t"+alv.Funct+"\t"+alv.util);    
                        }
                    }
                        // System.out.println("testttttt1");    
                        Collections.sort(AllDviews,new Comparator<AllDataViews> (){
                            
                                @Override
                            public int compare(AllDataViews args0, AllDataViews args1) {
				//if (args0 instanceof AllDataViews || args0 instanceof AllDataViews) {
					return args0.util - args1.util >= 0 ? -1 : 1;
                                        
				//} else {
				//	return 1;
				//}
			}
                            }     );
                          //  System.out.println("testttttt");
                            
                            //output data
                   
        }
                else{
		// sort views by utility
                Collections.sort(views, new Comparator<View>() {
           @Override
			public int compare(View arg0, View arg1) {
                            
				if (arg0 instanceof AggregateView || arg0 instanceof AggregateGroupByView) {
//					return arg0.getUtility(settings.distanceMetric, settings.normalizeDistributions) - 
//							arg1.getUtility(settings.distanceMetric, settings.normalizeDistributions) >= 0 ? -1 : 1;
//                                        
                                    return arg0.getUtilitywithfunc(settings.distanceMetric,orgAggFun,settings.normalizeDistributions) - 
							arg1.getUtilitywithfunc(settings.distanceMetric, orgAggFun,settings.normalizeDistributions) >= 0 ? -1 : 1;
                                        
				} else {
					return 1;
				}
			}
		});
                }
               
                System.out.println("Utility Distance: " + (System.currentTimeMillis() - start));
		// spit out views
		/*
		 * separate file for each
		 * dim_attr, measure_attr1, measure_attr2
		 * val1, val2, val3
		 */
                List<View> ret = views; //.subList(0, 10);
                System.out.println("Finished compute difference");
                //populate into table 
                
                        DefaultTableModel result = new DefaultTableModel();
                        result.addColumn("View_id");
                        result.addColumn("View Name");
                        result.addColumn("View Utility");
                        result.addColumn("Agg Function");
		if (!settings.makeGraphs) {
			//GraphingUtils.createFilesForGraphs(views);
                     if (orgAggFun==AggregateFunctions.ALL){
                        for (int i = 0; i < settings.Kviews; i++) {
                                String TopviewName=AllDviews.get(i).Viewid;
                            for (int j = 0; j < ret.size(); j++) {
                                String viewName=((AggregateGroupByView) ret.get(j)).getId();
                                if(TopviewName.equals(viewName)){
                                    String Fname=GraphingUtils.getcolNames(AllDviews.get(i).Viewid,settings.TestData);
                                    System.out.println(AllDviews.get(i).Viewid +"\t"+Fname+ "\t" + AllDviews.get(i).util+"\t"+ AllDviews.get(i).Funct);
                                     Vector textData = new Vector();
                                textData.add(AllDviews.get(i).Viewid);
                                textData.add(Fname);
                                textData.add(AllDviews.get(i).util);
                                textData.add(AllDviews.get(i).Funct);
                                result.addRow(textData );
                                    }
                            }
                        }
                }
                else{
		for (int i = 0; i < settings.Kviews; i++) {
        
                    
                
                    String Fname=GraphingUtils.getcolNames(((AggregateGroupByView) ret.get(i)).getId(),settings.TestData);
                    double util=ret.get(i).getUtilitywithfunc(settings.distanceMetric,orgAggFun ,settings.normalizeDistributions);
                    System.out.println(((AggregateGroupByView) ret.get(i)).getId() +"\t"+Fname+ "\t" +util +"\t"+ settings.Globalfunc.toString());
                        Vector textData = new Vector();
                                textData.add(((AggregateGroupByView) ret.get(i)).getId() );
                                textData.add(Fname);
                                textData.add(util);
                                textData.add(settings.Globalfunc.toString());
                                result.addRow(textData );                    
		}
               
                     
		}
                }
		
                else {

//the original Code
                GraphingUtils.cleanDir(new File("/home/himos/Public/cdb_results_old"));
                GraphingUtils.MoveOldData(new File("/home/himos/Public/cdb_results"), new File("/home/himos/Public/cdb_results_old"));
                GraphingUtils.cleanDir(new File("/home/himos/Public/cdb_results"));
		
                
                if (orgAggFun==AggregateFunctions.ALL){
                        for (int i = 0; i < settings.Kviews; i++) {
                                String TopviewName=AllDviews.get(i).Viewid;
                            for (int j = 0; j < ret.size(); j++) {
                                String viewName=((AggregateGroupByView) ret.get(j)).getId();
                                if(TopviewName.equals(viewName)){
                                    GraphingUtils.createFilesForGraphs(views,j,i,settings,AllDviews.get(i).Funct);
                                    //String Fname=GraphingUtils.getcolNames(((AggregateGroupByView) ret.get(i)).getId(),settings.TestData);
                                    String Fname=GraphingUtils.getcolNames(AllDviews.get(i).Viewid,settings.TestData);
                                    System.out.println(AllDviews.get(i).Viewid +"\t"+Fname+ "\t" + AllDviews.get(i).util+"\t"+ AllDviews.get(i).Funct);
                                    }
                            }
                        }
                }
                else{
		for (int i = 0; i < settings.Kviews; i++) {
        
                    
                    GraphingUtils.createFilesForGraphs(views,i,i,settings,orgAggFun.toString());
                    String Fname=GraphingUtils.getcolNames(((AggregateGroupByView) ret.get(i)).getId(),settings.TestData);
			//System.out.println(((AggregateGroupByView) ret.get(i)).getId() +"\t"+Fname+ "\t" + ret.get(i).getUtilitywithfunc(settings.distanceMetric,settings.Globalfunc ,settings.normalizeDistributions)+"\t"+ ((AggregateGroupByView) ret.get(i)).getFunction().toString());
                    System.out.println(((AggregateGroupByView) ret.get(i)).getId() +"\t"+Fname+ "\t" + ret.get(i).getUtilitywithfunc(settings.distanceMetric,orgAggFun ,settings.normalizeDistributions)+"\t"+ settings.Globalfunc.toString());
		}
               }
        }
		return ret;
	}	
        
	public List<AllDataViews> computeDifference() {
		// get the table metadata and identify the attributes that we want to analyze
		InputTablesMetadata[] queryMetadatas = this.getMetadata(inputQueries[0].tables,
				inputQueries[1].tables, this.numQueries);
		
		// we do not want to consider dimension attributes from the where clause
		for (Attribute attr : queryMetadatas[0].getDimensionAttributes()) {
			if (inputQueries[0].whereClause.contains(attr.name)) {
                            
			
                                queryMetadatas[0].getDimensionAttributes().remove(attr);
                            //queryMetadatas[0].getDimensionAttributes().remove(idx);
				break;
                        
			}
                
		}
		
		if (queryMetadatas[0].getDimensionAttributes().isEmpty() ||
			queryMetadatas[0].getMeasureAttributes().isEmpty()) {
			System.out.println("There are no dimensions or measures, quitting");
			return Lists.newArrayList();
		}
		fixOptimizationSettings();
		
		// compute queries we want to execute
		List<DifferenceOperator> ops = this.getDifferenceOperators();
		List<DifferenceQuery> queries = Lists.newArrayList();
		for (DifferenceOperator op : ops) {
			queries.addAll(op.getDifferenceQueries(inputQueries, queryMetadatas, 
					numQueries, settings));
		}
		
		// ask optimizer to optimize queries  
		Optimizer optimizer = new Optimizer(settings, logFile);
		List<DifferenceQuery> optimizedQueries = 
				optimizer.optimizeQueries(queries, queryMetadatas[0]);
		//System.out.println(optimizedQueries.size());
		
		List<View> views = null;
		QueryExecutor qe = new QueryExecutor(pool, settings, logFile);
		try {
			views = qe.execute(optimizedQueries, queries, connection, numQueries);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		if (settings.useParallelExecution) {
			try {
				pool.closeAllConnections();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error in closing connections");
			}
		}
		
		/* int numViews = views.size();
		
		for (int i = 0; i < numViews; i++) {
			views.addAll(views.get(0).constituentViews());
			views.remove(0);
		}
		*/
                //Added by Himos
                AggregateFunctions orgAggFun=settings.Globalfunc;
                
		List<AllDataViews> AllDviews=new ArrayList<AllDataViews>();
                long start = System.currentTimeMillis();
		if (settings.Globalfunc==AggregateFunctions.ALL){
                
                
                for (int m=1;m<=5;m++){
                    if (m==1) {settings.Globalfunc=AggregateFunctions.SUM;}
                    else if (m==2) {settings.Globalfunc=AggregateFunctions.COUNT;}
                    else if (m==3) {settings.Globalfunc=AggregateFunctions.AVG;}
                    else if (m==4) {settings.Globalfunc=AggregateFunctions.MAX;}
                    else if (m==5) {settings.Globalfunc=AggregateFunctions.MIN;}
                   
                for (int g=0;g<views.size();g++){
                        AllDataViews alv=new AllDataViews();
                        alv.Viewid=((AggregateGroupByView) views.get(g)).getId() ;
                        alv.util= views.get(g).getUtilitywithfunc(settings.distanceMetric,settings.Globalfunc ,settings.normalizeDistributions);
                        alv.Funct= settings.Globalfunc.toString();
                        AllDviews.add(alv);
                        //System.out.println(alv.Viewid+"\t"+alv.Funct+"\t"+alv.util);    
                        }
                    }
                        // System.out.println("testttttt1");    
                        Collections.sort(AllDviews,new Comparator<AllDataViews> (){
                            
                                @Override
                            public int compare(AllDataViews args0, AllDataViews args1) {
				//if (args0 instanceof AllDataViews || args0 instanceof AllDataViews) {
					return args0.util - args1.util >= 0 ? -1 : 1;
                                        
				//} else {
				//	return 1;
				//}
			}
                            }     );
                          //  System.out.println("testttttt");
                            
                            //output data
                   
        }
                else{
		// sort views by utility
                Collections.sort(views, new Comparator<View>() {
           @Override
			public int compare(View arg0, View arg1) {
                            
				if (arg0 instanceof AggregateView || arg0 instanceof AggregateGroupByView) {
//					return arg0.getUtility(settings.distanceMetric, settings.normalizeDistributions) - 
//							arg1.getUtility(settings.distanceMetric, settings.normalizeDistributions) >= 0 ? -1 : 1;
//                                        
                                    return arg0.getUtilitywithfunc(settings.distanceMetric,orgAggFun,settings.normalizeDistributions) - 
							arg1.getUtilitywithfunc(settings.distanceMetric, orgAggFun,settings.normalizeDistributions) >= 0 ? -1 : 1;
                                        
				} else {
					return 1;
				}
			}
		});
                if (settings.Globalfunc==AggregateFunctions.AVG){
                    
                          Collections.sort(views, new Comparator<View>() {
           @Override
			public int compare(View arg0, View arg1) {
                            
				if (arg0 instanceof AggregateView || arg0 instanceof AggregateGroupByView) {
//					return arg0.getUtility(settings.distanceMetric, settings.normalizeDistributions) - 
//							arg1.getUtility(settings.distanceMetric, settings.normalizeDistributions) >= 0 ? -1 : 1;
//                                        
                                    return arg0.getUtilitywithfunc(settings.distanceMetric,orgAggFun,settings.normalizeDistributions) - 
							arg1.getUtilitywithfunc(settings.distanceMetric, orgAggFun,settings.normalizeDistributions) >= 0 ? -1 : 1;
                                        
				} else {
					return 1;
				}
			}
		});
                    }
                AllDviews=convertviewtoallviews(views);
                }
               
                System.out.println("Utility Distance: " + (System.currentTimeMillis() - start));
		// spit out views
		/*
		 * separate file for each
		 * dim_attr, measure_attr1, measure_attr2
		 * val1, val2, val3
		 */
                List<View> ret = views; //.subList(0, 10);
                System.out.println("Finished compute difference");
                //populate into table 
                
                        
		if (!settings.makeGraphs) {
			//GraphingUtils.createFilesForGraphs(views);
                     if (orgAggFun==AggregateFunctions.ALL){
                        for (int i = 0; i < settings.Kviews; i++) {
                                String TopviewName=AllDviews.get(i).Viewid;
                            for (int j = 0; j < ret.size(); j++) {
                                String viewName=((AggregateGroupByView) ret.get(j)).getId();
                                if(TopviewName.equals(viewName)){
                                    String Fname=GraphingUtils.getcolNames(AllDviews.get(i).Viewid,settings.TestData);
                                    System.out.println(AllDviews.get(i).Viewid +"\t"+Fname+ "\t" + AllDviews.get(i).util+"\t"+ AllDviews.get(i).Funct);
                                  
                                    }
                            }
                        }
                }
                else{
                         
		for (int i = 0; i < settings.Kviews; i++) {
        
                    
                
                    String Fname=GraphingUtils.getcolNames(((AggregateGroupByView) ret.get(i)).getId(),settings.TestData);
                    double util=ret.get(i).getUtilitywithfunc(settings.distanceMetric,orgAggFun ,settings.normalizeDistributions);
                    System.out.println(((AggregateGroupByView) ret.get(i)).getId() +"\t"+Fname+ "\t" +util +"\t"+ settings.Globalfunc.toString());
                                    
		}
               
                     
		}
                }
		
                else {

//the original Code
                GraphingUtils.cleanDir(new File("/home/himos/Public/cdb_results_old"));
                GraphingUtils.MoveOldData(new File("/home/himos/Public/cdb_results"), new File("/home/himos/Public/cdb_results_old"));
                GraphingUtils.cleanDir(new File("/home/himos/Public/cdb_results"));
		
                
                if (orgAggFun==AggregateFunctions.ALL){
                        for (int i = 0; i < settings.Kviews; i++) {
                                String TopviewName=AllDviews.get(i).Viewid;
                            for (int j = 0; j < ret.size(); j++) {
                                String viewName=((AggregateGroupByView) ret.get(j)).getId();
                                if(TopviewName.equals(viewName)){
                                    GraphingUtils.createFilesForGraphs(views,j,i,settings,AllDviews.get(i).Funct);
                                    //String Fname=GraphingUtils.getcolNames(((AggregateGroupByView) ret.get(i)).getId(),settings.TestData);
                                    String Fname=GraphingUtils.getcolNames(AllDviews.get(i).Viewid,settings.TestData);
                                    System.out.println(AllDviews.get(i).Viewid +"\t"+Fname+ "\t" + AllDviews.get(i).util+"\t"+ AllDviews.get(i).Funct);
                                    }
                            }
                        }
                }
                else{
		for (int i = 0; i < settings.Kviews; i++) {
        
                    
                    GraphingUtils.createFilesForGraphs(views,i,i,settings,orgAggFun.toString());
                    String Fname=GraphingUtils.getcolNames(((AggregateGroupByView) ret.get(i)).getId(),settings.TestData);
			//System.out.println(((AggregateGroupByView) ret.get(i)).getId() +"\t"+Fname+ "\t" + ret.get(i).getUtilitywithfunc(settings.distanceMetric,settings.Globalfunc ,settings.normalizeDistributions)+"\t"+ ((AggregateGroupByView) ret.get(i)).getFunction().toString());
                    System.out.println(((AggregateGroupByView) ret.get(i)).getId() +"\t"+Fname+ "\t" + ret.get(i).getUtilitywithfunc(settings.distanceMetric,orgAggFun ,settings.normalizeDistributions)+"\t"+ settings.Globalfunc.toString());
		}
               }
        }
		return AllDviews;
	}	
        
	public List<View> computeDifferenceWithPruningSupport(List<String> prunedViews, int lowerbound, int upperbound) {
		// get the table metadata and identify the attributes that we want to analyze
		InputTablesMetadata[] queryMetadatas = this.getMetadata(inputQueries[0].tables,
				inputQueries[1].tables, this.numQueries);
		
		// we do not want to consider dimension attributes from the where clause
		for (Attribute attr : queryMetadatas[0].getDimensionAttributes()) {
			if (inputQueries[0].whereClause.contains(attr.name)) {
				queryMetadatas[0].getDimensionAttributes().remove(attr);
				break;
			}
		}
		
		if (queryMetadatas[0].getDimensionAttributes().isEmpty() ||
			queryMetadatas[0].getMeasureAttributes().isEmpty()) {
			System.out.println("There are no dimensions or measures, quitting");
			return Lists.newArrayList();
		}
		fixOptimizationSettings();
		
		// modify the where clause
		if (inputQueries[0].whereClause != null && !inputQueries[0].whereClause.isEmpty()) {
			inputQueries[0].whereClause += " AND ";
		}
		
		if (inputQueries[1].whereClause != null && !inputQueries[1].whereClause.isEmpty()) {
			inputQueries[1].whereClause += " AND ";
		}
		
		inputQueries[0].whereClause += " id <=" + upperbound + " AND id >" + lowerbound;
		inputQueries[1].whereClause += " id <=" + upperbound + " AND id >" + lowerbound;
		
		// compute queries we want to execute
		List<DifferenceOperator> ops = this.getDifferenceOperators();
		List<DifferenceQuery> queries = Lists.newArrayList();
		for (DifferenceOperator op : ops) {
			queries.addAll(op.getDifferenceQueries(inputQueries, queryMetadatas, 
					numQueries, settings));
		}
		
		for (int i = 0; i < queries.size(); i++) {
			if (prunedViews.contains(queries.get(i).toString())) {
				queries.remove(i);
				i--;
			}
		}
		
		// ask optimizer to optimize queries  
		Optimizer optimizer = new Optimizer(settings, logFile);
		List<DifferenceQuery> optimizedQueries = 
				optimizer.optimizeQueries(queries, queryMetadatas[0]);
		//System.out.println(optimizedQueries.size());
		
		List<View> views = null;
		QueryExecutor qe = new QueryExecutor(pool, settings, logFile);
		try {
			views = qe.execute(optimizedQueries, queries, connection, numQueries);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		/*
		int numViews = views.size();
		
		for (int i = 0; i < numViews; i++) {
			views.addAll(views.get(0).constituentViews());
			views.remove(0);
		}
		*/
		return views;
	}

	public List<View> computeDifferenceWrapper() {
		List<View> views = Lists.newArrayList();
		List<String> prunedViews = Lists.newArrayList();
		// for number of partitions (fix by id)
		// {
		//		find the views you need to execute
		//		optimize
		//		execute
		//		update old data
		// 		prune
		// }
		int numPhases = 10;
		int phaseSize = (int) Math.ceil(settings.num_rows * 1.0/numPhases);
		InputQuery q1 = InputQuery.deepCopy(inputQueries[0]);
		InputQuery q2 = InputQuery.deepCopy(inputQueries[1]);
		
		for (int i = 0; i < numPhases; i++) {
                    System.out.println("Phase"+(i+1));
			List<View> tmp;
			inputQueries[0] = InputQuery.deepCopy(q1);
			inputQueries[1] = InputQuery.deepCopy(q2);
			tmp = computeDifferenceWithPruningSupport(prunedViews, i * phaseSize, 
				(i +1) * phaseSize);
			if (i == 0) {
				views.addAll(tmp);
			} else {
				consolidateViews(views, tmp);
			}
			pruneViews(views, prunedViews);
		}

		closeConnections();
		
		sortViewsByUtility(views);
		
		System.out.println("Finished compute difference");
		List<View> ret = views; //.subList(0, 10);

		for (int i = 0; i < 10; i++) {
			System.out.println(((AggregateGroupByView) ret.get(i)).getId() + " " + ret.get(i).getUtility(settings.distanceMetric, settings.normalizeDistributions));
		}
		return ret;
	}
        
        public List<AllDataViews> convertviewtoallviews(List<View> views){
            
            List<AllDataViews> AllDviews=new ArrayList<AllDataViews>();
            for (int i = 0; i < views.size(); i++) {
        
                    
                    String Viewid=((AggregateGroupByView) views.get(i)).getId();
                    String Fname=GraphingUtils.getcolNames(((AggregateGroupByView) views.get(i)).getId(),settings.TestData);
                    double util=views.get(i).getUtilitywithfunc(settings.distanceMetric,settings.Globalfunc,settings.normalizeDistributions);
                    //System.out.println(((AggregateGroupByView) views.get(i)).getId() +"\t"+Fname+ "\t" +util +"\t"+ settings.Globalfunc.toString());
                     AllDataViews alv=new AllDataViews();
                    alv.Viewid   =  Viewid;
                    alv.ViewName=Fname.toString();
                    alv.util=util;
                    alv.Funct=settings.Globalfunc.toString();
                     AllDviews.add(alv);
		}
            return AllDviews;
        }
	
	private void closeConnections() {
		if (settings.useParallelExecution) {
			try {
				pool.closeAllConnections();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error in closing connections");
			}
		}
	}
		
	private void sortViewsByUtility(List<View> views) {
		Collections.sort(views, new Comparator<View>() {
	           @Override
				public int compare(View arg0, View arg1) {
					if (arg0 instanceof AggregateView || arg0 instanceof AggregateGroupByView) {
						return arg0.getUtility(settings.distanceMetric, settings.normalizeDistributions) - 
								arg1.getUtility(settings.distanceMetric, settings.normalizeDistributions) >= 0 ? -1 : 1;
					} else {
						return 1;
					}
				}
			});
	}
	
	private void pruneViews(List<View> views, List<String> prunedViews) {
		sortViewsByUtility(views);
		if (settings.MAB) {
			double delta1 = views.get(0).getUtility(settings.distanceMetric, settings.normalizeDistributions) -
					views.get(11).getUtility(settings.distanceMetric, settings.normalizeDistributions);
			double deltak = views.get(10).getUtility(settings.distanceMetric, settings.normalizeDistributions) - 
					views.get(views.size() - 1).getUtility(settings.distanceMetric, settings.normalizeDistributions);
			if (delta1 > deltak) {
				prunedViews.add(((AggregateGroupByView) views.get(views.size()-1)).getId());
				views.remove(views.size()-1);
			}
			// accept is not implemented
		} else { // CI
			System.out.println("here");
			prunedViews.add(((AggregateGroupByView) views.get(views.size()-1)).getId());
			views.remove(views.size()-1);
		}
		
	}

	private void consolidateViews(List<View> views, List<View> tmp) {
		for (View t : tmp) {
			AggregateGroupByView t_ = (AggregateGroupByView) t;
			for (View v : views) {
				AggregateGroupByView v_ = (AggregateGroupByView) v;
				if (t_.getId().equals(v_.getId())) {
					// combine the two distributions
					for (String s : t_.aggregateValues.keySet()) {
						if (!v_.aggregateValues.containsKey(s)) {
							v_.aggregateValues.put(s, new AggregateValuesWrapper());
						}
						AggregateValuesWrapper viewAggValues = v_.aggregateValues.get(s);
						AggregateValuesWrapper tmpAggValues = t_.aggregateValues.get(s);
						for (int i =0; i < 2; i++) {
							viewAggValues.datasetValues[i].count += tmpAggValues.datasetValues[i].count;
							viewAggValues.datasetValues[i].sum += tmpAggValues.datasetValues[i].sum;
							viewAggValues.datasetValues[i].generic += tmpAggValues.datasetValues[i].generic;
						}
					}
				}
			}
		}	
	}

	public void closeConnection() {
		this.connection.close();
	}

	public void connectToDatabase(DBSettings dbSetting) {
		this.connectToDatabase(dbSetting.database, dbSetting.databaseType, dbSetting.username, dbSetting.password);
		
	}
        
        
}

