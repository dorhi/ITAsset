package com.saea.itasset.repository;

import com.saea.itasset.dto.UserDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 사용자 정보 검색 SP 호출
    public List<UserDto> findUserInfo(String keyword) {
        String sql = "EXEC ITM_USER_INFO ?";
        
        return jdbcTemplate.query(sql, new RowMapper<UserDto>() {
            @Override
            public UserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                UserDto user = new UserDto();
                // 캡처 화면에 첨부해주신 반환 컬럼명에 따라 정확하게 맵핑합니다.
                user.setId(rs.getString("USERID")); 
                user.setName(rs.getString("USERNAME"));
                user.setDeptCode(rs.getString("DEPART"));
                user.setDeptName(rs.getString("DEPT_NAME"));
                user.setCompanyCode(rs.getString("COMPANY_CODE"));
                user.setSaeaGCode(rs.getString("SAEA_GCODE"));
                
                try {
                    // 패스워드 컬럼명도 제공해주신 사진에 따라 PASSWD로 맵핑
                    user.setPassword(rs.getString("PASSWD")); 
                } catch (SQLException ignore) {}

                return user;
            }
        }, keyword);
    }
}
