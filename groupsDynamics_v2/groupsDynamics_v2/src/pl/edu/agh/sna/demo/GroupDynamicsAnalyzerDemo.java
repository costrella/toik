package pl.edu.agh.sna.demo;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.mxgraph.model.mxGraphModel;

import pl.edu.agh.sna.dynamics.GroupEvolutionTracker;
import pl.edu.agh.sna.dynamics.gevi.GEVi;
import pl.edu.agh.sna.dynamics.gevi.jgraph.mxCustomCell;
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

	JTextArea resultSearch;

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

		evoTracker.setTimeslots(timeslots);
		evoTracker.evaluateSgci();

		gevi = (GEVi) context.getBean("gevi");
		gevi.visualiseGroupEvolution(evoTracker,
				evoTracker.getGroupTransitions(),
				evoTracker.getRatioBetweenGroupsForStrongMatching());

		createGui();
	}

	public int searchMember(String user) {
		Member member = new Member();
		for (Group gr : evoTracker.getGroupsActive()) {
			for (String mem : gr.getMembers()) {
				if (mem.equals(user)) {
					member.getStableGroups().add(gr);
				}
			}

		}
		String groupsId = "";
		List<Object> cells = new ArrayList<Object>();
		for (Group gr : member.getStableGroups()) {
			groupsId += gr.getGlobalName() + " slotNr:  " + gr.getSlotNo()
					+ ", ";
			cells.add(((mxGraphModel) gevi.g.getModel()).getCell(gr
					.getGlobalName()));
		}
		resultSearch.setText(groupsId);
		resultSearch.setToolTipText(groupsId);
		for (Object cell : cells) {
			if (cell != null) {
				mxCustomCell vertex = (mxCustomCell) cell;
				vertex.setStyle("fillColor=FF8157;strokeColor=turqoise;strokeWidth=3");
			}
		}
		gevi.g.refresh();
		return member.getStableGroups().size();
	}

	public void createGui() {
		JFrame frame = new JFrame("JFrame Example");

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 2, 10, 10));

		JButton button = new JButton();
		button.setText("Save");

		JButton refreshButton = new JButton();
		refreshButton.setText("Refresh");

		JButton defaultButton = new JButton();
		defaultButton.setText("Default");

		JButton searchButton = new JButton();
		searchButton.setText("Search");

		final JTextField evolutionThresholdTxt = new JTextField();
		final JTextField ratioBetweenGroupsForMatchingTxt = new JTextField();
		final JTextField ratioBetweenGroupsForStrongMatchingTxt = new JTextField();
		final JTextField ratioForConstancyTxt = new JTextField();
		final JTextField minDurationTimeForStableGroupsTxt = new JTextField();
		final JTextField memIdTxt = new JTextField();

		evolutionThresholdTxt.setText(String.valueOf(evoTracker
				.getEvolutionThreshold()));
		ratioBetweenGroupsForMatchingTxt.setText(String.valueOf(evoTracker
				.getRatioBetweenGroupsForMatching()));
		ratioBetweenGroupsForStrongMatchingTxt.setText(String
				.valueOf(evoTracker.getRatioBetweenGroupsForStrongMatching()));
		ratioForConstancyTxt.setText(String.valueOf(evoTracker
				.getRatioForConstancy()));
		minDurationTimeForStableGroupsTxt.setText(String.valueOf(evoTracker
				.getMinDurationTimeForStableGroups()));

		evolutionThresholdTxt
				.setToolTipText("<html><body>[0-1] próg u¿ywany do znalezienia przejœcia pomiêdzy grupami z s¹siednich slotów czasowych,<br> powy¿ej progu jest przejœcie miêdzy grupami <b>[domyœlna wartoœæ: 0.5]");
		ratioBetweenGroupsForMatchingTxt
				.setToolTipText("<html><body>[>1] gdy zostanie znalezione przejœcie miêdzy grupami to stosunek ich rozmiarów miêdzy sob¹<br> musi byæ mniejszy od tej wartoœci (im wiêksza wartoœæ tym pozwala na znalezienie przejœcia pomiêdzy grupami <br>o wiêkszej ró¿nicy rozmiarów miêdzy sob¹ )<b> [domyœlna wartoœæ: 50].");
		ratioBetweenGroupsForStrongMatchingTxt
				.setToolTipText("<html><body>[>1], powinno byæ mniejsze lub równe ratioBetweenGroupsForMatching -<br> gdy stosunek rozmiarów grup miêdzy sob¹ jest mniejszy od tej wartoœci<br> to jest silne dopasowanie (CHANGE_SIZE, CONSTANCY, MERGE, SPLIT lub SPLIT_MERGE),<br> w przec. wypadku s³abe (ADDITION lub DELETION) <b>[domyœlna wartoœæ: 10]");
		ratioForConstancyTxt
				.setToolTipText("<html><body>[0-0.3] - okreœla jak du¿o siê mo¿e zmieniæ rozmiar grupy, aby dalej mo¿na by³o <br> zakwalifikowaæ przejœcie jako CONSTANCY (ró¿nica rozmiarów grupa podzielona przez rozmiar<br pierwszej grupy - jeœli jest mniejsza od tej wartoœci to przypisane jest zdarzenie oznaczaj¹ce sta³oœæ grupy) <b>[domyœlna wartoœæ: 0.05]");
		minDurationTimeForStableGroupsTxt
				.setToolTipText("<html><body>[1-3] - okreœla w ilu slotach czasowych musi istnieæ grupa (wraz ze swoimi kontynuacjami). Takie grupy nazywane s¹ stabilnymi grupami. Przejœcia s¹ ograniczane tylko do stabilnych grup. <b>[domyœlna wartoœæ: 3]");

		resultSearch = new JTextArea();
		JScrollPane sp = new JScrollPane(resultSearch);
		panel.add(new JLabel("Evolution Threshold"));
		panel.add(evolutionThresholdTxt);
		panel.add(new JLabel("Ratio Between Groups For Matching"));
		panel.add(ratioBetweenGroupsForMatchingTxt);
		panel.add(new JLabel("Ratio Between Groups For Strong Matching"));
		panel.add(ratioBetweenGroupsForStrongMatchingTxt);
		panel.add(new JLabel("Ratio For Constancy"));
		panel.add(ratioForConstancyTxt);
		panel.add(new JLabel("Min Duration Time For Stable Groups"));
		panel.add(minDurationTimeForStableGroupsTxt);

		panel.add(button);
		panel.add(refreshButton);
		panel.add(defaultButton);
		panel.add(new JLabel());
		panel.add(new JLabel("Search member"));
		panel.add(new JLabel());

		panel.add(memIdTxt);
		panel.add(searchButton);
		panel.add(sp);

		frame.add(panel);
		frame.setSize(700, 250);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				evoTracker.setEvolutionThreshold(Double
						.parseDouble(evolutionThresholdTxt.getText()));
				evoTracker.setRatioBetweenGroupsForMatching(Integer
						.parseInt(ratioBetweenGroupsForMatchingTxt.getText()));
				evoTracker.setRatioBetweenGroupsForStrongMatching(Integer
						.parseInt(ratioBetweenGroupsForStrongMatchingTxt
								.getText()));
				evoTracker.setRatioForConstancy(Double
						.parseDouble(ratioForConstancyTxt.getText()));
				evoTracker.setMinDurationTimeForStableGroups(Integer
						.parseInt(minDurationTimeForStableGroupsTxt.getText()));
			}
		});

		refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// gevi.refreshAfterContextChanges();
				evoTracker.evaluateSgci();
				gevi.visualiseGroupEvolution(evoTracker,
						evoTracker.getGroupTransitions(),
						evoTracker.getRatioBetweenGroupsForStrongMatching());
			}
		});

		defaultButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				evoTracker.setEvolutionThreshold(0.5);
				evoTracker.setRatioBetweenGroupsForMatching(50);
				evoTracker.setRatioBetweenGroupsForStrongMatching(10);
				evoTracker.setRatioForConstancy(0.05);
				evoTracker.setMinDurationTimeForStableGroups(3);

				evolutionThresholdTxt.setText(String.valueOf(evoTracker
						.getEvolutionThreshold()));
				ratioBetweenGroupsForMatchingTxt.setText(String
						.valueOf(evoTracker.getRatioBetweenGroupsForMatching()));
				ratioBetweenGroupsForStrongMatchingTxt.setText(String
						.valueOf(evoTracker
								.getRatioBetweenGroupsForStrongMatching()));
				ratioForConstancyTxt.setText(String.valueOf(evoTracker
						.getRatioForConstancy()));
				minDurationTimeForStableGroupsTxt.setText(String
						.valueOf(evoTracker.getMinDurationTimeForStableGroups()));
			}
		});

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				searchMember(memIdTxt.getText());

			}
		});
	}
}
