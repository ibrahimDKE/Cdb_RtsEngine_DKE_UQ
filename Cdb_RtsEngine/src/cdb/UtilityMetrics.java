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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.util.FastMath;
import cdb.Constants.AggregateFunctions;
import cdb.AggregateValuesWrapper;

public class UtilityMetrics {
	
	public static final int DISTRIBUTION_EMPTY = -1;
	
	/**
	 * Compute the earth mover distance between the distribution generate from the query and the distribution from the entire dataset
	 * @param distributionForQuery
	 * @param distributionForDataset
	 * @return
	 */
	public static double EarthMoverDistanceOrg(HashMap<String, AggregateValuesWrapper> dist, AggregateFunctions func, boolean normalize) {
		if (dist.isEmpty()) {
			return DISTRIBUTION_EMPTY;
		}
		
		double utility = 0;
		for (String key : dist.keySet()) {
			utility += Math.abs(dist.get(key).datasetValues[0].getValue(func, normalize) - 
					dist.get(key).datasetValues[1].getValue(func, normalize));
		}

		utility /= 2;
		return utility;
	}
        
	public static double EarthMoverDistance(HashMap<String, AggregateValuesWrapper> dist, AggregateFunctions func, boolean normalize) {
		if (dist.isEmpty()) {
			return DISTRIBUTION_EMPTY;
		}
		
		//double utility = 0;
                double lastDistance = 0;
                double totalDistance = 0;
		for (String key : dist.keySet()) {
		//	utility += Math.abs(dist.get(key).datasetValues[0].getValue(func, normalize) - 
		//			dist.get(key).datasetValues[1].getValue(func, normalize));
                         final double currentDistance = (dist.get(key).datasetValues[0].getValue(func, normalize)+ lastDistance) - dist.get(key).datasetValues[1].getValue(func, normalize);
                         totalDistance += FastMath.abs(currentDistance);
                         lastDistance = currentDistance;
		}

		//utility /= 2;
		return totalDistance;
                //return utility;
	}
	public static double EarthMoverDistance(HashMap<String, AggregateValuesWrapper> dist, boolean normalize) {
		return EarthMoverDistance(dist, AggregateFunctions.SUM, normalize);
	}
	
	
	/**
	 * Compute the euclidean distance between the distribution generate from the query and the distribution from the entire dataset
	 * @param distributionForQuery
	 * @param distributionForDataset
	 * @return
	 */
	public static double EuclideanDistance(HashMap<String, AggregateValuesWrapper> dist, AggregateFunctions func, boolean normalize) {
		if (dist.isEmpty()) {
			return DISTRIBUTION_EMPTY;
		}
		
		double utility = 0;
		for (String key : dist.keySet()) {
			utility += Math.pow(dist.get(key).datasetValues[0].getValue(func, normalize) - 
									dist.get(key).datasetValues[1].getValue(func, normalize), 
								2);
                        double x=dist.get(key).datasetValues[0].getValue(func, normalize);
                        double y=dist.get(key).datasetValues[1].getValue(func, normalize);
                        y=x+y;
		}
		utility = Math.sqrt(utility);
		return utility;
	}
	
	public static double EuclideanDistance(HashMap<String, AggregateValuesWrapper> dist, boolean normalize) {
		return EuclideanDistance(dist, AggregateFunctions.SUM, normalize);
	}
	
	/**
	 * Compute the cosine distance between the distribution generate from the query and the distribution from the entire dataset
	 * @param distributionForQuery
	 * @param distributionForDataset
	 * @return
	 */
	public static double CosineDistance(HashMap<String, AggregateValuesWrapper> dist, AggregateFunctions func, boolean normalize) {
		if (dist.isEmpty()) {
			return DISTRIBUTION_EMPTY;
		}
		
		double utility = 0;
		double numerator = 0;
		double queryInDenominator = 0;
		double datasetInDenominator = 0;
		
		for (String key : dist.keySet()) {
			numerator += Math.abs(dist.get(key).datasetValues[0].getValue(func, normalize) * 
					dist.get(key).datasetValues[1].getValue(func, normalize));
			queryInDenominator += Math.pow(dist.get(key).datasetValues[0].getValue(func, normalize), 2);
			datasetInDenominator += Math.pow(dist.get(key).datasetValues[1].getValue(func, normalize), 2);
		}
		utility = numerator/(queryInDenominator * datasetInDenominator);
		return utility;
	}
	
	public static double CosineDistance(HashMap<String, AggregateValuesWrapper> dist, boolean normalize) {
		return CosineDistance(dist, AggregateFunctions.SUM, normalize);
	}
	
	/**
	 * Compute the fidelity distance between the distribution generate from the query and the distribution from the entire dataset
	 * @param distributionForQuery
	 * @param distributionForDataset
	 * @return
	 */
	public static double FidelityDistance(HashMap<String, AggregateValuesWrapper> dist, AggregateFunctions func, boolean normalize) {
		if (dist.isEmpty()) {
			return DISTRIBUTION_EMPTY;
		}
		double utility = 0;
		
		for (String key : dist.keySet()) {
			utility += Math.pow(dist.get(key).datasetValues[0].getValue(func, normalize) * dist.get(key).datasetValues[1].getValue(func, normalize), 1/2);
		}
		utility = -1 * Math.log(Math.sqrt(utility));
		return utility;
	}
	
	public static double FidelityDistance(HashMap<String, AggregateValuesWrapper> dist, boolean normalize) {
		return FidelityDistance(dist, AggregateFunctions.SUM, normalize);
	}
	
	/**
	 * Compute the pearson chi square distance between the distribution generate from the query and the distribution from the entire dataset
	 * @param distributionForQuery
	 * @param distributionForDataset
	 * @return
	 */
	public static double ChiSquaredDistance(HashMap<String, AggregateValuesWrapper> dist, AggregateFunctions func, boolean normalize) {
		if (dist.isEmpty()) {
			return DISTRIBUTION_EMPTY;
		}
		double utility = 0;
		double denominator = 0;
		double numerator = 0;
		
		for (String key : dist.keySet()) {
			numerator = Math.pow(dist.get(key).datasetValues[0].getValue(func, normalize) - dist.get(key).datasetValues[1].getValue(func, normalize), 2);
			denominator = Math.abs(dist.get(key).datasetValues[1].getValue(func, normalize));
			utility += numerator/denominator;
		}
		return utility;
	}
	
	public static double ChiSquaredDistance(HashMap<String, AggregateValuesWrapper> dist, boolean normalize) {
		return ChiSquaredDistance(dist, AggregateFunctions.SUM, normalize);
	}
	
	/**
	 * Compute the Kullback-Leibler Entropy distance between the distribution generate from the query and the distribution from the entire dataset
	 * @param distributionForQuery
	 * @param distributionForDataset
	 * @return
	 */
	public static double EntropyDistance(HashMap<String, AggregateValuesWrapper> dist, AggregateFunctions func, boolean normalize) {
		if (dist.isEmpty()) {
			return DISTRIBUTION_EMPTY;
		}
		double utility = 0;
		for (String key : dist.keySet()) {
			utility += Math.log(Math.abs(dist.get(key).datasetValues[0].getValue(func, normalize)
					- dist.get(key).datasetValues[1].getValue(func, normalize)))
					* dist.get(key).datasetValues[0].getValue(func, normalize);
		}
		return utility;
	}
	
	public static double EntropyDistance(HashMap<String, AggregateValuesWrapper> dist, boolean normalize) {
		return EntropyDistance(dist, AggregateFunctions.SUM, normalize);
	}
}