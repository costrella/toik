package pl.edu.agh.sna.io;

import java.io.IOException;
import java.util.Map;

import pl.edu.agh.sna.model.Group;

public interface IGroupReader {
	Map<String, Group> getGroups(String filename, String prefix, int slotNo) throws IOException;
}
