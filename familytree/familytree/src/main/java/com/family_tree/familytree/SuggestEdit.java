package com.family_tree.familytree;

import com.family_tree.enums.SuggestionStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "suggested_edits")
public class SuggestEdit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer suggestionId;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_member_edit"))
    private FamilyMember familyMember;

    @ManyToOne
    @JoinColumn(name = "suggested_by", foreignKey = @ForeignKey(name = "fk_user_edit"))
    private User suggestedBy;

    @Column(length = 100, nullable = false)
    private String fieldName; // Name of the field being edited

    @Column(length = 2000, nullable = false)
    private String oldValue; // Value being changed

    @Column(length = 2000, nullable = false)
    private String newValue; // New value suggested

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Ensure a value is always provided
    private SuggestionStatus suggestionStatus = SuggestionStatus.Pending;

    // Constructor to initialize default values
    public SuggestEdit() {
        this.suggestionStatus = SuggestionStatus.Pending;
    }

    // Getters and Setters
    public Integer getSuggestionId() {
        return suggestionId;
    }

    public void setSuggestionId(Integer suggestionId) {
        this.suggestionId = suggestionId;
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(FamilyMember familyMember) {
        this.familyMember = familyMember;
    }

    public User getSuggestedBy() {
        return suggestedBy;
    }

    public void setSuggestedBy(User suggestedBy) {
        this.suggestedBy = suggestedBy;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setSuggestionStatus(SuggestionStatus suggestionStatus) {
        this.suggestionStatus = suggestionStatus;
    }
}
  
