import React, { useState } from 'react';
import axios from 'axios';
import { Search, X } from 'lucide-react';

const UserSearchModal = ({ onSelect, onClose }) => {
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!keyword.trim()) return;

    setLoading(true);
    try {
      // 검색어 트림(trim) 처리하여 앞뒤 공백 제거 후 전송
      const searchKeyword = keyword.trim();
      const res = await axios.get(`/api/users/info?keyword=${encodeURIComponent(searchKeyword)}`);
      setResults(res.data);
    } catch (err) {
      console.error(err);
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content glass-panel">
        <div className="modal-header">
          <h3>사용자 검색</h3>
          <button className="close-btn" onClick={onClose}><X size={24}/></button>
        </div>
        
        <form onSubmit={handleSearch} className="search-form">
          <input 
            type="text" 
            placeholder="이름 또는 사번 입력" 
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          <button type="submit" disabled={loading}>
            <Search size={18} /> 검색
          </button>
        </form>

        <div className="search-results">
          {loading && <div className="loading-text">검색 중...</div>}
          {!loading && results.length === 0 && <div className="empty-text">검색 결과가 없습니다.</div>}
          
          <ul className="result-list">
            {results.map((u) => (
              <li key={u.id} onClick={() => onSelect(u)}>
                <span className="user-info-name">{u.name} ({u.id})</span>
                <span className="user-info-dept">{u.deptName}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default UserSearchModal;
