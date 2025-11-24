package com.example.gift_api_remaster.service;


import com.example.gift_api_remaster.exception.GiftApiException;
import com.example.gift_api_remaster.filtering.SearchCriteria;
import com.example.gift_api_remaster.filtering.SpecificationBuilder;
import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.ChildView;
import com.example.gift_api_remaster.model.command.CreateChildCommand;
import com.example.gift_api_remaster.model.command.UpdateChildCommand;
import com.example.gift_api_remaster.model.dto.ChildDto;
import com.example.gift_api_remaster.model.mapper.ChildMapper;
import com.example.gift_api_remaster.repository.ChildRepository;
import com.example.gift_api_remaster.repository.ChildViewRepository;
import com.example.gift_api_remaster.service.operations.ChildOperations;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.gift_api_remaster.model.mapper.ChildMapper.toDto;
import static com.example.gift_api_remaster.service.operations.ChildOperations.OPERATIONS_SUFFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildService {

    private static final int BATCH_SIZE = 10_000;
    private static final int TOTAL_CHILDREN = 2_000_000;
    private static final String INSERT_CHILDREN_QUERY = "INSERT INTO child (name, surname, birthday, version, dtype) VALUES %s";
    private static final String SINGLE_CHILD_PARAMS = "(\"%s\", \"%s\", \"%s\", \"0\", \"boy\"),";

    private final JdbcTemplate jdbcTemplate;
    private final ChildRepository childRepository;
    private final Map<String, ChildOperations> childrenOperations;
    private final ChildViewRepository childViewRepository;

    public ChildDto findById(long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new GiftApiException(MessageFormat
                        .format("Child with id {0} not found", id)));
        ChildOperations childOperations = resolveChildOperations(child);
        return childOperations.mapToDto(child);
    }

    private ChildOperations resolveChildOperations(Child child) {
        String operationKey = child.getClass().getSimpleName().toLowerCase() + OPERATIONS_SUFFIX;
        ChildOperations operations = childrenOperations.get(operationKey);
        if (operations == null) {
            throw new GiftApiException(MessageFormat
                    .format("Child with type {0} not supported", child.getClass().getSimpleName()));
        }
        return operations;
    }

    private ChildOperations resolveChildOperations(ChildView childView){
        String operationKey = childView.getClass().getSimpleName().toLowerCase() + OPERATIONS_SUFFIX;
        ChildOperations operations = childrenOperations.get(operationKey);
        if (operations == null) {
            throw new GiftApiException(MessageFormat
                    .format("Child with type {0} not supported", childView.getClass().getSimpleName()));
        }
        return operations;
    }

    @Transactional(readOnly = true)
    public Page<ChildDto> findAll(Pageable pageable, List<SearchCriteria> params) {
        Specification<ChildView> specification = SpecificationBuilder.buildSpecification(params);
//        Page<Long> childrenIds = childRepository.findAllIds(pageable);
//        List<Child> children = childRepository.findAllWithGiftsByIdIn(childrenIds.getContent());
//        List<ChildDto> list = children.stream()
//                .map(child -> {
//                    ChildOperations operations = resolveChildOperations(child);
//                    return operations.mapToDto(child);
//                })
//                .toList();
        return childViewRepository.findAll(specification, pageable)
                .map(childView -> {
                    ChildOperations operations = resolveChildOperations(childView);
                    return operations.mapToDto(childView);
                });
    }

    @Transactional(readOnly = true)
    public Page<ChildDto> findAll2(Pageable pageable) {
        return childRepository.findAllAsDto(pageable);
//        return childRepository.findAllWithGifts(pageable)
//                .map(ChildMapper::toDto);
    }


    public ChildDto save(CreateChildCommand command) {
        ChildOperations operations = resolveOperationsByType(command.getType());
        Child child = operations.create(command);
        Child savedChild = childRepository.save(child);
        return operations.mapToDto(savedChild);
    }

    private ChildOperations resolveOperationsByType(String type) {
        String operationKey = type.toLowerCase() + OPERATIONS_SUFFIX;
        ChildOperations operations = childrenOperations.get(operationKey);
        if (operations == null) {
            throw new GiftApiException(MessageFormat
                    .format("Child with type {0} not supported", type));
        }
        return operations;
    }

    @Transactional
    public ChildDto update(UpdateChildCommand command, long id) {
        Child original = childRepository.findById(id)
                .orElseThrow(() -> new GiftApiException(MessageFormat
                        .format("Child with id {0} not found", id)));
        Child updated = ChildMapper.update(command, original);
        try {
            return toDto(childRepository.saveAndFlush(updated));
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new GiftApiException(MessageFormat
                    .format("Child with id {0} was modified by another transaction", id));
        }
    }

    public void deleteById(long id) {
        if (!childRepository.existsById(id)) {
            throw new GiftApiException(MessageFormat
                    .format("Child with id {0} not found", id));
        }
        childRepository.deleteById(id);
    }

    public void uploadChildren(MultipartFile file) {
        importChildrenDB(file);
    }

    // Otymistycznie tym wychodzi 1600 rekorko na sekunde
    private Set<Child> parseCsv(MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<ChildCsvRepresentaion> strategy =
                    new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ChildCsvRepresentaion.class);
            CsvToBean<ChildCsvRepresentaion> csvToBean = new CsvToBeanBuilder<ChildCsvRepresentaion>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse()
                    .stream()
                    .map(csvLine -> Child.builder()
                            .name(csvLine.getName())
                            .surname(csvLine.getSurname())
                            .birthday(LocalDate.parse(csvLine.getBirthday()))
                            .build())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new GiftApiException("Error while parsing CSV file", e);
        }
    }

    public void importChildrenDB(MultipartFile file) {
        StringBuilder params = new StringBuilder();
        int counter = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                params.append(String.format(SINGLE_CHILD_PARAMS, parts[0], parts[1], parts[2]));
                if (++counter % BATCH_SIZE == 0) {
                    int paramsLength = params.length();
                    params.delete(paramsLength - 1, paramsLength);
                    jdbcTemplate.batchUpdate(String.format(INSERT_CHILDREN_QUERY, params));
                    log.info("Generated {} children", counter);
                    counter = 0;
                    params.setLength(0);
                }

            }
        } catch (RuntimeException | IOException e) {
            throw new GiftApiException(MessageFormat.format("Error while parsing CSV file: {0}", e.getMessage()), e);
        }
    }
}
