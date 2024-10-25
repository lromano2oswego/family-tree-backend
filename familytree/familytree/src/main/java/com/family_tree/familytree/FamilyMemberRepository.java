package com.family_tree.familytree;

import org.springframework.data.repository.CrudRepository;
import com.family_tree.familytree.FamilyMember;
public interface FamilyMemberRepository extends CrudRepository<FamilyMember, Integer> {

}
