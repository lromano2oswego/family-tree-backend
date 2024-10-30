package com.family_tree.familytree;

import com.family_tree.enums.SuggestionStatus;
import org.springframework.data.repository.CrudRepository;
import com.family_tree.familytree.SuggestEdit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SuggestEditRepository extends CrudRepository<SuggestEdit, Integer> {
    // Custom query methods can be added here

    //Retrieve pending suggestions for a family owner to review
    List<SuggestEdit> findBySuggestionStatus(SuggestionStatus status);

    //Update suggestion status to accepted
    @Modifying
    @Query("UPDATE SuggestEdit SET suggestionStatus = 'Accepted' WHERE suggestionId = :suggestionId")
    void acceptSuggestion(@Param("suggestionId") Integer suggestionId);

    //Delete suggestion if it is denied
    @Modifying
    @Query("DELETE FROM SuggestEdit WHERE suggestionId = :suggestionId")
    void deleteSuggestion(@Param("suggestionId") Integer suggestionId);

    //Delete suggestion by family member id if member is deleted
    @Modifying
    @Query("DELETE FROM SuggestEdit s WHERE s.member.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") Integer memberId);


    // Find all suggested edits for a specific family tree
    @Query("SELECT se FROM SuggestEdit se WHERE se.member.familyTree.id = :treeId") //se means suggested edit
    List<SuggestEdit> findByTreeId(@Param("treeId") Integer treeId);

}
