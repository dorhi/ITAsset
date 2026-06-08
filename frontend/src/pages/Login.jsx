import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!loginId.trim() || !password.trim()) return;
    
    try {
      await login(loginId, password);
      navigate('/');
    } catch (err) {
      const backendMessage = err.response?.data?.message;
      setError(backendMessage || '로그인에 실패했습니다. 아이디 또는 비밀번호를 확인해주세요.');
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2>IT 자산 관리 로그인 111</h2>
        <p>계정 정보를 입력해주세요</p>
        
        {error && <div className="error-msg">{error}</div>}
        
        <form onSubmit={handleLogin} className="login-form">
          <div className="form-group">
            <label>아이디 (ID)</label>
            <input 
              type="text" 
              value={loginId} 
              onChange={(e) => setLoginId(e.target.value)}
              placeholder="아이디를 입력하세요"
              required
            />
          </div>
          <div className="form-group">
            <label>비밀번호 (Password)</label>
            <input 
              type="password" 
              value={password} 
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호를 입력하세요"
              required
            />
          </div>
          <button type="submit" className="login-btn">로그인</button>
        </form>
      </div>
    </div>
  );
};

export default Login;
