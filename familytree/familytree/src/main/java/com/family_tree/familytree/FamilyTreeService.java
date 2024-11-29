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

    // Reverse mapping: New member ID -> Set of old member IDs
    private Map<Integer, Set<Integer>> reverseMapping = new HashMap<>();

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

        // Process conflicts and merge members
        List<ConflictLog> allConflicts = conflictLogRepository.findByTreeId(treeId1);
        for (ConflictLog conflict : allConflicts) {
            if (conflict.getStatus().equals(Status.Accepted.toString())) {
                FamilyMember member1 = familyMemberRepository.findById(conflict.getMember1Id())
                        .orElseThrow(() -> new RuntimeException("Member 1 not found"));
                Optional<FamilyMember> member2Opt = familyMemberRepository.findById(conflict.getMember2Id());
                if (member2Opt.isPresent()) {
                    FamilyMember member2 = member2Opt.get();
                    mergeMembers(member1, member2);
                    member1.setFamilyTree(mergedTree);
                    familyMemberRepository.save(member1);

                    // Add to ID mapping
                    idMapping.put(member2.getMemberId(), member1.getMemberId());

                    familyMemberRepository.delete(member2);
                }
            } else if (conflict.getStatus().equals(Status.Declined.toString())) {
                FamilyMember member1 = familyMemberRepository.findById(conflict.getMember1Id())
                        .orElseThrow(() -> new RuntimeException("Member 1 not found"));
                FamilyMember newMember1 = createNewMemberInMergedTree(member1, mergedTree);
                familyMemberRepository.save(newMember1);
                idMapping.put(member1.getMemberId(), newMember1.getMemberId());

                Optional<FamilyMember> member2Opt = familyMemberRepository.findById(conflict.getMember2Id());
                if (member2Opt.isPresent()) {
                    FamilyMember member2 = member2Opt.get();
                    FamilyMember newMember2 = createNewMemberInMergedTree(member2, mergedTree);
                    familyMemberRepository.save(newMember2);
                    idMapping.put(member2.getMemberId(), newMember2.getMemberId());
                }
            }
        }

        // Add unique members (not part of any conflicts)
        List<FamilyMember> uniqueMembers = getUniqueMembers(treeId1, treeId2);
        for (FamilyMember member : uniqueMembers) {
            if (!isMemberInTree(member, mergedTree)) {
                FamilyMember newMember = createNewMemberInMergedTree(member, mergedTree);
                familyMemberRepository.save(newMember);
                idMapping.put(member.getMemberId(), newMember.getMemberId());
            }
        }

        System.out.println("ID Mapping:");
        idMapping.forEach((oldId, newId) -> System.out.println("Old ID: " + oldId + ", New ID: " + newId));

        // Update relationships in the merged tree
        updateRelationships(mergedTree);

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

        // Map relationships using idMapping
        Integer pid = originalMember.getPid() != null ? idMapping.get(originalMember.getPid()) : null;
        Integer mid = originalMember.getMid() != null ? idMapping.get(originalMember.getMid()) : null;
        Integer fid = originalMember.getFid() != null ? idMapping.get(originalMember.getFid()) : null;

        System.out.println("Mapping relationships for new member: " + newMember.getName());
        System.out.println("Mapped PID: " + pid + ", MID: " + mid + ", FID: " + fid);

        newMember.setPid(pid);
        newMember.setMid(mid);
        newMember.setFid(fid);

        FamilyMember savedMember = familyMemberRepository.save(newMember);
        idMapping.put(originalMember.getMemberId(), savedMember.getMemberId());
        return savedMember;
    }

    /**
     * Method to update collaborations to the merged tree
     */
    private void mergeCollaborations(FamilyTree tree1, FamilyTree tree2, FamilyTree mergedTree) {
        List<Collaboration> tree1Collaborators = Optional.ofNullable(collaborationRepository.findByFamilyTreeId(tree1.getId())).orElse(new ArrayList<>());
        List<Collaboration> tree2Collaborators = Optional.ofNullable(collaborationRepository.findByFamilyTreeId(tree2.getId())).orElse(new ArrayList<>());

        List<Collaboration> mergedCollaborators = new ArrayList<>(tree1Collaborators);

        for (Collaboration collaborator : tree2Collaborators) {
            if (mergedCollaborators.stream().noneMatch(c -> c.getUser().getId().equals(collaborator.getUser().getId()))) {
                mergedCollaborators.add(collaborator);
            }
        }

        for (Collaboration collaborator : mergedCollaborators) {
            Collaboration newCollaboration = new Collaboration();
            newCollaboration.setUser(collaborator.getUser());
            newCollaboration.setFamilyTree(mergedTree);
            newCollaboration.setRole(collaborator.getRole());
            collaborationRepository.save(newCollaboration);
        }

        if (!tree2.getOwner().getId().equals(tree1.getOwner().getId())) {
            Collaboration ownerCollaboration = new Collaboration();
            ownerCollaboration.setUser(tree2.getOwner());
            ownerCollaboration.setFamilyTree(mergedTree);
            ownerCollaboration.setRole(Role.Owner);
            collaborationRepository.save(ownerCollaboration);
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
                if (membersAreIdentical(member1, member2)) {
                    ConflictLog match = createConflictLog(treeId1, member1, member2);
                    matches.add(match);
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

            mergeMembers(member1, member2);
            familyMemberRepository.delete(member2); // Delete duplicate
            familyMemberRepository.save(member1);

            conflict.setStatus(Status.Accepted.toString());
            conflict.setNotes("Merged into member " + member1.getMemberId());
        } else {
            conflict.setStatus(Status.Declined.toString());
            conflict.setNotes("Conflict rejected by user.");
        }

        conflictLogRepository.save(conflict);
        return "Conflict " + (isSamePerson ? "accepted" : "declined") + " successfully.";
    }

    /**
     * Merges information from member2 into member1.
     */
    private void mergeMembers(FamilyMember member1, FamilyMember member2) {
        // Merge additional info and death date
        if (member1.getAdditionalInfo() == null && member2.getAdditionalInfo() != null) {
            member1.setAdditionalInfo(member2.getAdditionalInfo());
        }
        if (member1.getDeathdate() == null && member2.getDeathdate() != null) {
            member1.setDeathdate(member2.getDeathdate());
        }

        // Retain attachments
        retainSingleAttachment(member1, member2);

        // Merge relationships (use the more complete data)
        member1.setPid(member1.getPid() != null ? member1.getPid() : member2.getPid());
        member1.setMid(member1.getMid() != null ? member1.getMid() : member2.getMid());
        member1.setFid(member1.getFid() != null ? member1.getFid() : member2.getFid());

        // Save merged member
        familyMemberRepository.save(member1);

        // Update mappings
        idMapping.put(member2.getMemberId(), member1.getMemberId());
        reverseMapping.computeIfAbsent(member1.getMemberId(), k -> new HashSet<>())
                .addAll(Arrays.asList(member1.getMemberId(), member2.getMemberId()));
    }

    /**
     * Reassigns attachment to the merged family members.
     */
    private void retainSingleAttachment(FamilyMember member1, FamilyMember member2) {
        List<Attachment> member1Attachments = attachmentRepository.findByMember_MemberId(member1.getMemberId());
        List<Attachment> member2Attachments = attachmentRepository.findByMember_MemberId(member2.getMemberId());

        Attachment retainedAttachment = null;

        if (!member1Attachments.isEmpty()) {
            retainedAttachment = member1Attachments.get(0);
        } else if (!member2Attachments.isEmpty()) {
            retainedAttachment = member2Attachments.get(0);
            retainedAttachment.setMember(member1);
            attachmentRepository.save(retainedAttachment);
        }

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
     * Method for updating relationships
    */
    private void updateRelationships(FamilyTree mergedTree) {
        List<FamilyMember> members = familyMemberRepository.findByFamilyTreeId(mergedTree.getId());

        // First pass: Populate idMapping and reverseMapping
        for (FamilyMember member : members) {
            idMapping.put(member.getMemberId(), member.getMemberId());
            reverseMapping.computeIfAbsent(member.getMemberId(), k -> new HashSet<>()).add(member.getMemberId());
        }

        // Second pass: Update relationships
        for (FamilyMember member : members) {
            System.out.println("Updating relationships for member: " + member.getName() + " (ID: " + member.getMemberId() + ")");

            // Update PID
            if (member.getPid() != null) {
                Integer newPid = resolveNewId(member.getPid());
                if (newPid != null) {
                    member.setPid(newPid);
                    System.out.println("Updated PID for " + member.getName() + " to " + newPid);
                } else {
                    System.out.println("PID mapping missing for old ID: " + member.getPid());
                }
            }

            // Update MID
            if (member.getMid() != null) {
                Integer newMid = resolveNewId(member.getMid());
                if (newMid != null) {
                    member.setMid(newMid);
                    System.out.println("Updated MID for " + member.getName() + " to " + newMid);
                } else {
                    System.out.println("MID mapping missing for old ID: " + member.getMid());
                }
            }

            // Update FID
            if (member.getFid() != null) {
                Integer newFid = resolveNewId(member.getFid());
                if (newFid != null) {
                    member.setFid(newFid);
                    System.out.println("Updated FID for " + member.getName() + " to " + newFid);

                    // Handle reciprocal update for spouse
                    FamilyMember spouse = familyMemberRepository.findById(newFid)
                            .orElseThrow(() -> new RuntimeException("Spouse not found for FID: " + newFid));
                    if (spouse.getFid() == null || !spouse.getFid().equals(member.getMemberId())) {
                        spouse.setFid(member.getMemberId());
                        familyMemberRepository.save(spouse);
                        System.out.println("Reciprocally updated FID for spouse " + spouse.getName() + " to " + member.getMemberId());
                    }
                } else {
                    System.out.println("FID mapping missing for old ID: " + member.getFid());
                }
            }

            // Save updated member
            familyMemberRepository.save(member);
        }
    }

    // Helper method to resolve the new ID from reverseMapping
    private Integer resolveNewId(Integer oldId) {
        return idMapping.get(oldId);
    }
}
