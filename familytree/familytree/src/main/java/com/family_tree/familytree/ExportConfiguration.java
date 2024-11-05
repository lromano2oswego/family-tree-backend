package com.family_tree.familytree;

import jakarta.persistence.*;

@Entity
@Table(name = "export_configurations")
public class ExportConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "family_tree_id", nullable = false)
    private FamilyTree familyTree;

    @Column(name = "format", nullable = false)
    private String format;  //  "PDF"

    @Column(name = "include_private_data", nullable = false)
    private boolean includePrivateData;

    // Getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FamilyTree getFamilyTree() {
        return familyTree;
    }

    public void setFamilyTree(FamilyTree familyTree) {
        this.familyTree = familyTree;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isIncludePrivateData() {
        return includePrivateData;
    }

    public void setIncludePrivateData(boolean includePrivateData) {
        this.includePrivateData = includePrivateData;
    }
}
