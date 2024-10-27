package com.family_tree.familytree;

import org.springframework.data.repository.CrudRepository;
import com.family_tree.familytree.Collaboration;
public interface CollaborationRepository extends CrudRepository<Collaboration, Integer> {
    //Add query calls here
}
