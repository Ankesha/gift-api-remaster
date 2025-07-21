package com.example.gift_api_remaster.model;

import com.example.gift_api_remaster.exception.GiftApiException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Accessors(chain = true)
public class Child implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surname;
    private LocalDate birthday;

    @OneToMany(mappedBy = "child")
    private Set<Gift> gifts;

    @Version
    private int version;

    @Override
    public Child clone() {
        try {
            return (Child) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new GiftApiException("Cloning not supported for Child entity", e);
        }
    }
}
