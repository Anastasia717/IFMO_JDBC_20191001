package com.efimchick.ifmo.web.jdbc;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        return resultSet -> {
            Set<Employee> employeeSet = new HashSet<>();
            try {
                while (resultSet.next())
                    employeeSet.add(mapRow(resultSet));
                return employeeSet;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new UnsupportedOperationException();
            }
        };
    }

    private Employee mapRow(ResultSet set) {
        try {
            Employee manager = null;
            if (set.getString("MANAGER") != null) {
                int managId = set.getInt("MANAGER");
                int currentRow = set.getRow();
                set.absolute(0);

                while (set.next())
                    if (managId == Integer.parseInt(set.getString("ID")))
                        manager = mapRow(set);
                set.absolute(currentRow);
            }

            return new Employee(new BigInteger(set.getString("ID")),
                    new FullName(set.getString("FIRSTNAME"), set.getString("LASTNAME"), set.getString("MIDDLENAME")),
                    Position.valueOf(set.getString("POSITION")),
                    LocalDate.parse(set.getString("HIREDATE")),
                    set.getBigDecimal("SALARY"),
                    manager);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }
}
