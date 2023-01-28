package telran.spring.company.service;

import java.util.*;
import telran.spring.company.model.Employee;

public interface CompanyService {

	Employee addEmployee(Employee empl);

	Employee updateEmployee(Employee empl);

	Employee deleteEmployee(int id);

	List<Employee> employeesBySalary(int salaryFrom, int salaryTo);

	List<Employee> employeesByAge(int ageFrom, int ageTo);

	List<Employee> employeesByBirthMonth(int monthNumber);
	
}
