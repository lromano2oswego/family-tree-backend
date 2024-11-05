package com.family_tree.familytree;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExportService {

    public String exportFamilyTree(FamilyTree familyTree, String format, boolean includePrivateData) {
        // Implement the logic to export the family tree in the specified format

        StringBuilder exportData = new StringBuilder();
        exportData.append("Family Tree: ").append(familyTree.getTreeName()).append("\n");

        // Get family members and include private data if specified
        List<FamilyMember> members = familyTree.getFamilyMembers();
        for (FamilyMember member : members) {
            if (!includePrivateData && member.isPrivate()) {
                continue; // Skip private members if includePrivateData is false
            }
            exportData.append("Member: ").append(member.getName()).append("\n")
                    .append("Birthdate: ").append(member.getBirthdate()).append("\n");

            if (member.getDeathdate() != null) {
                exportData.append("Deathdate: ").append(member.getDeathdate()).append("\n");
            }
            exportData.append("Gender: ").append(member.getGender()).append("\n\n");
        }

        // Return the export data as a string
        return exportData.toString();
    }
}