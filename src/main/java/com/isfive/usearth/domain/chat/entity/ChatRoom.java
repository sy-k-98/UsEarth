package com.isfive.usearth.domain.chat.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String roomName;

	@Column(nullable = false)
	private String description;
	@Setter
	@ColumnDefault("0")
	private Integer memberCount;

	@OneToMany(mappedBy = "chatRoom")
	private List<ChatRoomAppend> chatRoomAppends = new ArrayList<>();

	@Builder
	public ChatRoom (String roomName,String description) {
		this.roomName = roomName;
		this.description = description;
		this.memberCount = 0;
	}

	public void plusMemberCount(){
		this.memberCount++;
	}
	public void minusMemberCount(){this.memberCount--;}



}
