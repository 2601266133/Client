package com.cisco.client.model;

import java.util.List;

public class PPTInformation {
	String id;
	String filePath;
	String fileNewName;
	String fileOrignName;
	String imagePath;
	String imageUUID;
	String size;
	String createdDate;
	String createdBy;
	List<ImageInformation> imageInfoList;

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public List<ImageInformation> getImageInfoList() {
		return imageInfoList;
	}

	public void setImageInfoList(List<ImageInformation> imageInfoList) {
		this.imageInfoList = imageInfoList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileNewName() {
		return fileNewName;
	}

	public void setFileNewName(String fileNewName) {
		this.fileNewName = fileNewName;
	}

	public String getFileOrignName() {
		return fileOrignName;
	}

	public void setFileOrignName(String fileOrignName) {
		this.fileOrignName = fileOrignName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageUUID() {
		return imageUUID;
	}

	public void setImageUUID(String imageUUID) {
		this.imageUUID = imageUUID;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

}
