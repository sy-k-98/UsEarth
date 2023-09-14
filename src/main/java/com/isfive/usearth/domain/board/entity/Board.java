package com.isfive.usearth.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String summary;

	@Builder
	private Board(String title, String summary) {
		this.title = title;
		this.summary = summary;
	}

	public static Board createBoard(String title, String summary) {
		return Board.builder()
			.title(title)
			.summary(summary)
			.build();
	}
}
