package com.family_tree.familytree;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConflictLogRepository extends CrudRepository<ConflictLog, Integer> {

    // Custom query to find conflicts by tree ID and status
    @Query("SELECT c FROM ConflictLog c WHERE c.treeId = :treeId AND c.status = :status")
    List<ConflictLog> findByTreeIdAndStatus(@Param("treeId") Integer treeId, @Param("status") String status);

    // Custom query to find conflicts by tree ID only
    @Query("SELECT c FROM ConflictLog c WHERE c.treeId = :treeId")
    List<ConflictLog> findByTreeId(@Param("treeId") Integer treeId);
}
