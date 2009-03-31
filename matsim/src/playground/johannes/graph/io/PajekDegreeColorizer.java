/* *********************************************************************** *
 * project: org.matsim.*
 * PajekScalarColorizer.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

/**
 * 
 */
package playground.johannes.graph.io;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import playground.johannes.graph.Edge;
import playground.johannes.graph.Graph;
import playground.johannes.graph.GraphStatistics;
import playground.johannes.graph.Vertex;

/**
 * @author illenberger
 *
 */
public class PajekDegreeColorizer<V extends Vertex, E extends Edge> extends PajekColorizer<V, E> {
	
	private double k_min;
	
	private double k_max;
	
	private boolean logScale;
	
	public PajekDegreeColorizer(Graph g, boolean logScale) {
		super();
		setLogScale(logScale);
		DescriptiveStatistics stats = GraphStatistics.getDegreeStatistics(g);
		k_min = stats.getMin();
		k_max = stats.getMax();
	}
	
	public void setLogScale(boolean flag) {
		logScale = flag;
	}
	
	public String getEdgeColor(E e) {
		return getColor(-1);
	}

	public String getVertexFillColor(V ego) {
		int k = ego.getNeighbours().size();
		double color = -1;
		if(logScale) {
			double min = Math.log(k_min + 1);
			double max = Math.log(k_max + 1);
			color = (Math.log(k + 1) - min) / (max - min);
		} else {
			color = (k - k_min) / (k_max - k_min);
		}
		
		return getColor(color);
	}
}
