package com.example.employee.repository;

import com.example.employee.AbstractIntegrationTest;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link EmployeeRepository} com Testcontainers.
 *
 * <p>Exercícios TODO 6 e 7 — implemente os testes de integração.</p>
 *
 * <p><strong>Pré-requisito:</strong> Podman Desktop deve estar rodando.</p>
 */
@DisplayName("EmployeeRepository — Testes de Integração")
class EmployeeRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        department = departmentRepository.save(new Department(null, "Engenharia"));
    }

    // ====================================================================
    // TODO 6: Testes de integração — save e findById
    // ====================================================================

    @Test
    @DisplayName("deve salvar e recuperar funcionário por ID")
    void shouldSaveAndFindById() {
        // Arrange
        Employee employee = new Employee("Ana Silva", "ana@email.com",
        "123.456.789-09", new BigDecimal("5000.00"), department);

        // Act
        Employee saved = employeeRepository.save(employee);
        Optional<Employee> found = employeeRepository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        Employee foundEmployee = found.get();
        assertThat(foundEmployee.getName()).isEqualTo(employee.getName());
        assertThat(foundEmployee.getEmail()).isEqualTo(employee.getEmail());
        assertThat(foundEmployee.getCpf()).isEqualTo(employee.getCpf());
        assertThat(foundEmployee.getSalary()).isEqualTo(employee.getSalary());
        assertThat(foundEmployee.getDepartment().getName()).isEqualTo(employee.getDepartment().getName());
        assertThat(foundEmployee.getCreatedAt()).isNotNull();
    }

    /**
     * TODO 6: Implementar teste de busca por email
     *
     * Passos:
     * 1. Salvar um funcionário
     * 2. Buscar com repository.findByEmail()
     * 3. Verificar que encontrou o funcionário correto
     */
    @Test
    @DisplayName("deve encontrar funcionário por email")
    void shouldFindByEmail() {
        // Arrange
        Employee employee = new Employee("Ana Silva", "ana@email.com",
        "123.456.789-09", new BigDecimal("5000.00"), department);
        Employee saved = employeeRepository.save(employee);

        // Act
        Optional<Employee> findedByEmail = employeeRepository.findByEmail(saved.getEmail());

        // Assert
        assertThat(findedByEmail).isPresent();
        Employee findedEmployee = findedByEmail.get();
        assertThat(findedEmployee.getEmail()).isEqualTo(employee.getEmail());
        assertThat(findedEmployee.getName()).isEqualTo(employee.getName());
    }

    // ====================================================================
    // TODO 7: Teste de UNIQUE constraint no email
    // ====================================================================
    @Test
    @DisplayName("deve rejeitar email duplicado (UNIQUE constraint)")
    void shouldRejectDuplicateEmail() {
        // Arrange
        Employee employee = new Employee("Ana Silva", "ana@email.com",
        "123.456.789-09", new BigDecimal("5000.00"), department);
        employeeRepository.saveAndFlush(employee);
        Employee employee2 = new Employee("Ana Carla", "ana@email.com",
        "123.456.789-09", new BigDecimal("5000.00"), department);

        // Act & Assert
        assertThatThrownBy(() -> employeeRepository.saveAndFlush(employee2))
                .isInstanceOf(Exception.class);
    }

    // ====================================================================
    // Testes de exemplo (já implementados como referência)
    // ====================================================================
    @Nested
    @DisplayName("Testes de referência")
    class Reference {

        @Test
        @DisplayName("deve retornar vazio para email inexistente")
        void shouldReturnEmptyForInvalidEmail() {
            Optional<Employee> found = employeeRepository.findByEmail("naoexiste@email.com");
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("deve verificar existência por email")
        void shouldCheckExistsByEmail() {
            Employee emp = new Employee("Ana Silva", "ana.ref@email.com",
                    "123.456.789-09", new BigDecimal("5000.00"), department);
            employeeRepository.save(emp);

            assertThat(employeeRepository.existsByEmail("ana.ref@email.com")).isTrue();
            assertThat(employeeRepository.existsByEmail("naoexiste@email.com")).isFalse();
        }
    }
}
