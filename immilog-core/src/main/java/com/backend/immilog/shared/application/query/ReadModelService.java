package com.backend.immilog.shared.application.query;

import java.util.List;
import java.util.Optional;

public interface ReadModelService<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    List<T> findByCriteria(QueryCriteria criteria);

    PagedResult<T> findPagedByCriteria(
            QueryCriteria criteria,
            PageRequest pageRequest
    );

    long countByCriteria(QueryCriteria criteria);

    boolean existsById(ID id);
}