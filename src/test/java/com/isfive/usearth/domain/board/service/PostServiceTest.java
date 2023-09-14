package com.isfive.usearth.domain.board.service;

import com.isfive.usearth.domain.board.dto.PostsResponse;
import com.isfive.usearth.domain.board.entity.Board;
import com.isfive.usearth.domain.board.entity.Post;
import com.isfive.usearth.domain.board.entity.PostLike;
import com.isfive.usearth.domain.board.repository.BoardRepository;
import com.isfive.usearth.domain.board.repository.PostLikeRepository;
import com.isfive.usearth.domain.board.repository.post.PostRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.isfive.usearth.domain.board.entity.Post.createPost;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostServiceTest {

    @Autowired MemberRepository memberRepository;

    @Autowired BoardRepository boardRepository;

    @Autowired PostRepository postRepository;

    @Autowired PostLikeRepository postLikeRepository;

    @Autowired PostService postService;

    @DisplayName("사용자는 게시글을 페이징 조회하여 좋아요 한 게시글들을 확인 할 수 있다.")
    @Test
    void readPosts() {
        //given
        Member writer = Member.builder().username("writer").build();
        Member member1 = Member.builder().username("member1").build();
        Member member2 = Member.builder().username("member2").build();
        memberRepository.save(writer);
        memberRepository.save(member1);
        memberRepository.save(member2);

        Board board = Board.createBoard("게시판 제목", "게시판 요약");
        boardRepository.save(board);

        Post post1 = createPost(writer, board, "제목1", "내용1", new ArrayList<>());
        Post post2 = createPost(writer, board, "제목2", "내용2", new ArrayList<>());
        Post post3 = createPost(writer, board, "제목3", "내용3", new ArrayList<>());

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        postLikeRepository.save(new PostLike(member1, post1));
        postLikeRepository.save(new PostLike(member2, post2));
        post1.increaseLikeCount();
        post2.increaseLikeCount();


        //when
        Page<PostsResponse> responses1 = postService.readPosts(board.getId(), 1, member1.getUsername());
        Page<PostsResponse> responses2 = postService.readPosts(board.getId(), 1, member2.getUsername());

        //then

        // member1, member2 가 각각 조회한 결과값
        List<PostsResponse> content1 = responses1.getContent();
        List<PostsResponse> content2 = responses2.getContent();

        assertThat(content1.get(1))
                .extracting("id", "likedByUser")
                .contains(post2.getId(), false);
        assertThat(content1.get(2))
                .extracting("id", "likedByUser")
                .contains(post1.getId(), true);
        assertThat(content2.get(1))
                .extracting("id", "likedByUser")
                .contains(post2.getId(), true);
        assertThat(content2.get(2))
                .extracting("id", "likedByUser")
                .contains(post1.getId(), false);
    }


    @DisplayName("동시에 100개 의 좋아요 요청이 왔을 때 데이터 정합성을 유지해야 한다.")
    @Test
    void request100LikeConcurrence() throws InterruptedException {

        // given
        int count = 100;

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
            executorService.submit(() -> {
                    try {
                        postService.like(post.getId(), email);
                    }
                    finally {
                        latch.countDown();
                    }
                }
            );
        }

        latch.await();

        // then
        List<PostLike> all = postLikeRepository.findAll();
        Post findPost = postRepository.findById(post.getId()).get();

        assertThat(findPost.getLikeCount()).isEqualTo(all.size());
    }
}