package com.hanghae7.alcoholcommunity.domain.party.service;

import com.hanghae7.alcoholcommunity.domain.chat.repository.ChatMessageRepository;
import com.hanghae7.alcoholcommunity.domain.chat.repository.ChatRoomRepository;
import com.hanghae7.alcoholcommunity.domain.common.entity.S3Service;
import com.hanghae7.alcoholcommunity.domain.member.entity.Member;
import com.hanghae7.alcoholcommunity.domain.member.repository.MemberRepository;
import com.hanghae7.alcoholcommunity.domain.party.dto.request.PartyRequestDto;
import com.hanghae7.alcoholcommunity.domain.party.entity.Party;
import com.hanghae7.alcoholcommunity.domain.party.repository.PartyParticipateRepository;
import com.hanghae7.alcoholcommunity.domain.party.repository.PartyRepository;
import com.hanghae7.alcoholcommunity.domain.common.ResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

// import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class PartyServiceTest {
	@Mock
	private PartyRepository partyRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private S3Service s3Service;
	@Mock
	private ChatRoomRepository chatRoomRepository;
	@Mock
	private ChatMessageRepository chatMessageRepository;
	@Mock
	private PartyParticipateRepository partyParticipateRepository;

	@InjectMocks
	private PartyService partyService;

	@DisplayName("모임 게시물을 생성할 수 있다.")
	@Test
	void createParty() throws IOException {
		// given
		PartyRequestDto requestDto = new PartyRequestDto();
		Member member = Mockito.mock(Member.class);
		MockMultipartFile image = new MockMultipartFile("image.jpg", "image.jpg", "image/jpeg", new byte[0]);

		Party party = new Party(requestDto, member.getMemberName(), member.getMemberUniqueId());
		party.setImageUrl(s3Service.upload(image));
		// 이미지 파일을 안올렸을때도 생성가능한 로직
		when(member.getAuthority()).thenReturn("USER");

		// when
		ResponseEntity<ResponseDto> response = partyService.createParty(requestDto, member, image);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("모임 생성에 성공했습니다.", response.getBody().getMsg());
	}

	// @DisplayName("사용이 정지된 회원은 모임 게시물을 생성할 수 없다.")
	// @Test
	// void createParty_block_exception() throws IOException {
	// 	// given
	// 	PartyRequestDto requestDto = new PartyRequestDto();
	// 	Member member = Mockito.mock(Member.class);
	// 	MockMultipartFile image = new MockMultipartFile("image.jpg", "image.jpg", "image/jpeg", new byte[0]);
	//
	// 	Party party = new Party(requestDto, member.getMemberName(), member.getMemberUniqueId());
	// 	party.setImageUrl(s3Service.upload(image));
	// 	when(member.getAuthority()).thenReturn("BLOCK");
	//
	// 	// when
	// 	// then
	// 	assertThatThrownBy(() -> partyService.createParty(requestDto, member, image))
	// 		.isInstanceOf(IllegalStateException.class)
	// 		.hasMessageContaining("정지된 회원입니다.");
	// }


}
