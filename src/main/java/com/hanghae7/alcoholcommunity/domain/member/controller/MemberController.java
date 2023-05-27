package com.hanghae7.alcoholcommunity.domain.member.controller;

import com.hanghae7.alcoholcommunity.domain.common.ResponseDto;
import com.hanghae7.alcoholcommunity.domain.common.security.UserDetailsImplement;
import com.hanghae7.alcoholcommunity.domain.member.dto.IndividualPageResponseDto;
import com.hanghae7.alcoholcommunity.domain.member.dto.MemberPageUpdateRequestDto;
import com.hanghae7.alcoholcommunity.domain.member.dto.MemberResponseDto;
import com.hanghae7.alcoholcommunity.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.IOException;

/**
 * Please explain the class!!
 * MemberController Class
 * @fileName      : MemberController
 * @author        : 승현
 * @since         : 2023-05-19
 */
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * Member page response entity.
     *
     * @param userDetails the user details
     * @return the response entity
     */
    @GetMapping("/member/info")
    public ResponseEntity<ResponseDto> memberPage(@AuthenticationPrincipal UserDetailsImplement userDetails){

        try{
            return memberService.memberPage(userDetails.getMember().getMemberUniqueId());
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseDto(400, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Member page update response entity.
     *
     * @param requestDto  the request dto
     * @param image       the image
     * @param userDetails the user details
     * @return the response entity
     */
    @PutMapping(value ="/member/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> memberPageUpdate(@RequestPart(value = "data") MemberPageUpdateRequestDto requestDto,
                                                        @RequestPart(value = "image", required = false) MultipartFile image,
                                                        @AuthenticationPrincipal UserDetailsImplement userDetails) {

        try {
            return memberService.memberPageUpdate(requestDto, userDetails.getMember(), image);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseDto(400, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            //e.printStackTrace();  // 예외처리 명확하게 해야 함
            return new ResponseEntity<>(new ResponseDto(400, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Individual page response entity.
     *
     * @param memberId    the member id
     * @param userDetails the user details
     * @return the response entity
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ResponseDto> individualPage(@PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImplement userDetails){
        try{
            return memberService.individualPage(memberId);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseDto(400, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
