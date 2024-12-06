package com.family_tree.familytree;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MergeRequestRepository extends CrudRepository<MergeRequest, Integer> {
    // Find all merge requests for a specific target tree by its ID
    List<MergeRequest> findByTargetTree_Id(Integer targetTreeId);
    @Modifying
    @Transactional
    @Query("DELETE FROM MergeRequest m WHERE m.targetTree.id = :treeId")
    void deleteByTargetTreeId(@Param("treeId") Integer treeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MergeRequest m WHERE m.requesterTree.id = :treeId")
    void deleteByRequesterTreeId(@Param("treeId") Integer treeId);
    List<MergeRequest> findByRequesterTree_Id(Integer requesterTreeId);
}
