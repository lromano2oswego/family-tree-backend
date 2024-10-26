package com.family_tree.familytree;

import org.springframework.data.repository.CrudRepository;
import com.family_tree.familytree.SuggestEdit;

public interface SuggestEditRepository extends CrudRepository<SuggestEdit, Integer> {
    // Custom query methods can be added here
}
