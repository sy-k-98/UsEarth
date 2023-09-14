package com.isfive.usearth.web.board;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isfive.usearth.domain.board.entity.Board;
import com.isfive.usearth.domain.board.entity.Post;
import com.isfive.usearth.domain.board.entity.PostComment;
import com.isfive.usearth.domain.board.entity.PostFileImage;
import com.isfive.usearth.domain.board.entity.PostLike;
import com.isfive.usearth.domain.board.repository.BoardRepository;
import com.isfive.usearth.domain.board.repository.PostCommentRepository;
import com.isfive.usearth.domain.board.repository.PostLikeRepository;
import com.isfive.usearth.domain.board.repository.post.PostRepository;
import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.web.board.dto.PostCreateRequest;
import com.isfive.usearth.web.board.dto.PostUpdateRequest;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostControllerTest {

	@Autowired MockMvc mockMvc;
	@Autowired ObjectMapper objectMapper;
	@Autowired BoardRepository boardRepository;
	@Autowired PostRepository postRepository;
	@Autowired PostLikeRepository postLikeRepository;
	@Autowired MemberRepository memberRepository;
	@Autowired PostCommentRepository postCommentRepository;

	@WithMockUser(username = "writer")
	@DisplayName("사용자는 게시글을 작성 할 수 있다.")
	@Test
	void writePost() throws Exception {
		//given
		Member member = Member.builder()
				.username("writer")
				.build();

		memberRepository.save(member);

		Board board = Board.createBoard("게시판 제목", "게시판 요약");
		boardRepository.save(board);

		PostCreateRequest request = PostCreateRequest.builder()
				.title("제목")
				.content("내용")
				.build();

		MockMultipartFile requestDTO = new MockMultipartFile("request", "request",
			"application/json",
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

		//when   //then
		mockMvc.perform(MockMvcRequestBuilders.multipart("/boards/{boardId}/posts", board.getId())
						.file(requestDTO)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				)
				.andExpect(status().isCreated())
				.andDo(print());

		List<Post> all = postRepository.findAll();
		assertThat(all.size()).isEqualTo(1);
	}

	@DisplayName("사용자는 게시글을 페이징 조회 할 수 있다.")
	@Test
	void findPosts() throws Exception {
		//given
		Member member = Member.builder()
				.username("member")
				.build();

		memberRepository.save(member);

		Board board = Board.createBoard("게시판 제목", "게시판 요약");
		boardRepository.save(board);

		for (int i = 1; i <= 20; i++) {
			Post post = Post.createPost(member, board, "title" + i, "content" + i, new ArrayList<>());
			postRepository.save(post);
		}

		//when //then
		mockMvc.perform(get("/boards/{boardId}/posts", board.getId())
						.contentType(APPLICATION_JSON)
						.param("page", "1")
				)
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.size()").value(10))
				.andExpect(jsonPath("$.content[0].title").value("title20"))
				.andExpect(jsonPath("$.content[9].title").value("title11"))
				.andExpect(status().isOk())
				.andDo(print());
	}

	@DisplayName("사용자는 게시글을 조회 할 수 있다.")
	@Test
	void findPost() throws Exception {
		//given
		Member member = Member.builder()
				.username("member")
				.nickname("nickname")
				.build();

		memberRepository.save(member);

		Board board = Board.createBoard("게시판 제목", "게시판 요약");
		boardRepository.save(board);

		Post post = Post.createPost(member, board, "title", "content", new ArrayList<>());
		postRepository.save(post);

		PostComment postComment1 = PostComment.createPostComment(member, post, "댓글1");
		PostComment postComment2 = PostComment.createPostComment(member, post, "댓글2");

		postCommentRepository.saveAll(List.of(postComment1, postComment2));

		PostComment reply1 = PostComment.createPostComment(member, post, "답글1.");
		PostComment reply2 = PostComment.createPostComment(member, post, "답글2.");
		PostComment reply3 = PostComment.createPostComment(member, post, "답글3.");
		PostComment reply4 = PostComment.createPostComment(member, post, "답글4.");
		postComment1.addReply(reply1);
		postComment1.addReply(reply2);
		postComment2.addReply(reply3);
		postComment2.addReply(reply4);

		postCommentRepository.saveAll(List.of(reply1, reply2, reply3, reply4));

		//when //then
		mockMvc.perform(get("/posts/{postId}", post.getId())
						.contentType(APPLICATION_JSON)
				)
				.andExpect(jsonPath("$.id").value(post.getId()))
				.andExpect(jsonPath("$.title").value(post.getTitle()))
				.andExpect(jsonPath("$.writer").value(member.getNickname()))
				.andExpect(jsonPath("$.content").value(post.getContent()))
				.andExpect(jsonPath("$.views").value(post.getViews()))
				.andExpect(jsonPath("$.postCommentResponse.content.size()").value(2))
				.andExpect(jsonPath("$.postCommentResponse.content[0].postCommentResponses.size()").value(2))
				.andExpect(jsonPath("$.postCommentResponse.content[1].postCommentResponses.size()").value(2))
				.andExpect(status().isOk())
				.andDo(print());
	}

	@Autowired EntityManager em;

	@WithMockUser(username = "member")
	@DisplayName("사용자는 게시글을 삭제 할 수 있다.")
	@Test
	void deletePost() throws Exception {
		//given
		Member member = Member.builder().username("member").build();
		memberRepository.save(member);

		Board board = Board.createBoard("게시판 제목", "게시판 요약");
		boardRepository.save(board);

		Post post = Post.createPost(member, board, "title", "content", new ArrayList<>());
		postRepository.save(post);

		//when //then
		mockMvc.perform(delete("/posts/{postId}", post.getId()))
				.andExpect(status().isOk())
				.andDo(print());

		List<Post> all = postRepository.findAll();
		assertThat(all).isEmpty();
	}

	@WithMockUser(username = "member")
	@DisplayName("사용자는 게시글을 수정할 수 있다.")
	@Test
	void updatePost() throws Exception {
	    //given
		Member member = Member.builder().username("member").build();
		memberRepository.save(member);

		Board board = Board.createBoard("게시판 제목", "게시판 요약");
		boardRepository.save(board);

		Post post = Post.createPost(member, board,
				"title",
				"content",
				new ArrayList<>(List.of(new PostFileImage(new FileImage("name1", "name2")))));
		postRepository.save(post);

		PostUpdateRequest request = PostUpdateRequest.builder()
				.title("수정 제목")
				.content("수정 내용")
				.build();

		MockMultipartFile requestDTO = new MockMultipartFile("request", "request",
				"application/json",
				objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

		//when   //then
		mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/posts/{postId}", post.getId())
						.file(requestDTO)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				)
				.andExpect(status().isOk())
				.andDo(print());

		Post findPost = postRepository.findById(post.getId()).orElseThrow();
		assertThat(findPost.getTitle()).isEqualTo("수정 제목");
		assertThat(findPost.getContent()).isEqualTo("수정 내용");
	}

	@WithMockUser(username = "member")
	@DisplayName("사용자는 게시글을 좋아요 할 수 있다.")
	@Test
	void like() throws Exception {
		//given
		Member writer = Member.builder()
				.username("writer")
				.build();
		Member member = Member.builder()
				.username("member")
				.build();
		memberRepository.saveAll(List.of(writer, member));

		Board board = Board.createBoard("게시판 제목", "게시판 요약");
		boardRepository.save(board);

		Post post = Post.createPost(writer, board, "title", "content", new ArrayList<>());
		postRepository.save(post);

		//when //then
		mockMvc.perform(post("/posts/{postId}/like", post.getId()))
				.andExpect(status().isOk())
				.andDo(print());

		List<PostLike> all = postLikeRepository.findAll();
		assertThat(all).isNotEmpty();

		PostLike postLike = all.get(0);
		assertThat(postLike.getPost()).isEqualTo(post);
		assertThat(postLike.getMember()).isEqualTo(member);
	}

	@WithMockUser(username = "member")
	@DisplayName("사용자는 게시글을 좋아요를 취소 할 수 있다.")
	@Test
	void cancelLike() throws Exception {
		//given
		Member writer = Member.builder()
				.username("writer")
				.build();
		Member member = Member.builder()
				.username("member")
				.build();
		memberRepository.saveAll(List.of(writer, member));

		Board board = Board.createBoard("게시판 제목", "게시판 요약");
		boardRepository.save(board);

		Post post = Post.createPost(writer, board, "title", "content", new ArrayList<>());
		postRepository.save(post);

		PostLike postLike = new PostLike(member, post);
		postLikeRepository.save(postLike);

		//when //then
		mockMvc.perform(post("/posts/{postId}/like", post.getId()))
				.andExpect(status().isOk())
				.andDo(print());

		List<PostLike> all = postLikeRepository.findAll();
		assertThat(all).isEmpty();
	}
}