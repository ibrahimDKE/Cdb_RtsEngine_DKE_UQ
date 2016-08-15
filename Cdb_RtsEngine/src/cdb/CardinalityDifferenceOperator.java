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

public class CardinalityDifferenceOperator implements DifferenceOperator {

	@Override
	public List<DifferenceQuery> getDifferenceQueries(
			InputQuery[] inputQueries, InputTablesMetadata[] queryMetadatas, 
			int numDatasets, ExperimentalSettings settings) {
		List<DifferenceQuery> queries = Lists.newArrayList();
		DifferenceQuery dq = new DifferenceQuery(
				ExperimentalSettings.DifferenceOperators.CARDINALITY, inputQueries);
		List<String> aggFuncs = Lists.newArrayList();
		aggFuncs.add("COUNT");
		dq.addAggregateAttribute(Attribute.selectAllAttribute(), aggFuncs);
		queries.add(dq);
		return queries;
	}

}
