package pl.edu.agh.sna.demo;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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

	private GEVi gevi;

	@Autowired
	private CFinderReader cfinderReader;

	@Autowired
	private GroupContextReader contextReader;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private GroupEvolutionTracker evoTracker;

	public void analyzeExampleWithTopicContext() throws Exception {

		int k = 5; // example provides data only for k=5

		DateTime startDate = new DateTime(2011, 12, 11, 0, 0);
		DateTime endDate = new DateTime(2012, 3, 31, 23, 59);
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

		// GroupEvolutionTracker evoTracker = new GroupEvolutionTracker();
		evoTracker.setTimeslots(timeslots);
		evoTracker.evaluateSgci();

		gevi = (GEVi) context.getBean("gevi");
		gevi.visualiseGroupEvolution(evoTracker.getGroupTransitions(),
				evoTracker.getRatioBetweenGroupsForStrongMatching());

		// demonstrate change of context
		// Thread.sleep(10000);
		// gevi.setCategory("Variety");
		// gevi.setMaxThresholdForContext(0.2);
		// gevi.refreshAfterContextChanges();

		createGui();
	}

	public void createGui() {
		JFrame frame = new JFrame("JFrame Example");

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());


		JButton button = new JButton();
		button.setText("Save");

		JButton refreshButton = new JButton();
		refreshButton.setText("Refresh");

		final JTextField evolutionThresholdTxt = new JTextField();
		final JTextField ratioBetweenGroupsForMatchingTxt = new JTextField();
		evolutionThresholdTxt.setText(String.valueOf(evoTracker.getEvolutionThreshold()));
		ratioBetweenGroupsForMatchingTxt.setText(String.valueOf(evoTracker.getRatioBetweenGroupsForMatching()));

		panel.add(button);
		panel.add(refreshButton);
		panel.add(new JLabel("EvolutionThreshold"));
		panel.add(evolutionThresholdTxt);
		panel.add(new JLabel("RatioBetweenGroupsForMatching"));
		panel.add(ratioBetweenGroupsForMatchingTxt);

		frame.add(panel);
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("Before:"
						+ evoTracker.getEvolutionThreshold());
				evoTracker.setEvolutionThreshold(Double.parseDouble(evolutionThresholdTxt
						.getText()));
				evoTracker.setRatioBetweenGroupsForMatching(Integer.parseInt(ratioBetweenGroupsForMatchingTxt.getText()));
				System.out.println("After:"
						+ evoTracker.getEvolutionThreshold());
			}
		});

		refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				gevi.refreshAfterContextChanges();
				evoTracker.evaluateSgci();
				gevi.visualiseGroupEvolution(evoTracker.getGroupTransitions(),
						evoTracker.getRatioBetweenGroupsForStrongMatching());
			}
		});
	}

}
