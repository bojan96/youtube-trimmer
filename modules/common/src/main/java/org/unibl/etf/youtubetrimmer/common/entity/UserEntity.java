package org.unibl.etf.youtubetrimmer.common.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Basic
    @Column(name = "username", nullable = false, length = 255)
    private String username;
    @Basic
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<JobEntity> jobs = new ArrayList<>();

    public void addJob(JobEntity job) {
        jobs.add(job);
        job.setUser(this);
    }
}
