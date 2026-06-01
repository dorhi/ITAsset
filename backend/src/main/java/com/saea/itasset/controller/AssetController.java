package com.saea.itasset.controller;

import com.saea.itasset.dto.AssignRequestDto;
import com.saea.itasset.dto.ReturnRequestDto;
import com.saea.itasset.dto.UserDto;
import com.saea.itasset.service.AssetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    // 현재 요청을 보낸 사용자 정보 가져오기 헬퍼
    private UserDto getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (UserDto) session.getAttribute("USER_INFO");
        }
        return null;
    }

    // 1. 자산 조회 API
    @GetMapping("/search")
    public ResponseEntity<?> searchAsset(@RequestParam("eno") String eno) {
        if (eno == null) return ResponseEntity.badRequest().build();
        logger.info("Asset search request for ENO: [{}]", eno.trim());
        
        Map<String, Object> result = assetService.searchAsset(eno.trim());
        
        if (result == null || result.isEmpty()) {
            logger.warn("Asset not found or data is empty for ENO: [{}]", eno.trim());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "자산을 찾을 수 없거나 데이터가 비어 있습니다."));
        }
        
        logger.info("Asset found for ENO: [{}] -> [{}]", eno.trim(), result);
        return ResponseEntity.ok(result);
    }

    // 2. 자산 회수 API
    @PostMapping("/return")
    public ResponseEntity<?> returnAsset(@RequestBody ReturnRequestDto dto, HttpServletRequest request) {
        UserDto currentUser = getCurrentUser(request);
        if (currentUser == null) return ResponseEntity.status(401).build();

        // 관리자인지 확인 (1550)
        if (!currentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "회수 권한이 없습니다."));
        }

        try {
            assetService.returnAsset(dto.getEno(), dto.getMemberId(), currentUser.getId());
            return ResponseEntity.ok(Map.of("message", "자산이 반납(회수) 처리 되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "회수 처리 실패: " + e.getMessage()));
        }
    }

    // 3. 자산 지급 API
    @PostMapping("/assign")
    public ResponseEntity<?> assignAsset(@RequestBody AssignRequestDto dto, HttpServletRequest request) {
        UserDto currentUser = getCurrentUser(request);
        if (currentUser == null) return ResponseEntity.status(401).build();

        logger.info("Asset assignment request - DTO: [{}], Requested by: [{}]", dto, currentUser.getId());
        
        try {
            assetService.assignAsset(dto, currentUser.getId());
            return ResponseEntity.ok(Map.of("message", "자산 배정(지급)이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "지급 처리 실패: " + e.getMessage()));
        }
    }
}
