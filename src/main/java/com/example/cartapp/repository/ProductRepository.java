package com.example.cartapp.repository;

import com.example.cartapp.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class ProductRepository {

    private final List<Product> products = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong();


    @PostConstruct
    public void init() {
        save(new Product(null, "Ноутбук Lenovo", new BigDecimal("32999.00")));
        save(new Product(null, "Смартфон Samsung Galaxy", new BigDecimal("18999.00")));
        save(new Product(null, "Навушники Sony WH-1000XM5", new BigDecimal("12999.00")));
        save(new Product(null, "Клавіатура механічна", new BigDecimal("2499.00")));
        save(new Product(null, "Миша бездротова", new BigDecimal("899.00")));
    }


    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.incrementAndGet());
            products.add(product);
        } else {
            int index = indexOf(product.getId());
            if (index >= 0) {
                products.set(index, product);
            } else {
                products.add(product);
            }
        }
        return product;
    }


    public List<Product> findAll() {
        return new ArrayList<>(products);
    }


    public Optional<Product> findById(Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }


    public Optional<Product> update(Long id, Product updated) {
        int index = indexOf(id);
        if (index < 0) {
            return Optional.empty();
        }
        updated.setId(id);
        products.set(index, updated);
        return Optional.of(updated);
    }


    public boolean deleteById(Long id) {
        return products.removeIf(p -> p.getId().equals(id));
    }

    private int indexOf(Long id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
