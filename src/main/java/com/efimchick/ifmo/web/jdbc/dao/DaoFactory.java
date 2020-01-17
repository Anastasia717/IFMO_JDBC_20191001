package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {
    private Employee newEmployee(ResultSet set){
        try {
            return new Employee(
                    new BigInteger(set.getString("id")),
                    new FullName(set.getString("firstName"), set.getString("lastName"), set.getString("middleName")),
                    Position.valueOf(set.getString("position")),
                    LocalDate.parse(set.getString("hireDate")),
                    new BigDecimal(set.getString("salary")),
                    BigInteger.valueOf(set.getInt("manager")),
                    BigInteger.valueOf(set.getInt("department")));

        } catch (SQLException e){
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    private ResultSet executeRequest(String request){
        try {
            return ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
        } catch (SQLException e){
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    private Department newDepartment(ResultSet resultSet){
        try {
            return new Department(new BigInteger(resultSet.getString("id")),
                    resultSet.getString("name"),
                    resultSet.getString("location"));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                try {
                    ResultSet resultSet = executeRequest("SELECT * FROM employee WHERE department = " + department.getId());
                    List<Employee> employees = new ArrayList<>();

                    while (resultSet.next()) {
                        employees.add(newEmployee(resultSet));
                    }

                    return employees;
                } catch (SQLException e){
                    e.printStackTrace();
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                try {
                    ResultSet resultSet = executeRequest("SELECT * FROM employee WHERE manager = " + employee.getId());
                    List<Employee> employees = new ArrayList<>();

                    while (resultSet.next()) {
                        employees.add(newEmployee(resultSet));
                    }

                    return employees;
                } catch (SQLException e){
                    e.printStackTrace();
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = executeRequest("SELECT * FROM employee WHERE id = " + Id.toString());

                    if (resultSet.next())
                        return Optional.of(newEmployee(resultSet));
                    else
                        return Optional.empty();
                } catch (SQLException e){
                    e.printStackTrace();
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll() {
                try {
                    ResultSet resultSet = executeRequest("SELECT * FROM employee");
                    List<Employee> employees = new ArrayList<>();

                    while (resultSet.next()) {
                        employees.add(newEmployee(resultSet));
                    }

                    return employees;
                } catch (SQLException e){
                    e.printStackTrace();
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public Employee save(Employee employee) {
                executeRequest("INSERT INTO employee VALUES ('"
                                + employee.getId() + "', '"
                                + employee.getFullName().getFirstName() + "', '"
                                + employee.getFullName().getLastName() + "', '"
                                + employee.getFullName().getMiddleName() + "', '"
                                + employee.getPosition() + "', '"
                                + employee.getManagerId() + "', '"
                                + Date.valueOf(employee.getHired()) + "', '"
                                + employee.getSalary() + "', '"
                                + employee.getDepartmentId() + "')");

                return employee;
            }

            @Override
            public void delete(Employee employee) {
                try {
                    ConnectionSource.instance().createConnection().createStatement().execute(
                            "DELETE FROM employee WHERE ID = " + employee.getId().toString());
                } catch (SQLException e){
                    e.printStackTrace();
                    throw new UnsupportedOperationException();
                }
            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = executeRequest("SELECT * FROM department WHERE id = " + Id.toString());

                    if (resultSet.next())
                        return Optional.of(newDepartment(resultSet));
                    else
                        return Optional.empty();
                } catch (SQLException e){
                    e.printStackTrace();
                    return Optional.empty();
                }

            }

            @Override
            public List<Department> getAll() {
                try {
                    ResultSet resultSet = executeRequest("SELECT * FROM department");
                    List<Department> deps = new ArrayList<>();

                    while (resultSet.next()) {
                        deps.add(newDepartment(resultSet));
                    }

                    return deps;
                } catch (SQLException e){
                    e.printStackTrace();
                    throw new UnsupportedOperationException();
                }
            }

            @Override
            public Department save(Department department) {
                if (getById(department.getId()).equals(Optional.empty())) {
                    executeRequest("INSERT INTO department VALUES ('" +
                                    department.getId()       + "', '" +
                                    department.getName()     + "', '" +
                                    department.getLocation() + "')");
                } else {
                    executeRequest("UPDATE department SET " +
                                    "NAME = '"     + department.getName()     + "', " +
                                    "LOCATION = '" + department.getLocation() + "' " +
                                    "WHERE ID = '" + department.getId()       + "'");
                }

                return department;
            }

            @Override
            public void delete(Department department) {
                try {
                    ConnectionSource.instance().createConnection().createStatement().execute(
                            "DELETE FROM department WHERE ID = " + department.getId().toString());
                } catch (SQLException e){
                    e.printStackTrace();
                    throw new UnsupportedOperationException();
                }
            }
        };
    }
}
