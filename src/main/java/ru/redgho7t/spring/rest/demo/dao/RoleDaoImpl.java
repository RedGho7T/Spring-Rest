package ru.redgho7t.spring.rest.demo.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.redgho7t.spring.rest.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class RoleDaoImpl implements RoleDao {

    private static final Logger logger = LoggerFactory.getLogger(RoleDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> findAll() {
        logger.debug("Fetching all roles");
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r", Role.class);
        return query.getResultList();
    }

    @Override
    public Optional<Role> findById(Long id) {
        logger.debug("Searching role by ID: {}", id);
        Role role = entityManager.find(Role.class, id);
        return Optional.ofNullable(role);
    }

    @Override
    public Optional<Role> findByName(String name) {
        logger.debug("Searching role by name: {}", name);
        try {
            TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class);
            query.setParameter("name", name);
            Role role = query.getSingleResult();
            return Optional.of(role);
        } catch (NoResultException e) {
            logger.debug("Role not found: {}", name);
            return Optional.empty();
        }
    }

    @Override
    public void save(Role role) {
        logger.debug("Saving role: {}", role.getName());
        if (role.getId() == null) {
            entityManager.persist(role);
            logger.debug("Role created: {}", role.getName());
        } else {
            entityManager.merge(role);
            logger.debug("Role updated: {}", role.getName());
        }
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting role with ID: {}", id);
        Role role = entityManager.find(Role.class, id);
        if (role != null) {
            entityManager.remove(role);
            logger.debug("Role deleted: {}", role.getName());
        } else {
            logger.warn("Role with ID {} not found for deletion", id);
        }
    }
}
