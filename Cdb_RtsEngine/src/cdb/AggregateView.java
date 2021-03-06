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

import cdb.ExperimentalSettings.DifferenceOperators;
import cdb.ExperimentalSettings.DistanceMetric;
import cdb.Constants.AggregateFunctions;
import cdb.AggregateValuesWrapper.AggregateValues;

import com.google.common.collect.Maps;


/**
 * Generic view class for differences that are based on aggregates and group-bys
 * @author manasi
 *
 */
public abstract class AggregateView implements View {
	public HashMap<String, AggregateValuesWrapper> aggregateValues;
	public String groupByAttribute;
	public String aggregateAttribute;
        //Added by Himos
       // public String MaxAggFunUtlity;
	
	public AggregateView(DifferenceQuery dq) {
		groupByAttribute = dq.groupByAttributes.get(0).name;
		aggregateAttribute = dq.aggregateAttributes.get(0).name;
		aggregateValues = Maps.newHashMap();
                //Added by Himos
                 //MaxAggFunUtlity="";
	}
	
	public AggregateView(String groupByAttribute, String aggregateAttribute) {
		this.groupByAttribute = groupByAttribute;
		this.aggregateAttribute = aggregateAttribute;
		aggregateValues = Maps.newHashMap();
                //Added by Himos
                 //MaxAggFunUtlity="";
	}

	@Override
	public abstract DifferenceOperators getOperator();

	/**
	 * return the aggregate values for the given group by values and for the 
	 * given dataset
	 * @param groupByValue
	 * @param group : 1 or 2 corresponding to dataset
	 * @return
	 */
	private AggregateValues getAggregateValues(String groupByValue, int group) {
		groupByValue = groupByValue.toLowerCase();
		if (!aggregateValues.containsKey(groupByValue)) {	
			aggregateValues.put(groupByValue, new AggregateValuesWrapper());
		}
		AggregateValuesWrapper w = aggregateValues.get(groupByValue);
		if (aggregateValues.get(groupByValue) == null) {
			System.out.println("here1 null");
		}
		if (aggregateValues.get(groupByValue).datasetValues == null) {
			System.out.println("here2 null");
		}
		return aggregateValues.get(groupByValue).datasetValues[group - 1]; // groups are 1, 2, must index from 0
		
	}

	/**
	 * Add the given value for the specified aggregate column and group by value for the
	 * specified group
	 * @param groupBy
	 * @param agg
	 * @param object
	 * @param group
	 */
	public synchronized void addAggregateValue(String groupBy, AggregateFunctions func, Object object, int group) {
		AggregateValues vals = getAggregateValues(groupBy, group);
		if (object == null) {
			System.out.println(object);
		}
		double newValue = (Double) object;
		switch (func) {
		case COUNT:
			vals.count += newValue;
			break;
		case SUM:
			vals.sum += newValue;
			break;
		case AVG:
                        //Added by HImos
                        //vals.average += newValue;
			System.out.println("cannot update avg directly");
			break;
                case MAX:
			vals.Maxim += newValue;
			break;
                case MIN:
			vals.Minim += newValue;
			break;
		}
	}

	public abstract double getUtility(DistanceMetric distanceMetric, boolean normalize);
}
