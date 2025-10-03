package ru.redgho7t.spring.rest.demo.service;

import ru.redgho7t.spring.rest.demo.model.Role;

import java.util.List;

public interface RoleService {

    List<Role> getAllRoles();

    Role getRoleById(Long id);

    Role getRoleByName(String name);
}
