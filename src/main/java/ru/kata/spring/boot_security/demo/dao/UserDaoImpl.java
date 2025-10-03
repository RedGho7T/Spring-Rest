package ru.kata.spring.boot_security.demo.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findAll() {
        logger.debug("Fetching all users from database");
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    @Override
    public Optional<User> findById(Long id) {
        logger.debug("Searching for user by ID: {}", id);
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public void save(User user) {
        logger.debug("Saving user: {}", user.getEmail());
        entityManager.persist(user);
        logger.debug("User {} saved successfully", user.getEmail());
    }

    @Override
    public void update(User user) {
        logger.debug("Updating user: {}", user.getEmail());
        entityManager.merge(user);
        logger.debug("User {} updated successfully", user.getEmail());
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
            logger.debug("User with ID {} deleted successfully", id);
        } else {
            logger.warn("User with ID {} not found for deletion", id);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.debug("Checking existence of email: {}", email);
        try {
            Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            boolean exists = count > 0;
            logger.debug("Email {} exists: {}", email, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking email existence for {}: {}", email, e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Searching for user by email: {}", email);
        try {
            User user = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            logger.debug("User found by email: {}", email);
            return Optional.of(user);
        } catch (NoResultException e) {
            logger.debug("No user found with email: {}", email);
            return Optional.empty();
        }
    }
}
