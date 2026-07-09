import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { Bar, BarChart, CartesianGrid, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis, Cell } from "recharts";
import { Download, AlertCircle, CheckCircle, BarChart3, PieChartIcon } from "lucide-react";
import { ChartCard } from "@/components/ChartCard";
import { EmptyState } from "@/components/EmptyState";
import { Loading } from "@/components/Loading";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { getErrorMessage } from "@/services/api";
import { getReport } from "@/services/report";
import type { Report } from "@/types/report";

export function ReportDetail() {
  const { id } = useParams();
  const [report, setReport] = useState<Report | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!id) {
      setError("Report id is required");
      setLoading(false);
      return;
    }
    getReport(id)
      .then(setReport)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, [id]);

  const statusData = useMemo(() => {
    if (!report) return [];
    return [
      { name: "Passed Rules", value: report.rules.filter((rule) => rule.passed).length },
      { name: "Failed Rules", value: report.rules.filter((rule) => !rule.passed).length }
    ];
  }, [report]);

  function downloadTxt() {
    if (!report) return;
    let text = `==================================================\n`;
    text += `              DATA QUALITY REPORT\n`;
    text += `==================================================\n`;
    text += `Report ID:      #${report.id}\n`;
    text += `File Name:      ${report.filename}\n`;
    text += `Status:         ${report.passed ? "PASSED" : "FAILED"}\n`;
    text += `Total Rules:    ${report.totalRules}\n`;
    text += `Total Errors:   ${report.totalErrors}\n`;
    text += `Date Created:   ${new Date(report.createdAt).toLocaleString()}\n\n`;
    
    text += `--------------------------------------------------\n`;
    text += `                  RULE SUMMARY\n`;
    text += `--------------------------------------------------\n`;
    
    report.rules.forEach((rule, idx) => {
      text += `${idx + 1}. ${rule.ruleName}\n`;
      text += `   Status:      ${rule.passed ? "PASSED" : "FAILED"}\n`;
      text += `   Errors:      ${rule.errorCount}\n`;
      if (rule.errorMessages.length > 0) {
        text += `   Error logs:\n`;
        rule.errorMessages.forEach((msg) => {
          text += `     - ${msg}\n`;
        });
      }
      text += `\n`;
    });
    text += `==================================================\n`;

    const blob = new Blob([text], { type: "text/plain;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `validation-report-${report.id}.txt`;
    link.click();
    URL.revokeObjectURL(url);
  }

  if (loading) {
    return <Loading label="Loading detailed audit report" />;
  }

  if (error || !report) {
    return <EmptyState title="Could not load report" description={error} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center border-b border-border pb-5">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground flex items-center gap-2">
            Audit Run #{report.id}
          </h1>
          <p className="text-sm text-muted-foreground font-mono mt-1">{report.filename}</p>
        </div>
        <Button 
          variant="outline" 
          onClick={downloadTxt} 
          className="border-neutral-300 dark:border-neutral-700 hover:bg-neutral-100 dark:hover:bg-neutral-800 text-xs shrink-0 self-start sm:self-auto"
        >
          <Download className="h-4 w-4" />
          Download Report (TXT)
        </Button>
      </div>

      {/* Metrics Row */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-xs text-muted-foreground uppercase font-bold tracking-wider">Evaluation Status</CardTitle>
          </CardHeader>
          <CardContent>
            <Badge variant={report.passed ? "success" : "destructive"} className="text-xs px-2.5 py-0.5 mt-1">
              {report.passed ? "Pass" : "Fail"}
            </Badge>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-xs text-muted-foreground uppercase font-bold tracking-wider">Total Asserts Run</CardTitle>
          </CardHeader>
          <CardContent className="text-2xl font-bold text-foreground">
            {report.totalRules}
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-xs text-muted-foreground uppercase font-bold tracking-wider">Failures Flagged</CardTitle>
          </CardHeader>
          <CardContent className="text-2xl font-bold text-destructive">
            {report.totalErrors}
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-xs text-muted-foreground uppercase font-bold tracking-wider">Completion Date</CardTitle>
          </CardHeader>
          <CardContent className="text-sm font-medium text-foreground py-1">
            {new Date(report.createdAt).toLocaleString()}
          </CardContent>
        </Card>
      </div>

      {/* Charts Row */}
      <div className="grid gap-6 xl:grid-cols-2">
        <ChartCard title="Rules Execution Status" description="Distribution of passed and failed rules inside the execution matrix.">
          <div className="h-72 mt-4 flex items-center justify-center">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={statusData} dataKey="value" nameKey="name" innerRadius={70} outerRadius={95} labelLine={false} label>
                  <Cell fill="#10a37f" />
                  <Cell fill="#ef4444" />
                </Pie>
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: "var(--card)", 
                    borderColor: "var(--border)", 
                    borderRadius: "8px",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.1)"
                  }}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </ChartCard>

        <ChartCard title="Error Breakdown by Assertion" description="Exception counts detected per validation target rule.">
          <div className="h-72 mt-4">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={report.rules}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
                <XAxis dataKey="ruleName" stroke="var(--muted-foreground)" tick={{ fontSize: 10 }} tickLine={false} axisLine={false} />
                <YAxis stroke="var(--muted-foreground)" fontSize={11} tickLine={false} axisLine={false} />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: "var(--card)", 
                    borderColor: "var(--border)", 
                    borderRadius: "8px",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.1)"
                  }}
                />
                <Bar dataKey="errorCount" radius={[6, 6, 0, 0]}>
                  {report.rules.map((rule, idx) => (
                    <Cell key={`cell-${idx}`} fill={rule.passed ? "#10a37f" : "#ef4444"} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </ChartCard>
      </div>

      {/* Rules Breakdown Log */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Detailed Rules Checklist</CardTitle>
          <CardDescription>Comprehensive audit trace and generated error logs for all configured validation constraints.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          {report.rules.map((rule) => (
            <div 
              key={rule.id} 
              className={`rounded-xl border p-4 transition-all duration-200 ${
                rule.passed 
                  ? "border-emerald-500/15 bg-emerald-500/5" 
                  : "border-destructive/20 bg-destructive/5"
              }`}
            >
              <div className="flex items-center justify-between gap-3">
                <div className="flex items-center gap-2">
                  {rule.passed ? (
                    <CheckCircle className="h-4 w-4 text-emerald-600 dark:text-emerald-400 shrink-0" />
                  ) : (
                    <AlertCircle className="h-4 w-4 text-destructive shrink-0" />
                  )}
                  <p className="font-semibold text-sm text-foreground">{rule.ruleName}</p>
                </div>
                <Badge variant={rule.passed ? "success" : "destructive"} className="text-[10px] px-2">
                  {rule.passed ? "Passed" : "Failed"}
                </Badge>
              </div>

              {rule.errorMessages.length > 0 && (
                <div className="mt-3 bg-background border border-destructive/10 rounded-lg p-3">
                  <p className="text-xs font-bold text-destructive mb-2 uppercase tracking-wide">Error Trace Log ({rule.errorCount} events)</p>
                  <ul className="space-y-1.5 list-disc pl-5 text-xs text-muted-foreground">
                    {rule.errorMessages.map((message, mIdx) => (
                      <li key={`${rule.id}-detail-msg-${mIdx}`} className="leading-normal font-mono">
                        {message}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  );
}
