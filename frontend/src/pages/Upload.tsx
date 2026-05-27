import { useState } from "react";
import { Link } from "react-router-dom";
import { CheckCircle2, Loader2, XCircle } from "lucide-react";
import { FileUploader } from "@/components/FileUploader";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";
import { useToast } from "@/components/ui/toast";
import { getErrorMessage } from "@/services/api";
import { getReport, validateCsv } from "@/services/report";
import type { Report, ValidationResponse } from "@/types/report";

export function Upload() {
  const { toast } = useToast();
  const [file, setFile] = useState<File | null>(null);
  const [progress, setProgress] = useState(0);
  const [loading, setLoading] = useState(false);
  const [summary, setSummary] = useState<ValidationResponse | null>(null);
  const [report, setReport] = useState<Report | null>(null);

  async function handleValidate() {
    if (!file) {
      toast({ title: "Select a CSV file", kind: "info" });
      return;
    }

    setLoading(true);
    setProgress(0);
    setReport(null);
    try {
      const result = await validateCsv(file, setProgress);
      setSummary(result);
      const fullReport = await getReport(result.reportId);
      setReport(fullReport);
      toast({ title: "Validation complete", description: `Report #${result.reportId} generated.`, kind: "success" });
    } catch (error) {
      toast({ title: "Validation failed", description: getErrorMessage(error), kind: "error" });
    } finally {
      setLoading(false);
      setProgress(100);
    }
  }

  const passedRules = report?.rules.filter((rule) => rule.passed) ?? [];
  const failedRules = report?.rules.filter((rule) => !rule.passed) ?? [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Upload CSV</h1>
        <p className="text-sm text-muted-foreground">Run null, range, and duplicate checks against a dataset.</p>
      </div>
      <div className="grid gap-6 xl:grid-cols-[1fr_420px]">
        <div className="space-y-4">
          <FileUploader file={file} onFileChange={setFile} />
          <Card>
            <CardContent className="space-y-4 pt-5">
              <Progress value={progress} />
              <Button onClick={handleValidate} disabled={!file || loading}>
                {loading ? <Loader2 className="h-4 w-4 animate-spin" /> : <CheckCircle2 className="h-4 w-4" />}
                Validate CSV
              </Button>
            </CardContent>
          </Card>
        </div>
        <Card>
          <CardHeader>
            <CardTitle>Validation Result</CardTitle>
            <CardDescription>{summary ? `Report #${summary.reportId}` : "Results appear after validation."}</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {summary ? (
              <>
                <div className="grid grid-cols-3 gap-3">
                  <div className="rounded-md border border-border p-3">
                    <p className="text-xs text-muted-foreground">Rules</p>
                    <p className="text-xl font-semibold">{summary.totalRules}</p>
                  </div>
                  <div className="rounded-md border border-border p-3">
                    <p className="text-xs text-muted-foreground">Errors</p>
                    <p className="text-xl font-semibold">{summary.totalErrors}</p>
                  </div>
                  <div className="rounded-md border border-border p-3">
                    <p className="text-xs text-muted-foreground">Status</p>
                    <Badge variant={summary.passed ? "success" : "destructive"}>{summary.passed ? "Passed" : "Failed"}</Badge>
                  </div>
                </div>
                <div>
                  <p className="mb-2 text-sm font-medium">Passed rules</p>
                  <div className="space-y-2">
                    {passedRules.map((rule) => (
                      <div key={rule.id} className="flex items-center gap-2 text-sm text-emerald-600 dark:text-emerald-400">
                        <CheckCircle2 className="h-4 w-4" />
                        {rule.ruleName}
                      </div>
                    ))}
                  </div>
                </div>
                <div>
                  <p className="mb-2 text-sm font-medium">Failed rules</p>
                  <div className="space-y-3">
                    {failedRules.map((rule) => (
                      <div key={rule.id} className="rounded-md border border-destructive/30 p-3">
                        <div className="flex items-center gap-2 text-sm font-medium text-destructive">
                          <XCircle className="h-4 w-4" />
                          {rule.ruleName}
                        </div>
                        <ul className="mt-2 space-y-1 text-xs text-muted-foreground">
                          {rule.errorMessages.slice(0, 5).map((message) => <li key={message}>{message}</li>)}
                        </ul>
                      </div>
                    ))}
                  </div>
                </div>
                <Button variant="outline" asChild>
                  <Link to={`/reports/${summary.reportId}`}>Open report detail</Link>
                </Button>
              </>
            ) : (
              <p className="text-sm text-muted-foreground">Upload a CSV file and start validation.</p>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
