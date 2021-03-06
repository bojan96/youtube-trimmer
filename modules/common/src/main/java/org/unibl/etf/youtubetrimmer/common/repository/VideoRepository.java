package org.unibl.etf.youtubetrimmer.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unibl.etf.youtubetrimmer.common.entity.VideoEntity;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Integer> {
}
