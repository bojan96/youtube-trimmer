package org.unibl.etf.youtubetrimmer.common.entity;

import lombok.Data;
import org.unibl.etf.youtubetrimmer.common.converter.JobStatusConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "job")
public class JobEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic
    @Column(name = "status", nullable = false)
    @Convert(converter = JobStatusConverter.class)
    private JobStatus status;
    @Basic
    @Column(name = "trim_from", nullable = false)
    private Integer trimFrom;
    @Basic
    @Column(name = "trim_to", nullable = false)
    private Integer trimTo;
    @Basic
    @Column(name = "trimmed_video_reference", nullable = true, length = 2048)
    private String trimmedVideoReference;
    @Basic
    @Column(name = "date", nullable = true)
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "id", nullable = false)
    private VideoEntity video;

}
