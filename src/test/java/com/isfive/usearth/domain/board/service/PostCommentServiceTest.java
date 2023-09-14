package com.isfive.usearth.domain.board.service;

import com.isfive.usearth.domain.board.dto.PostCommentResponse;
import com.isfive.usearth.domain.board.entity.Board;
import com.isfive.usearth.domain.board.entity.Post;
import com.isfive.usearth.domain.board.entity.PostComment;
import com.isfive.usearth.domain.board.repository.BoardRepository;
import com.isfive.usearth.domain.board.repository.PostCommentRepository;
import com.isfive.usearth.domain.board.repository.post.PostRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.isfive.usearth.domain.board.entity.Post.createPost;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostCommentServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired PostRepository postRepository;
    @Autowired PostCommentService postCommentService;
    @Autowired PostCommentRepository postCommentRepository;

    @DisplayName("댓글 작성 동시성 문제 발생 시 재시도한다.")
    @Test
    void retryLogCheck() throws InterruptedException {

        int count = 5;
        Member writer = Member.builder().username("writer").build();
        memberRepository.save(writer);
        for (int i = 0; i < count; i++) {
            Member member = Member.builder().username("member" + i).build();
            memberRepository.save(member);
        }

        Board board = Board.createBoard("게시판 제목", "게시판 요약");
        boardRepository.save(board);

        Post post = createPost(writer, board, "제목1", "내용1", new ArrayList<>());
        postRepository.save(post);

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(count);

        // when

        for (int i = 0; i < count; i++) {
            String email = "member" + i;
            String content = "content" + i;
            executorService.submit(() -> {
                        try {
                            postCommentService.createComment(post.getId(), content, email);
                        }
                        finally {
                            latch.countDown();
                        }
                    }
            );
        }

        latch.await();

        List<PostComment> all = postCommentRepository.findAll();
        Post findPost = postRepository.findById(post.getId()).orElseThrow();

        assertThat(findPost.getCommentCount()).isEqualTo(all.size());
    }

    @DisplayName("사용자는 댓글에 대댓글을 작성 할 수 있다.")
    @Test
    void createReply() {
        //given
        Member writer = Member.builder().username("writer").build();
        memberRepository.save(writer);

        Board board = Board.createBoard("게시판 제목", "게시판 요약");
        boardRepository.save(board);

        Post post = createPost(writer, board, "제목1", "내용1", new ArrayList<>());
        postRepository.save(post);

        PostComment postComment = PostComment.createPostComment(writer, post, "댓글입니다.");
        postCommentRepository.save(postComment);
        //when

        postCommentService.createReply(postComment.getId(), "대댓글입니다.", "writer");
        //then


        PostComment comment = postCommentRepository.findById(postComment.getId()).orElseThrow();
        PostComment reply = postCommentRepository.findById(postComment.getId() + 1).orElseThrow();

        assertThat(reply.getPostComment()).isEqualTo(comment);
    }

    @DisplayName("사용자는 대댓글에 대댓글을 작성 할 수 없다.")
    @Test
    void notAllowWriteReplyAtReply() {
        //given
        Member writer = Member.builder().username("writer").build();
        memberRepository.save(writer);

        Board board = Board.createBoard("게시판 제목", "게시판 요약");
        boardRepository.save(board);

        Post post = createPost(writer, board, "제목1", "내용1", new ArrayList<>());
        postRepository.save(post);

        PostComment postComment = PostComment.createPostComment(writer, post, "댓글입니다.");
        postCommentRepository.save(postComment);
        PostComment reply = PostComment.createPostComment(writer, post, "댓글입니다.");
        postComment.addReply(reply);
        postCommentRepository.save(reply);

        //when//then
        assertThatThrownBy(() -> postCommentService.createReply(reply.getId(), "대댓글입니다.", "writer"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("대댓글에는 대댓글을 작성 할 수 없습니다.");

    }

    @DisplayName("사용자는 삭제된 댓글에 대댓글을 작성 할 수 없다.")
    @Test
    void notAllowWriteReplyAtDeletedComment() {
        //given
        Member writer = Member.builder().username("writer").build();
        memberRepository.save(writer);

        Board board = Board.createBoard("게시판 제목", "게시판 요약");
        boardRepository.save(board);

        Post post = createPost(writer, board, "제목1", "내용1", new ArrayList<>());
        postRepository.save(post);

        PostComment postComment = PostComment.builder()
                .member(writer)
                .post(post)
                .content("댓글입니다.")
                .deleted(true)
                .build();
        postCommentRepository.save(postComment);

        //when//then
        assertThatThrownBy(() -> postCommentService.createReply(postComment.getId(), "대댓글입니다.", "writer"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("삭제된 댓글에는 댓글을 작성 할 수 없습니다.");
    }

    @DisplayName("사용자는 댓글을 페이징 조회 할 수 있다.")
    @Test
    void findComments() {
        //given
        Member writer = Member.builder().username("writer").build();
        memberRepository.save(writer);

        Board board = Board.createBoard("게시판 제목", "게시판 요약");
        boardRepository.save(board);

        Post post = createPost(writer, board, "제목1", "내용1", new ArrayList<>());
        postRepository.save(post);

        PostComment postComment1 = PostComment.createPostComment(writer, post, "댓글1");
        PostComment postComment2 = PostComment.createPostComment(writer, post, "댓글2");
        postCommentRepository.saveAll(List.of(postComment1, postComment2));

        PostComment reply1 = PostComment.createPostComment(writer, post, "답글1.");
        PostComment reply2 = PostComment.createPostComment(writer, post, "답글2.");
        PostComment reply3 = PostComment.createPostComment(writer, post, "답글3.");
        PostComment reply4 = PostComment.createPostComment(writer, post, "답글4.");
        postComment1.addReply(reply1);
        postComment1.addReply(reply2);
        postComment2.addReply(reply3);
        postComment2.addReply(reply4);
        postCommentRepository.saveAll(List.of(reply1, reply2, reply3, reply4));

        //when
        List<PostCommentResponse> postCommentResponses = postCommentService.findComments(post.getId(), 1).getContent();

        //then
        assertThat(postCommentResponses.size()).isEqualTo(2);
        assertThat(postCommentResponses.get(0).getId()).isEqualTo(postComment2.getId());
        assertThat(postCommentResponses.get(1).getId()).isEqualTo(postComment1.getId());

        assertThat(postCommentResponses.get(0).getPostCommentResponses().size()).isEqualTo(2);
        assertThat(postCommentResponses.get(0).getPostCommentResponses().get(0).getId()).isEqualTo(reply3.getId());
        assertThat(postCommentResponses.get(0).getPostCommentResponses().get(1).getId()).isEqualTo(reply4.getId());
        assertThat(postCommentResponses.get(1).getPostCommentResponses().size()).isEqualTo(2);
        assertThat(postCommentResponses.get(1).getPostCommentResponses().get(0).getId()).isEqualTo(reply1.getId());
        assertThat(postCommentResponses.get(1).getPostCommentResponses().get(1).getId()).isEqualTo(reply2.getId());
    }
}