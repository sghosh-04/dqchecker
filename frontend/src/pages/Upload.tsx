import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { CheckCircle2, Loader2, XCircle, Plus, Trash2, Settings, Sparkles } from "lucide-react";
import { FileUploader } from "@/components/FileUploader";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";
import { useToast } from "@/components/ui/toast";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { getErrorMessage } from "@/services/api";
import { getReport, validateCsv, parseHeaders } from "@/services/report";
import type { RuleConfig } from "@/services/report";
import type { Report, ValidationResponse, RuleResult } from "@/types/report";

export function Upload() {
  const { toast } = useToast();
  const [file, setFile] = useState<File | null>(null);
  const [progress, setProgress] = useState(0);
  const [loading, setLoading] = useState(false);
  const [parsingHeaders, setParsingHeaders] = useState(false);
  const [summary, setSummary] = useState<ValidationResponse | null>(null);
  const [report, setReport] = useState<Report | null>(null);

  // Dynamic Rule Builder States
  const [headers, setHeaders] = useState<string[]>([]);
  const [rules, setRules] = useState<RuleConfig[]>([]);
  const [selectedColumn, setSelectedColumn] = useState<string>("");
  const [selectedRuleType, setSelectedRuleType] = useState<string>("NULL_CHECK");
  const [minValue, setMinValue] = useState<string>("");
  const [maxValue, setMaxValue] = useState<string>("");

  useEffect(() => {
    if (file) {
      setHeaders([]);
      setRules([]);
      setSummary(null);
      setReport(null);
      setProgress(0);
      setParsingHeaders(true);

      parseHeaders(file)
        .then((cols) => {
          setHeaders(cols);
          if (cols.length > 0) {
            setSelectedColumn(cols[0]);
          }
        })
        .catch((error) => {
          toast({ title: "Failed to parse file columns", description: getErrorMessage(error), kind: "error" });
        })
        .finally(() => {
          setParsingHeaders(false);
        });
    } else {
      setHeaders([]);
      setRules([]);
    }
  }, [file]);

  function handleAddRule() {
    if (!selectedColumn) {
      toast({ title: "Select a column", kind: "info" });
      return;
    }

    let min: number | null = null;
    let max: number | null = null;

    if (selectedRuleType === "RANGE_CHECK") {
      if (minValue === "" && maxValue === "") {
        toast({ title: "Range Check parameters required", description: "Either Min or Max is required.", kind: "info" });
        return;
      }
      if (minValue !== "") {
        min = parseFloat(minValue);
        if (isNaN(min)) {
          toast({ title: "Invalid Min value", description: "Min value must be a number.", kind: "error" });
          return;
        }
      }
      if (maxValue !== "") {
        max = parseFloat(maxValue);
        if (isNaN(max)) {
          toast({ title: "Invalid Max value", description: "Max value must be a number.", kind: "error" });
          return;
        }
      }
      if (min !== null && max !== null && min > max) {
        toast({ title: "Invalid Range limits", description: "Min limit cannot exceed Max limit.", kind: "error" });
        return;
      }
    }

    const rule: RuleConfig = {
      type: selectedRuleType,
      columnName: selectedColumn,
      min,
      max,
    };

    // Check duplicate rule configs
    const exists = rules.some((r) => r.type === rule.type && r.columnName === rule.columnName);
    if (exists) {
      toast({ title: "Duplicate rule", description: "This rule type is already defined for this column.", kind: "info" });
      return;
    }

    setRules([...rules, rule]);
    setMinValue("");
    setMaxValue("");
    toast({ title: "Rule added successfully", kind: "success" });
  }

  function handleDeleteRule(index: number) {
    setRules(rules.filter((_, i) => i !== index));
  }

  function loadRecommendedRules() {
    const recommended: RuleConfig[] = [];
    if (headers.includes("name")) {
      recommended.push({ type: "NULL_CHECK", columnName: "name", min: null, max: null });
    }
    if (headers.includes("age")) {
      recommended.push({ type: "RANGE_CHECK", columnName: "age", min: 0, max: 100 });
    }
    if (headers.includes("email")) {
      recommended.push({ type: "DUPLICATE_CHECK", columnName: "email", min: null, max: null });
    }

    if (recommended.length === 0 && headers.length > 0) {
      recommended.push({ type: "NULL_CHECK", columnName: headers[0], min: null, max: null });
    }

    setRules(recommended);
    toast({ title: "Recommended rules loaded", kind: "success" });
  }

  async function handleValidate() {
    if (!file) {
      toast({ title: "Select a CSV or Excel file", kind: "info" });
      return;
    }
    if (rules.length === 0) {
      toast({ title: "Configure at least one rule", description: "Add a validation rule before executing checks.", kind: "info" });
      return;
    }

    setLoading(true);
    setProgress(0);
    setReport(null);
    try {
      const result = await validateCsv(file, rules, setProgress);
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

  const passedRules = report?.rules.filter((rule: RuleResult) => rule.passed) ?? [];
  const failedRules = report?.rules.filter((rule: RuleResult) => !rule.passed) ?? [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Dynamic Data Validation</h1>
        <p className="text-sm text-muted-foreground">Upload your CSV or Excel dataset, define validation rules, and evaluate quality metrics.</p>
      </div>

      <div className="grid gap-6 xl:grid-cols-[1fr_450px]">
        {/* Left Side: Upload & Rule Builder */}
        <div className="space-y-6">
          <FileUploader file={file} onFileChange={setFile} />

          {file && (
            <Card>
              <CardHeader className="flex flex-row items-center justify-between">
                <div>
                  <CardTitle className="text-lg flex items-center gap-2">
                    <Settings className="h-5 w-5 text-indigo-500" />
                    Configure Validation Rules
                  </CardTitle>
                  <CardDescription>Select rules to apply to the parsed file headers.</CardDescription>
                </div>
                {headers.length > 0 && (
                  <Button variant="outline" size="sm" onClick={loadRecommendedRules} className="gap-1">
                    <Sparkles className="h-4 w-4 text-amber-500" />
                    Auto Recommend
                  </Button>
                )}
              </CardHeader>
              <CardContent className="space-y-6">
                {parsingHeaders ? (
                  <div className="flex items-center justify-center py-6 gap-2 text-sm text-muted-foreground">
                    <Loader2 className="h-5 w-5 animate-spin" />
                    Parsing dataset columns...
                  </div>
                ) : (
                  <>
                    {/* Add Rule Form */}
                    <div className="grid gap-4 md:grid-cols-3 items-end border border-border/40 rounded-lg p-4 bg-muted/20">
                      <div className="space-y-2">
                        <Label htmlFor="column">Column</Label>
                        <select
                          id="column"
                          value={selectedColumn}
                          onChange={(e) => setSelectedColumn(e.target.value)}
                          className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500"
                        >
                          {headers.map((h) => (
                            <option key={h} value={h}>
                              {h}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="ruleType">Rule Type</Label>
                        <select
                          id="ruleType"
                          value={selectedRuleType}
                          onChange={(e) => setSelectedRuleType(e.target.value)}
                          className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500"
                        >
                          <option value="NULL_CHECK">Null Check</option>
                          <option value="RANGE_CHECK">Range Check</option>
                          <option value="DUPLICATE_CHECK">Duplicate Check</option>
                        </select>
                      </div>

                      <div className="flex gap-2">
                        {selectedRuleType === "RANGE_CHECK" ? (
                          <div className="grid grid-cols-2 gap-2 flex-1">
                            <div className="space-y-1">
                              <Input
                                placeholder="Min"
                                type="number"
                                value={minValue}
                                onChange={(e) => setMinValue(e.target.value)}
                                className="h-10"
                              />
                            </div>
                            <div className="space-y-1">
                              <Input
                                placeholder="Max"
                                type="number"
                                value={maxValue}
                                onChange={(e) => setMaxValue(e.target.value)}
                                className="h-10"
                              />
                            </div>
                          </div>
                        ) : (
                          <div className="flex-1 text-xs text-muted-foreground flex items-center justify-center border border-dashed rounded-md bg-muted/40 h-10">
                            No parameters needed
                          </div>
                        )}

                        <Button onClick={handleAddRule} type="button" className="h-10 px-3 bg-indigo-600 hover:bg-indigo-700 text-white shrink-0">
                          <Plus className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>

                    {/* Active Rules List */}
                    <div>
                      <Label className="text-sm font-medium">Configured Validation Rules ({rules.length})</Label>
                      {rules.length === 0 ? (
                        <p className="text-xs text-muted-foreground mt-2 border border-dashed rounded-lg p-6 text-center">
                          No rules defined yet. Add custom rules above or load recommended settings.
                        </p>
                      ) : (
                        <div className="mt-3 border rounded-lg overflow-hidden bg-background">
                          <Table>
                            <TableHeader>
                              <TableRow>
                                <TableHead className="font-semibold text-foreground">Column</TableHead>
                                <TableHead className="font-semibold text-foreground">Rule Type</TableHead>
                                <TableHead className="font-semibold text-foreground">Configuration</TableHead>
                                <TableHead className="w-12"></TableHead>
                              </TableRow>
                            </TableHeader>
                            <TableBody>
                              {rules.map((rule, idx) => (
                                <TableRow key={`${rule.columnName}-${rule.type}-${idx}`}>
                                  <TableCell className="font-medium text-indigo-600 dark:text-indigo-400">{rule.columnName}</TableCell>
                                  <TableCell>
                                    <Badge variant="muted" className="capitalize">
                                      {rule.type.toLowerCase().replace("_", " ")}
                                    </Badge>
                                  </TableCell>
                                  <TableCell className="text-xs text-muted-foreground">
                                    {rule.type === "RANGE_CHECK"
                                      ? `Min: ${rule.min ?? "-∞"} | Max: ${rule.max ?? "∞"}`
                                      : "Default"}
                                  </TableCell>
                                  <TableCell>
                                    <Button
                                      variant="ghost"
                                      size="sm"
                                      onClick={() => handleDeleteRule(idx)}
                                      className="text-destructive hover:text-destructive/95 hover:bg-destructive/10 h-8 w-8 p-0"
                                    >
                                      <Trash2 className="h-4 w-4" />
                                    </Button>
                                  </TableCell>
                                </TableRow>
                              ))}
                            </TableBody>
                          </Table>
                        </div>
                      )}
                    </div>
                  </>
                )}
              </CardContent>
            </Card>
          )}

          {/* Validation Progress & Button */}
          <Card>
            <CardContent className="space-y-4 pt-5">
              {progress > 0 && progress < 100 && (
                <div className="space-y-2">
                  <Progress value={progress} />
                  <p className="text-xs text-muted-foreground text-center">Uploading dataset: {progress}%</p>
                </div>
              )}
              <Button
                onClick={handleValidate}
                disabled={!file || rules.length === 0 || loading || parsingHeaders}
                className="w-full bg-indigo-600 hover:bg-indigo-700 text-white gap-2 font-medium"
              >
                {loading ? <Loader2 className="h-4 w-4 animate-spin" /> : <CheckCircle2 className="h-4 w-4" />}
                Run Validation Check
              </Button>
            </CardContent>
          </Card>
        </div>

        {/* Right Side: Report Result Dashboard */}
        <Card className="h-fit">
          <CardHeader>
            <CardTitle>Validation Output</CardTitle>
            <CardDescription>
              {summary ? `Summary metrics for Report #${summary.reportId}` : "Results appear after running validation."}
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {summary ? (
              <>
                <div className="grid grid-cols-3 gap-3">
                  <div className="rounded-md border border-border/80 p-3 bg-muted/5">
                    <p className="text-xs text-muted-foreground">Total Rules</p>
                    <p className="text-xl font-bold text-indigo-600 dark:text-indigo-400">{summary.totalRules}</p>
                  </div>
                  <div className="rounded-md border border-border/80 p-3 bg-muted/5">
                    <p className="text-xs text-muted-foreground">Errors Found</p>
                    <p className="text-xl font-bold text-destructive">{summary.totalErrors}</p>
                  </div>
                  <div className="rounded-md border border-border/80 p-3 bg-muted/5">
                    <p className="text-xs text-muted-foreground">Status</p>
                    <Badge variant={summary.passed ? "success" : "destructive"} className="mt-1">
                      {summary.passed ? "Passed" : "Failed"}
                    </Badge>
                  </div>
                </div>

                {report && (
                  <>
                    {passedRules.length > 0 && (
                      <div className="space-y-2">
                        <p className="text-sm font-semibold text-emerald-600 dark:text-emerald-400">Passed Rules ({passedRules.length})</p>
                        <div className="space-y-1.5 border border-emerald-500/25 bg-emerald-500/5 rounded-lg p-3">
                          {passedRules.map((rule: RuleResult) => (
                            <div key={rule.id} className="flex items-center gap-2 text-xs text-emerald-700 dark:text-emerald-400">
                              <CheckCircle2 className="h-3.5 w-3.5 shrink-0" />
                              <span className="font-medium">{rule.ruleName}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}

                    {failedRules.length > 0 && (
                      <div className="space-y-2">
                        <p className="text-sm font-semibold text-destructive">Failed Rules ({failedRules.length})</p>
                        <div className="space-y-3">
                          {failedRules.map((rule: RuleResult) => (
                            <div key={rule.id} className="rounded-lg border border-destructive/20 bg-destructive/5 p-3">
                              <div className="flex items-center gap-2 text-xs font-semibold text-destructive">
                                <XCircle className="h-3.5 w-3.5 shrink-0" />
                                {rule.ruleName}
                              </div>
                              <ul className="mt-2 pl-5 list-disc space-y-1 text-[11px] text-muted-foreground">
                                {rule.errorMessages.slice(0, 5).map((message: string, mIdx: number) => (
                                  <li key={`${rule.id}-msg-${mIdx}`}>{message}</li>
                                ))}
                                {rule.errorMessages.length > 5 && (
                                  <li className="list-none text-[10px] text-muted-foreground/75 italic">
                                    + {rule.errorMessages.length - 5} more errors
                                  </li>
                                )}
                              </ul>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}

                    <Button variant="outline" className="w-full border-indigo-500/35 hover:bg-indigo-500/5 hover:text-indigo-600" asChild>
                      <Link to={`/reports/${summary.reportId}`}>Open Detailed Report</Link>
                    </Button>
                  </>
                )}
              </>
            ) : (
              <div className="text-center py-12 text-sm text-muted-foreground border border-dashed rounded-lg bg-muted/10">
                Upload a file to start configuring validation parameters.
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
