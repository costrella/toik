package pl.edu.agh.sna.io;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import pl.edu.agh.sna.model.Group;

@Component("groupContextReader")
public class GroupContextReader {

	public void readContext(String filename, String contextName, Map<String, Group> groupMap) throws IOException {

		List<String> lines = FileUtils.readLines(new File(filename));
		for (String line : lines) {
			if (line.length() == 0) {
				continue;
			}
			String[] parts = line.split(";");
			if (parts.length == 3) {
				String groupName = parts[0];
				String category = parts[1];
				Double value = Double.parseDouble(parts[2]);

				Group group = groupMap.get(groupName);
				if (group != null) {
					group.addContext(contextName);
					group.getContext(contextName).put(category, value);
				}
			}

		}
	}

}
