package ru.redgho7t.spring.rest.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.redgho7t.spring.rest.demo.dao.RoleDao;
import ru.redgho7t.spring.rest.demo.model.Role;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
        logger.info("RoleService initialized");
    }

    @Override
    public List<Role> getAllRoles() {
        logger.debug("Fetching all roles");
        return roleDao.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        logger.debug("Fetching role by ID: {}", id);
        Optional<Role> roleOpt = roleDao.findById(id);
        return roleOpt.orElse(null);
    }

    @Override
    public Role getRoleByName(String name) {
        logger.debug("Fetching role by name: {}", name);
        Optional<Role> roleOpt = roleDao.findByName(name);
        return roleOpt.orElse(null);
    }
}