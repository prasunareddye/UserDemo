package com.code.challenge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Role {
    @Id
    private String name;
}
