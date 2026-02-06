package org.homanhquan.productservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
/**
 * AOP can be applied to Controller, Service, or Repository methods to wrap execution, modify inputs/outputs, or handle cross-cutting concerns such as logging, transactions, caching, or validation.
 *
 * Handler Interceptor is a component that intercepts HTTP requests before and after they reach the Controller. It's part of Spring MVC's request lifecycle.
 * There are 3 methods:
 * preHandle()
 * Runs BEFORE Controller method is invoked.
 * Return true → continue request; false → stop request.
 * Use for: Authentication, Authorization, JWT validation, logging.
 * postHandle()
 * Runs AFTER Controller method completes (before response serialization).
 * Can modify response or add headers.
 * Use for: Add custom headers, logging response status.
 * Note: Often skipped (ModelAndView is null in REST APIs).
 * afterCompletion()
 * Runs AFTER response is sent (cleanup phase).
 * Executes even if exception occurred.
 * Use for: Resource cleanup, final logging, monitoring.
 * II. Why is it not common in modern Spring Boot?
 * There are 2 things that do much better than Handler Interceptor:
 * Security Filter (Spring Security):
 * Runs BEFORE DispatcherServlet (lower level).
 * Better for authentication/authorization.
 * Dedicated for security concerns.
 * More flexible.
 * AOP (Aspect-Oriented Programming):
 * Can intercept at method level (not just HTTP).
 * Better for cross-cutting concerns (logging, caching, transactions).
 * More powerful than Interceptor.
 * When to use Interceptor?
 * HTTP-level concerns only.
 * Logging request/response.
 * Adding custom headers.
 * Request validation (not security).
 * Modern practice: Use Security Filter for auth, AOP for business logic, Interceptor for HTTP-specific tasks.
 *
 * AOP (Aspect-Oriented Programming) is a programming technique that allows separation of cross-cutting concerns from core business logic.
 * Cross-Cutting Concerns are functionalities used across multiple parts of an application:
 * Logging: Log entries in multiple methods.
 * Security: Access permission checks.
 * Transaction Management: Managing transactions.
 * Caching: Data caching.
 * Error Handling: Exception management.
 * Performance Monitoring: Execution time measurement.
 * Auditing: Recording sensitive operations.
 *
 * II. Core AOP Concepts
 * 1. Aspect
 * An Aspect is a class containing advice and pointcuts.
 * 2. Join Point
 * A Join Point is a point in the program where an aspect can be applied (method execution, exception thrown, field access, etc.).
 * In Spring AOP, join points only support method execution.
 * 3. Pointcut
 * A Pointcut is an expression that determines which join points will have advice applied.
 * Some types of Expression Syntax:
 * a. execution()
 * execution([modifiers] return-type [package].class.method(params) [throws exceptions])
 * Wildcards:
 * = match 1 element
 * .. = match 0 or more elements (packages or parameters)
 * b. within()
 * Match all methods within a package or class:
 * c. @annotation()
 * Match methods with a specific annotation:
 * d. args()
 * Match methods based on parameters:
 * e. Combining Pointcuts
 * Use &&, ||, ! to combine expressions:
 * 4. Advice
 * Advice is the action taken at a specific join point.
 * Types of Advice:
 * Advice	Execution Time	Use Cases
 * @Before Before the method executes	Validation, logging input parameters
 * @After After the method executes (success or error)	Cleanup, audit logging
 * @AfterReturning After successful method execution	Logging results, caching results
 * @AfterThrowing When the method throws an exception	Error logging, notifications
 * @Around Wraps the method (before and after execution)	Performance monitoring, transaction management
 * 5. Target Object
 * The Target Object is the object that advice is applied to (e.g., CustomerService).
 * 6. Weaving
 * Weaving is the process of linking aspects with target objects. Spring AOP uses runtime weaving (proxy-based).
 * III. Advice Types in Detail
 * 1. @Before
 * Runs BEFORE method execution. Cannot modify return value.
 * Use Cases:
 * Input parameter validation.
 * Request logging.
 * Security checks.
 * 2. @AfterReturning
 * Runs AFTER successful method execution. Can access return value.
 * Use Cases:
 * Response logging.
 * Caching results.
 * Success notifications.
 * 3. @AfterThrowing
 * Runs when method THROWS AN EXCEPTION. Can access the exception.
 * Use Cases:
 * Error logging.
 * Sending alerts/notifications.
 * Custom rollback logic.
 * 4. @After
 * Runs AFTER method execution (success or failure). Like a finally block.
 * Use Cases:
 * Resource cleanup.
 * Audit logging.
 * Releasing locks.
 * 5. @Around
 * MOST POWERFUL - wraps the method, controls entire execution flow.
 * Use Cases:
 * Performance monitoring
 * Transaction management
 * Caching
 * Retry logic
 * Modifying return values
 * ⚠️ Important:
 * MUST call pjp.proceed() for the original method to execute
 * MUST return the result
 * IV. Advice Execution Order
 * When multiple aspects exist:
 * @Around (before part)
 *   ↓
 * @Before
 *   ↓
 * METHOD EXECUTION
 *   ↓
 * @AfterReturning / @AfterThrowing
 *   ↓
 * @After
 *   ↓
 * @Around (after part)
 * V. JoinPoint vs ProceedingJoinPoint
 * JoinPoint: Used for: @Before, @After, @AfterReturning, @AfterThrowing
 * ProceedingJoinPoint: ONLY used for: @Around
 * VI. Spring Caching with Redis
 * 1. Overview
 * Spring Caching is a declarative caching mechanism that uses AOP to intercept method calls and manage cache operations automatically.
 * Benefits:
 * Reduces database queries.
 * Improves application performance.
 * Separates caching logic from business logic.
 * Supports multiple cache providers (Redis, EhCache, Caffeine, etc.).
 */
public class AuditAspect {
    // Áp dụng cho create, update, delete trong service
    /*
    @Pointcut("execution(* org.homanhquan.productservice.service..*.create*(..)) || " +
            "execution(* org.homanhquan.productservice.service..*.update*(..)) || " +
            "execution(* org.homanhquan.productservice.service..*.delete*(..))")

     */
    @Pointcut("execution(* org.homanhquan.productservice.service..*(..))")
    public void auditActions() {}

    @After("auditActions()")
    public void logAudit(JoinPoint joinPoint) {
        log.info("🧾 [Audit] User action: {}.{}() called with args = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
        // Optional: lưu audit log vào DB / Kafka / file log
    }
}