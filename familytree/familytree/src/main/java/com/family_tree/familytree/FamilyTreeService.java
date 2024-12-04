package com.family_tree.familytree;

import com.family_tree.enums.PrivacySetting;
import com.family_tree.enums.Role;
import com.family_tree.enums.Status;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FamilyTreeService {

    @Autowired
    private FamilyTreeRepository familyTreeRepository;
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private ConflictLogRepository conflictLogRepository;
    @Autowired
    private MergeRequestRepository mergeRequestRepository;
    @Autowired
    private CollaborationRepository collaborationRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private UserRepository userRepository;

    // Mapping for old member IDs to new member IDs during the merge
    private Map<Integer, Integer> idMapping = new HashMap<>();
    /**
     * Initiates a merge request between two family trees.
     */
    public String requestMerge(Integer requesterTreeId, Integer targetTreeId, Integer initiatorUserId) {
        FamilyTree requesterTree = familyTreeRepository.findById(requesterTreeId)
                .orElseThrow(() -> new RuntimeException("Requester tree not found"));
        FamilyTree targetTree = familyTreeRepository.findById(targetTreeId)
                .orElseThrow(() -> new RuntimeException("Target tree not found"));
        User initiator = userRepository.findById(initiatorUserId)
                .orElseThrow(() -> new RuntimeException("Initiator not found"));

        MergeRequest mergeRequest = new MergeRequest();
        mergeRequest.setRequesterTree(requesterTree);
        mergeRequest.setTargetTree(targetTree);
        mergeRequest.setInitiator(initiator); // Set initiator
        mergeRequest.setStatus("Pending");
        mergeRequestRepository.save(mergeRequest);

        return "Merge request sent successfully.";
    }

    /**
     * Accepts a merge request.
     */
    public String acceptMergeRequest(Integer mergeRequestId) {
        MergeRequest mergeRequest = mergeRequestRepository.findById(mergeRequestId)
                .orElseThrow(() -> new RuntimeException("Merge request not found"));
        mergeRequest.setStatus("Accepted");
        mergeRequestRepository.save(mergeRequest);
        return "Merge request accepted.";
    }

    /**
     * Declines a merge request.
     */
    public String declineMergeRequest(Integer mergeRequestId) {
        MergeRequest mergeRequest = mergeRequestRepository.findById(mergeRequestId)
                .orElseThrow(() -> new RuntimeException("Merge request not found"));
        mergeRequest.setStatus("Declined");
        mergeRequestRepository.save(mergeRequest);
        return "Merge request declined.";
    }

    /**
     * Finalizes the merge of two trees after conflicts are resolved, creating a single tree with unique members.
     */
    @Transactional
    public FamilyTree finalizeMerge(Integer treeId1, Integer treeId2) {
        FamilyTree tree1 = familyTreeRepository.findById(treeId1)
                .orElseThrow(() -> new RuntimeException("Tree 1 not found"));
        FamilyTree tree2 = familyTreeRepository.findById(treeId2)
                .orElseThrow(() -> new RuntimeException("Tree 2 not found"));

        // Create a new merged tree
        FamilyTree mergedTree = new FamilyTree();
        mergedTree.setTreeName(tree1.getTreeName() + " & " + tree2.getTreeName());
        mergedTree.setPrivacySetting(PrivacySetting.Private);
        mergedTree.setOwner(tree1.getOwner());

        familyTreeRepository.save(mergedTree);

        // Maintain a mapping of old IDs to new IDs for merged members
        Map<Integer, Integer> idMapping = new HashMap<>();

        // Process conflicts and merge members
        List<ConflictLog> allConflicts = conflictLogRepository.findByTreeId(treeId1);
        for (ConflictLog conflict : allConflicts) {
            if (conflict.getStatus().equals(Status.Accepted.toString())) {
                // Merge accepted members
                FamilyMember member1 = familyMemberRepository.findById(conflict.getMember1Id())
                        .orElseThrow(() -> new RuntimeException("Member 1 not found"));
                Optional<FamilyMember> member2Opt = familyMemberRepository.findById(conflict.getMember2Id());
                if (member2Opt.isPresent()) {
                    FamilyMember member2 = member2Opt.get();
                    mergeMembers(member1, member2);
                    member1.setFamilyTree(mergedTree);
                    familyMemberRepository.save(member1);
                    idMapping.put(member2.getMemberId(), member1.getMemberId());
                    familyMemberRepository.delete(member2);
                }
            } else if (conflict.getStatus().equals(Status.Declined.toString())) {
                // Add both individuals as separate entries
                FamilyMember member1 = familyMemberRepository.findById(conflict.getMember1Id())
                        .orElseThrow(() -> new RuntimeException("Member 1 not found"));
                FamilyMember newMember1 = createNewMemberInMergedTree(member1, mergedTree);
                familyMemberRepository.save(newMember1);

                Optional<FamilyMember> member2Opt = familyMemberRepository.findById(conflict.getMember2Id());
                if (member2Opt.isPresent()) {
                    FamilyMember member2 = member2Opt.get();
                    FamilyMember newMember2 = createNewMemberInMergedTree(member2, mergedTree);
                    familyMemberRepository.save(newMember2);
                }
            }
        }

        // Add unique members (not part of any conflicts)
        List<FamilyMember> uniqueMembers = getUniqueMembers(treeId1, treeId2);
        for (FamilyMember member : uniqueMembers) {
            if (!isMemberInTree(member, mergedTree)) {
                FamilyMember newMember = createNewMemberInMergedTree(member, mergedTree);
                familyMemberRepository.save(newMember);
            }
        }

        // Update relationships for merged members
        updateMergedRelationships(mergedTree, idMapping);

        // Merge collaborators
        mergeCollaborations(tree1, tree2, mergedTree);

        return mergedTree;
    }

    /**
     * Helper method for finalizeMerge to find if a member is in a tree
     */
    private boolean isMemberInTree(FamilyMember member, FamilyTree tree) {
        return familyMemberRepository.findByFamilyTreeId(tree.getId()).stream()
                .anyMatch(existingMember -> membersAreIdentical(existingMember, member));
    }

    /**
     * Method to create a family member on the new tree
     */
    private FamilyMember createNewMemberInMergedTree(FamilyMember originalMember, FamilyTree mergedTree) {
        FamilyMember newMember = new FamilyMember();
        newMember.setName(originalMember.getName());
        newMember.setBirthdate(originalMember.getBirthdate());
        newMember.setDeathdate(originalMember.getDeathdate());
        newMember.setGender(originalMember.getGender());
        newMember.setAdditionalInfo(originalMember.getAdditionalInfo());
        newMember.setFamilyTree(mergedTree);

        // Update relationships using the ID mapping
        if (originalMember.getPid() != null && idMapping.containsKey(originalMember.getPid())) {
            newMember.setPid(idMapping.get(originalMember.getPid()));
        }

        if (originalMember.getFid() != null && idMapping.containsKey(originalMember.getFid())) {
            newMember.setFid(idMapping.get(originalMember.getFid()));
        }

        if (originalMember.getMid() != null && idMapping.containsKey(originalMember.getMid())) {
            newMember.setMid(idMapping.get(originalMember.getMid()));
        }

        familyMemberRepository.save(newMember);
        idMapping.put(originalMember.getMemberId(), newMember.getMemberId());

        // Copy attachments
        copyAttachments(originalMember, newMember);

        return newMember;
    }


    /**
     * Method to update collaborations to the merged tree
     */

    private void mergeCollaborations(FamilyTree tree1, FamilyTree tree2, FamilyTree mergedTree) {
        try {
            // Fetch collaborators from both trees, handling potential empty lists
            List<Collaboration> tree1Collaborators = Optional.ofNullable(collaborationRepository.findByFamilyTreeId(tree1.getId())).orElse(new ArrayList<>());
            List<Collaboration> tree2Collaborators = Optional.ofNullable(collaborationRepository.findByFamilyTreeId(tree2.getId())).orElse(new ArrayList<>());

            List<Collaboration> mergedCollaborators = new ArrayList<>(tree1Collaborators);

            // Add collaborators from tree2, avoiding duplicates
            for (Collaboration collaborator : tree2Collaborators) {
                if (mergedCollaborators.stream().noneMatch(c -> c.getUser().getId().equals(collaborator.getUser().getId()))) {
                    mergedCollaborators.add(collaborator);
                }
            }

            // Save each collaborator to the merged tree, checking for database issues
            for (Collaboration collaborator : mergedCollaborators) {
                try {
                    Collaboration newCollaboration = new Collaboration();
                    newCollaboration.setUser(collaborator.getUser());
                    newCollaboration.setFamilyTree(mergedTree);
                    newCollaboration.setRole(collaborator.getRole()); // Preserve role
                    collaborationRepository.save(newCollaboration);
                } catch (Exception e) {
                    System.err.println("Error saving collaboration for user " + collaborator.getUser().getId() + ": " + e.getMessage());
                }
            }

            // Add tree2's owner as collaborator if they are not the owner of tree1
            if (!tree2.getOwner().getId().equals(tree1.getOwner().getId())) {
                try {
                    Collaboration ownerCollaboration = new Collaboration();
                    ownerCollaboration.setUser(tree2.getOwner());
                    ownerCollaboration.setFamilyTree(mergedTree);
                    ownerCollaboration.setRole(Role.Owner); // Assign role as Owner
                    collaborationRepository.save(ownerCollaboration);
                } catch (Exception e) {
                    System.err.println("Error saving owner collaboration for tree2's owner: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error merging collaborations for trees " + tree1.getId() + " and " + tree2.getId() + ": " + e.getMessage());
        }
    }

    /**
     * Helper method to determine if two members are identical.
     */
    private boolean membersAreIdentical(FamilyMember member1, FamilyMember member2) {
        return member1.getName().equals(member2.getName()) &&
                member1.getBirthdate().equals(member2.getBirthdate()) &&
                member1.getGender() == member2.getGender();
    }

    /**
     * Creates a conflict log entry for partially matching members.
     */
    private ConflictLog createConflictLog(Integer treeId, FamilyMember member1, FamilyMember member2) {
        ConflictLog conflict = new ConflictLog();
        conflict.setTreeId(treeId);

        // Member 1 details
        conflict.setMember1Id(member1.getMemberId());
        conflict.setMember1Name(member1.getName());
        conflict.setMember1Birthdate(member1.getBirthdate());
        conflict.setMember1Deathdate(member1.getDeathdate());
        conflict.setMember1AdditionalInfo(member1.getAdditionalInfo());

        // Member 2 details
        conflict.setMember2Id(member2.getMemberId());
        conflict.setMember2Name(member2.getName());
        conflict.setMember2Birthdate(member2.getBirthdate());
        conflict.setMember2Deathdate(member2.getDeathdate());
        conflict.setMember2AdditionalInfo(member2.getAdditionalInfo());

        conflict.setStatus("Pending"); // Default status
        return conflict;
    }

    @Transactional
    public List<ConflictLog> getConflictsByTreeIdAndStatus(Integer treeId, String status) {
        return conflictLogRepository.findByTreeIdAndStatus(treeId, status);
    }


    /**
     * Creates a conflict log entry for fully matching members to be resolved
     */
    @Transactional
    public List<ConflictLog> getMatchingMembers(Integer treeId1, Integer treeId2) {
        List<FamilyMember> tree1Members = familyMemberRepository.findByFamilyTreeId(treeId1);
        List<FamilyMember> tree2Members = familyMemberRepository.findByFamilyTreeId(treeId2);
        List<ConflictLog> matches = new ArrayList<>();

        for (FamilyMember member1 : tree1Members) {
            for (FamilyMember member2 : tree2Members) {
                if (membersAreIdentical(member1, member2)) {  // Use identical matching logic
                    ConflictLog match = createConflictLog(treeId1, member1, member2);
                    matches.add(match);

                    // Save each match to the ConflictLog repository
                    conflictLogRepository.save(match);
                }
            }
        }

        return matches;
    }

    /**
     * Used to confirm a match between two family members
     */
    @Transactional
    public String confirmMatch(Integer conflictId, boolean isSamePerson) {
        ConflictLog conflict = conflictLogRepository.findById(conflictId)
                .orElseThrow(() -> new RuntimeException("Conflict not found"));

        if (isSamePerson) {
            FamilyMember member1 = familyMemberRepository.findById(conflict.getMember1Id())
                    .orElseThrow(() -> new RuntimeException("Member 1 not found"));
            FamilyMember member2 = familyMemberRepository.findById(conflict.getMember2Id())
                    .orElseThrow(() -> new RuntimeException("Member 2 not found"));

            // Merge member2 into member1
            mergeMembers(member1, member2);
            familyMemberRepository.delete(member2); // Delete duplicate
            familyMemberRepository.save(member1);

            conflict.setStatus(Status.Accepted.toString()); // Convert enum to String
            conflict.setNotes("Merged into member " + member1.getMemberId());
        } else {
            conflict.setStatus(Status.Declined.toString()); // Convert enum to String
            conflict.setNotes("Conflict rejected by user.");
        }

        conflictLogRepository.save(conflict);
        return "Conflict " + (isSamePerson ? "accepted" : "declined") + " successfully.";
    }

    /**
     * Merges information from member2 into member1.
     */
    private void mergeMembers(FamilyMember member1, FamilyMember member2) {
        if (member1.getAdditionalInfo() == null && member2.getAdditionalInfo() != null) {
            member1.setAdditionalInfo(member2.getAdditionalInfo());
        }
        if (member1.getDeathdate() == null && member2.getDeathdate() != null) {
            member1.setDeathdate(member2.getDeathdate());
        }

        // Retain only one attachment between member1 and member2
        retainSingleAttachment(member1, member2);

        // Save the updated member1 after merging
        familyMemberRepository.save(member1);

        // Delete member2 after reassigning data
        familyMemberRepository.delete(member2);
    }

    /**
     * Reassigns attachment to the merged family members.
     */
    private void retainSingleAttachment(FamilyMember member1, FamilyMember member2) {
        List<Attachment> member1Attachments = attachmentRepository.findByMember_MemberId(member1.getMemberId());
        List<Attachment> member2Attachments = attachmentRepository.findByMember_MemberId(member2.getMemberId());

        // Retain only one attachment (you can customize the selection logic here)
        Attachment retainedAttachment = null;

        if (!member1Attachments.isEmpty()) {
            retainedAttachment = member1Attachments.get(0); // Keep the first attachment of member1
        } else if (!member2Attachments.isEmpty()) {
            retainedAttachment = member2Attachments.get(0); // If member1 has no attachments, keep member2's first
            retainedAttachment.setMember(member1); // Reassign it to member1
            attachmentRepository.save(retainedAttachment);
        }

        // Remove all other attachments associated with member1 and member2
        for (Attachment attachment : member1Attachments) {
            if (!attachment.equals(retainedAttachment)) {
                attachmentRepository.delete(attachment);
            }
        }
        for (Attachment attachment : member2Attachments) {
            attachmentRepository.delete(attachment);
        }
    }


    /**
     * Method to copy attachments and add it to the new family member
     */
    private void copyAttachments(FamilyMember sourceMember, FamilyMember targetMember) {
        List<Attachment> sourceAttachments = attachmentRepository.findByMember_MemberId(sourceMember.getMemberId());
        for (Attachment attachment : sourceAttachments) {
            Attachment newAttachment = new Attachment();
            newAttachment.setMember(targetMember);
            newAttachment.setTypeOfFile(attachment.getTypeOfFile());
            newAttachment.setFileData(attachment.getFileData());
            newAttachment.setUploadedBy(attachment.getUploadedBy()); // Keep original uploader
            attachmentRepository.save(newAttachment);
        }
    }
    /**
     * Retrieves the owner of a specific family tree.
     *
     * @param treeId The ID of the family tree.
     * @return The owner of the family tree.
     */
    public User getTreeOwner(Integer treeId) {
        FamilyTree tree = familyTreeRepository.findById(treeId)
                .orElseThrow(() -> new RuntimeException("Family tree not found"));
        return tree.getOwner();
    }
    /**
     * Gathers unique family members across two trees, excluding duplicates.
     */
    public List<FamilyMember> getUniqueMembers(Integer treeId1, Integer treeId2) {
        List<FamilyMember> tree1Members = familyMemberRepository.findByFamilyTreeId(treeId1);
        List<FamilyMember> tree2Members = familyMemberRepository.findByFamilyTreeId(treeId2);

        List<FamilyMember> uniqueMembers = new ArrayList<>(tree1Members);

        for (FamilyMember member2 : tree2Members) {
            boolean isUnique = true;
            for (FamilyMember member1 : tree1Members) {
                if (membersAreIdentical(member1, member2)) {
                    isUnique = false;
                    break;
                }
            }
            if (isUnique) {
                uniqueMembers.add(member2);
            }
        }

        return uniqueMembers;
    }

    /**
     *Method to update only merged relationships
     */
    private void updateMergedRelationships(FamilyTree mergedTree, Map<Integer, Integer> idMapping) {
        for (Map.Entry<Integer, Integer> entry : idMapping.entrySet()) {
            Integer oldId = entry.getKey();
            Integer newId = entry.getValue();

            FamilyMember mergedMember = familyMemberRepository.findById(newId)
                    .orElseThrow(() -> new RuntimeException("Merged member not found"));

            FamilyMember oldMember = familyMemberRepository.findById(oldId)
                    .orElseThrow(() -> new RuntimeException("Original member not found"));

            // Update relationships
            if (oldMember.getPid() != null && idMapping.containsKey(oldMember.getPid())) {
                mergedMember.setPid(idMapping.get(oldMember.getPid()));
            }
            if (oldMember.getFid() != null && idMapping.containsKey(oldMember.getFid())) {
                mergedMember.setFid(idMapping.get(oldMember.getFid()));
            }
            if (oldMember.getMid() != null && idMapping.containsKey(oldMember.getMid())) {
                mergedMember.setMid(idMapping.get(oldMember.getMid()));
            }

            familyMemberRepository.save(mergedMember);
        }
    }
}
