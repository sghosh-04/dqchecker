import { useEffect, useMemo, useState } from "react";
import { CheckCircle2, ClipboardList, Database, XCircle } from "lucide-react";
import { Bar, BarChart, CartesianGrid, Cell, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { ChartCard } from "@/components/ChartCard";
import { EmptyState } from "@/components/EmptyState";
import { Loading } from "@/components/Loading";
import { ReportCard } from "@/components/ReportCard";
import { Badge } from "@/components/ui/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { getErrorMessage } from "@/services/api";
import { getReports } from "@/services/report";
import type { Report } from "@/types/report";

export function Dashboard() {
  const [reports, setReports] = useState<Report[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getReports()
      .then(setReports)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  const stats = useMemo(() => {
    const passed = reports.filter((report) => report.passed).length;
    const failed = reports.length - passed;
    return { total: reports.length, passed, failed, files: new Set(reports.map((report) => report.filename)).size };
  }, [reports]);

  const trends = reports
    .slice()
    .reverse()
    .map((report) => ({
      id: `#${report.id}`,
      errors: report.totalErrors,
      rules: report.totalRules
    }));

  const ruleFailures = Object.values(
    reports.flatMap((report) => report.rules).reduce<Record<string, { name: string; failures: number }>>((acc, rule) => {
      acc[rule.ruleName] = acc[rule.ruleName] ?? { name: rule.ruleName, failures: 0 };
      acc[rule.ruleName].failures += rule.errorCount;
      return acc;
    }, {})
  );

  if (loading) {
    return <Loading label="Loading dashboard" />;
  }

  if (error) {
    return <EmptyState title="Could not load dashboard" description={error} />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Dashboard</h1>
        <p className="text-sm text-muted-foreground">Validation health, rule failures, and recent uploads.</p>
      </div>
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <ReportCard title="Total Reports" value={stats.total} icon={ClipboardList} />
        <ReportCard title="Passed" value={stats.passed} icon={CheckCircle2} tone="success" />
        <ReportCard title="Failed" value={stats.failed} icon={XCircle} tone="danger" />
        <ReportCard title="Uploaded Files" value={stats.files} icon={Database} />
      </div>
      {reports.length === 0 ? (
        <EmptyState title="No reports yet" description="Upload a CSV file to generate your first validation report." />
      ) : (
        <>
          <div className="grid gap-4 xl:grid-cols-2">
            <ChartCard title="Validation Trends" description="Errors detected across recent reports.">
              <div className="h-72">
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={trends}>
                    <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                    <XAxis dataKey="id" stroke="hsl(var(--muted-foreground))" />
                    <YAxis stroke="hsl(var(--muted-foreground))" />
                    <Tooltip />
                    <Line type="monotone" dataKey="errors" stroke="#2563eb" strokeWidth={2} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </ChartCard>
            <ChartCard title="Rule Failures" description="Total errors grouped by rule.">
              <div className="h-72">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={ruleFailures}>
                    <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                    <XAxis dataKey="name" stroke="hsl(var(--muted-foreground))" tick={{ fontSize: 11 }} />
                    <YAxis stroke="hsl(var(--muted-foreground))" />
                    <Tooltip />
                    <Bar dataKey="failures" radius={[6, 6, 0, 0]}>
                      {ruleFailures.map((entry) => <Cell key={entry.name} fill="#2563eb" />)}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </ChartCard>
          </div>
          <ChartCard title="Recent Reports">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Report ID</TableHead>
                  <TableHead>File</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Errors</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {reports.slice(0, 5).map((report) => (
                  <TableRow key={report.id}>
                    <TableCell>#{report.id}</TableCell>
                    <TableCell>{report.filename}</TableCell>
                    <TableCell>
                      <Badge variant={report.passed ? "success" : "destructive"}>{report.passed ? "Passed" : "Failed"}</Badge>
                    </TableCell>
                    <TableCell>{report.totalErrors}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </ChartCard>
        </>
      )}
    </div>
  );
}
