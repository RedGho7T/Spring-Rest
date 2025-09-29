package ru.kata.spring.boot_security.demo.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // НОВЫЕ ПОЛЯ
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    // СТАРОЕ ПОЛЕ - сохраняем для совместимости
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    // Связь Many-to-Many с ролями
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Конструкторы
    public User() {}

    // Старый конструктор - для совместимости
    public User(String name, int age, String email, String password) {
        String[] nameParts = name.split(" ", 2);
        this.firstName = nameParts[0];
        this.lastName = nameParts.length > 1 ? nameParts[1] : nameParts[0];
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    // Новый конструктор с отдельными именем и фамилией
    public User(String firstName, String lastName, int age, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.password = password;
        updateFullName();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // НОВЫЕ геттеры/сеттеры
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    // СТАРЫЕ геттеры/сеттеры - для совместимости
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        // При установке name пытаемся разделить на firstName и lastName
        if (firstName == null || lastName == null) {
            String[] nameParts = name.split(" ", 2);
            if (firstName == null) {
                this.firstName = nameParts[0];
            }
            if (lastName == null) {
                this.lastName = nameParts.length > 1 ? nameParts[1] : nameParts[0];
            }
        }
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Метод для автоматического формирования полного имени
    public void updateFullName() {
        if (firstName != null && lastName != null) {
            this.name = firstName + " " + lastName;
        }
    }

    // Вспомогательные методы
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return name != null ? name : "";
    }

    // УПРОЩЕННАЯ РЕАЛИЗАЦИЯ UserDetails - без шифрования паролей
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles; // Роли реализуют GrantedAuthority
    }

    @Override
    public String getPassword() {
        return password; // Возвращаем пароль как есть
    }

    @Override
    public String getUsername() {
        return email; // Используем email как имя пользователя
    }

    // Все методы возвращают true - пользователь активен
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Методы для JPA callbacks
    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        updateFullName();
    }

    @Override
    public String toString() {
        return "User{id=" + id +
                ", firstName='" + firstName + "'" +
                ", lastName='" + lastName + "'" +
                ", email='" + email + "'}";
    }
}