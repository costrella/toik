package pl.edu.agh.sna.demo;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.joda.time.DateTime;

import pl.edu.agh.sna.db.Dataset;
import pl.edu.agh.sna.db.DbManager;
import pl.edu.agh.sna.model.BasicComment;
import pl.edu.agh.sna.model.DirectedWeightedGraph;
import pl.edu.agh.sna.model.TimeSlot;
import pl.edu.agh.sna.util.GraphBuilder;
import pl.edu.agh.sna.util.TimeslotUtil;

public class GraphBuilderDemo {
	private void makeGraphsForDataset() throws Exception {
		DbManager dbManager = new DbManager(Dataset.SALON24);

		DateTime startDate = new DateTime(2008, 1, 1, 0, 0);
		DateTime endDate = new DateTime(2012, 3, 31, 23, 59);
		LinkedHashMap<Integer, TimeSlot> timeslotMap = TimeslotUtil.createTimeslotsForPeriodsAsMap(startDate, endDate,
				7, 4);

		for (int slot = 206; slot < timeslotMap.keySet().size(); slot += 1) {
			Date startTDate = timeslotMap.get(slot).getStartDate();
			Date endTDate = timeslotMap.get(slot).getEndDate();

			System.out.println("slot: " + slot);
			List<BasicComment> commentList = dbManager.getBasicCommentList(startTDate, endTDate);

			DirectedWeightedGraph graph = GraphBuilder.buildGraphInCommentsModel(commentList);
			graph.saveGraph("D:\\demo\\all_graph." + slot + ".txt");
			commentList = null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		GraphBuilderDemo demo = new GraphBuilderDemo();
		demo.makeGraphsForDataset();
	}

}
