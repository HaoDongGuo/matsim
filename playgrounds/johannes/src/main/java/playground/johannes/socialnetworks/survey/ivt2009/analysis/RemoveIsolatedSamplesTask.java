/* *********************************************************************** *
 * project: org.matsim.*
 * RemoveIsolatedSamplesTask.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
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
package playground.johannes.socialnetworks.survey.ivt2009.analysis;

import java.util.HashSet;
import java.util.Set;

import playground.johannes.socialnetworks.snowball2.analysis.SnowballPartitions;
import playground.johannes.socialnetworks.survey.ivt2009.graph.SampledSocialGraph;
import playground.johannes.socialnetworks.survey.ivt2009.graph.SampledSocialGraphBuilder;
import playground.johannes.socialnetworks.survey.ivt2009.graph.SampledSocialVertex;

/**
 * @author illenberger
 *
 */
public class RemoveIsolatedSamplesTask implements GraphTask<SampledSocialGraph> {

	@SuppressWarnings("unchecked")
	@Override
	public SampledSocialGraph apply(SampledSocialGraph graph) {
		Set<SampledSocialVertex> partition = SnowballPartitions.createSampledPartition((Set<SampledSocialVertex>)(graph.getVertices()));
		Set<SampledSocialVertex> remove = new HashSet<SampledSocialVertex>();
		for(SampledSocialVertex vertex : partition) {
			if(vertex.getNeighbours().size() < 2) {
				remove.add(vertex);
			}
		}
		
		SampledSocialGraphBuilder builder = new SampledSocialGraphBuilder();
		for(SampledSocialVertex vertex : remove)
			builder.removeVertex(graph, vertex);
		
		return graph;
	}

}
