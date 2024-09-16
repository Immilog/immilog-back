package com.backend.immilog.post.infrastructure.jpa.repository;

import com.backend.immilog.post.infrastructure.jpa.entities.JobBoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobBoardJpaRepository extends JpaRepository<JobBoardEntity, Long> {

}
