package ru.redgho7t.spring.rest.demo.dao;

import ru.redgho7t.spring.rest.demo.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleDao {

    List<Role> findAll();

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);

    void save(Role role);

    void deleteById(Long id);
}