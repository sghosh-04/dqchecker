import { api, TOKEN_KEY } from "./api";
import type { AuthUser, LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from "@/types/auth";

export async function login(payload: LoginRequest) {
  const { data } = await api.post<LoginResponse>("/auth/login", payload);
  localStorage.setItem(TOKEN_KEY, data.token);
  return data;
}

export async function register(payload: RegisterRequest) {
  const { data } = await api.post<RegisterResponse>("/auth/register", payload);
  return data;
}

export function logout() {
  localStorage.removeItem(TOKEN_KEY);
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getCurrentUser(): AuthUser | null {
  const token = getToken();
  if (!token) {
    return null;
  }

  try {
    const payload = JSON.parse(atob(token.split(".")[1])) as { sub?: string; role?: "USER" | "ADMIN" };
    return {
      email: payload.sub ?? "user",
      role: payload.role ?? "USER"
    };
  } catch {
    return null;
  }
}
