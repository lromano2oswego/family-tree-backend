package com.family_tree.familytree;

import org.springframework.data.jpa.repository.JpaRepository;
import com.family_tree.familytree.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

}
