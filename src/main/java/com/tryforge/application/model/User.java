package com.tryforge.application.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Email
    @NotBlank
    @NotNull
    @Column(unique = true, updatable = false)
    String email;

    String password;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dateCreated;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dateOfBirth;

    private String addressLocation;

    private Double latitude, longitude;

    @PrePersist
    public void init(){
        dateCreated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(String addressLocation) {
        this.addressLocation = addressLocation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                email.equals(user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(dateCreated, user.dateCreated) &&
                Objects.equals(dateOfBirth, user.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, dateCreated, dateOfBirth);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
