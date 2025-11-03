package com.example.gift_api_remaster.model;

import com.example.gift_api_remaster.exception.GiftApiException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Accessors(chain = true) // sprawia ze seter zwraca od razu obiekt
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
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
