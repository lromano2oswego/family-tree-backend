package com.family_tree.familytree;

import com.family_tree.enums.PrivacySetting;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.family_tree.familytree.FamilyTree;
import org.springframework.data.repository.query.Param;

public interface FamilyTreeRepository extends CrudRepository<FamilyTree, Integer> {
    // Custom query methods can be added here


    // Find all family trees by owner (user) ID
    List<FamilyTree> findByOwner_Id(Integer userId);

    //Query for finding only public trees
    List<FamilyTree> findByPrivacySetting(PrivacySetting privacySetting);

    //Update tree information (Name and privacy settings)
    @Modifying
    @Query("UPDATE FamilyTree SET treeName = :treeName, privacySetting = :privacySetting WHERE id = :id")
    void updateTreeInfo(@Param("treeName") String treeName, @Param("privacySetting") PrivacySetting privacySetting, @Param("id") Integer id);

    //For cascading deletion of a user
    @Modifying
    @Query("DELETE FROM FamilyTree WHERE owner.id = :userId")
    void deleteByOwnerId(@Param("userId") Integer userId);

    FamilyTree getFamilyTreeById(Integer id);
}
