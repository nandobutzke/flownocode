package com.flownocode.api.domain;

import com.flownocode.api.domain.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "flows")
@Getter
@Setter
@NoArgsConstructor
public class Flow extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private UUID startBlockId;

    @OneToMany(mappedBy = "flow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Block> blocks = new ArrayList<>();
}
