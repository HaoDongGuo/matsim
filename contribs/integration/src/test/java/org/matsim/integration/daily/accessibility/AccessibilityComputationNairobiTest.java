/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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
package org.matsim.integration.daily.accessibility;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.contrib.accessibility.AccessibilityStartupListener;
import org.matsim.contrib.accessibility.Modes4Accessibility;
import org.matsim.contrib.accessibility.utils.AccessibilityRunUtils;
import org.matsim.contrib.accessibility.utils.VisualizationUtils;
import org.matsim.contrib.matrixbasedptrouter.MatrixBasedPtRouterConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlansConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.config.groups.VspExperimentalConfigGroup.VspDefaultsCheckingLevel;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.testcases.MatsimTestUtils;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author dziemke
 */
public class AccessibilityComputationNairobiTest {
	public static final Logger log = Logger.getLogger(AccessibilityComputationNairobiTest.class);

	private static final Double cellSize = 500.;

	@Rule public MatsimTestUtils utils = new MatsimTestUtils() ;

	@Test
	public void doAccessibilityTest() throws IOException {
		// Input and output
		String folderStructure = "../../";
		String networkFile = "matsimExamples/countries/ke/nairobi/2015-10-15_network.xml";
		// Adapt folder structure that may be different on different machines, in particular on server
		folderStructure = PathUtils.tryANumberOfFolderStructures(folderStructure, networkFile);
		networkFile = folderStructure + networkFile ;
//		final String facilitiesFile = folderStructure + "matsimExamples/countries/ke/nairobi/2015-10-15_facilities.xml";
//		final String facilitiesFile = "../../../shared-svn/projects/maxess/data/nairobi/land_use/nairobi_LU_2010/facilities.xml";
//		final String facilitiesFile = "../../../shared-svn/projects/maxess/data/nairobi/kodi/schools/secondary/facilities.xml";
		final String facilitiesFile = folderStructure + "matsimExamples/countries/ke/nairobi/2016-07-09_facilities_airports.xml"; //airports
//		final String facilitiesFile = "../../../shared-svn/projects/maxess/data/nairobi/facilities/04/facilities.xml"; //airports
		final String outputDirectory = utils.getOutputDirectory();
//		final String outputDirectory = "../../../shared-svn/projects/maxess/data/nairobi/output/27/";
//		String travelTimeMatrix = "../../../shared-svn/projects/maxess/data/nairobi/digital_matatus/gtfs/matrix/temp/tt.csv";
//		String travelDistanceMatrix = "../../../shared-svn/projects/maxess/data/nairobi/digital_matatus/gtfs/matrix/temp/td.csv";
//		String ptStops = "../../../shared-svn/projects/maxess/data/nairobi/digital_matatus/gtfs/matrix/temp/IDs.csv";
		
		// Parameters
		final String crs = "EPSG:21037"; // = Arc 1960 / UTM zone 37S, for Nairobi, Kenya
		final Envelope envelope = new Envelope(240000, 280000, 9844000, 9874000);
		String name = "ke_nairobi_" + cellSize.toString().split("\\.")[0];
		name = name + "_airport_min";
		
		// QGis parameters
		boolean createQGisOutput = true;
		final boolean includeDensityLayer = true;
		final Double lowerBound = -7.; // (upperBound - lowerBound) is ideally easily divisible by 7
		final Double upperBound = 0.0;
		final Integer range = 9; // In the current implementation, this must always be 9
		final int symbolSize = 510; // Usually chosen a little bit larger than cellSize
		final int populationThreshold = (int) (1 / (1000/cellSize * 1000/cellSize));
		
		// Config and scenario
		final Config config = ConfigUtils.createConfig(new AccessibilityConfigGroup(), new MatrixBasedPtRouterConfigGroup());
		config.network().setInputFile(networkFile);
		config.facilities().setInputFile(facilitiesFile);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory(outputDirectory);
		config.controler().setLastIteration(0);
		config.planCalcScore().setBrainExpBeta(200); // Set to high value to base computation on time to nearest facility only
		
		// Choose modes for accessibility computation
		AccessibilityConfigGroup accessibilityConfigGroup = ConfigUtils.addOrGetModule(config, AccessibilityConfigGroup.GROUP_NAME, AccessibilityConfigGroup.class);
		accessibilityConfigGroup.setComputingAccessibilityForMode(Modes4Accessibility.freeSpeed, true);
		accessibilityConfigGroup.setComputingAccessibilityForMode(Modes4Accessibility.car, true);

		// Matrix-based pt
//		MatrixBasedPtRouterConfigGroup mbpcg = (MatrixBasedPtRouterConfigGroup) config.getModule(MatrixBasedPtRouterConfigGroup.GROUP_NAME);
//		mbpcg.setPtStopsInputFile(ptStops);
//		mbpcg.setUsingTravelTimesAndDistances(true);
//		mbpcg.setPtTravelDistancesInputFile(travelDistanceMatrix);
//		mbpcg.setPtTravelTimesInputFile(travelTimeMatrix);
		
		// Some (otherwise irrelevant) settings to make the vsp check happy
		config.timeAllocationMutator().setMutationRange(7200.);
		config.timeAllocationMutator().setAffectingDuration(false);
		config.plans().setRemovingUnneccessaryPlanAttributes(true);
		config.plans().setActivityDurationInterpretation(PlansConfigGroup.ActivityDurationInterpretation.tryEndTimeThenDuration);
		
		config.vspExperimental().setVspDefaultsCheckingLevel(VspDefaultsCheckingLevel.warn);
		
		StrategySettings stratSets = new StrategySettings();
		stratSets.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta.toString());
		stratSets.setWeight(1.);
		config.strategy().addStrategySettings(stratSets);
		
		final Scenario scenario = ScenarioUtils.loadScenario(config);
		
		assertNotNull(config);
		
		// Collect activity types
//		final List<String> activityTypes = AccessibilityRunUtils.collectAllFacilityOptionTypes(scenario);
//		log.info("Found activity types: " + activityTypes);
		final List<String> activityTypes = new ArrayList<>();
		activityTypes.add("airport");
//		activityTypes.add("Commercial"); // land-use file version
//		activityTypes.add("Industrial"); // land-use file version
//		activityTypes.add("Public Purpose"); // land-use file version
//		activityTypes.add("Recreational"); // land-use file version
		
		// Network density points
		ActivityFacilities measuringPoints = AccessibilityRunUtils.createMeasuringPointsFromNetwork(scenario.getNetwork(), cellSize);
		double maximumAllowedDistance = 0.5 * cellSize;
		final ActivityFacilities densityFacilities = AccessibilityRunUtils.createNetworkDensityFacilities(
				scenario.getNetwork(), measuringPoints, maximumAllowedDistance);

		// Controller
		final Controler controler = new Controler(scenario);
		controler.addControlerListener(new AccessibilityStartupListener(activityTypes, densityFacilities, crs, name, envelope, cellSize));
		controler.run();
		
		// QGis
		if (createQGisOutput == true) {
			String osName = System.getProperty("os.name");
			String workingDirectory = config.controler().getOutputDirectory();
			for (String actType : activityTypes) {
				String actSpecificWorkingDirectory = workingDirectory + actType + "/";
				for ( Modes4Accessibility mode : Modes4Accessibility.values()) {
					VisualizationUtils.createQGisOutput(actType, mode, envelope, workingDirectory, crs, includeDensityLayer,
							lowerBound, upperBound, range, symbolSize, populationThreshold);
					VisualizationUtils.createSnapshot(actSpecificWorkingDirectory, mode, osName);
				}
			}  
		}
	}
}