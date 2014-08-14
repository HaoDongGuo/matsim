/*
 * *********************************************************************** *
 * project: org.matsim.*                                                   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2014 by the members listed in the COPYING,        *
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
 * *********************************************************************** *
 */

package playground.boescpa.converters.vissim;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import playground.boescpa.converters.vissim.tools.*;

import java.util.HashMap;

/**
 * Provides the environment to convert MATSim-Events to an ANMRoutes file importable in VISSIM.
 *
 * @author boescpa
 */
public class ConvEvents2Anm {

	private final BaseGridCreator baseGridCreator;
	private final NetworkMatcher networkMatcher;
	private final RouteConverter routeConverter;
	private final TripMatcher tripMatcher;

	public ConvEvents2Anm(BaseGridCreator baseGridCreator, NetworkMatcher networkMatcher,
						  RouteConverter routeConverter, TripMatcher tripMatcher) {
		this.baseGridCreator = baseGridCreator;
		this.networkMatcher = networkMatcher;
		this.routeConverter = routeConverter;
		this.tripMatcher = tripMatcher;
	}

	public static void main(String[] args) {
		ConvEvents2Anm convEvents2Anm = createDefaultConvEvents2Anm();
		convEvents2Anm.convert(args);
	}

	public static ConvEvents2Anm createDefaultConvEvents2Anm() {
		return new ConvEvents2Anm(new DefaultBaseGridCreator(), new DefaultNetworkMatcher(),
				new DefaultRouteConverter(), new DefaultTripMatcher());
	}

	public void convert(String[] args) {
		String path2VissimZoneShp = args[0];
		String path2MATSimNetwork = args[1];
		String path2VissimNetworkAnm = args[2];
		String path2EventsFile = args[3];
		String path2AnmroutesFile = args[4];
		String path2NewAnmFile = args[5];

		Network mutualBaseGrid = this.baseGridCreator.createMutualBaseGrid(path2VissimZoneShp);
		HashMap<Id, Id[]> keyMsNetwork = this.networkMatcher.mapMsNetwork(path2MATSimNetwork, mutualBaseGrid, path2VissimZoneShp);
		HashMap<Id, Id[]> keyAmNetwork = this.networkMatcher.mapAmNetwork(path2VissimNetworkAnm, mutualBaseGrid);
		HashMap<Id, Long[]> msTrips = this.routeConverter.convertEvents(keyMsNetwork, path2EventsFile, path2MATSimNetwork, path2VissimZoneShp);
		HashMap<Id, Long[]> amTrips = this.routeConverter.convertRoutes(keyAmNetwork, path2AnmroutesFile);
		HashMap<Id, Integer> demandPerAnmTrip = this.tripMatcher.matchTrips(msTrips, amTrips);
		writeAnmRoutes(demandPerAnmTrip, path2AnmroutesFile, path2NewAnmFile);
	}

	/**
	 * Rewrite ANMRoutes file with new demand numbers for each ANM-Route
	 *
	 * @param demandPerAnmTrip
	 * @param path2AnmFile
	 * @param path2NewAnmFile At the specified location a new ANMRoutes-File will be created. It is an exact copy
	 *                        of the given AnmFile except for the demands stated at the routes. These are the new
	 *                        demands given in demandPerAnmTrip.
	 */
	public void writeAnmRoutes(HashMap<Id, Integer> demandPerAnmTrip, String path2AnmFile, String path2NewAnmFile) {

	};

	public interface BaseGridCreator {

		/**
		 * Create mutual base grid.
		 *
		 * @param path2VissimZoneShp
		 * @return A new data set (nodes) which represents both input networks jointly.
		 */
		public Network createMutualBaseGrid(String path2VissimZoneShp);

	}

	public interface NetworkMatcher {

		/**
		 * Creates a key that maps the provided matsim network (links) to the mutual base grid.
		 * If the matsim network is larger than the zones provided the network is cut.
		 *
		 * @param path2MATSimNetwork
		 * @param mutualBaseGrid
		 * @param path2VissimZoneShp
		 * @return The key that matches the network (links) to the base grid.
		 */
		HashMap<Id,Id[]> mapMsNetwork(String path2MATSimNetwork, Network mutualBaseGrid, String path2VissimZoneShp);

		/**
		 * Creates a key that maps the provided Vissim/Visum network (its links) to the mutual base grid.
		 *
		 * @param path2VissimNetworkAnm	Visum-Anm-Format
		 * @param mutualBaseGrid	MATSim-Network-Format
		 * @return The key that matches the network (links) to the base grid.
		 */
		HashMap<Id,Id[]> mapAmNetwork(String path2VissimNetworkAnm, Network mutualBaseGrid);
	}

	public interface RouteConverter {

		/**
		 * Convert MATSim-Events to trips in matched network (in the matched zone).
		 *
		 * @param keyMsNetwork
		 * @param path2EventsFile
		 * @param path2VissimZoneShp
		 * @return A HashMap which represents each trip (derived from events, assigned a trip Id) in the form of
		 * 			an id-array (Long[]) representing a sequence of elements of the matched network.
		 */
		public HashMap<Id,Long[]> convertEvents(HashMap<Id, Id[]> keyMsNetwork, String path2EventsFile, String path2MATSimNetwork, String path2VissimZoneShp);

		/**
		 * Convert ANM-Routes to trips in matched network
		 *
		 * @param keyAmNetwork
		 * @param path2AnmFile
		 * @return A HashMap which represents each trip (derived from AnmRoutes, assigned the AnmRoute Id) in the form
		 * 			of an id-array (Long[]) representing a sequence of elements of the matched network.
		 */
		public HashMap<Id,Long[]> convertRoutes(HashMap<Id, Id[]> keyAmNetwork, String path2AnmFile);
	}

	public interface TripMatcher {

		/**
		 * Find for each MATSim-trip a best match in the ANM-trips
		 *
		 * @param msTrips
		 * @param amTrips
		 * @return A HashMap having for each ANM-trip (Id) the number (Integer) of MATSim-trips which were found to
		 * 			to match this ANM-trip best.
		 */
		HashMap<Id,Integer> matchTrips(HashMap<Id, Long[]> msTrips, HashMap<Id, Long[]> amTrips);
	}
}