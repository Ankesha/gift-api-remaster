package com.example.gift_api_remaster.filtering;

import com.example.gift_api_remaster.model.Child;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class SpecificationBuilder {

    public static Specification<Child> buildSpecification(List<SearchCriteria> params) {

        List<ChildSpecification> specifications = new ArrayList<>();
        for (SearchCriteria param : params) {
            ChildSpecification newSpecification = new ChildSpecification(param);
            specifications.add(newSpecification);
        }

        //name=Maciek, surname=Nowak, birthday=2018-03-03
        //3 różen spec -> dziecko ma na imię Maciek,  dziecko ma na nazwisko Nowak,  dziecko urodziło się 2018-03-03


        Specification<Child> spec = Specification.where(null);
        for (ChildSpecification specification : specifications) {
            spec.and(specification);
        }
        // brak warunków  i  dziecko ma na imię Maciek  i  ma na nazwisko Nowak  i  urodziło się 2018-03-03


        return spec;
    }

    //


}
