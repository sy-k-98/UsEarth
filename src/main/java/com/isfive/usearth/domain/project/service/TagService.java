package com.isfive.usearth.domain.project.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isfive.usearth.domain.project.entity.Tag;
import com.isfive.usearth.domain.project.repository.ProjectRepository;
import com.isfive.usearth.domain.project.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

	private final ProjectRepository projectRepository;
	private final TagRepository tagRepository;

	public List<String> createTagList(String hashTag) {
		hashTag = hashTag.replace("\"", "");
		List<String> hashList = Arrays.stream(hashTag.split("#"))
			.map(String::trim)
			.filter(s -> s.length() > 0)
			.collect(Collectors.toList());

		return hashList;
	}

	public List<Tag> convertTagStrToEntity(List<String> hashList) {
		List<Tag> tagList = new ArrayList<>();
		for (String hash : hashList) {
			Tag tag = Tag.builder()
				.name(hash)
				.build();
			tagRepository.save(tag);
			tagList.add(tag);
		}
		tagRepository.saveAll(tagList);

		return tagList;
	}

}
