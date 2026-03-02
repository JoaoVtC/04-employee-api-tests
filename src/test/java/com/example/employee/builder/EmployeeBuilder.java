package com.example.employee.builder;

import com.example.employee.dto.EmployeeRequest;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;

import java.math.BigDecimal;

/**
 * Data Builder para facilitar a criação de objetos de teste.
 */
public class EmployeeBuilder {

    private Long id = 1L;
    private String name = "Ana Silva";
    private String email = "ana.silva@email.com";
    private String cpf = "436.972.178-44";
    private BigDecimal salary = new BigDecimal("5000.00");
    private Department department = new Department(1L, "Engenharia");

    public EmployeeBuilder withId(Long id){
        this.id = id;
        return this;
    }

    public EmployeeBuilder withName(String name){
        this.name = name;
        return this;
    }

    public EmployeeBuilder withEmail(String email){
        this.email = email;
        return this;
    }

    public EmployeeBuilder withCpf(String cpf){
        this.cpf = cpf;
        return this;
    }

    public EmployeeBuilder withSalary(BigDecimal salary){
        this.salary = salary;
        return this;
    }

    public EmployeeBuilder withDepartment(Department department){
        this.department = department;
        return this;
    }

    public Employee build(){
        Employee employee = new Employee(name, email, cpf, salary, department);
        employee.setId(id);

        return employee;
    }


    public EmployeeRequest buildRequest(){
        return new EmployeeRequest(name, email, cpf, salary, department.getId());
    }

}
