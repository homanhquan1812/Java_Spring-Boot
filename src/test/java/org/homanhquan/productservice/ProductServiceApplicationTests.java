package org.homanhquan.productservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * I. JUnit 5
 * 1. Definition
 * JUnit 5 is the latest version of the JUnit testing framework for Java. It’s used to write and run automated unit tests.
 * JUnit 5 = JUnit Platform + Jupiter + Vintage
 * Platform: Runs the tests.
 * Jupiter: Test engine for writing JUnit 5 tests (@Test).
 * Vintage: Backward compatibility for JUnit 4.
 * 2. Common Annotations
 * Annotation	Description
 * @Test Marks a test method
 * @BeforeEach Runs before each test method
 * @AfterEach Runs after each test method
 * @BeforeAll Runs once before all tests (must be static)
 * @AfterAll Runs once after all tests (must be static)
 * @DisplayName("...") Custom name for the test
 * @Disabled Temporarily skip a test
 * @Nested Organize related tests together
 * 3. Common Assertions
 * Assertion	Purpose
 * assertEquals(expected, actual)	Check equality
 * assertNotNull(value)	Check not null
 * assertTrue(condition)	Check boolean true
 * assertThrows(Exception.class, () -> {...})	Check exception thrown
 * assertAll(() -> {...}, () -> {...})	Group multiple assertions
 * 4. Parameterized Tests
 * Annotation	Purpose
 * @ParameterizedTest Run a test with multiple inputs
 * @ValueSource(ints = {1,2,3})	Provide simple values
 * @CsvSource({"input,expected"}) Provide CSV-like input data
 * @MethodSource("methodName") Provide complex test data from a static method
 * II. Mockito
 * 1. Definition
 * Mockito is used to mock dependencies and isolate the class under test. You use it to avoid calling real database, API, or heavy logic.
 * 2. Common annotations
 * Annotation	Purpose
 * @Mock Creates a mock object
 * @InjectMocks Injects mocks into the tested class
 * @Spy Partial mock — uses real methods unless stubbed
 * @Captor Captures arguments passed to mocks
 * @ExtendWith(MockitoExtension.class) Enables Mockito in JUnit 5
 * @ExtendWith(MockitoExtension.class)
 * class CustomerServiceTest {
 *
 *     @Mock
 *     private CustomerRepository repository;
 *
 *     @InjectMocks
 *     private CustomerServiceImpl service;
 *
 *     @Test
 *     void testFindById() {
 *         Customer mockCustomer = new Customer(1L, "John");
 *         when(repository.findById(1L)).thenReturn(Optional.of(mockCustomer));
 *
 *         CustomerDto result = service.getCustomerById(1L);
 *
 *         assertEquals("John", result.getName());
 *         verify(repository).findById(1L);
 *     }
 * }
 * 3. Argument Matchers
 * Matcher	Purpose
 * any()	Any object
 * any(Customer.class)	Any instance of Customer
 * anyString()	Any String
 * anyLong()	Any Long
 * eq(value)	Match a specific value
 * isNull() / isNotNull()	Match null / not-null values
 * Important Rule: When using matchers, all arguments must be matchers.
 * // ✅ Correct
 * when(service.update(eq(1L), any(CustomerDto.class)));
 *
 * // ❌ Wrong – mixing matcher and actual value
 * when(service.update(1L, any(CustomerDto.class)));
 * 4. Stubbing Variations
 * Method	Description
 * when(...).thenReturn(...)	Define return value for a mock call
 * verify(mock).method()	Check if a mock method was called
 * verify(mock, times(2))	Verify call count
 * doThrow(...).when(mock)	Mock an exception
 * ArgumentCaptor	Capture arguments passed to a mock
 * III. Testing Layers in Spring Boot
 * Layer	Common Testing Level	Notes
 * service/	⭐⭐⭐⭐	Most important — unit tests for business logic
 * repository/	⭐⭐⭐	Integration tests (often with in-memory DB)
 * controller/	⭐⭐⭐	REST slice tests using @WebMvcTest
 * graphql/	⭐⭐	Depends on whether the project uses GraphQL
 * security/	⭐⭐	Tests for JWT, filters, and rate limiting
 * mapper/	⭐	Simple mappings — minimal testing needed
 * config/	⭐	Rarely tested unless configuration is complex
 * exception/	⭐	Usually tested indirectly via controller tests
 * IV. Spring Test Annotations
 * 1. Controller Tests
 * Annotation	Purpose
 * @WebMvcTest(ControllerClass.class) Load only the web layer
 * @MockBean Mock beans within the Spring context
 * @Autowired MockMvc    Perform HTTP requests
 * @AutoConfigureMockMvc(addFilters = false)	Disable security filters
 * 2. Service Tests
 * Annotation	Purpose
 * @ExtendWith(MockitoExtension.class) Enable Mockito (no Spring context)
 * @Mock Create mock dependencies
 * @InjectMocks Inject mocks into the class under test
 * 3. Repository Tests
 * Annotation	Purpose
 * @DataJpaTest Load JPA and in-memory DB
 * @AutoConfigureTestDatabase(replace = NONE)	Use actual DB config
 * @Autowired TestEntityManager    Manage test entities manually
 * 4. Integration Tests
 * Annotation	Purpose
 * @SpringBootTest Load full application context
 * @Transactional Roll back after each test
 * @Sql("data.sql") Load test SQL scripts
 * V. Common Test Patterns
 * 1. AAA Pattern
 * Arrange-Act-Assert (AAA) is a test pattern used to structure unit tests:
 * Arrange: Set up the test (objects, data).
 * Act: Execute the behavior under test.
 * Assert: Verify the result.
 * @Test
 * void testGetById() {
 *     // === Arrange (Given) ===
 *     Customer customer = new Customer(1L, "John");
 *     when(repository.findById(1L)).thenReturn(Optional.of(customer));
 *
 *     // === Act (When) ===
 *     Customer result = service.getById(1L);
 *
 *     // === Assert (Then) ===
 *     assertNotNull(result);
 *     assertEquals("John", result.getName());
 *     verify(repository).findById(1L);
 * }
 * 2. Test Naming Convention
 * Test Naming Convention is a standardized way to name test methods for clarity and readability. It helps developers quickly understand what is being tested and what the expected outcome is.
 * Pattern: methodName_whenCondition_thenExpectedResult
 * methodName: The method being tested
 * whenCondition: The scenario or input condition
 * thenExpectedResult: The expected behavior or output
 * Examples:
 * getCustomer_whenIdExists_returnsCustomer()
 * getCustomer_whenIdNotExists_throwsException()
 * createCustomer_withInvalidEmail_throwsBadRequestException()
 * 3. Setup & Teardown
 * Setup & Teardown is a pattern for preparing test conditions before each test and cleaning up after. This ensures tests are isolated and don't affect each other.
 * Setup (@BeforeEach): Initialize common test data or dependencies before each test method runs.
 * Teardown (@AfterEach): Clean up resources (close connections, delete files, etc.) after each test completes.
 * private Customer customer;
 *
 * @BeforeEach
 * void setUp() {
 *     customer = new Customer(1L, "John", "john@test.com");
 * }
 *
 * @AfterEach
 * void tearDown() {
 *     // Clean up resources if needed
 * }
 * 4. Other Testing Methodologies
 * Keep in mind that these testing methodologies are not common in Spring Boot.
 * a. BDD (Behavioral-Driven Development)
 * Behavioral Driven Development (BDD) is a testing style that focuses on the behavior of an application. It uses natural language to describe tests and encourages collaboration between developers, testers, and business.
 * @Test
 * void shouldReturnCustomer_WhenIdExists() {
 *     // Given
 *     when(repository.findById(1L)).thenReturn(Optional.of(new Customer("John")));
 *
 *     // When
 *     Customer result = service.getById(1L);
 *
 *     // Then
 *     assertEquals("John", result.getName());
 * }
 * b. TDD (Test-Driven Development)
 * Test-Driven Development (TDD) is a development process where you:
 * Write a failing test first.
 * Write the minimal code to pass the test.
 * Refactor the code.
 * // Step 1: write failing test
 * @Test
 * void calculateDiscount_shouldReturn10Percent() {
 *     assertEquals(90, service.calculateDiscount(100));
 * }
 *
 * // Step 2: implement minimal code
 * int calculateDiscount(int price) {
 *     return price * 0.9; // make test pass
 * }
 * c. Black-box vs White-box Testing
 * Type	Description	Example
 * Black-box	Test behavior without knowing internal logic	Test API endpoint, service output
 * White-box	Test internal logic with knowledge of implementation	Unit test with mock dependencies
 * Gray-box	Combination of both	Integration test touching DB + logic
 * ✅ Rule of thumb:
 * Controller tests (@WebMvcTest) → Black-box (test HTTP request/response).
 * Service tests → Gray-box (test output + verify mocked dependencies).
 * Repository tests (@DataJpaTest) → Black-box (test queries without knowing JPA internals).
 * Integration tests (@SpringBootTest) → Black-box (test end-to-end flow).
 * Note: Most Spring Boot tests are naturally gray-box - you test behavior while also verifying internal interactions with mocks.
 * VI. Quick Reference Cheat Sheet
 * 1. MockMvc Common Patterns (Controller Tests)
 * // GET
 * mockMvc.perform(get("/api/customers/1"))
 *     .andExpect(status().isOk())
 *     .andExpect(jsonPath("$.name").value("John"));
 *
 * // POST
 * mockMvc.perform(post("/api/customers")
 *         .contentType(MediaType.APPLICATION_JSON)
 *         .content(jsonString))
 *     .andExpect(status().isCreated());
 *
 * // PUT
 * mockMvc.perform(put("/api/customers/1")
 *         .contentType(MediaType.APPLICATION_JSON)
 *         .content(jsonString))
 *     .andExpect(status().isOk());
 *
 * // DELETE
 * mockMvc.perform(delete("/api/customers/1"))
 *     .andExpect(status().isNoContent());
 * 2. Mockito Quick Patterns
 * // Stub return value
 * when(service.getById(1L)).thenReturn(customer);
 *
 * // Stub exception
 * when(service.getById(999L)).thenThrow(new NotFoundException());
 *
 * // Verify calls
 * verify(service).getById(1L);
 * verify(service, never()).delete(any());
 * verify(service, times(2)).save(any());
 *
 * Testing (JUnit/Mockito) is performed during development and CI/CD, not part of the runtime flow
 */

@SpringBootTest
class ProductServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
