package com.example.employee.service;

import com.example.employee.builder.EmployeeBuilder;
import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.exception.SalaryBelowMinimumException;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;
import com.example.employee.repository.DepartmentRepository;
import com.example.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testes parametrizados do {@link EmployeeService}.
 *
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService — Testes Parametrizados")
class EmployeeServiceParameterizedTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService service;

    @ParameterizedTest(name = "CPF {0} para {1}")
    @DisplayName("deve aceitar CPFs no formato válido")
    @CsvSource({
            "123.456.789-09, Ana Silva, ana@email.com",
            "987.654.321-00, Carlos Santos, carlos@email.com",
            "111.222.333-44, Maria Oliveira, maria@email.com"
    })
    void shouldAcceptValidCpfFormats(String cpf, String name, String email) {
        // Arrange
        Department department = new Department(2L, "Financeiro");
        EmployeeRequest employeeRequest = new EmployeeBuilder().withId(1L).withName(name).withEmail(email).withDepartment(department).withCpf(cpf).buildRequest();
        Employee employee = EmployeeMapper.toEntity(employeeRequest, department);

        when(employeeRepository.existsByEmail(employeeRequest.email())).thenReturn(false);
        when(departmentRepository.findById(employeeRequest.departmentId())).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        EmployeeResponse created = service.create(employeeRequest);

        // Assert
        assertThat(created.cpf()).isEqualTo(cpf);
        
    }

    
    @ParameterizedTest(name = "salário R$ {0} deve ser rejeitado")
    @DisplayName("deve rejeitar salários abaixo do mínimo")
    @ValueSource(strings = {"0.01", "500.00", "1000.00", "1411.99"})
    void shouldRejectInvalidSalaries(String salaryStr) {
        // Arrange
        BigDecimal invalidSalary = new BigDecimal(salaryStr);
        var request = new EmployeeBuilder()
           .withSalary(invalidSalary)
           .withEmail("test-" + salaryStr + "@email.com")
           .buildRequest();

        // Act & Assert
        assertThatThrownBy(() -> service.create(request)).isInstanceOf(SalaryBelowMinimumException.class);
    }
}
