package org.homanhquan.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {

    /**
     * Spring Boot is a framework that simplifies building and running Spring applications by providing auto-configuration
     * based on your dependencies, an embedded server (such as Tomcat, Jetty, or Undertow), and greatly reducing the amount of manual setup required.
     * ==================================================
     * Spring Data JPA simplifies working with relational databases by providing CRUD operations and query methods on entities,
     * using JPA (often with Hibernate) to reduce boilerplate compared to plain JDBC.
     * To be more specific, Hibernate is a JPA implementation (ORM framework) that maps Java objects to database tables and handles SQL generation, caching, transactions, etc.
     * ==================================================
     * Spring MVC is a web framework module within the Spring Framework that implements the MVC design pattern. It is typically used to build server-side rendered web applications,
     * where controllers return HTML views (using technologies like JSP, Thymeleaf, etc.) instead of JSON responses like in Spring REST.
     * ==================================================
     * Bean is an object that is created and managed by Spring, instead of you manually creating it with new.
     * When you annotate a class or method with Spring annotations (like @Component, @Service, @Repository, or @Bean),
     * Spring will create, configure, and manage the lifecycle of that object for you. This object is now called a Spring bean.
     * Spring beans let you:
     * - Reuse components (singleton by default).
     * - Inject dependencies automatically using @Autowired - An annotation that automatically injects a bean by type into another bean.
     * - Manage configuration and wiring for those components in one place.
     * ==================================================
     * Bean lifecycles:
     * - Bean created (Instantiation).
     * - Dependencies injected from Constructors, Fields, Setters.
     * - Initialized by @PostConstruct.
     * - Bean is in use.
     * - Destroyed by @PreDestroy.
     * ==================================================
     * Bean scopes: Defines how long a bean lives and how many instances of it exist within the Spring container or web context.
     * There are 6 common bean scopes (2 first core scopes + 4 last web scopes):
     * - Singleton: Only one instance in the whole Spring container.
     *   Usage (90-95%): @Service, @Component, @Repository, etc is singleton (stateless object).
     * - Prototype: New instance every time you request it.
     *   Usage (2-5%): Stateful objects that need fresh state each time (e.g., report generators, file processors).
     * - Request: New instance per HTTP request.
     *   Usage (1-3%): Request tracking (request ID, logging context, current user info).
     * - Session: New instance per HTTP session (same user keeps same bean).
     *   Usage (1-2%): Shopping cart, user preferences, session-specific data (stateful session).
     * - Application: One instance per ServletContext (whole web app).
     *   Usage (< 1%): Global counters, application-wide configurations shared across all users.
     * - WebSocket: One instance per WebSocket session.
     *   Usage (< 1%): Real-time chat, live notifications, WebSocket connection state.
     * ==================================================
     * Dependency Injection (DI) is a design pattern where an object’s dependencies are provided (injected) from outside, rather than the object creating them itself.
     * In Spring, DI is the foundation of how objects (beans) are wired together automatically by the IoC container.
     * ==================================================
     * Circular Dependency: A situation where two or more beans depend on each other, creating a cycle that Spring cannot resolve during initialization.
     * Ex: Bean A needs Bean B, and Bean B needs Bean A → Spring doesn't know which one to create first.
     * -> How to fix: Use @Lazy, Setters, or refactor design (Best practice).
     * ==================================================
     * IoC (Inversion of Control) is a principle where the control of object creation and management is transferred from the developer to the Spring framework.
     * IoC Container is the “box” that holds all the Beans and is responsible for:
     * - Creating Beans.
     * - Managing the lifecycle of Beans.
     * - Injecting Beans where they are needed.
     * There are two types of IoC Container:
     * - BeanFactory: The root interface for Spring's IoC container. It is responsible for creating and managing beans.
     *   It lazily initializes beans, that means it creates a bean only when you request it.
     * - ApplicationContext: A more powerful and feature-rich container that extends BeanFactory. It provides extra features such as:
     *   Automatic bean creation (Eager by default), Environment support (Profiles, properties), etc. SpringApplication always builds on top of ApplicationContext.
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
