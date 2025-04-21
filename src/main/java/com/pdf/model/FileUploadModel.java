package com.pdf.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "file_upload_model")
@Data
public class FileUploadModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fileName;
	private String absolutePath;
	private String relativePath;
	private String fileUrl;
	private Long createdBy;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	@JsonFormat(pattern = "dd-MMM-yy")
	private Date createdAt;

	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FileAccessUser> fileAccessUser = new ArrayList<>();

	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FileComment> fileComment = new ArrayList<>();

}
