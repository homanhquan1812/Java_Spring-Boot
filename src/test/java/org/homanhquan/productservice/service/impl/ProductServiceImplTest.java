package org.homanhquan.productservice.service.impl;

import org.homanhquan.productservice.builder.product.entity.BrandTestFixture;
import org.homanhquan.productservice.builder.product.entity.ProductTestFixture;
import org.homanhquan.productservice.builder.product.request.CreateProductRequestBuilder;
import org.homanhquan.productservice.builder.product.request.UpdateProductRequestBuilder;
import org.homanhquan.productservice.builder.product.response.ProductResponseBuilder;
import org.homanhquan.productservice.dto.product.request.CreateProductRequest;
import org.homanhquan.productservice.dto.product.request.UpdateProductRequest;
import org.homanhquan.productservice.dto.product.response.ProductResponse;
import org.homanhquan.productservice.entity.Product;
import org.homanhquan.productservice.exception.ResourceNotFoundException;
import org.homanhquan.productservice.mapper.ProductMapper;
import org.homanhquan.productservice.projection.ProductProjection;
import org.homanhquan.productservice.repository.BrandRepository;
import org.homanhquan.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing definition:
 * - JUnit 5 is the latest version of the JUnit testing framework for Java. It’s used to write and run automated unit tests.
 * - JUnit 5 = JUnit Platform + Jupiter + Vintage:
 *   + Platform: Runs the tests.
 *   + Jupiter: Test engine for writing JUnit 5 tests (@Test).
 *   + Vintage: Backward compatibility for JUnit 4.
 * - Mockito is used to mock dependencies and isolate the class under test. You use it to avoid calling real database, API, or heavy logic.
 * - Testing (JUnit/Mockito) is performed during development and CI/CD, not part of the runtime flow.
 * ==================================================
 * Annotation explanation:
 * - @ExtendWith(MockitoExtension.class): Enables Mockito in JUnit 5.
 * - @Mock: Creates a mock object (Repository, Mapper).
 * - @InjectMocks: Injects mocks into the tested class (Service).
 * - @BeforeEach: Runs before each test method.
 * - @Nested: Organizes related tests together.
 * - @DisplayName: Custom name for the test.
 * - @Test: Marks a test method.
 * - @Disabled: Temporarily skips a test.
 * ==================================================
 * Test Naming Convention is a standardized way to name test methods that helps developers quickly understand what is being tested and what the expected outcome is.
 * Common Patterns: methodName_whenCondition_thenExpectedResult
 * - [GET]:
 *   + getAllProducts_ShouldReturnAllProducts() -> List.
 *   + getProductsPage_ShouldReturnPagedProducts() -> Pageable.
 *   + getProductById_WhenProductExists_ShouldReturnProduct() -> Find by product ID.
 *   + getProductById_WhenProductNotExists_ShouldThrowException() -> Find by product ID.
 * - [POST]:
 *   + createProduct_WithValidData_ShouldCreateProduct() -> Find by brand ID.
 *   + createProduct_WhenBrandNotExists_ShouldThrowException() -> Find by brand ID.
 * - [PUT/PATCH]:
 *   + updateProduct_WithValidData_ShouldUpdateProduct() -> Find by product ID.
 *   + updateProductStatus_WithValidData_ShouldUpdateStatus() -> Find by product ID.
 *   + updateProduct_WhenProductNotExists_ShouldThrowException() -> Find by product ID.
 * - [DELETE]:
 *   + deleteProduct_WhenProductExists_ShouldDeleteProduct() -> Find by product ID.
 *   + deleteProduct_WhenProductNotExists_ShouldThrowException() -> Find by product ID.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID userId;
    private Long productId;
    private Long brandId;
    private Product product;
    private ProductResponse productResponse;
    private ProductProjection productProjection;

    @BeforeEach
    void setUp() {
        // Setup ID
        userId = UUID.randomUUID();
        productId = 1L;
        brandId = 1L;
        product = ProductTestFixture.buildProduct();
        productResponse = ProductResponseBuilder.productResponse();
        productProjection = mock(ProductProjection.class);
    }

    @Nested
    @DisplayName("[GET] /api/product/...")
    class GetProductMethods {

        @Test
        @DisplayName("Should get all products successfully")
        void getAllProducts_ShouldReturnAllProducts() {
            /**
             * GIVEN "return productMapper.projectionToDtoList(productRepository.findAllProductsWithBrandName());" in Service:
             * - When productRepository.findAllProductsWithBrandName(), then return list of product projections.
             * - Next, when productMapper.projectionToDtoList(), then return list of product responses.
             */
            List<ProductResponse> responses = List.of(productResponse);
            List<ProductProjection> projections = List.of(productProjection);

            when(productRepository.findAllProductsWithBrandName()).thenReturn(projections);
            when(productMapper.projectionToDtoList(projections)).thenReturn(responses);

            /**
             * WHEN Controller invokes "productService.getAllProducts()", we get a result (response).
             */
            List<ProductResponse> result = productService.getAll();

            /**
             * THEN we:
             * - Check the result with these methods: isNotNull(), hasSize(), isEqualTo(), containsExactly() that includes isNotNull() & hasSize(), etc.
             * - Verify the methods if they were invoked.
             */
            assertThat(result).containsExactly(productResponse);

            verify(productRepository).findAllProductsWithBrandName();
            verify(productMapper).projectionToDtoList(projections);
        }

        @Test
        @DisplayName("Should get products page successfully")
        void getProductsPage_ShouldReturnPagedProducts() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductProjection> projectionPage = new PageImpl<>(List.of(productProjection));

            when(productRepository.findProductsPageWithBrandName(pageable)).thenReturn(projectionPage);
            when(productMapper.projectionToDto(productProjection)).thenReturn(productResponse);

            // When
            var result = productService.getPage(pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(productRepository).findProductsPageWithBrandName(pageable);
        }

        @Test
        @DisplayName("Should get product by id successfully")
        void getProductById_WhenProductExists_ShouldReturnProduct() {
            // Given
            when(productRepository.findProductByIdWithBrandName(productId))
                    .thenReturn(Optional.of(productProjection));
            when(productMapper.projectionToDto(productProjection)).thenReturn(productResponse);

            // When
            ProductResponse result = productService.getById(productId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(productResponse);

            verify(productRepository).findProductByIdWithBrandName(productId);
            verify(productMapper).projectionToDto(productProjection);
        }

        @Test
        @DisplayName("Should throw exception when product not found by id")
        void getProductById_WhenProductNotExists_ShouldThrowException() {
            // Given
            when(productRepository.findProductByIdWithBrandName(productId))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.getById(productId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: " + productId);

            verify(productRepository).findProductByIdWithBrandName(productId);
            verify(productMapper, never()).projectionToDto(any());
        }
    }

    @Nested
    @DisplayName("[POST] /api/product/...")
    class CreateProductMethods {

        @Test
        @DisplayName("Should create product successfully")
        void createProduct_WithValidData_ShouldCreateProduct() {
            // Given
            CreateProductRequest request = CreateProductRequestBuilder.createProductRequest();

            when(productMapper.toEntity(request)).thenReturn(product);
            when(brandRepository.findById(brandId)).thenReturn(Optional.of(BrandTestFixture.buildBrand()));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(productMapper.toDto(product)).thenReturn(productResponse);

            // When
            ProductResponse result = productService.create(userId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(productResponse);

            verify(brandRepository).findById(brandId);
            verify(productRepository).save(any(Product.class));
            verify(productMapper).toEntity(request);
            verify(productMapper).toDto(product);
        }

        @Test
        @DisplayName("Should throw exception when brand not found during creation")
        void createProduct_WhenBrandNotExists_ShouldThrowException() {
            // Given
            CreateProductRequest request = CreateProductRequestBuilder.createProductRequest();

            when(productMapper.toEntity(request)).thenReturn(product);
            when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.create(userId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Brand not found with id: 1");

            verify(brandRepository).findById(brandId);
            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("[PATCH/PUT] /api/product/...")
    class UpdateProductMethods {

        @Test
        @DisplayName("Should update product successfully")
        void updateProduct_WithValidData_ShouldUpdateProduct() {
            // Given
            UpdateProductRequest request = UpdateProductRequestBuilder.updateProductRequest();

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toDto(product)).thenReturn(productResponse);
            doNothing().when(productMapper).updateEntityFromDto(request, product);

            // When
            ProductResponse result = productService.update(userId, productId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(productResponse);

            verify(productRepository).findById(productId);
            verify(productMapper).updateEntityFromDto(request, product);
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent product")
        void updateProduct_WhenProductNotExists_ShouldThrowException() {
            // Given
            UpdateProductRequest request = UpdateProductRequestBuilder.updateProductRequest();

            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.update(userId, productId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: " + productId);

            verify(productRepository).findById(productId);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update product status successfully")
        void updateProductStatus_WithValidData_ShouldUpdateStatus() {
            // Given
            //UpdateProductStatusRequest request = new UpdateProductStatusRequest(Status.SUSPENDED);

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toDto(product)).thenReturn(productResponse);
            //doNothing().when(productMapper).updateStatusFromDto(request, product);

            // When
            ProductResponse result = productService.updateStatus(userId, productId);

            // Then
            assertThat(result).isNotNull();
            assertThat(product.getDeletedBy()).isEqualTo(userId);
            assertThat(product.getDeletedAt()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

            verify(productRepository).findById(productId);
            verify(productRepository).save(product);
        }
    }

    @Nested
    @DisplayName("[DELETE] /api/product/...")
    class DeleteProductMethods {

        @Test
        @DisplayName("Should delete product successfully")
        void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            doNothing().when(productRepository).delete(product);

            // When
            productService.delete(userId, productId);

            // Then
            verify(productRepository).findById(productId);
            verify(productRepository).delete(product);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent product")
        void deleteProduct_WhenProductNotExists_ShouldThrowException() {
            // Given
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.delete(userId, productId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: " + productId);

            verify(productRepository).findById(productId);
            verify(productRepository, never()).delete(any());
        }
    }
}