package org.matsim.run;

import org.matsim.contrib.signals.otfvis.OTFVisWithSignalsLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.PlansConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.prepare.CreateNetwork;
import org.matsim.prepare.CreateTransitSchedule;
import org.matsim.prepare.PreparePopulation;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(
        header = ":: Open Düsseldorf Scenario ::",
        version = "1.0"
)
@MATSimApplication.Prepare({CreateNetwork.class, CreateTransitSchedule.class, PreparePopulation.class})
public class RunDuesseldorfScenario extends MATSimApplication {

    /**
     * Default coordinate system of the scenario.
     */
    public static final String COORDINATE_SYSTEM = "EPSG:25832";

    /**
     *  6.627° - 6.950°
     */
    public static final double[] X_EXTENT = new double[]{333926.98, 357174.31};
    /**
     *  51.121 - 51.319°
     */
    public static final double[] Y_EXTENT = new double[]{5665283.05, 5687261.18};

    @CommandLine.Option(names = "--otfvis", defaultValue = "false", description = "Enable OTFVis live view")
    private boolean otfvis;

    public RunDuesseldorfScenario() {
        super("scenarios/duesseldorf-1pct/input/duesseldorf-1pct.config.xml");
    }

    public static void main(String[] args) {
        MATSimApplication.run(RunDuesseldorfScenario.class, args);
    }

    @Override
    protected Config prepareConfig(Config config) {

        //addDefaultActivityParams(config);

        for (long ii = 600; ii <= 97200; ii += 600) {

            for (String act : List.of("home", "restaurant", "other", "visit", "errands", "educ_higher", "educ_secondary")) {
                config.planCalcScore().addActivityParams(new ActivityParams(act + "_" + ii + ".0").setTypicalDuration(ii));
            }

            config.planCalcScore().addActivityParams(new ActivityParams("work_" + ii + ".0").setTypicalDuration(ii).setOpeningTime(6. * 3600.).setClosingTime(20. * 3600.));
            config.planCalcScore().addActivityParams(new ActivityParams("business_" + ii + ".0").setTypicalDuration(ii).setOpeningTime(6. * 3600.).setClosingTime(20. * 3600.));
            config.planCalcScore().addActivityParams(new ActivityParams("leisure_" + ii + ".0").setTypicalDuration(ii).setOpeningTime(9. * 3600.).setClosingTime(27. * 3600.));
            config.planCalcScore().addActivityParams(new ActivityParams("shopping_" + ii + ".0").setTypicalDuration(ii).setOpeningTime(8. * 3600.).setClosingTime(20. * 3600.));
        }

        // config.planCalcScore().addActivityParams(new ActivityParams("freight").setTypicalDuration(12. * 3600.));
        config.planCalcScore().addActivityParams(new ActivityParams("car interaction").setTypicalDuration(60));

        config.plans().setHandlingOfPlansWithoutRoutingMode(PlansConfigGroup.HandlingOfPlansWithoutRoutingMode.useMainModeIdentifier);

        return config;
    }

    @Override
    protected void prepareControler(Controler controler) {

        if (otfvis)
            controler.addOverridingModule(new OTFVisWithSignalsLiveModule());

        //  controler.addOverridingModule( new AbstractModule() {
        //      @Override
        //      public void install() {
        //          install( new SwissRailRaptorModule() );
        //      }
        //  } );
    }
}
