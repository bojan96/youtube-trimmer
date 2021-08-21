package org.unibl.etf.youtubetrimmer.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<JobEntity> jobs = new ArrayList<>();

    public void addJob(JobEntity job) {
        jobs.add(job);
        job.setVideo(this);
    }
}
