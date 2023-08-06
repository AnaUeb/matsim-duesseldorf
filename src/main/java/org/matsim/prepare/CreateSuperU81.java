package org.matsim.prepare;


import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.mobsim.qsim.pt.TransitVehicle;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.routes.LinkNetworkRouteFactory;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.TransitScheduleUtils;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.vehicles.*;

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

		// read in existing files
		var transitSchedule = Paths.get(".\\scenarios\\input\\duesseldorf-v1.7-transitSchedule.xml.gz");
		var vehicleFile = Paths.get(".\\scenarios\\input\\duesseldorf-v1.7-transitVehicles.xml.gz");
		new TransitScheduleReader(scenario).readFile(transitSchedule.toString());
		var network = NetworkUtils.readNetwork(".\\scenarios\\input\\duesseldorf-v1.7-network-with-pt.xml.gz");

		MatsimVehicleReader vehicleReader = new MatsimVehicleReader(scenario.getTransitVehicles());
		vehicleReader.readFile(vehicleFile.toString());

		// vehicle types
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
		var RatingenWest = network.getNodes().get(Id.createNodeId("7926173979"));
		var NeussRheinparkcenter = network.getNodes().get(Id.createNodeId("253117775"));
		var Freiligrathplatz  = network.getNodes().get(Id.createNodeId("pt_de:05111:18028:2:7"));
		var DLoerick = network.getNodes().get(Id.createNodeId("25938824"));


		// pt link n > s
		var start_link_n_s = createLink("u81_1_ns", pt_start,RatingenWest);
		var connecting_link_n_s_1 = createLink("u81_2_ns", RatingenWest,Freiligrathplatz);
		var connecting_link_n_s_2 = createLink("u81_3_ns", Freiligrathplatz, DLoerick);
		var connecting_link_n_s_3 = createLink("u81_4_ns", DLoerick,NeussRheinparkcenter);
		var end_link_n_s = createLink("u81_5_ns", NeussRheinparkcenter, pt_end);
		network.addLink(connecting_link_n_s_1);
		network.addLink(connecting_link_n_s_2);
		network.addLink(connecting_link_n_s_3);
		network.addLink(start_link_n_s);
		network.addLink(end_link_n_s);

		// pt link s > n
		var route = List.of();
		var start_link_s_n = createLink("u81_1_sn", pt_end,NeussRheinparkcenter);
		var connecting_link_s_n = createLink("u81_2_sn", NeussRheinparkcenter, DLoerick);
		var connecting_link_s_n_1 = createLink("u81_3_sn", DLoerick, Freiligrathplatz);
		var connecting_link_s_n_2 = createLink("u81_4_sn", Freiligrathplatz,RatingenWest);
		var end_link_s_n = createLink("u81_5_sn", RatingenWest, pt_start);
		network.addLink(connecting_link_s_n);
		network.addLink(connecting_link_s_n_1);
		network.addLink(connecting_link_s_n_2);
		network.addLink(start_link_s_n);
		network.addLink(end_link_s_n);


		// network route n > s and s > n
		NetworkRoute networkRoute_n_s = RouteUtils.createLinkNetworkRouteImpl(start_link_n_s.getId(),
				List.of(connecting_link_n_s_1.getId(),connecting_link_n_s_2.getId(),connecting_link_n_s_3.getId()), end_link_n_s.getId());
		NetworkRoute networkRoute_s_n = RouteUtils.createLinkNetworkRouteImpl(start_link_s_n.getId(),
				List.of(connecting_link_s_n.getId(),connecting_link_s_n_1.getId(),connecting_link_s_n_2.getId()), end_link_s_n.getId());


		// facilities n > s
		var stop1_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("stop_1_ns", TransitStopFacility.class),pt_start.getCoord(),false);
		var stop2_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("stop_2_ns",TransitStopFacility.class), Freiligrathplatz.getCoord(),false);
		var stop3_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("stop_3_ns", TransitStopFacility.class),DLoerick.getCoord(),false);
		var stop4_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("stop_4_ns",TransitStopFacility.class), pt_end.getCoord(),false);
		stop1_facility_n_s.setLinkId(start_link_n_s.getId());
		stop2_facility_n_s.setLinkId(connecting_link_n_s_1.getId());
		stop3_facility_n_s.setLinkId(connecting_link_n_s_2.getId());
		stop4_facility_n_s.setLinkId(end_link_n_s.getId());
		scenario.getTransitSchedule().addStopFacility(stop1_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop2_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop3_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop4_facility_n_s);

		// facilities s > n
		var stop1_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("stop_1_sn", TransitStopFacility.class),pt_start.getCoord(),false);
		var stop2_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("stop_2_sn", TransitStopFacility.class),DLoerick.getCoord(),false);
		var stop3_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("stop_3_sn", TransitStopFacility.class),Freiligrathplatz.getCoord(),false);
		var stop4_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("stop_4_sn",TransitStopFacility.class), pt_end.getCoord(),false);
		stop1_facility_s_n.setLinkId(start_link_s_n.getId());
		stop2_facility_s_n.setLinkId(connecting_link_s_n.getId());
		stop3_facility_s_n.setLinkId(connecting_link_s_n_1.getId());
		stop4_facility_s_n.setLinkId(end_link_s_n.getId());
		scenario.getTransitSchedule().addStopFacility(stop1_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop2_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop3_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop4_facility_s_n);

		// stations s > n
		var stop1_n_s = scheduleFactory.createTransitRouteStop(stop1_facility_n_s,0,0); //why 0,0?
		var stop2_n_s = scheduleFactory.createTransitRouteStop(stop2_facility_n_s,1200,1210);
		var stop3_n_s = scheduleFactory.createTransitRouteStop(stop3_facility_n_s,2400,2410); //why 0,0?
		var stop4_n_s = scheduleFactory.createTransitRouteStop(stop4_facility_n_s,3600,3610);

		// stations N > S
		var stop1_s_n = scheduleFactory.createTransitRouteStop(stop1_facility_s_n,0,0); //why 0,0?
		var stop2_s_n = scheduleFactory.createTransitRouteStop(stop2_facility_s_n,1200,1210);
		var stop3_s_n = scheduleFactory.createTransitRouteStop(stop3_facility_s_n,2400,2410); //why 0,0?
		var stop4_s_n = scheduleFactory.createTransitRouteStop(stop4_facility_s_n,3600,3610);

		//route
		var route_n_s = scheduleFactory.createTransitRoute(Id.create("route_1_ns", TransitRoute.class),networkRoute_n_s,List.of(stop1_n_s,stop2_n_s,stop3_n_s,stop4_n_s),"pt");
		var route_s_n = scheduleFactory.createTransitRoute(Id.create("route_1_sn", TransitRoute.class),networkRoute_s_n,List.of(stop1_s_n,stop2_s_n,stop3_s_n,stop4_s_n),"pt");

		// create departures and vehicles for each departure N > S
		for (int i = 0 * 3600; i < 24 * 3600; i += 300) {
			var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
			var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("shuttle_vehicle_n_" + i), vehicleType);
			departure.setVehicleId(vehicle.getId());

			scenario.getTransitVehicles().addVehicle(vehicle);
			route_n_s.addDeparture(departure);
		}

		// create departures and vehicles for each departure S > N
		for (int i = 0 * 3600; i < 24 * 3600; i += 300) {
			var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
			var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("shuttle_vehicle_s_" + i), vehicleType);
			departure.setVehicleId(vehicle.getId());

			scenario.getTransitVehicles().addVehicle(vehicle);
			route_s_n.addDeparture(departure);
		}

		// line N > S
		var line_n_s = scheduleFactory.createTransitLine(Id.create("shuttle_n_s", TransitLine.class));
		line_n_s.addRoute(route_n_s);
		scenario.getTransitSchedule().addTransitLine(line_n_s);

		// line S > N
		var line_s_n = scheduleFactory.createTransitLine(Id.create("shuttle_s_n", TransitLine.class));
		line_s_n.addRoute(route_s_n);
		scenario.getTransitSchedule().addTransitLine(line_s_n);

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
