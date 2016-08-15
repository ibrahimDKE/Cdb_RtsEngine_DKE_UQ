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


public class AggregateValuesWrapper {
	public class AggregateValues {
		public double count;
		public double sum;
		public double average;
		public double countNormalized;
		public double sumNormalized;
		public double averageNormalized;
		public double generic;
		public double genericNormalized;
                public double Maxim;
                public double Minim;
                public double MaximNormalized;
                public double MinimNormalized;
		//public String MaxAggFunUtlity; //Added By Himos
		public AggregateValues() {
			
		}
		
		public AggregateValues(double count, double sum, double average,double Maxim,double Minim) {
			this.count = count;
			this.sum = sum;
			this.average = average;
                        this.Maxim = Maxim;
                        this.Minim = Minim;
		}
		
		public double getValue(AggregateFunctions func, boolean normalize) {
			switch (func) {
			case ALL:
				return !normalize ? sum : sumNormalized;
			case COUNT:
                            //Added by HImos
                            return !normalize ? count : countNormalized;
			case SUM:
                            //Added by Himos
                            return !normalize ? sum : sumNormalized;
			case AVG:
				return !normalize ? average : averageNormalized;
                        case MAX:
				return !normalize ? Maxim : MaximNormalized;
                        case MIN:
				return !normalize ? Minim : MinimNormalized;    
			}
			return -1;
		}
	}
	public AggregateValues datasetValues[] = {new AggregateValues(), new AggregateValues()};
	
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof AggregateValuesWrapper)) {
			return false;
		}
		AggregateValuesWrapper o_ = (AggregateValuesWrapper) o;
		for (int i = 0 ; i < datasetValues.length; i++) {
			if ((Math.abs(o_.datasetValues[i].count - this.datasetValues[i].count) > 0.01) || 
			    (Math.abs(o_.datasetValues[i].sum - this.datasetValues[i].sum) > 0.01) || 
                                (Math.abs(o_.datasetValues[i].Maxim - this.datasetValues[i].Maxim) > 0.01) || 
                                (Math.abs(o_.datasetValues[i].Minim - this.datasetValues[i].Minim) > 0.01) || 
			    (Math.abs(o_.datasetValues[i].average - this.datasetValues[i].average) > 0.01)) {
				return false;
			}
		}
		return true;
	}
}
