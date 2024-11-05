package com.family_tree.familytree;

import com.family_tree.enums.Gender;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.family_tree.familytree.FamilyMember;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface FamilyMemberRepository extends CrudRepository<FamilyMember, Integer> {
    //Retrieve all members in a tree (for viewing or deletion purposes)
    List<FamilyMember> findByFamilyTreeId(Integer treeId);

    //Delete family members by tree id (for cascading tree deletion, i.e. a user decides to delete a tree, its members must be removed as well)
    @Modifying
    @Query("DELETE FROM FamilyMember WHERE familyTree.id = :treeId")
    void deleteByFamilyTreeId(@Param("treeId") Integer treeId);

    //Update individual details (for editing a family member)
    @Modifying
    @Query("UPDATE FamilyMember SET name = :name, birthdate = :birthdate, gender = :gender WHERE memberId = :memberId")
    void updateMemberInfo(@Param("name") String name, @Param("birthdate") Date birthdate, @Param("gender") Gender gender, @Param("memberId") Integer memberId);

    //Delete a specific family member by member id
    @Modifying
    @Query("DELETE FROM FamilyMember WHERE memberId = :memberId")
    void deleteByMemberId(@Param("memberId") Integer memberId);

    //Delete family member based on tree id(used for cascade deletion)
    @Modifying
    @Query("DELETE FROM FamilyMember WHERE familyTree.id = :treeId")
    void deleteByTreeId(@Param("treeId") Integer treeId);

    @Modifying
    @Query("DELETE FROM FamilyMember WHERE owner.id = :userId OR addedBy.id = :userId")
    void deleteByOwnerOrAddedBy(@Param("userId") Integer userId);

}
