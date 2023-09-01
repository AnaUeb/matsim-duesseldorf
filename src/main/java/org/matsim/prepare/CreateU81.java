
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
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.vehicles.MatsimVehicleReader;
import org.matsim.vehicles.MatsimVehicleWriter;
import org.matsim.vehicles.VehicleType;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class CreateU81 {

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
		//var vehicleType1 = scenario.getTransitVehicles();
		// vehicle settings
		vehicleType.setNetworkMode(TransportMode.pt);
		vehicleType.getCapacity().setStandingRoom(600);
		vehicleType.getCapacity().setSeats(300);
		//vehicleType.setMaximumVelocity(36);
		// add vehicle to scenario
		scenario.getTransitVehicles().addVehicleType(vehicleType);

		//start and end pt and add to network
		var pt_start = network.getFactory().createNode(Id.createNodeId("81-start"), new Coord( 349449.09 + 100, 5685550.5 + 100));
		var pt_end = network.getFactory().createNode(Id.createNodeId("81-end"), new Coord(342235.96875+100, 567574.5+100));
		network.addNode(pt_start);
		network.addNode(pt_end);
		var RatingenWest = network.getNodes().get(Id.createNodeId("7926173979"));
		var Wasserwerk = network.getNodes().get(Id.createNodeId("pt_de:05158:19216:0:2"));
		var ImRott = network.getNodes().get(Id.createNodeId("pt_de:05158:19737:2:2"));
		var BDFlughafen = network.getNodes().get(Id.createNodeId("pt_de:05111:18488:97:5"));
		var DFTerminal = network.getNodes().get(Id.createNodeId("pt_de:05111:18517:98:SBg"));
		var Freiligrathplatz  = network.getNodes().get(Id.createNodeId("pt_de:05111:18028:2:7"));
		var Moerikestr = network.getNodes().get(Id.createNodeId("pt_de:05111:18027:0:1"));
		var MSAMesseNord = network.getNodes().get(Id.createNodeId("pt_de:05111:18075:0:2"));
		var DLoerick = network.getNodes().get(Id.createNodeId("pt_de:05111:18140:0:1"));
		var WillstaetterStr = network.getNodes().get(Id.createNodeId("2144666313"));
		var BoehlerWeg = network.getNodes().get(Id.createNodeId("6630866539"));
		var ZuelpicherStr = network.getNodes().get(Id.createNodeId("pt_de:05111:18974"));
		var DVogesenstr= network.getNodes().get(Id.createNodeId("pt_de:05111:18128:1:4"));
		var AmKaiser = network.getNodes().get(Id.createNodeId("pt_de:05162:20390"));
		var BluecherStr = network.getNodes().get(Id.createNodeId("pt_de:05162:20391:72:3"));
		var Rheintorstr = network.getNodes().get(Id.createNodeId("133197699"));
		var Hafenstr = network.getNodes().get(Id.createNodeId("pt_de:05162:20718:0:1"));
		var NBusbahnhof = network.getNodes().get(Id.createNodeId("pt_de:05162:20063:4:6"));
		var Stadthalle = network.getNodes().get(Id.createNodeId("pt_de:05162:20195:4:5"));
		var AlexianerPlatz = network.getNodes().get(Id.createNodeId("pt_de:05162:20196:1:2"));
		var Hammfelddamm = network.getNodes().get(Id.createNodeId("pt_de:05162:20114:1:1"));
		var Langemarkstr = network.getNodes().get(Id.createNodeId("pt_de:05162:20183:1:2"));
		var KoenigsbergerStr = network.getNodes().get(Id.createNodeId("2312184760"));
		var NeussRheinparkcenter = network.getNodes().get(Id.createNodeId("pt_de:05162:20496:2:2"));


		// pt link n > s
		var start_link_n_s = createLink("u81_1_ns", pt_start,RatingenWest);
		var cl_ns_1 = createLink("u81_2_ns", RatingenWest,Wasserwerk);
		var cl_ns_2 = createLink("u81_3_ns", Wasserwerk, ImRott);
		var cl_ns_3 = createLink("u81_4_ns", ImRott,BDFlughafen);
		var cl_ns_4 = createLink("u81_5_ns",BDFlughafen ,DFTerminal);
		var cl_ns_5 = createLink("u81_6_ns",DFTerminal, Freiligrathplatz );
		var cl_ns_6 = createLink("u81_7_ns", Freiligrathplatz ,Moerikestr );
		var cl_ns_7 = createLink("u81_8_ns", Moerikestr,MSAMesseNord );
		var cl_ns_8 = createLink("u81_9_ns", MSAMesseNord , DLoerick);
		var cl_ns_9 = createLink("u81_10_ns", DLoerick,WillstaetterStr);
		var cl_ns_10 = createLink("u81_11_ns",WillstaetterStr ,BoehlerWeg);
		var cl_ns_11 = createLink("u81_12_ns",BoehlerWeg, ZuelpicherStr );
		var cl_ns_12 = createLink("u81_13_ns", ZuelpicherStr ,DVogesenstr);
		var cl_ns_13 = createLink("u81_14_ns", DVogesenstr,AmKaiser);
		var cl_ns_14 = createLink("u81_15_ns",AmKaiser ,BluecherStr);
		var cl_ns_15 = createLink("u81_16_ns",BluecherStr, Rheintorstr );
		var cl_ns_16 = createLink("u81_17_ns", Rheintorstr ,Hafenstr);
		var cl_ns_17 = createLink("u81_18_ns", Hafenstr,NBusbahnhof );
		var cl_ns_18 = createLink("u81_19_ns", NBusbahnhof , Stadthalle );
		var cl_ns_19 = createLink("u81_20_ns", Stadthalle ,AlexianerPlatz );
		var cl_ns_20 = createLink("u81_21_ns",AlexianerPlatz ,Hammfelddamm);
		var cl_ns_21 = createLink("u81_22_ns", Hammfelddamm,Langemarkstr);
		var cl_ns_22 = createLink("u81_23_ns", Langemarkstr, KoenigsbergerStr );
		var cl_ns_23 = createLink("u81_24_ns", KoenigsbergerStr ,NeussRheinparkcenter);
		var end_link_n_s = createLink("u81_25_ns", NeussRheinparkcenter, pt_end);
		network.addLink(cl_ns_1);
		network.addLink(cl_ns_2);
		network.addLink(cl_ns_3);
		network.addLink(cl_ns_4);
		network.addLink(cl_ns_5);
		network.addLink(cl_ns_6);
		network.addLink(cl_ns_7);
		network.addLink(cl_ns_8);
		network.addLink(cl_ns_9);
		network.addLink(cl_ns_10);
		network.addLink(cl_ns_11);
		network.addLink(cl_ns_12);
		network.addLink(cl_ns_13);
		network.addLink(cl_ns_14);
		network.addLink(cl_ns_15);
		network.addLink(cl_ns_16);
		network.addLink(cl_ns_17);
		network.addLink(cl_ns_18);
		network.addLink(cl_ns_19);
		network.addLink(cl_ns_20);
		network.addLink(cl_ns_21);
		network.addLink(cl_ns_22);
		network.addLink(cl_ns_23);
		network.addLink(start_link_n_s);
		network.addLink(end_link_n_s);

		// pt link s > n
		var route = List.of();
		var start_link_s_n = createLink("u81_1_sn", pt_end,NeussRheinparkcenter);
		var cl_sn_1 = createLink("u81_2_sn",NeussRheinparkcenter,KoenigsbergerStr);
		var cl_sn_2 = createLink("u81_3_sn",KoenigsbergerStr,Langemarkstr);
		var cl_sn_3 = createLink("u81_4_sn",Langemarkstr,Hammfelddamm);
		var cl_sn_4 = createLink("u81_5_sn",Hammfelddamm,AlexianerPlatz);
		var cl_sn_5 = createLink("u81_6_sn",AlexianerPlatz,Stadthalle);
		var cl_sn_6 = createLink("u81_7_sn",Stadthalle,NBusbahnhof);
		var cl_sn_7 = createLink("u81_8_sn",NBusbahnhof,Hafenstr);
		var cl_sn_8 = createLink("u81_9_sn",Hafenstr,Rheintorstr);
		var cl_sn_9 = createLink("u81_10_sn",Rheintorstr,BluecherStr);
		var cl_sn_10 = createLink("u81_11_sn",BluecherStr,AmKaiser);
		var cl_sn_11 = createLink("u81_12_sn",AmKaiser,DVogesenstr);
		var cl_sn_12 = createLink("u81_13_sn",DVogesenstr,ZuelpicherStr);
		var cl_sn_13 = createLink("u81_14_sn",ZuelpicherStr,BoehlerWeg);
		var cl_sn_14 = createLink("u81_15_sn",BoehlerWeg,WillstaetterStr);
		var cl_sn_15 = createLink("u81_16_sn",WillstaetterStr,DLoerick);
		var cl_sn_16 = createLink("u81_17_sn",DLoerick,MSAMesseNord);
		var cl_sn_17 = createLink("u81_18_sn",MSAMesseNord,Moerikestr);
		var cl_sn_18 = createLink("u81_19_sn",Moerikestr,Freiligrathplatz);
		var cl_sn_19 = createLink("u81_20_sn",Freiligrathplatz,DFTerminal);
		var cl_sn_20 = createLink("u81_21_sn",DFTerminal,BDFlughafen);
		var cl_sn_21 = createLink("u81_22_sn",BDFlughafen,ImRott);
		var cl_sn_22 = createLink("u81_23_sn",ImRott,Wasserwerk);
		var cl_sn_23 = createLink("u81_24_sn",Wasserwerk,RatingenWest);
		var end_link_s_n = createLink("u81_25_sn", RatingenWest, pt_start);
		network.addLink(cl_sn_1);
		network.addLink(cl_sn_2);
		network.addLink(cl_sn_3);
		network.addLink(cl_sn_4);
		network.addLink(cl_sn_5);
		network.addLink(cl_sn_6);
		network.addLink(cl_sn_7);
		network.addLink(cl_sn_8);
		network.addLink(cl_sn_9);
		network.addLink(cl_sn_10);
		network.addLink(cl_sn_11);
		network.addLink(cl_sn_12);
		network.addLink(cl_sn_13);
		network.addLink(cl_sn_14);
		network.addLink(cl_sn_15);
		network.addLink(cl_sn_16);
		network.addLink(cl_sn_17);
		network.addLink(cl_sn_18);
		network.addLink(cl_sn_19);
		network.addLink(cl_sn_20);
		network.addLink(cl_sn_21);
		network.addLink(cl_sn_22);
		network.addLink(cl_sn_23);
		network.addLink(start_link_s_n);
		network.addLink(end_link_s_n);


		// network route n > s and s > n
		NetworkRoute networkRoute_n_s = RouteUtils.createLinkNetworkRouteImpl(start_link_n_s.getId(),
				List.of(cl_ns_1.getId(),cl_ns_2.getId(),cl_ns_3.getId(),cl_ns_4.getId(),cl_ns_5.getId(),cl_ns_6.getId(),
						cl_ns_7.getId(),cl_ns_8.getId(),cl_ns_9.getId(),cl_ns_10.getId(),cl_ns_11.getId(),
						cl_ns_12.getId(),cl_ns_13.getId(),cl_ns_14.getId(),cl_ns_15.getId(),cl_ns_16.getId(),
						cl_ns_17.getId(),cl_ns_18.getId(),cl_ns_19.getId(),cl_ns_20.getId(),
						cl_ns_21.getId(),cl_ns_22.getId(),cl_ns_23.getId()), end_link_n_s.getId());
		NetworkRoute networkRoute_s_n = RouteUtils.createLinkNetworkRouteImpl(start_link_s_n.getId(),
				List.of(cl_sn_1.getId(),cl_sn_2.getId(),cl_sn_3.getId(),cl_sn_4.getId(),cl_sn_5.getId(),cl_sn_6.getId(),
						cl_sn_7.getId(),cl_sn_8.getId(),cl_sn_9.getId(),cl_sn_10.getId(),cl_sn_11.getId(),
						cl_sn_12.getId(),cl_sn_13.getId(),cl_sn_14.getId(),cl_sn_15.getId(),cl_sn_16.getId(),
						cl_sn_17.getId(),cl_sn_18.getId(),cl_sn_19.getId(),cl_sn_20.getId(),
						cl_sn_21.getId(),cl_sn_22.getId(),cl_sn_23.getId()), end_link_s_n.getId());


		// facilities n > s
		var stop1_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("RatingenWest_ns", TransitStopFacility.class),pt_start.getCoord(),false);
		var stop2_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("Wasserwerk_ns", TransitStopFacility.class),Wasserwerk.getCoord(),false);
		var stop3_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("ImRott_ns", TransitStopFacility.class),ImRott.getCoord(),false);
		var stop4_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("BDFlughafen_ns", TransitStopFacility.class),BDFlughafen.getCoord(),false);
		var stop5_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("DFTerminal_ns", TransitStopFacility.class),DFTerminal.getCoord(),false);
		var stop6_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("Freiligrathplatz_ns",TransitStopFacility.class), Freiligrathplatz.getCoord(),false);
		var stop7_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("Moerikestr_ns", TransitStopFacility.class),Moerikestr.getCoord(),false);
		var stop8_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("MSAMesseNord_ns", TransitStopFacility.class),MSAMesseNord.getCoord(),false);
		var stop9_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("DLoerick_ns", TransitStopFacility.class),DLoerick.getCoord(),false);
		var stop10_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("WillstaetterStr_ns", TransitStopFacility.class),WillstaetterStr.getCoord(),false);
		var stop11_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("BoehlerWeg_ns", TransitStopFacility.class),BoehlerWeg.getCoord(),false);
		var stop12_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("ZuelpicherStr_ns", TransitStopFacility.class),ZuelpicherStr.getCoord(),false);
		var stop13_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("DVogesenstr_ns", TransitStopFacility.class),DVogesenstr.getCoord(),false);
		var stop14_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("AmKaiser_ns", TransitStopFacility.class),AmKaiser.getCoord(),false);
		var stop15_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("BluecherStr_ns", TransitStopFacility.class),BluecherStr.getCoord(),false);
		var stop16_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("Rheintorstr_ns", TransitStopFacility.class),Rheintorstr.getCoord(),false);
		var stop17_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("Hafenstr_ns", TransitStopFacility.class),Hafenstr.getCoord(),false);
		var stop18_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("NBusbahnhof_ns", TransitStopFacility.class),NBusbahnhof.getCoord(),false);
		var stop19_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("Stadthalle_ns", TransitStopFacility.class),Stadthalle.getCoord(),false);
		var stop20_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("AlexianerPlatz_ns", TransitStopFacility.class),AlexianerPlatz.getCoord(),false);
		var stop21_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("Hammfelddamm_ns", TransitStopFacility.class),Hammfelddamm.getCoord(),false);
		var stop22_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("Langemarkstr_ns", TransitStopFacility.class),Langemarkstr.getCoord(),false);
		var stop23_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("KoenigsbergerStr_ns", TransitStopFacility.class),KoenigsbergerStr.getCoord(),false);
		var stop24_facility_n_s = scheduleFactory.createTransitStopFacility(Id.create("NeussRheinparkcenter_ns",TransitStopFacility.class), pt_end.getCoord(),false);
		stop1_facility_n_s.setLinkId(start_link_n_s.getId());
		stop2_facility_n_s.setLinkId(cl_ns_1.getId());
		stop3_facility_n_s.setLinkId(cl_ns_2.getId());
		stop4_facility_n_s.setLinkId(cl_ns_3.getId());
		stop5_facility_n_s.setLinkId(cl_ns_4.getId());
		stop6_facility_n_s.setLinkId(cl_ns_5.getId());
		stop7_facility_n_s.setLinkId(cl_ns_6.getId());
		stop8_facility_n_s.setLinkId(cl_ns_7.getId());
		stop9_facility_n_s.setLinkId(cl_ns_8.getId());
		stop10_facility_n_s.setLinkId(cl_ns_9.getId());
		stop11_facility_n_s.setLinkId(cl_ns_10.getId());
		stop12_facility_n_s.setLinkId(cl_ns_11.getId());
		stop13_facility_n_s.setLinkId(cl_ns_12.getId());
		stop14_facility_n_s.setLinkId(cl_ns_13.getId());
		stop15_facility_n_s.setLinkId(cl_ns_14.getId());
		stop16_facility_n_s.setLinkId(cl_ns_15.getId());
		stop17_facility_n_s.setLinkId(cl_ns_16.getId());
		stop18_facility_n_s.setLinkId(cl_ns_17.getId());
		stop19_facility_n_s.setLinkId(cl_ns_18.getId());
		stop20_facility_n_s.setLinkId(cl_ns_19.getId());
		stop21_facility_n_s.setLinkId(cl_ns_20.getId());
		stop22_facility_n_s.setLinkId(cl_ns_21.getId());
		stop23_facility_n_s.setLinkId(cl_ns_22.getId());
		stop24_facility_n_s.setLinkId(end_link_n_s.getId());
		scenario.getTransitSchedule().addStopFacility(stop1_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop2_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop3_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop4_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop5_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop6_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop7_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop8_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop9_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop10_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop11_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop12_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop13_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop14_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop15_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop16_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop17_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop18_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop19_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop20_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop21_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop22_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop23_facility_n_s);
		scenario.getTransitSchedule().addStopFacility(stop24_facility_n_s);

		// facilities s > n
		var stop1_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("NeussRheinparkcenter_sn", TransitStopFacility.class),pt_start.getCoord(),false);
		var stop2_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("KoenigsbergerStr_sn", TransitStopFacility.class),KoenigsbergerStr.getCoord(),false);
		var stop3_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("Langemarkstr_sn", TransitStopFacility.class),Langemarkstr.getCoord(),false);
		var stop4_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("Hammfelddamm_sn", TransitStopFacility.class),Hammfelddamm.getCoord(),false);
		var stop5_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("AlexianerPlatz_sn", TransitStopFacility.class),AlexianerPlatz.getCoord(),false);
		var stop6_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("Stadthalle_sn", TransitStopFacility.class),Stadthalle.getCoord(),false);
		var stop7_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("NBusbahnhof_sn", TransitStopFacility.class),NBusbahnhof.getCoord(),false);
		var stop8_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("Hafenstr_sn", TransitStopFacility.class),Hafenstr.getCoord(),false);
		var stop9_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("Rheintorstr_sn", TransitStopFacility.class),Rheintorstr.getCoord(),false);
		var stop10_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("BluecherStr_sn", TransitStopFacility.class),BluecherStr.getCoord(),false);
		var stop11_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("AmKaiser_sn", TransitStopFacility.class),AmKaiser.getCoord(),false);
		var stop12_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("DVogesenstr_sn", TransitStopFacility.class),DVogesenstr.getCoord(),false);
		var stop13_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("ZuelpicherStr_sn", TransitStopFacility.class),ZuelpicherStr.getCoord(),false);
		var stop14_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("BoehlerWeg_sn", TransitStopFacility.class),BoehlerWeg.getCoord(),false);
		var stop15_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("WillstaetterStr_sn", TransitStopFacility.class),WillstaetterStr.getCoord(),false);
		var stop16_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("DLoerick_sn", TransitStopFacility.class),DLoerick.getCoord(),false);
		var stop17_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("MSAMesseNord_sn", TransitStopFacility.class),MSAMesseNord.getCoord(),false);
		var stop18_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("Moerikestr_sn", TransitStopFacility.class),Moerikestr.getCoord(),false);
		var stop19_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("Freiligrathplatz_sn",TransitStopFacility.class), Freiligrathplatz.getCoord(),false);
		var stop20_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("DFTerminal_sn", TransitStopFacility.class),DFTerminal.getCoord(),false);
		var stop21_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("BDFlughafen_sn", TransitStopFacility.class),BDFlughafen.getCoord(),false);
		var stop22_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("ImRott_sn", TransitStopFacility.class),ImRott.getCoord(),false);
		var stop23_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("Wasserwerk_sn", TransitStopFacility.class),Wasserwerk.getCoord(),false);
		var stop24_facility_s_n = scheduleFactory.createTransitStopFacility(Id.create("RatingenWest_sn",TransitStopFacility.class), pt_end.getCoord(),false);
		stop1_facility_s_n.setLinkId(start_link_s_n.getId());
		stop2_facility_s_n.setLinkId(cl_sn_1.getId());
		stop3_facility_s_n.setLinkId(cl_sn_2.getId());
		stop4_facility_s_n.setLinkId(cl_sn_3.getId());
		stop5_facility_s_n.setLinkId(cl_sn_4.getId());
		stop6_facility_s_n.setLinkId(cl_sn_5.getId());
		stop7_facility_s_n.setLinkId(cl_sn_6.getId());
		stop8_facility_s_n.setLinkId(cl_sn_7.getId());
		stop9_facility_s_n.setLinkId(cl_sn_8.getId());
		stop10_facility_s_n.setLinkId(cl_sn_9.getId());
		stop11_facility_s_n.setLinkId(cl_sn_10.getId());
		stop12_facility_s_n.setLinkId(cl_sn_11.getId());
		stop13_facility_s_n.setLinkId(cl_sn_12.getId());
		stop14_facility_s_n.setLinkId(cl_sn_13.getId());
		stop15_facility_s_n.setLinkId(cl_sn_14.getId());
		stop16_facility_s_n.setLinkId(cl_sn_15.getId());
		stop17_facility_s_n.setLinkId(cl_sn_16.getId());
		stop18_facility_s_n.setLinkId(cl_sn_17.getId());
		stop19_facility_s_n.setLinkId(cl_sn_18.getId());
		stop20_facility_s_n.setLinkId(cl_sn_19.getId());
		stop21_facility_s_n.setLinkId(cl_sn_20.getId());
		stop22_facility_s_n.setLinkId(cl_sn_21.getId());
		stop23_facility_s_n.setLinkId(cl_sn_21.getId());
		stop24_facility_s_n.setLinkId(end_link_s_n.getId());
		scenario.getTransitSchedule().addStopFacility(stop1_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop2_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop3_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop4_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop5_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop6_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop7_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop8_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop9_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop10_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop11_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop12_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop13_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop14_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop15_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop16_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop17_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop18_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop19_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop20_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop21_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop22_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop23_facility_s_n);
		scenario.getTransitSchedule().addStopFacility(stop24_facility_s_n);

		// stations s > n
		var stop1_n_s=scheduleFactory.createTransitRouteStop(stop1_facility_n_s,0,0);
		var stop2_n_s=scheduleFactory.createTransitRouteStop(stop2_facility_n_s,30,40);
		var stop3_n_s=scheduleFactory.createTransitRouteStop(stop3_facility_n_s,60,70);
		var stop4_n_s=scheduleFactory.createTransitRouteStop(stop4_facility_n_s,90,100);
		var stop5_n_s=scheduleFactory.createTransitRouteStop(stop5_facility_n_s,120,130);
		var stop6_n_s=scheduleFactory.createTransitRouteStop(stop6_facility_n_s,150,160);
		var stop7_n_s=scheduleFactory.createTransitRouteStop(stop7_facility_n_s,180,190);
		var stop8_n_s=scheduleFactory.createTransitRouteStop(stop8_facility_n_s,210,220);
		var stop9_n_s=scheduleFactory.createTransitRouteStop(stop9_facility_n_s,240,250);
		var stop10_n_s=scheduleFactory.createTransitRouteStop(stop10_facility_n_s,270,280);
		var stop11_n_s=scheduleFactory.createTransitRouteStop(stop11_facility_n_s,300,310);
		var stop12_n_s=scheduleFactory.createTransitRouteStop(stop12_facility_n_s,330,340);
		var stop13_n_s=scheduleFactory.createTransitRouteStop(stop13_facility_n_s,360,370);
		var stop14_n_s=scheduleFactory.createTransitRouteStop(stop14_facility_n_s,390,400);
		var stop15_n_s=scheduleFactory.createTransitRouteStop(stop15_facility_n_s,420,430);
		var stop16_n_s=scheduleFactory.createTransitRouteStop(stop16_facility_n_s,450,460);
		var stop17_n_s=scheduleFactory.createTransitRouteStop(stop17_facility_n_s,480,490);
		var stop18_n_s=scheduleFactory.createTransitRouteStop(stop18_facility_n_s,510,520);
		var stop19_n_s=scheduleFactory.createTransitRouteStop(stop19_facility_n_s,540,550);
		var stop20_n_s=scheduleFactory.createTransitRouteStop(stop20_facility_n_s,570,580);
		var stop21_n_s=scheduleFactory.createTransitRouteStop(stop21_facility_n_s,600,610);
		var stop22_n_s=scheduleFactory.createTransitRouteStop(stop22_facility_n_s,630,640);
		var stop23_n_s=scheduleFactory.createTransitRouteStop(stop23_facility_n_s,660,670);
		var stop24_n_s=scheduleFactory.createTransitRouteStop(stop24_facility_n_s,690,700);


		// stations n > s
		var stop1_s_n=scheduleFactory.createTransitRouteStop(stop1_facility_s_n,0,0);
		var stop2_s_n=scheduleFactory.createTransitRouteStop(stop2_facility_s_n,30,40);
		var stop3_s_n=scheduleFactory.createTransitRouteStop(stop3_facility_s_n,60,70);
		var stop4_s_n=scheduleFactory.createTransitRouteStop(stop4_facility_s_n,90,100);
		var stop5_s_n=scheduleFactory.createTransitRouteStop(stop5_facility_s_n,120,130);
		var stop6_s_n=scheduleFactory.createTransitRouteStop(stop6_facility_s_n,150,160);
		var stop7_s_n=scheduleFactory.createTransitRouteStop(stop7_facility_s_n,180,190);
		var stop8_s_n=scheduleFactory.createTransitRouteStop(stop8_facility_s_n,210,220);
		var stop9_s_n=scheduleFactory.createTransitRouteStop(stop9_facility_s_n,240,250);
		var stop10_s_n=scheduleFactory.createTransitRouteStop(stop10_facility_s_n,270,280);
		var stop11_s_n=scheduleFactory.createTransitRouteStop(stop11_facility_s_n,300,310);
		var stop12_s_n=scheduleFactory.createTransitRouteStop(stop12_facility_s_n,330,340);
		var stop13_s_n=scheduleFactory.createTransitRouteStop(stop13_facility_s_n,360,370);
		var stop14_s_n=scheduleFactory.createTransitRouteStop(stop14_facility_s_n,390,400);
		var stop15_s_n=scheduleFactory.createTransitRouteStop(stop15_facility_s_n,420,430);
		var stop16_s_n=scheduleFactory.createTransitRouteStop(stop16_facility_s_n,450,460);
		var stop17_s_n=scheduleFactory.createTransitRouteStop(stop17_facility_s_n,480,490);
		var stop18_s_n=scheduleFactory.createTransitRouteStop(stop18_facility_s_n,510,520);
		var stop19_s_n=scheduleFactory.createTransitRouteStop(stop19_facility_s_n,540,550);
		var stop20_s_n=scheduleFactory.createTransitRouteStop(stop20_facility_s_n,570,580);
		var stop21_s_n=scheduleFactory.createTransitRouteStop(stop21_facility_s_n,600,610);
		var stop22_s_n=scheduleFactory.createTransitRouteStop(stop22_facility_s_n,630,640);
		var stop23_s_n=scheduleFactory.createTransitRouteStop(stop23_facility_s_n,660,670);
		var stop24_s_n=scheduleFactory.createTransitRouteStop(stop24_facility_s_n,690,700);

		//route
		var route_n_s = scheduleFactory.createTransitRoute(Id.create("U81_ns", TransitRoute.class),
				networkRoute_n_s,List.of(stop1_n_s,stop2_n_s,stop3_n_s,stop4_n_s,stop5_n_s,stop6_n_s,stop7_n_s,stop8_n_s
						,stop9_n_s,stop10_n_s,stop11_n_s,stop12_n_s,stop13_n_s,stop14_n_s,stop15_n_s,stop16_n_s,
						stop17_n_s,stop18_n_s,stop19_n_s,stop20_n_s,stop21_n_s,stop22_n_s,stop23_n_s,stop24_n_s),"pt");
		var route_s_n = scheduleFactory.createTransitRoute(Id.create("U81_sn", TransitRoute.class),
				networkRoute_s_n,List.of(stop1_s_n,stop2_s_n,stop3_s_n,stop4_s_n,stop5_s_n,stop6_s_n,stop7_s_n,stop8_s_n,
						stop9_s_n,stop10_s_n,stop11_s_n,stop12_s_n,stop13_s_n,stop14_s_n,stop15_s_n,stop16_s_n,
						stop17_s_n,stop18_s_n,stop19_s_n,stop20_s_n,stop21_s_n,stop22_s_n,stop23_s_n,stop24_s_n),"pt");

		// create departures and vehicles for each departure N > S
		for (int i = 0 * 3600; i < 24 * 3600; i += 300) {
			var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
			var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("U81_vehicle_ns_" + "100"+i), vehicleType);
			departure.setVehicleId(vehicle.getId());

			scenario.getTransitVehicles().addVehicle(vehicle);
			route_n_s.addDeparture(departure);
		}

		// create departures and vehicles for each departure S > N
		for (int i = 0 * 3600; i < 24 * 3600; i += 300) {
			var departure = scheduleFactory.createDeparture(Id.create("departure_" + i, Departure.class), i);
			var vehicle = scenario.getTransitVehicles().getFactory().createVehicle(Id.createVehicleId("U81_vehicle_sn_"+"100" + i), vehicleType);
			departure.setVehicleId(vehicle.getId());

			scenario.getTransitVehicles().addVehicle(vehicle);
			route_s_n.addDeparture(departure);
		}

		// line N > S
		var line_n_s = scheduleFactory.createTransitLine(Id.create("U81_ns", TransitLine.class));
		line_n_s.addRoute(route_n_s);
		scenario.getTransitSchedule().addTransitLine(line_n_s);

		// line S > N
		var line_s_n = scheduleFactory.createTransitLine(Id.create("U81_sn", TransitLine.class));
		line_s_n.addRoute(route_s_n);
		scenario.getTransitSchedule().addTransitLine(line_s_n);

		new NetworkWriter(network).write(root.resolve("network-with-U81.xml.gz").toString());
		new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(root.resolve("transit-Schedule-U81.xml.gz").toString());
		new MatsimVehicleWriter(scenario.getTransitVehicles()).writeFile(root.resolve("transit-vehicles-U81.xml.gz").toString());


	}
	private static Link createLink(String id, Node from, Node to) {

		var connection = networkFactory.createLink(Id.createLinkId(id), from, to);
		connection.setAllowedModes(Set.of(TransportMode.pt));
		connection.setFreespeed(100);
		connection.setCapacity(10000);
		return connection;
	}



}
