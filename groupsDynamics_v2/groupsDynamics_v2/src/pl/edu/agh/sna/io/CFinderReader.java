package pl.edu.agh.sna.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import pl.edu.agh.sna.model.Group;

public class CFinderReader implements IGroupReader {

	public Map<String, Group> getGroups(String filename, String prefix, int slot) throws IOException {
		Map<String, Group> groupMap = new HashMap<String, Group>();

		List<String> lines = FileUtils.readLines(new File(filename));
		for (String line : lines) {
			if (line.length() == 0 || line.startsWith("#")) {
				continue;
			}
			int sepIdx = line.indexOf(":");
			if (sepIdx > 0) {
				String groupName = prefix + line.substring(0, sepIdx);
				String nodes = line.substring(sepIdx + 2);
				String[] nnodes = nodes.split(" ");
				Set<String> nodeSet = new HashSet<>(Arrays.asList(nnodes));
				Group group = new Group(groupName);
				group.setLocalName(line.substring(0, sepIdx));
				group.addMembers(nodeSet);
				group.setSlotNo(slot);
				groupMap.put(groupName, group);
			}
		}

		return groupMap;
	}

}
