package com.saea.itasset.dto;

import lombok.Data;

@Data
public class ReturnRequestDto {
    private String eno;            // 자산번호 (QR 스캔)
    private String memberId;       // 기존에 할당된 사용자 사번
}
