package org.matsim.prepare;


import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.routes.LinkNetworkRouteFactory;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.TransitScheduleUtils;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.vehicles.MatsimVehicleWriter;
import org.matsim.vehicles.VehicleType;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.matsim.core.utils.io.MatsimFileTypeGuesser.FileType.Network;

public class CreateSuperU81{

	private static LinkNetworkRouteFactory routeFactory = new LinkNetworkRouteFactory();
	private static NetworkFactory networkFactory = NetworkUtils.createNetwork().getFactory();
	private static TransitScheduleFactory scheduleFactory = ScenarioUtils.createScenario(ConfigUtils.createConfig()).getTransitSchedule().getFactory();

	public static void main(String[] args) {

		var root = Paths.get(".\\scenarios\\input");


		var scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		var network = NetworkUtils.readNetwork("scenarios/input/duesseldorf-v1.7-network-with-pt.xml.gz");
		var typeId = Id.create("Super81", VehicleType.class);
		var vehicleType = scenario.getVehicles().getFactory().createVehicleType(typeId);
		// vehicle settings
		vehicleType.setNetworkMode(TransportMode.pt);
		vehicleType.getCapacity().setStandingRoom(100);
		vehicleType.getCapacity().setSeats(10000);
		vehicleType.setLength(20);
		vehicleType.setMaximumVelocity(36);
		// add vehicle to scenario
		scenario.getTransitVehicles().addVehicleType(vehicleType);

		//start and end pt and add to network
		var pt_start = network.getFactory().createNode(Id.createNodeId("81-start"), new Coord( 349449.09 + 100, 5685550.5 + 100));;
		var pt_end = network.getFactory().createNode(Id.createNodeId("81-end"), new Coord(336566.87+100, 5675732.0200000005+100));
		network.addNode(pt_start);
		network.addNode(pt_end);
		var fromNode = network.getNodes().get(Id.createNodeId("7926173979"));
		var toNode = network.getNodes().get(Id.createNodeId("253117775"));

		// pt link for north-south
		var start_link = createLink("u81_1", pt_start, fromNode);
		var connecting_link = createLink("u81_2", fromNode, toNode);
		var end_link = createLink("u81_3", toNode, pt_end);
		network.addLink(connecting_link);
		network.addLink(start_link);
		network.addLink(end_link);

		// network route
		NetworkRoute networkRoute = RouteUtils.createLinkNetworkRouteImpl(start_link.getId(), List.of(connecting_link.getId()), end_link.getId());

		// facilities
		var stop1_facility = scheduleFactory.createTransitStopFacility(Id.create("stop_1", TransitStopFacility.class), pt_start.getCoord(), false);
		var stop2_facility = scheduleFactory.createTransitStopFacility(Id.create("stop_2", TransitStopFacility.class), pt_end.getCoord(), false);
		stop1_facility.setLinkId(start_link.getId());
		stop2_facility.setLinkId(end_link.getId());
		scenario.getTransitSchedule().addStopFacility(stop1_facility);
		scenario.getTransitSchedule().addStopFacility(stop2_facility);

		// stations
		var stop1 = scheduleFactory.createTransitRouteStop(stop1_facility, 0, 0); //why 0,0?
		var stop2 = scheduleFactory.createTransitRouteStop(stop2_facility, 3600, 3610);

		//route
		var route = scheduleFactory.createTransitRoute(Id.create("u81_route_1", TransitRoute.class), networkRoute, List.of(stop1, stop2), "pt");

		// create departures and vehicles for each departure
		for (int i = 0 * 3600; i < 23 * 3600; i += 300) {
			var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
			var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("super_u81_vehicle_" + i), vehicleType);
			departure.setVehicleId(vehicle.getId());

			scenario.getTransitVehicles().addVehicle(vehicle);
			route.addDeparture(departure);
		}

		// line
		var line = scheduleFactory.createTransitLine(Id.create("u81_n_s", TransitLine.class));
		line.addRoute(route);
		scenario.getTransitSchedule().addTransitLine(line);

		new NetworkWriter(network).write(root.resolve("network-with-super81.xml.gz").toString());
		new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(root.resolve("transit-Schedule-super81.xml.gz").toString());
		new MatsimVehicleWriter(scenario.getTransitVehicles()).writeFile(root.resolve("transit-vehicles-super81.xml.gz").toString());


	}

	private static Link createLink(String id, Node from, Node to) {

		var connection = networkFactory.createLink(Id.createLinkId(id), from, to);
		connection.setAllowedModes(Set.of(TransportMode.pt));
		connection.setFreespeed(100);
		connection.setCapacity(10000);
		return connection;
	}
}


