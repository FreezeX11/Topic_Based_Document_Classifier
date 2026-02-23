package classifieur;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Classifieur {
	void Train(List<File>files);
	Map<String,Set<File>> predict_class(List<File> files) throws FileNotFoundException;
}
