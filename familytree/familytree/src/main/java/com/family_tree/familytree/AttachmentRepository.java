package com.family_tree.familytree;

import org.springframework.data.jpa.repository.JpaRepository;
import com.family_tree.familytree.Attachment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    //Retrieve attachments by member id
    List<Attachment> findByMember_MemberId(Integer memberId);

    //Delete all attachments by member id(for deleting a family member)
    void deleteByMember_MemberId(Integer memberId);
}
