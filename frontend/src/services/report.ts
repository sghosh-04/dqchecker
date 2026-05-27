import { api } from "./api";
import type { AdminUser, Report, ValidationResponse } from "@/types/report";

export async function validateCsv(file: File, onProgress?: (progress: number) => void) {
  const formData = new FormData();
  formData.append("file", file);

  const { data } = await api.post<ValidationResponse>("/validate", formData, {
    headers: { "Content-Type": "multipart/form-data" },
    onUploadProgress: (event) => {
      if (event.total && onProgress) {
        onProgress(Math.round((event.loaded * 100) / event.total));
      }
    }
  });

  return data;
}

export async function getReports() {
  const { data } = await api.get<Report[]>("/reports/all");
  return data;
}

export async function getReport(id: string | number) {
  const { data } = await api.get<Report>(`/reports/${id}`);
  return data;
}

export async function getUsers() {
  const { data } = await api.get<AdminUser[]>("/admin/users");
  return data;
}
