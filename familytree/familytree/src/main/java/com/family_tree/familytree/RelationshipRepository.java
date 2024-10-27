package com.family_tree.familytree;

import org.springframework.data.jpa.repository.JpaRepository;
import com.family_tree.familytree.Relationship;
public interface RelationshipRepository extends JpaRepository<Relationship, Integer> {

}
