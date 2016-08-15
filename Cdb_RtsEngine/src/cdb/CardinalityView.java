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
import java.util.List;

import cdb.ExperimentalSettings.DifferenceOperators;
import cdb.ExperimentalSettings.DistanceMetric;

import com.google.common.collect.Lists;


/**
 * View that returns how many tuples are present in the full dataset
 * @author manasi
 *
 */
public class CardinalityView extends AggregateView {
	public CardinalityView(DifferenceQuery dq) {
		super(dq);
	}

	@Override
	public DifferenceOperators getOperator() {
		return DifferenceOperators.CARDINALITY;
	}

	public List<Double> getCardinalities() {
		if (this.aggregateValues == null || this.aggregateValues.get("none") == null) {
			return Lists.newArrayList(null, null);
		}
		
		AggregateValuesWrapper cardinalityAggregates = this.aggregateValues.get("none");
		
		// do a bunch of stuff to avoid possible null pointer exceptions :[
		List<Double> output = Lists.newArrayList(null, null);
		
		if (cardinalityAggregates.datasetValues[0] == null) {
			output.set(0, null);
		} else {
			output.set(0, cardinalityAggregates.datasetValues[0].count);
		}
		
		if (cardinalityAggregates.datasetValues[1] == null) {
			output.set(1, null);
		} else {
			output.set(1, cardinalityAggregates.datasetValues[1].count);
		}
		return output;
	}

	public String toString() {
		String ret = "";
		ret += "diff_type:cardinality_diff;";
		ret += "dataset_num:1;";
		ret += "cardinality:" + this.aggregateValues.get("none").datasetValues[0].count + ";";
		ret += "dataset_num:2;";
		ret += "cardinality:" + this.aggregateValues.get("none").datasetValues[1].count + ";";
		return ret;
	}

	@Override
	public double getUtility(DistanceMetric distanceMetric) {
		return 0;
	}
	
	@Override
	public double getUtility(DistanceMetric distanceMetric, boolean normalizeDistributions) {
		return 0;
	}
        //Added by Himos
        @Override
	public double getUtilitywithfunc(DistanceMetric distanceMetric,AggregateFunctions AGF, boolean normalizeDistributions) {
		return 0;
	}

	@Override
	public List<View> constituentViews() {
		List<View> res = Lists.newArrayList();
		res.add(this);
		return res;
	}
        
        
        
	
}
