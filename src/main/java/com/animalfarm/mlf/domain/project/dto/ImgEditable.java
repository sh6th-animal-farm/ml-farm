package com.animalfarm.mlf.domain.project.dto;

import java.util.List;

public interface ImgEditable {
	Long getProjectId();

	List<String> getProjectImageNames();

	List<Long> getDeletedPictureIds();
}
