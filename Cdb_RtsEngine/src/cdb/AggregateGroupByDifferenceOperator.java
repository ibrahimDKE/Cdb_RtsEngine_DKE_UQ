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

import cdb.ExperimentalSettings;

import com.google.common.collect.Lists;

import cdb.Attribute;
import cdb.DifferenceQuery;
import cdb.InputQuery;
import cdb.InputTablesMetadata;
import cdb.Utils;

public class AggregateGroupByDifferenceOperator implements DifferenceOperator {

	@Override
	public List<DifferenceQuery> getDifferenceQueries(
			InputQuery[] inputQueries, InputTablesMetadata[] queryMetadatas, 
			int numDatasets, ExperimentalSettings settings) {
		List<DifferenceQuery> queries = Lists.newArrayList();
		List<Attribute> dimAttr = queryMetadatas[0].getDimensionAttributes();
		List<Attribute> aggAttr = queryMetadatas[0].getMeasureAttributes();
		
		// get group bys
		int gbSize = settings.groupBySize;
		List<List<Attribute>> gbs = Utils.getGroups(dimAttr, gbSize);
		for (int i = 0; i < gbs.size(); i++) {
			for (int j = 0; j < aggAttr.size(); j++) {
				DifferenceQuery dq = new DifferenceQuery(
						ExperimentalSettings.DifferenceOperators.AGGREGATE, inputQueries);
				dq.groupByAttributes.addAll(gbs.get(i));
				List<String> aggFuncs = Lists.newArrayList();
				aggFuncs.add("COUNT");
				aggFuncs.add("SUM");
				aggFuncs.add("AVG");
                                aggFuncs.add("MAX");
                                aggFuncs.add("MIN");
				dq.addAggregateAttribute(aggAttr.get(j), aggFuncs);
				dq.derivedFrom.add(dq);
				queries.add(dq);
			}
		}
		return queries;
	}

}
