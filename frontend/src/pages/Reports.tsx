import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { EmptyState } from "@/components/EmptyState";
import { Loading } from "@/components/Loading";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { getErrorMessage } from "@/services/api";
import { getReports } from "@/services/report";
import type { Report } from "@/types/report";

export function Reports() {
  const navigate = useNavigate();
  const [reports, setReports] = useState<Report[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getReports()
      .then(setReports)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <Loading label="Loading reports registry" />;
  }

  if (error) {
    return <EmptyState title="Could not load reports" description={error} />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-foreground">Validation Registry</h1>
        <p className="text-sm text-muted-foreground">Historical list of all data quality evaluations performed on the platform.</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">All Runs</CardTitle>
          <CardDescription>Select any validation run below to view detailed breakdown logs and errors.</CardDescription>
        </CardHeader>
        <CardContent>
          {reports.length === 0 ? (
            <EmptyState title="No validation reports recorded" />
          ) : (
            <div className="border rounded-xl overflow-hidden bg-background">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="font-semibold text-foreground">Report ID</TableHead>
                    <TableHead className="font-semibold text-foreground">Execution Date</TableHead>
                    <TableHead className="font-semibold text-foreground">Source File</TableHead>
                    <TableHead className="font-semibold text-foreground">Status</TableHead>
                    <TableHead className="font-semibold text-foreground">Errors Found</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {reports.map((report) => (
                    <TableRow 
                      key={report.id} 
                      className="cursor-pointer transition-colors hover:bg-neutral-100/50 dark:hover:bg-neutral-900/40" 
                      onClick={() => navigate(`/reports/${report.id}`)}
                    >
                      <TableCell className="font-medium text-foreground">#{report.id}</TableCell>
                      <TableCell className="text-xs text-muted-foreground">
                        {new Date(report.createdAt).toLocaleString()}
                      </TableCell>
                      <TableCell className="font-medium text-indigo-600 dark:text-indigo-400">
                        {report.filename}
                      </TableCell>
                      <TableCell>
                        <Badge variant={report.passed ? "success" : "destructive"}>
                          {report.passed ? "Passed" : "Failed"}
                        </Badge>
                      </TableCell>
                      <TableCell className="font-mono text-xs font-semibold">
                        {report.totalErrors} exceptions
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
