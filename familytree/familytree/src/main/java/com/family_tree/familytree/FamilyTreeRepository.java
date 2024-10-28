package com.family_tree.familytree;

import com.family_tree.enums.PrivacySetting;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.family_tree.familytree.FamilyTree;
import org.springframework.data.repository.query.Param;

public interface FamilyTreeRepository extends CrudRepository<FamilyTree, Integer> {
    // Custom query methods can be added here

    //Update tree information (Name and privacy settings)
    @Modifying
    @Query("UPDATE FamilyTree SET treeName = :treeName, privacySetting = :privacySetting WHERE id = :id")
    void updateTreeInfo(@Param("treeName") String treeName, @Param("privacySetting") PrivacySetting privacySetting, @Param("id") Integer id);

}
