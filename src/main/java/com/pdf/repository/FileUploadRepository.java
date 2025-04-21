package com.pdf.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pdf.model.FileUploadModel;

public interface FileUploadRepository extends JpaRepository<FileUploadModel, Long> {

	Page<FileUploadModel> findAllByCreatedByOrFileAccessUserUserId(Pageable pageable, Long getUserId, Long getUserId2);

}
