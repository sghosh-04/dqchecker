export interface ValidationResponse {
  reportId: number;
  passed: boolean;
  totalRules: number;
  totalErrors: number;
}

export interface RuleResult {
  id: number;
  ruleName: string;
  passed: boolean;
  errorCount: number;
  errorMessages: string[];
}

export interface Report {
  id: number;
  passed: boolean;
  totalRules: number;
  totalErrors: number;
  createdAt: string;
  filename: string;
  rules: RuleResult[];
}

export interface AdminUser {
  id: number;
  username: string;
  email: string;
  role: "USER" | "ADMIN";
  createdAt: string;
}
