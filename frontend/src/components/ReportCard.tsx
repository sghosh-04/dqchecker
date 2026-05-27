import type { LucideIcon } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export function ReportCard({ title, value, icon: Icon, tone = "default" }: { title: string; value: string | number; icon: LucideIcon; tone?: "default" | "success" | "danger" }) {
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground">{title}</CardTitle>
        <Icon className={tone === "success" ? "h-4 w-4 text-emerald-500" : tone === "danger" ? "h-4 w-4 text-destructive" : "h-4 w-4 text-primary"} />
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{value}</div>
      </CardContent>
    </Card>
  );
}
