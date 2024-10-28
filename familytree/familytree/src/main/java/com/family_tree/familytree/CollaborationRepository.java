package com.family_tree.familytree;

import com.family_tree.enums.Status;
import org.springframework.data.repository.CrudRepository;
import com.family_tree.familytree.Collaboration;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollaborationRepository extends CrudRepository<Collaboration, Integer> {
    //Add query calls here

    //Find collaboration by user and tree (for accepting or denying invitation)
    Optional<Collaboration> findByFamilyTreeIdAndUserId(Integer treeId, Integer userId);

    //Update collaboration status (for accepting or declining an invitation)
    @Modifying
    @Query("UPDATE Collaboration SET status = :status WHERE familyTree.id = :treeId AND user.id = :userId")
    void updateCollaborationStatus(@Param("status") Status status, @Param("treeId") Integer treeId, @Param("userId") Integer userId);

}
