package org.homanhquan.productservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.homanhquan.productservice.entity.common.DateAuditable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brand")
@Getter
@Setter
@NoArgsConstructor
public class Brand extends DateAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "opening_hours", nullable = false)
    private LocalTime openingHours;

    @Column(name = "closing_hours", nullable = false)
    private LocalTime closingHours;

    // Manual equals() & hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brand other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @OneToMany(mappedBy = "brand", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE
    }, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public static Brand of(String name, String description, String address, LocalTime openingHours, LocalTime closingHours) {
        Brand brand = new Brand();

        brand.name = name;
        brand.description = description;
        brand.address = address;
        brand.openingHours = openingHours;
        brand.closingHours = closingHours;

        return brand;
    }
}
