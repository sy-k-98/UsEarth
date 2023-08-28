package com.isfive.usearth.domain.project.entity;

import com.isfive.usearth.domain.common.BaseEntity;
import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.Period;
import com.isfive.usearth.domain.maker.entity.Maker;
import com.isfive.usearth.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String summary;

    private String story;

    private Integer targetAmount;

    private Integer totalFundingAmount;

    @Embedded
    private FileImage repImage; // 대표이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id")
    private Maker maker;

    @Embedded
    private Period fundingDate;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<Tag> searchTags = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<Reward> rewards = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<ProjectFileImage> projectImages = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ProjectComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ProjectLike> likes = new ArrayList<>();

    public void setMaker(Maker maker) {
        this.maker = maker;
        maker.getProjects().add(this);
    }

    public void setRepImage(FileImage fileImage) {
        this.repImage = fileImage;
    }

    public void addTag(Tag tag) {
        this.getSearchTags().add(tag);
        if (tag.getProject() != this)
            tag.setProject(this);
    }

    public void addReward(Reward reward) {
        this.getRewards().add(reward);
        if (reward.getProject() != this)
            reward.setProject(this);
    }

    public void addProjectFileImage(ProjectFileImage image) {
        this.getProjectImages().add(image);
        if (image.getProject() != this)
            image.setProject(this);
    }

    public void update(Project projectUpdate) {
        if (projectUpdate.getTitle() != null)
            this.title = projectUpdate.getTitle();
        if (projectUpdate.getSummary() != null)
            this.summary = projectUpdate.getSummary();
        if (projectUpdate.getRepImage() != null)
            this.repImage = projectUpdate.getRepImage();
    }

    public void delete() {
        deletedAt = LocalDateTime.now();
    }

}
