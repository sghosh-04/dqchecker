import { useEffect, useMemo, useState } from "react";
import { CheckCircle2, ClipboardList, Database, XCircle, TrendingUp, BarChart4 } from "lucide-react";
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
    return <Loading label="Loading analytics dashboard" />;
  }

  if (error) {
    return <EmptyState title="Could not load dashboard" description={error} />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-foreground">Data Quality Monitor</h1>
        <p className="text-sm text-muted-foreground">Monitor platform health, validation trends, and real-time exceptions.</p>
      </div>

      {/* Stats Cards */}
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <ReportCard title="Total Run Reports" value={stats.total} icon={ClipboardList} />
        <ReportCard title="Validation Passed" value={stats.passed} icon={CheckCircle2} tone="success" />
        <ReportCard title="Validation Failed" value={stats.failed} icon={XCircle} tone="danger" />
        <ReportCard title="Monitored Files" value={stats.files} icon={Database} />
      </div>

      {reports.length === 0 ? (
        <EmptyState title="No analytics data available" description="Upload a CSV or Excel spreadsheet on the validate page to build metrics." />
      ) : (
        <>
          {/* Charts Row */}
          <div className="grid gap-6 xl:grid-cols-2">
            <ChartCard title="Validation Trend Analysis" description="Historical trace of error occurrence over successive validations.">
              <div className="h-72 mt-4">
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={trends}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                    <XAxis dataKey="id" stroke="var(--muted-foreground)" fontSize={11} tickLine={false} axisLine={false} />
                    <YAxis stroke="var(--muted-foreground)" fontSize={11} tickLine={false} axisLine={false} />
                    <Tooltip 
                      contentStyle={{ 
                        backgroundColor: "var(--card)", 
                        borderColor: "var(--border)", 
                        borderRadius: "8px",
                        boxShadow: "0 4px 12px rgba(0,0,0,0.1)"
                      }}
                      labelClassName="font-semibold text-foreground"
                    />
                    <Line type="monotone" dataKey="errors" stroke="#10a37f" strokeWidth={2.5} dot={{ r: 4 }} activeDot={{ r: 6 }} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </ChartCard>

            <ChartCard title="Error Breakdown by Rule Type" description="Distribution of quality check failures grouped by specific assertions.">
              <div className="h-72 mt-4">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={ruleFailures}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                    <XAxis dataKey="name" stroke="var(--muted-foreground)" tick={{ fontSize: 10 }} tickLine={false} axisLine={false} />
                    <YAxis stroke="var(--muted-foreground)" fontSize={11} tickLine={false} axisLine={false} />
                    <Tooltip 
                      contentStyle={{ 
                        backgroundColor: "var(--card)", 
                        borderColor: "var(--border)", 
                        borderRadius: "8px",
                        boxShadow: "0 4px 12px rgba(0,0,0,0.1)"
                      }}
                    />
                    <Bar dataKey="failures" radius={[6, 6, 0, 0]}>
                      {ruleFailures.map((entry, idx) => (
                        <Cell key={`cell-${idx}`} fill="#10a37f" />
                      ))}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </ChartCard>
          </div>

          {/* Recent Reports Table */}
          <ChartCard title="Recent Validations" description="Overview of the latest data quality runs.">
            <div className="mt-4 border rounded-xl overflow-hidden bg-background">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="font-semibold text-foreground">Report ID</TableHead>
                    <TableHead className="font-semibold text-foreground">Source File</TableHead>
                    <TableHead className="font-semibold text-foreground">Status</TableHead>
                    <TableHead className="font-semibold text-foreground">Exceptions Detected</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {reports.slice(0, 5).map((report) => (
                    <TableRow key={report.id}>
                      <TableCell className="font-medium">#{report.id}</TableCell>
                      <TableCell className="text-indigo-600 dark:text-indigo-400 font-medium">{report.filename}</TableCell>
                      <TableCell>
                        <Badge variant={report.passed ? "success" : "destructive"}>
                          {report.passed ? "Passed" : "Failed"}
                        </Badge>
                      </TableCell>
                      <TableCell className="font-mono text-xs">{report.totalErrors} errors</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </ChartCard>
        </>
      )}
    </div>
  );
}
