package com.code.challenge.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter
@Setter

public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Street address is required.")
    private String streetAddress;
    @NotBlank(message = "Apartment number cannot be left blank.")
    private String apartmentNumber;
    @NotBlank(message = "City is required.")
    private String city;
    @NotBlank(message = "State is required.")
    private String state;
    @NotBlank(message = "Postal code is required.")
    private String postalCode;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


    public void updateFrom(UserAddress other) {
        this.streetAddress = other.streetAddress;
        this.city = other.city;
        this.state = other.state;
        this.postalCode = other.postalCode;
        this.apartmentNumber = other.apartmentNumber;
    }
}
