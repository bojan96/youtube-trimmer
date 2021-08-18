package org.unibl.etf.youtubetrimmer.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.youtubetrimmer.common.entity.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity, Integer> {
}
