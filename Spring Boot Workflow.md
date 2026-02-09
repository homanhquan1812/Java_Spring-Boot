Client: Send HTTP Request (GET/POST/PUT/DELETE) with JSON/XML body.
    ↓
Security Filter Chain
    | CORS, CSRF
    | Authentication (JWT/Session)
    | Authorization (URL-level)
    ↓
DispatcherServlet (Front Controller): Receive request.
    ↓
Handler Mapping: Find appropriate Controller + Interceptors (Optional).
    ↓
Handler Adapter: Prepare to invoke Controller method:
    |
    | Check authorization (@PreAuthorize, @PostAuthorize).
    | Parse request params (@RequestParam, @PathVariable, @RequestBody, etc).
    | Deserialize JSON/XML to DTO (via HttpMessageConverter) for POST/PUT.
    | Validate with @Valid.
    ↓
Controller: Invoke method + Call Service.
    ↓    
Service: Process business logic:
    |
    | @Transactional starts transactions.
    | Call Repository to query Database (JPA/JDBC).
    | Convert DTO → Entity through Mapper (POST/PUT).
    ↓     
Database: Execute query and return data to Repository:
    |
    | GET: Filtered Entity/Projection.
    | POST/PUT: Saved Entity.
    ↓  
Service: Process additional business logic (if needed):
    |
    | Convert Entity/Projection → DTO through Mapper (GET).
    | Convert Entity → DTO through Mapper (POST/PUT).
    ↓  
Controller: Prepare response data:
    |
    | HttpMessageConverter: Serialize Object to JSON/XML.
    | Adds Security Headers (X-Frame-Options, HSTS, CSP, etc.).
    ↓
DispatcherServlet: Send response to client.
    ↓
Client receives response.