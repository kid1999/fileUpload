package kid1999.upload.dto;

import kid1999.upload.model.HomeWork;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Projects {
	private HomeWork homeWork;
	private int count;
}
