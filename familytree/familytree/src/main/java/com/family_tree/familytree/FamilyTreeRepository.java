package com.family_tree.familytree;

import org.springframework.data.repository.CrudRepository;
import com.family_tree.familytree.FamilyTree;
public interface FamilyTreeRepository extends CrudRepository<FamilyTree, Integer> {
    // Custom query methods can be added here
}
