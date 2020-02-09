package com.efimchick.ifmo.web.jdbc.service;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServiceFactory {
        private Employee getEmployeeFromResultSet(ResultSet resultSet, boolean MainManager, boolean b) {

            try {
                BigInteger id = new BigInteger(String.valueOf(resultSet.getInt("ID")));
                FullName fullName = new FullName(resultSet.getString("FIRSTNAME"),
                        resultSet.getString("LASTNAME"), resultSet.getString("MIDDLENAME"));

                Position position = Position.valueOf(resultSet.getString("POSITION"));
                LocalDate hiredate = LocalDate.parse(resultSet.getString("HIREDATE"));

                BigDecimal salary = new BigDecimal(String.valueOf(resultSet.getInt("SALARY")));
                BigInteger depId = BigInteger.valueOf(resultSet.getInt("DEPARTMENT"));
                Employee manager = null;

                BigInteger manId = BigInteger.valueOf(resultSet.getInt("MANAGER"));

                List<Department> departments = getDepartment();
                Department result = null;
                for (Department department : departments) {
                    if (department.getId().equals(depId)) {
                        result = department;
                    }
                }

                if (manId != null && MainManager) {
                    if(b) MainManager = false;

                    ResultSet ResultSet = getResultSet("SELECT * FROM EMPLOYEE");
                    assert ResultSet != null;
                    while (ResultSet.next()) {

                        if (BigInteger.valueOf(ResultSet.getInt("ID")).equals(manId)) {
                            manager = getEmployeeFromResultSet(ResultSet, MainManager, b);
                        }

                    }
                }
                return new Employee(id, fullName, position, hiredate, salary, manager, result);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new UnsupportedOperationException();
            }
        }

        private List<Employee> getEmployees(ResultSet resultSet, boolean b) throws SQLException {
            if (resultSet == null)
                throw new UnsupportedOperationException();


            List<Employee> allEmployees = new ArrayList<>();
            while (resultSet.next()) {
                Employee emp = getEmployeeFromResultSet(resultSet, true, b);
                allEmployees.add(emp);
            }
            return allEmployees;
        }

        private List<Department> getDepartment() {

            ResultSet resultset = getResultSet("SELECT * FROM DEPARTMENT");
            List<Department> allDepartments = new ArrayList<>();

            while (true) {
                try {
                    if (resultset != null && !resultset.next()) break;

                    if (resultset != null) allDepartments.add(new Department(new BigInteger(resultset.getString("ID")),
                            resultset.getString("NAME"), resultset.getString("LOCATION")));
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new UnsupportedOperationException();
                }

            }
            return allDepartments;
        }


        private List<Employee> getPage(List<Employee> list, Paging paging) {
            int cur = paging.itemPerPage;
            int start = paging.itemPerPage * (paging.page - 1);
            return list.subList(start, Math.min(cur * paging.page, list.size()));
        }

        public EmployeeService employeeService() {
            return new EmployeeService() {
                @Override
                public List<Employee> getAllSortByHireDate(Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE ORDER BY HIREDATE");
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public List<Employee> getAllSortByLastname(Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE ORDER BY LASTNAME");
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public List<Employee> getAllSortBySalary(Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE ORDER BY SALARY");
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE ORDER BY DEPARTMENT, LASTNAME");
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = ? ORDER BY HIREDATE ", department.getId());
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE WHERE DEPARTMENT =? ORDER BY SALARY ", department.getId());
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = ? ORDER BY LASTNAME ", department.getId());
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE WHERE MANAGER = ? ORDER BY LASTNAME", manager.getId());
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE WHERE MANAGER = ? ORDER BY HIREDATE", manager.getId());
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE WHERE MANAGER = ? ORDER BY SALARY", manager.getId());
                    try {
                        return getPage(Objects.requireNonNull(getEmployees(resultSet, true)), paging);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE WHERE ID = ?", employee.getId());
                    try {
                        return Objects.requireNonNull(getEmployees(resultSet, false)).get(0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }

                @Override
                public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                    ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = ?" + " ORDER BY SALARY DESC LIMIT 1 OFFSET " + (salaryRank - 1), department.getId());
                    try {
                        return Objects.requireNonNull(getEmployees(resultSet, true)).get(0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new UnsupportedOperationException();
                    }
                }
            };

        }
    private ResultSet getResultSet(String sql, Object... params) {
        try {
            Connection connection = ConnectionSource.instance().createConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            if (params != null && params.length > 0) for (int i = 1; i < params.length + 1; i++)
                preparedStatement.setBigDecimal(i, new BigDecimal((BigInteger) params[i - 1]));
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

}
