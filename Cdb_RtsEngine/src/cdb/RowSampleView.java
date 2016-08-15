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

import com.google.common.collect.Lists;

import cdb.Utils;

/**
 * View that shows a set of rows from each dataset
 * @author manasi
 *
 */
public class RowSampleView implements View {
	public List<String> columnNames; // assume that both datasets have same columns
	public List<List<String>> rows1;
	public List<List<String>> rows2;
	
	public RowSampleView() {
		this.columnNames = Lists.newArrayList();
		this.rows1 = Lists.newArrayList();
		this.rows2 = Lists.newArrayList();
	}

	@Override
	public DifferenceOperators getOperator() {
		return DifferenceOperators.DATA_SAMPLE;
	}

	public List<String> getColumnNames() {
		return this.columnNames;
	}

	public List<List<List<String>>> getRows() {
		return Lists.newArrayList(this.rows1, this.rows2);
	}
	
	public String toString() {
		String ret = "";
		ret += "diff_type:row_sample_diff;";
		ret += "columnNames:" + Utils.serializeList(columnNames) + ";";
		ret += "dataset_num:1;";
		ret += "rows:" + Utils.serializeListofLists(rows1) + ";";
		ret += "dataset_num:2;";
		ret += "rows:" + Utils.serializeListofLists(rows2) + ";";
		return ret;
	}

	@Override
	public double getUtility(DistanceMetric distanceMetric) {
		return 0;
	}

	@Override
	public double getUtility(DistanceMetric distanceMetric,
			boolean normalizeDistributions) {
		// TODO Auto-generated method stub
		return 0;
	}
        
         //Added by Himos
        @Override
	public double getUtilitywithfunc(DistanceMetric distanceMetric,Constants.AggregateFunctions AGF, boolean normalizeDistributions) {
		return 0;
	}
	
	public List<View> constituentViews() {
		List<View> res = Lists.newArrayList();
		res.add(this);
		return res;
	}
        
}
