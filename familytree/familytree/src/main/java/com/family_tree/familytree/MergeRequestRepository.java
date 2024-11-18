package com.family_tree.familytree;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MergeRequestRepository extends CrudRepository<MergeRequest, Integer> {
    // Find all merge requests for a specific target tree by its ID
    List<MergeRequest> findByTargetTree_Id(Integer targetTreeId);

    //Add this if you want to find merge requests by requester tree ID
    List<MergeRequest> findByRequesterTree_Id(Integer requesterTreeId);
}
