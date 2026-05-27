import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { getCurrentUser, getToken, logout as clearToken } from "@/services/auth";
import type { AuthUser } from "@/types/auth";

interface AuthContextValue {
  token: string | null;
  user: AuthUser | null;
  isAuthenticated: boolean;
  setAuthenticated: () => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => getToken());

  useEffect(() => {
    const expire = () => setToken(null);
    window.addEventListener("dqchecker:auth-expired", expire);
    return () => window.removeEventListener("dqchecker:auth-expired", expire);
  }, []);

  const value = useMemo<AuthContextValue>(() => {
    const user = token ? getCurrentUser() : null;
    return {
      token,
      user,
      isAuthenticated: Boolean(token),
      setAuthenticated: () => setToken(getToken()),
      logout: () => {
        clearToken();
        setToken(null);
      }
    };
  }, [token]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
}
