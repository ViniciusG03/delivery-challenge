package com.cocobambu.delivery.repository;

import com.cocobambu.delivery.model.OrderWrapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Repository
public class OrderRepository {

    @Value("${orders.file.path}")
    private String filePath;

    private final ObjectMapper objectMapper;
    private final List<OrderWrapper> orders = new ArrayList<>();
    private final Object lock = new Object();

    public OrderRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        loadFromFile();
    }

    public List<OrderWrapper> findAll() {
        synchronized (lock) {
            return Collections.unmodifiableList(new ArrayList<>(orders));
        }
    }

    public Optional<OrderWrapper> findById(String id) {
        synchronized (lock) {
            return orders.stream()
                    .filter(o -> o.getOrderId().equals(id))
                    .findFirst();
        }
    }

    public OrderWrapper save(OrderWrapper orderWrapper) {
        synchronized (lock) {
            orders.removeIf(o -> o.getOrderId().equals(orderWrapper.getOrderId()));
            orders.add(orderWrapper);
            flushToFile();
            return orderWrapper;
        }
    }

    public boolean deleteById(String id) {
        synchronized (lock) {
            boolean removed = orders.removeIf(o -> o.getOrderId().equals(id));
            if (removed) {
                flushToFile();
            }
            return removed;
        }
    }

    private void loadFromFile() {
        Path path = resolveFilePath();
        if (Files.exists(path)) {
            List<OrderWrapper> loaded = objectMapper.readValue(
                    path.toFile(),
                    new TypeReference<List<OrderWrapper>>() {}
            );
            orders.clear();
            orders.addAll(loaded);
        }
    }

    private void flushToFile() {
        try {
            Path path = resolveFilePath();
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Path tmpFile = parent != null
                    ? parent.resolve("pedidos.tmp")
                    : Path.of("pedidos.tmp");

            objectMapper.writeValue(tmpFile.toFile(), orders);

            try {
                Files.move(tmpFile, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmpFile, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write orders to file", e);
        }
    }

    private Path resolveFilePath() {
        return Path.of(filePath).toAbsolutePath().normalize();
    }
}
