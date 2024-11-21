package com.family_tree.familytree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.family_tree.enums.SuggestionStatus;
import java.util.Optional;

@Service
public class SuggestEditService {
    private final SuggestEditRepository suggestEditRepository;

    @Autowired
    public SuggestEditService(SuggestEditRepository suggestedEditRepository, SuggestEditRepository suggestedEditRepository1) {
        this.suggestEditRepository = suggestedEditRepository;
    }

    // Method to get all pending suggested edits
    public List<SuggestEdit> getPendingEdits() {
        return suggestEditRepository.findBySuggestionStatus(SuggestionStatus.Pending);
    }

    // Method to create a new suggested edit
    public SuggestEdit createSuggestedEdit(SuggestEdit edit) {
        if (edit == null) {
            throw new IllegalArgumentException("Suggested edit cannot be null");
        }
        return suggestEditRepository.save(edit);
    }

    // Method to update the status of a suggested edit
    public SuggestEdit updateSuggestedEditStatus(Integer suggestionId, SuggestionStatus status) {
        if (suggestionId == null || status == null) {
            throw new IllegalArgumentException("Suggestion ID and status cannot be null");
        }

        Optional<SuggestEdit> optionalEdit = suggestEditRepository.findById(suggestionId);
        if (optionalEdit.isEmpty()) {
            throw new RuntimeException("Suggestion not found");
        }

        SuggestEdit edit = optionalEdit.get();
        edit.setSuggestionStatus(status);
        return suggestEditRepository.save(edit);
    }

    // Method to find all suggested edits for a specific family member
    public List<SuggestEdit> getEditsForFamilyMember(Integer memberId) {
        return suggestEditRepository.findByFamilyMember_MemberId(memberId);
    }




}
