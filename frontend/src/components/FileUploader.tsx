import { UploadCloud } from "lucide-react";
import { useRef } from "react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function FileUploader({ file, onFileChange }: { file: File | null; onFileChange: (file: File | null) => void }) {
  const inputRef = useRef<HTMLInputElement | null>(null);

  return (
    <div
      className={cn(
        "flex min-h-56 flex-col items-center justify-center rounded-lg border border-dashed border-border bg-card p-8 text-center transition-colors",
        "hover:border-primary/60 hover:bg-muted/40"
      )}
    >
      <UploadCloud className="h-10 w-10 text-primary" />
      <p className="mt-4 text-sm font-medium">{file ? file.name : "Drop a CSV file here"}</p>
      <p className="mt-1 text-sm text-muted-foreground">{file ? `${Math.round(file.size / 1024)} KB ready to validate` : "CSV files with headers are supported"}</p>
      <input
        ref={inputRef}
        type="file"
        accept=".csv,text/csv"
        className="hidden"
        onChange={(event) => onFileChange(event.target.files?.[0] ?? null)}
      />
      <div className="mt-5 flex gap-2">
        <Button type="button" onClick={() => inputRef.current?.click()}>
          Select CSV
        </Button>
        {file && (
          <Button type="button" variant="outline" onClick={() => onFileChange(null)}>
            Clear
          </Button>
        )}
      </div>
    </div>
  );
}
