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

import cdb.DifferenceQuery;
import cdb.InputQuery;
import cdb.InputTablesMetadata;

public interface DifferenceOperator {
	// TODO: this may not need experimental settings at all
	public List<DifferenceQuery> getDifferenceQueries(InputQuery[] inputQueries, 
			InputTablesMetadata[] queryMetadatas, int numDatasets, ExperimentalSettings settings);

}
