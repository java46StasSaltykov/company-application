package telran.spring.company.model;

import java.io.Serializable;
import jakarta.validation.constraints.*;

public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Min(100000000)
	@Max(999999999)
	public int id;
	
	@NotEmpty
	@Pattern(regexp = "([A-Z][a-z]*)")
	public String firstName;
	
	@NotEmpty
	@Pattern(regexp = "([A-Z][a-z]*)")
	public String lastName;
	
	@NotEmpty
	@Pattern(regexp = "^\\d{4}-([0]\\d|1[0-2])-([0-2]\\d|3[01])$")
	public String birthDate;
	
	@NotEmpty
	@Min(value = 5000)
	@Max(value = 45000)
	public int salary;
	
}
