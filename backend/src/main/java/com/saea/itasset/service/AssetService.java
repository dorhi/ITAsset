package com.saea.itasset.service;

import com.saea.itasset.dto.AssignRequestDto;
import com.saea.itasset.dto.UserDto;
import com.saea.itasset.repository.AssetRepository;
import com.saea.itasset.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public AssetService(AssetRepository assetRepository, UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    // 1. 자산 조회
    public Map<String, Object> searchAsset(String eno) {
        return assetRepository.searchAsset(eno);
    }

    // 2. 자산 회수 (반납)
    @Transactional
    public void returnAsset(String eno, String memberId, String currentUserId) {
        // Validation check if needed
        assetRepository.returnAsset(eno, memberId, currentUserId);
    }

    // 3. 자산 지급
    @Transactional
    public void assignAsset(AssignRequestDto requestDto, String currentUserId) {
        // 0. 중복 지급 방지 체크 (이미 할당된 자산인지 확인)
        Map<String, Object> assetInfo = assetRepository.searchAsset(requestDto.getEno());
        if (assetInfo != null && !assetInfo.isEmpty()) {
            Object memberObj = assetInfo.get("MEMBERNM");
            if (memberObj == null) memberObj = assetInfo.get("membernm");
            
            if (memberObj != null && !memberObj.toString().trim().isEmpty()) {
                throw new RuntimeException("이미 [" + memberObj.toString().trim() + "]님에게 할당된 자산입니다. 회수 처리 후 다시 시도하세요.");
            }
        }

        // 1. UNO 생성
        String uno = assetRepository.createUno();
        if (uno == null || uno.isEmpty()) {
            throw new RuntimeException("UNO 채번 실패");
        }

        // 2. 사용자 정보 보완 (CompanyCode, SaeaGCode가 없을 경우)
        String companyCode = requestDto.getCompanyCode();
        String saeaGCode = requestDto.getSaeaGCode();
        
        if (companyCode == null || saeaGCode == null) {
            List<UserDto> users = userRepository.findUserInfo(requestDto.getMemberId());
            if (users != null && !users.isEmpty()) {
                UserDto targetUser = users.get(0);
                companyCode = targetUser.getCompanyCode();
                saeaGCode = targetUser.getSaeaGCode();
            } else {
                throw new IllegalArgumentException("지급 대상자 정보를 찾을 수 없습니다.");
            }
        }

        // 3. 매핑 SP 실행
        assetRepository.assignAsset(
            requestDto.getEno(), 
            uno, 
            companyCode, 
            saeaGCode, 
            requestDto.getMemberId(), 
            currentUserId
        );
    }
}
