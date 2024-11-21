package com.family_tree.familytree;

import com.family_tree.enums.SuggestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SuggestEditRepository extends JpaRepository<SuggestEdit, Integer> {
    // Find suggested edits by the ID of the user who suggested them
    List<SuggestEdit> findBySuggestedBy_Id(Integer userId);

    // Find suggested edits by their status (Pending, Accepted, Denied)
   // List<SuggestEdit> findBySuggestionStatus(SuggestionStatus status);

    List<SuggestEdit> findBySuggestionStatus(SuggestionStatus suggestionStatus);


    List<SuggestEdit> findByFamilyMember_MemberId(Integer memberId);

    List<SuggestEdit> findByFamilyMember_FamilyTree_Id(Integer treeId);

    void deleteBySuggestedById(Integer userId);

    void deleteByFamilyMember_MemberId(Integer memberId);
}
