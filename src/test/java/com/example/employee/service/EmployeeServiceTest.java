package com.example.employee.service;

import com.example.employee.builder.EmployeeBuilder;
import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.exception.DuplicateEmailException;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.exception.SalaryBelowMinimumException;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;
import com.example.employee.repository.DepartmentRepository;
import com.example.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do {@link EmployeeService} usando Mockito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService — Testes Unitários")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService service;

    @Captor
    private ArgumentCaptor<Employee> employeeCaptor;

    // ====================================================================
    // Teste de criação com sucesso
    // ====================================================================
    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("deve criar funcionário com sucesso")
        void shouldCreateEmployee() {
            // Arrange
            Department department = new Department(2L, "Financeiro");
            EmployeeRequest employeeRequest = new EmployeeBuilder().withId(1L).withName("João").withEmail("joao@email.com").withDepartment(department).buildRequest();
            Employee employee = EmployeeMapper.toEntity(employeeRequest, department);


            when(employeeRepository.existsByEmail(employeeRequest.email())).thenReturn(false);
            when(departmentRepository.findById(employeeRequest.departmentId())).thenReturn(Optional.of(department));
            when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

            // Act
            EmployeeResponse created = service.create(employeeRequest);

            // Assert

            assertThat(created).isEqualTo(EmployeeMapper.toResponse(employee));
            verify(employeeRepository).save(employeeCaptor.capture());
            Employee captured = employeeCaptor.getValue();
            assertThat(captured.getName()).isEqualTo(employeeRequest.name());
            assertThat(captured.getEmail()).isEqualTo(employeeRequest.email());
            assertThat(captured.getCpf()).isEqualTo(employeeRequest.cpf());
            assertThat(captured.getSalary()).isEqualTo(employeeRequest.salary());
            assertThat(captured.getDepartment().getId()).isEqualTo(employeeRequest.departmentId());
        }

        // ================================================================
        // Teste de salário mínimo
        // ================================================================

        @Test
        @DisplayName("deve lançar exceção quando salário é menor que o mínimo")
        void shouldThrowWhenSalaryBelowMinimum() {
            // Arrange
            Department department = new Department(2L, "Financeiro");
            EmployeeRequest employeeRequest = new EmployeeBuilder().withId(1L).withName("João").withEmail("joao@email.com").withDepartment(department).withSalary(BigDecimal.valueOf(1000.0)).buildRequest();

            // Act & Assert
            assertThatThrownBy(() -> service.create(employeeRequest)).isInstanceOf(SalaryBelowMinimumException.class);
            verify(employeeRepository, never()).save(any());
        }

        // ================================================================
        // Teste de email duplicado
        // ================================================================

        @Test
        @DisplayName("deve lançar exceção quando email já existe")
        void shouldThrowWhenEmailExists() {
            // Arrange
            Department department = new Department(1L, "Financeiro");
            EmployeeRequest employeeRequest = new EmployeeBuilder().withId(1L).withName("João").withEmail("joao@email.com").withDepartment(department).buildRequest();

            when(employeeRepository.existsByEmail(employeeRequest.email())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> service.create(employeeRequest)).isInstanceOf(DuplicateEmailException.class);
            verify(employeeRepository, never()).save(any());
        }
    }

    // ====================================================================
    // findById — Testes de exemplo (já implementados)
    // ====================================================================
    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("deve lançar exceção quando funcionário não encontrado")
        void shouldThrowWhenNotFound() {
            when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(999L))
                    .isInstanceOf(EmployeeNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    // ====================================================================
    // delete — Testes de exemplo (já implementados)
    // ====================================================================
    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deve deletar funcionário existente")
        void shouldDeleteEmployee() {
            when(employeeRepository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(employeeRepository).deleteById(1L);
        }

        @Test
        @DisplayName("deve lançar exceção ao deletar inexistente")
        void shouldThrowWhenDeleting() {
            when(employeeRepository.existsById(anyLong())).thenReturn(false);

            assertThatThrownBy(() -> service.delete(999L))
                    .isInstanceOf(EmployeeNotFoundException.class);

            verify(employeeRepository, never()).deleteById(anyLong());
        }
    }
}
