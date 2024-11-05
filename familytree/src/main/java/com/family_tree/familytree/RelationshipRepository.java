package com.family_tree.familytree;

import com.family_tree.enums.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.family_tree.familytree.Relationship;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelationshipRepository extends JpaRepository<Relationship, Integer> {
    //Retrieve relationships in a tree by tree id
    List<Relationship> findByFamilyTreeId(Integer treeId);

    //Delete relationships by tree id(for cascading tree deletion)
    @Modifying
    @Query("DELETE FROM Relationship WHERE familyTree.id = :treeId")
    void deleteByTreeId(@Param("treeId") Integer treeId);

    //Delete relationships for a specific family member on a tree
    @Modifying
    @Query("DELETE FROM Relationship WHERE member1.memberId = :memberId OR member2.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") Integer memberId);

    //Update a relationship type
    @Modifying
    @Query("UPDATE Relationship SET relationship = :relationship WHERE (member1.memberId = :member1Id AND member2.memberId = :member2Id) OR (member1.memberId = :member2Id AND member2.memberId = :member1Id)")
    void updateRelationshipType(@Param("relationship") RelationshipType relationship, @Param("member1Id") Integer member1Id, @Param("member2Id") Integer member2Id);

}
