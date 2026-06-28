package com.richenterprises.banking_api.entity;

// JPA (Hibernate imports)
import jakarta.persistence.*;

// Lombok imports
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

// Java Time import
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * user entity for authentication and role-based access control.
 * This will map to the users table in PostgreSQL.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Primary key.
     * This will use IDENTITY to leverage PostgreSQL's BIGSERIAL type which 
     * automatically generates a sequential, unique primary key value for the table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* A unique identifier for login. It is enforced at the database level. */
    @Column(unique = true, nullable = false)
    private String email;

    /* Hash encrypted password */
    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    /*Display name for the user */
    private String name;

    /*The role which determines access level: CUSTOMER or ADMIN.
    * The role is stored as a STRING rather than an ORDINAL, avoiding the corruption of
    prexisting data when the enum is updated.
    */
    @Enumerated(EnumType.STRING)
    private Role role;

    /* A Timestamp that is set automatically upon creation.
     * It is never updated after it is inserted.
    */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
