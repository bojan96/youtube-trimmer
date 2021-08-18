package org.unibl.etf.youtubetrimmer.common.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "video")
public class VideoEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic
    @Column(name = "video_uid", nullable = false, length = 255)
    private String videoUid;
    @Basic
    @Column(name = "video_reference", nullable = true, length = 2048)
    private String videoReference;
    @OneToMany(mappedBy = "video")
    private List<JobEntity> jobs;

}
