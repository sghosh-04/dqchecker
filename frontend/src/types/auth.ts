export type Role = "USER" | "ADMIN";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  message: string;
  token: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role?: Role;
}

export interface RegisterResponse {
  message: string;
}

export interface AuthUser {
  email: string;
  role: Role;
}
