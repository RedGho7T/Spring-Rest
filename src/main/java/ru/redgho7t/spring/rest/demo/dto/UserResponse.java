package ru.redgho7t.spring.rest.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.redgho7t.spring.rest.demo.model.Role;
import ru.redgho7t.spring.rest.demo.model.User;

import java.util.HashSet;
import java.util.Set;

public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;

    @JsonIgnoreProperties({"users"})
    private Set<Role> roles;

    public UserResponse() {
        this.roles = new HashSet<>();
    }

    public UserResponse(User user) {
        if (user == null) {
            this.roles = new HashSet<>();
            return;
        }

        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.age = user.getAge();
        this.email = user.getEmail();

        try {
            if (user.getRoles() != null) {
                this.roles = new HashSet<>(user.getRoles());
            } else {
                this.roles = new HashSet<>();
            }
        } catch (Exception e) {
            this.roles = new HashSet<>();
        }
    }

    public static UserResponse fromUser(User user) {
        if (user == null) {
            return new UserResponse();
        }
        return new UserResponse(user);
    }

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "Unknown User";
        }
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public Set<String> getRoleNames() {
        Set<String> roleNames = new HashSet<>();
        if (roles != null) {
            for (Role role : roles) {
                if (role != null && role.getName() != null) {
                    roleNames.add(role.getName());
                }
            }
        }
        return roleNames;
    }

    public boolean isAdmin() {
        return getRoleNames().contains("ROLE_ADMIN");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles != null ? roles : new HashSet<>();
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", roles=" + (roles != null ? roles.size() : 0) + " roles" +
                '}';
    }
}