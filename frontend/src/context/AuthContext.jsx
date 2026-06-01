import React, { createContext, useState, useEffect, useContext } from 'react';
import axios from 'axios';

// axios 기본 설정 (인증 쿠키 포함)
axios.defaults.withCredentials = true;

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // 초기 로드 시 현재 세션 확인
  useEffect(() => {
    checkSession();
  }, []);

  const checkSession = async () => {
    try {
      const res = await axios.get('/api/users/current');
      if (res.status === 200 && res.data && typeof res.data === 'object' && res.data.id) {
        setUser(res.data);
      } else {
        setUser(null);
      }
    } catch (error) {
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (id, password) => {
    // 실제로는 PW도 있어야 하지만 요구사항 문서 상 ID 기반
    const res = await axios.post('/api/users/login', { id, password });
    setUser(res.data);
  };

  const logout = async () => {
    await axios.post('/api/users/logout');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, checkSession, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
