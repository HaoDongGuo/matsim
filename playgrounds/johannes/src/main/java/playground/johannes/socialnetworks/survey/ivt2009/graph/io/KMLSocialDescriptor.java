/* *********************************************************************** *
 * project: org.matsim.*
 * KMLSocialDescriptor.java
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
package playground.johannes.socialnetworks.survey.ivt2009.graph.io;

import net.opengis.kml._2.PlacemarkType;

import org.matsim.contrib.sna.graph.spatial.SpatialVertex;

import playground.johannes.socialnetworks.snowball2.spatial.io.KMLSnowballDescriptor;
import playground.johannes.socialnetworks.survey.ivt2009.graph.SampledSocialVertex;

/**
 * @author illenberger
 *
 */
public class KMLSocialDescriptor extends KMLSnowballDescriptor {

	@Override
	public void addDetail(PlacemarkType kmlPlacemark, SpatialVertex object) {
		super.addDetail(kmlPlacemark, object);
		
		StringBuilder builder = new StringBuilder(kmlPlacemark.getDescription());
		builder.append("<br>Source: ");
		for(SampledSocialVertex vertex : ((SampledSocialVertex)object).getSources()) {
			builder.append(vertex.getPerson().getId().toString());
			builder.append(" ");
		}
		
		kmlPlacemark.setDescription(builder.toString());
	}

}
