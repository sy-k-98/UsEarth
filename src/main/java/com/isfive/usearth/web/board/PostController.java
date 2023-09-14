package com.isfive.usearth.web.board;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.isfive.usearth.domain.board.dto.PostResponse;
import com.isfive.usearth.domain.board.dto.PostsResponse;
import com.isfive.usearth.domain.board.entity.PostFileImage;
import com.isfive.usearth.domain.board.repository.PostFileImageRepository;
import com.isfive.usearth.domain.board.service.PostService;
import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.FileImageService;
import com.isfive.usearth.web.board.dto.PostCreateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "5. Post", description = "Post API")
public class PostController {

	private final PostService postService;
	private final PostFileImageRepository postFileImageRepository;
	private final FileImageService fileImageService;

	@Operation(summary = "게시글 등록")
	@PostMapping(path = "/boards/{boardId}/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> writePost(Authentication auth,
										  @PathVariable("boardId") Long boardId,
										  @RequestPart("request") @Valid PostCreateRequest request,
										  @RequestPart(name = "postImages", required = false) List<MultipartFile> postImages) {
		List<FileImage> fileImages = createFileImageList(postImages);

		postService.createPost(boardId, auth.getName(), request.getTitle(), request.getContent(), fileImages);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Operation(summary = "게시글 목록 조회")
	@GetMapping("/boards/{boardId}/posts")
	public ResponseEntity<Page<PostsResponse>> findPosts(Authentication auth,
			@PathVariable("boardId") Long boardId,
			@RequestParam(name = "page", defaultValue = "1") Integer page) {
		String username = auth == null ? "" : auth.getName();
		Page<PostsResponse> postsResponses = postService.readPosts(boardId, page, username);
		return new ResponseEntity<>(postsResponses, HttpStatus.OK);
	}

	@Operation(summary = "게시글 단일 조회")
	@GetMapping("/posts/{postId}")
	public ResponseEntity<PostResponse> findPost(Authentication auth, @PathVariable("postId") Long postId) {
		String username = auth == null ? "" : auth.getName();
		PostResponse response = postService.readPost(postId, username);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "게시글 수정")
	@PutMapping(path = "/posts/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> updatePost(Authentication auth,
										   @PathVariable("postId") Long postId,
										   @RequestPart("request") @Valid PostCreateRequest request,
										   @RequestPart(name = "postImages", required = false) List<MultipartFile> postImages) {

		List<PostFileImage> postFileImages = postFileImageRepository.findAllByPost_Id(postId);
		List<FileImage> fileImages = createFileImageList(postImages);

		postService.updatePost(postId, auth.getName(), request.getTitle(), request.getContent(), fileImages);

		deleteFileImages(postFileImages);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "게시글 삭제")
	@DeleteMapping("/posts/{postId}")
	public ResponseEntity<Void> deletePost(Authentication auth, @PathVariable("postId") Long postId) {
		postService.deletePost(postId, auth.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "게시글 좋아요 기능")
	@PostMapping("/posts/{postId}/like")
	public ResponseEntity<Void> like(Authentication auth, @PathVariable("postId") Long postId) {
		postService.like(postId, auth.getName());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private List<FileImage> createFileImageList(List<MultipartFile> postImages) {
		List<FileImage> fileImages = new ArrayList<>();
		if (postImages != null) {
			fileImages = fileImageService.createFileImageList(postImages);
		}
		return fileImages;
	}

	private void deleteFileImages(List<PostFileImage> postFileImages) {
		postFileImages.forEach(postFileImage -> fileImageService.deleteFile(postFileImage.getStoredName()));
	}
}
