import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { Bar, BarChart, CartesianGrid, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis, Cell } from "recharts";
import { Download } from "lucide-react";
import { ChartCard } from "@/components/ChartCard";
import { EmptyState } from "@/components/EmptyState";
import { Loading } from "@/components/Loading";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
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
      { name: "Passed", value: report.rules.filter((rule) => rule.passed).length },
      { name: "Failed", value: report.rules.filter((rule) => !rule.passed).length }
    ];
  }, [report]);

  function downloadJson() {
    if (!report) return;
    const blob = new Blob([JSON.stringify(report, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `validation-report-${report.id}.json`;
    link.click();
    URL.revokeObjectURL(url);
  }

  if (loading) {
    return <Loading label="Loading report" />;
  }

  if (error || !report) {
    return <EmptyState title="Could not load report" description={error} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Report #{report.id}</h1>
          <p className="text-sm text-muted-foreground">{report.filename}</p>
        </div>
        <Button variant="outline" onClick={downloadJson}>
          <Download className="h-4 w-4" />
          Download JSON
        </Button>
      </div>
      <div className="grid gap-4 md:grid-cols-4">
        <Card><CardHeader><CardTitle className="text-sm">Status</CardTitle></CardHeader><CardContent><Badge variant={report.passed ? "success" : "destructive"}>{report.passed ? "Passed" : "Failed"}</Badge></CardContent></Card>
        <Card><CardHeader><CardTitle className="text-sm">Rules</CardTitle></CardHeader><CardContent className="text-2xl font-bold">{report.totalRules}</CardContent></Card>
        <Card><CardHeader><CardTitle className="text-sm">Errors</CardTitle></CardHeader><CardContent className="text-2xl font-bold">{report.totalErrors}</CardContent></Card>
        <Card><CardHeader><CardTitle className="text-sm">Created</CardTitle></CardHeader><CardContent className="text-sm">{new Date(report.createdAt).toLocaleString()}</CardContent></Card>
      </div>
      <div className="grid gap-4 xl:grid-cols-2">
        <ChartCard title="Rule Status">
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={statusData} dataKey="value" nameKey="name" innerRadius={70} outerRadius={100} label>
                  <Cell fill="#10b981" />
                  <Cell fill="#ef4444" />
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </ChartCard>
        <ChartCard title="Errors By Rule">
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={report.rules}>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                <XAxis dataKey="ruleName" stroke="hsl(var(--muted-foreground))" tick={{ fontSize: 11 }} />
                <YAxis stroke="hsl(var(--muted-foreground))" />
                <Tooltip />
                <Bar dataKey="errorCount" fill="#2563eb" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </ChartCard>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Rules</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {report.rules.map((rule) => (
            <div key={rule.id} className="rounded-lg border border-border p-4">
              <div className="flex items-center justify-between gap-3">
                <p className="font-medium">{rule.ruleName}</p>
                <Badge variant={rule.passed ? "success" : "destructive"}>{rule.passed ? "Passed" : "Failed"}</Badge>
              </div>
              {rule.errorMessages.length > 0 && (
                <ul className="mt-3 space-y-1 text-sm text-muted-foreground">
                  {rule.errorMessages.map((message) => <li key={message}>{message}</li>)}
                </ul>
              )}
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  );
}
