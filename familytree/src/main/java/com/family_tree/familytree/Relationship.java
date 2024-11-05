package com.family_tree.familytree;

import com.family_tree.enums.RelationshipType;
import jakarta.persistence.*;

@Entity
@Table(name = "relationships")
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer relationshipId;

    @ManyToOne
    @JoinColumn(name = "tree_id", foreignKey = @ForeignKey(name = "fk_tree_relationship"))
    private FamilyTree familyTree;

    @ManyToOne
    @JoinColumn(name = "member1_id", foreignKey = @ForeignKey(name = "fk_member1_relationship"))
    private FamilyMember member1;

    @ManyToOne
    @JoinColumn(name = "member2_id", foreignKey = @ForeignKey(name = "fk_member2_relationship"))
    private FamilyMember member2;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Parent', 'Spouse', 'Sibling', 'Child', 'Other')")
    private RelationshipType relationship;

    // Getters and Setters
    public Integer getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(Integer relationshipId) {
        this.relationshipId = relationshipId;
    }

    public FamilyTree getFamilyTree() {
        return familyTree;
    }

    public void setFamilyTree(FamilyTree familyTree) {
        this.familyTree = familyTree;
    }

    public FamilyMember getMember1() {
        return member1;
    }

    public void setMember1(FamilyMember member1) {
        this.member1 = member1;
    }

    public FamilyMember getMember2() {
        return member2;
    }

    public void setMember2(FamilyMember member2) {
        this.member2 = member2;
    }

    public RelationshipType getRelationship() {
        return relationship;
    }

    public void setRelationship(RelationshipType relationship) {
        this.relationship = relationship;
    }
}
