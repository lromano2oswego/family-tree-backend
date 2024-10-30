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

    // Retrieve a single attachment by mediaId
    Attachment findByMediaId(Integer mediaId);

    //Delete by member id (used for cascade deletion)
    @Modifying
    @Query("DELETE FROM Attachment a WHERE a.member.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") Integer memberId);


}
