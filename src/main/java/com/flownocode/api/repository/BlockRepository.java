package com.flownocode.api.repository;

import com.flownocode.api.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface BlockRepository extends JpaRepository<Block, UUID> {

    boolean existsByIdIn(Collection<UUID> ids);
}
