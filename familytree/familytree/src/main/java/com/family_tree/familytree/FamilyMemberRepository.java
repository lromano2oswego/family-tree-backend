package com.family_tree.familytree;

import com.family_tree.enums.Gender;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface FamilyMemberRepository extends CrudRepository<FamilyMember, Integer> {

    // Retrieve all members in a tree (for viewing or deletion purposes)
    List<FamilyMember> findByFamilyTreeId(Integer treeId);

    // Delete family members by tree id (for cascading tree deletion, i.e. a user decides to delete a tree, its members must be removed as well)
    @Modifying
    @Query("DELETE FROM FamilyMember WHERE familyTree.id = :treeId")
    void deleteByFamilyTreeId(@Param("treeId") Integer treeId);

    // Update individual details (for editing a family member)
    @Modifying
    @Query("UPDATE FamilyMember SET name = :name, birthdate = :birthdate, gender = :gender WHERE memberId = :memberId")
    void updateMemberInfo(@Param("name") String name, @Param("birthdate") Date birthdate, @Param("gender") Gender gender, @Param("memberId") Integer memberId);

    // Delete a specific family member by member id
    @Modifying
    @Query("DELETE FROM FamilyMember WHERE memberId = :memberId")
    void deleteByMemberId(@Param("memberId") Integer memberId);

    // Delete family member based on tree id (used for cascade deletion)
    @Modifying
    @Query("DELETE FROM FamilyMember WHERE familyTree.id = :treeId")
    void deleteByTreeId(@Param("treeId") Integer treeId);

    // Delete members based on the user who owns or added them
    @Modifying
    @Query("DELETE FROM FamilyMember WHERE owner.id = :userId OR addedBy.id = :userId")
    void deleteByOwnerOrAddedBy(@Param("userId") Integer userId);

    // Retrieve members with a specific parent (father) ID
    List<FamilyMember> findByPid(Integer pid);

    // Retrieve members with a specific mother ID
    List<FamilyMember> findByMid(Integer mid);

    // Retrieve members with a specific spouse ID
    List<FamilyMember> findByFid(Integer fid);

    // Update the pid, mid, or fid of a family member
    @Modifying
    @Query("UPDATE FamilyMember SET pid = :pid WHERE memberId = :memberId")
    void updatePid(@Param("pid") Integer pid, @Param("memberId") Integer memberId);

    @Modifying
    @Query("UPDATE FamilyMember SET mid = :mid WHERE memberId = :memberId")
    void updateMid(@Param("mid") Integer mid, @Param("memberId") Integer memberId);

    @Modifying
    @Query("UPDATE FamilyMember SET fid = :fid WHERE memberId = :memberId")
    void updateFid(@Param("fid") Integer fid, @Param("memberId") Integer memberId);

    // Find all members with no pid or mid (e.g., for identifying root nodes or orphans)
    @Query("SELECT m FROM FamilyMember m WHERE m.pid IS NULL AND m.mid IS NULL")
    List<FamilyMember> findOrphans();
}
