package pl.edu.agh.sna.demo;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import pl.edu.agh.sna.dynamics.GroupEvolutionTracker;
import pl.edu.agh.sna.dynamics.gevi.GEVi;
import pl.edu.agh.sna.io.CFinderReader;
import pl.edu.agh.sna.io.GroupContextReader;
import pl.edu.agh.sna.model.Group;
import pl.edu.agh.sna.model.TimeSlot;
import pl.edu.agh.sna.util.TimeslotUtil;

@Component("groupDynamics")
public class GroupDynamicsAnalyzerDemo {

	@Autowired
	private CFinderReader cfinderReader;

	@Autowired
	private GroupContextReader contextReader;
	
	@Autowired
    private ApplicationContext context;

	public void analyzeExampleWithTopicContext() throws Exception {

		int k = 5; // example provides data only for k=5

		// DateTime startDate = new DateTime(2011, 12, 11, 0, 0);
		DateTime startDate = new DateTime(2014, 11, 11, 0, 0);
		DateTime endDate = new DateTime(2014, 11, 20, 23, 59);
		List<TimeSlot> timeslots = TimeslotUtil.createTimeslotsForPeriods(
				startDate, endDate, 7, 4);

		for (int slot = 0; slot < timeslots.size(); slot++) {

			TimeSlot timeslot = timeslots.get(slot);
			String prefix = slot + "_";
			String path = "example/all_graph" + slot + ".txt_files-d\\k=" + k
					+ "\\";
			Map<String, Group> groupInSlotMap = cfinderReader.getGroups(path
					+ "directed_communities", prefix, slot);
			timeslot.setGroupMap(groupInSlotMap);

			// read topics
			contextReader.readContext("example/topics" + slot + ".txt",
					"topics", groupInSlotMap);
		}

		GroupEvolutionTracker evoTracker = new GroupEvolutionTracker();
		evoTracker.setTimeslots(timeslots);
		evoTracker.evaluateSgci();

		 GEVi gevi = (GEVi) context.getBean("gevi");
		gevi.visualiseGroupEvolution(evoTracker.getGroupTransitions(),
				evoTracker.getRatioBetweenGroupsForStrongMatching());

		// demonstrate change of context
		// Thread.sleep(10000);
		// gevi.setCategory("Variety");
		// gevi.setMaxThresholdForContext(0.2);
		// gevi.refreshAfterContextChanges();
	}

}
