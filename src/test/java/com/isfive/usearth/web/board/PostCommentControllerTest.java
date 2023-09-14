package com.isfive.usearth.web.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isfive.usearth.domain.board.entity.Board;
import com.isfive.usearth.domain.board.entity.Post;
import com.isfive.usearth.domain.board.entity.PostComment;
import com.isfive.usearth.domain.board.repository.BoardRepository;
import com.isfive.usearth.domain.board.repository.PostCommentRepository;
import com.isfive.usearth.domain.board.repository.post.PostRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.web.board.dto.PostCommentCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PostCommentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired BoardRepository boardRepository;
    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostCommentRepository postCommentRepository;

    @WithMockUser(username = "comment writer")
    @DisplayName("사용자는 댓글을 작성할 수 있다.")
    @Test
    void writeComment() throws Exception {
        //given
        Member postWriter = Member.builder()
                .username("post writer")
                .build();
        Member commentWriter = Member.builder()
                .username("comment writer")
                .build();
        memberRepository.saveAll(List.of(postWriter, commentWriter));

        Board board = Board.createBoard("게시판 제목", "게시판 요약");
        boardRepository.save(board);

        Post post = Post.createPost(postWriter, board, "title", "content", new ArrayList<>());
        postRepository.save(post);
        PostCommentCreateRequest request = new PostCommentCreateRequest("댓글입니다.");
        //when //then

        mockMvc.perform(post("/posts/{postId}/comments", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(MockMvcResultHandlers.print());

        Post findPost = postRepository.findById(post.getId()).orElseThrow();
        List<PostComment> all = postCommentRepository.findAll();

        assertThat(findPost.getCommentCount()).isEqualTo(1);
        assertThat(all.size()).isEqualTo(1);
    }

    @WithMockUser(username = "comment writer")
    @DisplayName("사용자는 댓글을 작성할 수 있다.")
    @Test
    void writeReply() throws Exception {
        //given
        Member postWriter = Member.builder()
                .username("post writer")
                .build();
        Member commentWriter = Member.builder()
                .username("comment writer")
                .build();
        memberRepository.saveAll(List.of(postWriter, commentWriter));

        Board board = Board.createBoard("게시판 제목", "게시판 요약");
        boardRepository.save(board);

        Post post = Post.createPost(postWriter, board, "title", "content", new ArrayList<>());
        postRepository.save(post);

        PostComment postComment = PostComment.createPostComment(postWriter, post, "댓글입니다.");
        postCommentRepository.save(postComment);

        PostCommentCreateRequest request = new PostCommentCreateRequest("댓글입니다.");
        //when //then

        mockMvc.perform(post("/post-comments/{commentId}/reply", postComment.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(MockMvcResultHandlers.print());


        PostComment comment = postCommentRepository.findById(postComment.getId()).orElseThrow();
        PostComment reply = postCommentRepository.findById(postComment.getId() + 1).orElseThrow();

        assertThat(reply.getPostComment()).isEqualTo(comment);
    }

    @WithMockUser(username = "comment writer")
    @DisplayName("사용자는 댓글을 삭제할 수 있다.")
    @Test
    void deleteReply() throws Exception {
        //given
        Member postWriter = Member.builder()
                .username("post writer")
                .build();
        Member commentWriter = Member.builder()
                .username("comment writer")
                .build();
        memberRepository.saveAll(List.of(postWriter, commentWriter));

        Board board = Board.createBoard("게시판 제목", "게시판 요약");
        boardRepository.save(board);

        Post post = Post.createPost(postWriter, board, "title", "content", new ArrayList<>());
        postRepository.save(post);

        PostComment postComment = PostComment.createPostComment(commentWriter, post, "댓글입니다.");
        postCommentRepository.save(postComment);

        PostCommentCreateRequest request = new PostCommentCreateRequest("댓글입니다.");
        //when //then

        mockMvc.perform(delete("/post-comments/{commentId}", postComment.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(MockMvcResultHandlers.print());

        PostComment comment = postCommentRepository.findById(postComment.getId()).orElseThrow();
        assertThat(comment.isDeleted()).isTrue();
    }

}