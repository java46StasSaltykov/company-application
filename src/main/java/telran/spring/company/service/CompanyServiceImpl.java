package telran.spring.company.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.*;
import telran.spring.company.model.Employee;

@Service
public class CompanyServiceImpl implements CompanyService {
	
	private static Logger LOG = LoggerFactory.getLogger(CompanyService.class);
	
	@Value("${app.file.name:employees.data}")
	private String fileName;
	private HashMap<Integer, Employee> employees;

	@Override
	public Employee addEmployee(Employee empl) {
		if (getAge(empl) > 70 || getAge(empl) < 20) {
			throw new IllegalArgumentException("Age not in range; should be older than 20 and younger than 70.");
		}
		Employee newEmployee = new Employee(); 
		newEmployee.id = (int) ((Math.random() * (999999999 - 100000000)) + 100000000);
		newEmployee.firstName = empl.firstName;
		newEmployee.lastName = empl.lastName;
		newEmployee.birthDate = empl.birthDate;
		newEmployee.salary = empl.salary;
		employees.put(newEmployee.id, newEmployee);
		LOG.debug("Successfuly added a new employee {}.", newEmployee.firstName);
		return newEmployee;
	}

	@Override
	public Employee updateEmployee(Employee empl) {
		if (getAge(empl) > 70 || getAge(empl) < 20) {
			throw new IllegalArgumentException("Age not in range; should be older than 20 and younger than 70.");
		}
		Employee pastEmployee = new Employee();
		if (employees.containsKey(empl.id)) {
			pastEmployee = employees.get(empl.id);
			employees.get(empl.id).firstName = empl.firstName;
			employees.get(empl.id).lastName = empl.lastName;
			employees.get(empl.id).birthDate = empl.birthDate;
			employees.get(empl.id).salary = empl.salary;
			LOG.debug("Successfuly updated an employee with id {}.", empl.id);
		} else if (Integer.toString(empl.id).isEmpty() || !employees.containsKey(empl.id)) {
			throw new NoSuchElementException();
		}
		return pastEmployee;
	}

	@Override
	public Employee deleteEmployee(int id) {
		if (employees.containsKey(id)) {
			LOG.debug("Successfuly deleted an employee with id {}.", id);
			return employees.remove(id);
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public List<Employee> employeesBySalary(int salaryFrom, int salaryTo) {
		if (salaryFrom > salaryTo) {
			throw new IllegalArgumentException();
		}
		List<Employee> res = employees.keySet().stream().map(e -> employees.get(e)).filter(e -> (e.salary >= salaryFrom && e.salary <= salaryTo)).toList();
		if (res.isEmpty()) {
			LOG.debug("Found {} employees in the given salary range.", res.size());
		} else {
			LOG.warn("Found 0 employees in the given salary range.");
		}
		return res;
	}

	@Override
	public List<Employee> employeesByAge(int ageFrom, int ageTo) {
		if (ageFrom > ageTo) {
			throw new IllegalArgumentException();
		}
		List<Employee> res = employees.keySet().stream().map(e -> employees.get(e)).filter(e -> (getAge(e) >= ageFrom && getAge(e) <= ageTo)).toList();
		if (res.isEmpty()) {
			LOG.debug("Found {} employees in the given age range.", res.size());
		} else {
			LOG.warn("Found 0 employees in the given age range.");
		} 
		return res;
	}
	
	private int getAge(Employee e) {
		LocalDate emplBirthDate = LocalDate.parse(e.birthDate);
		return Period.between(emplBirthDate, LocalDate.now()).getYears();
	}

	@Override
	public List<Employee> employeesByBirthMonth(int monthNumber) {
		if (monthNumber > 12 || monthNumber < 1) {
			throw new IllegalArgumentException();
		}
		List <Employee> res = employees.keySet().stream().map(e -> employees.get(e)).filter(e -> monthNumber == getBirthMonth(e)).toList();
		if (res.isEmpty()) {
			LOG.debug("Found {} employees born in the given month.", res.size());
		} else {
			LOG.warn("Found 0 employees born in the given month.");
		}
		return res;
	}
	
	private int getBirthMonth(Employee e) {
		return Integer.parseInt(e.birthDate.split("-")[1]);
	}
	
	@PostConstruct
	void restoreEmployees() {
		try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName))) {
			employees = (HashMap<Integer, Employee>) input.readObject();
			LOG.debug("Employees {} has been restored", employees.keySet());
		} catch(FileNotFoundException e) {
			LOG.warn("File {} doesn't exists", fileName);
			employees = new HashMap<>();
		} catch (Exception e) {
			LOG.error("Error occured while restoring employees {}", e.getMessage());
		}
	}
	
	@PreDestroy
	void saveEmployees() {
		try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName))) {
			output.writeObject(employees);
			LOG.debug("Employees were saved to file {}", fileName);
		} catch(Exception e) {
			LOG.error("Exception occured while saving to file {}", e.getMessage());
		}
	}

}
