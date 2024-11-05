package com.family_tree.familytree;

import com.family_tree.enums.SuggestionStatus;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "suggested_edits")
public class SuggestEdit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer suggestionId;

    // Reference to the family member being edited
    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_member_suggested_edit"))
    private FamilyMember member;

    // User who suggested the edit
    @ManyToOne
    @JoinColumn(name = "suggested_by", foreignKey = @ForeignKey(name = "fk_user_suggested_edit"))
    private User suggestedBy;

    @Column(length = 100)
    private String fieldName;  // Name of the field being edited

    @Column(length = 2000)
    private String oldValue;   // Value being changed

    @Column(length = 2000)
    private String newValue;   // New value provided by the user

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Pending', 'Accepted', 'Denied')")
    private SuggestionStatus suggestionStatus = SuggestionStatus.Pending;

    // Getters and Setters

    public Integer getSuggestionId() {
        return suggestionId;
    }

    public void setSuggestionId(Integer suggestionId) {
        this.suggestionId = suggestionId;
    }

    public FamilyMember getMember() {
        return member;
    }

    public void setMember(FamilyMember member) {
        this.member = member;
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

    public SuggestionStatus getSuggestionStatus() {
        return suggestionStatus;
    }

    public void setSuggestionStatus(SuggestionStatus suggestionStatus) {
        this.suggestionStatus = suggestionStatus;
    }
}
