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
import java.util.List;

import cdb.ExperimentalSettings.DifferenceOperators;
import cdb.ExperimentalSettings.DistanceMetric;

/**
 * Defines a view, i.e. a particular type of summary of the two datasets
 * @author manasi
 *
 */
public interface View {
	
	public DifferenceOperators getOperator();
	public double getUtility(DistanceMetric distanceMetric);
	public double getUtility(DistanceMetric distanceMetric,
			boolean normalizeDistributions);
        //Added by Himos
        public double getUtilitywithfunc(DistanceMetric distanceMetric, Constants.AggregateFunctions AGF,
			boolean normalizeDistributions);
	public List<View> constituentViews();
        
       // public String getHighFunction(); 
	//public View getTopDifferent();

}
