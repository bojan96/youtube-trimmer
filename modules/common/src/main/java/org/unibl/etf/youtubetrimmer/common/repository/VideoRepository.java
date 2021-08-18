package org.unibl.etf.youtubetrimmer.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.youtubetrimmer.common.entity.VideoEntity;

public interface VideoRepository extends JpaRepository<VideoEntity, Integer> {
}
